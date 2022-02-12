package ladislav.sevcuj.endlessdarts.ui.viewmodels

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ladislav.sevcuj.endlessdarts.App
import ladislav.sevcuj.endlessdarts.DartBoard
import ladislav.sevcuj.endlessdarts.DateInstance
import ladislav.sevcuj.endlessdarts.asDatetimeString
import ladislav.sevcuj.endlessdarts.db.*
import ladislav.sevcuj.endlessdarts.db.Target

class GameScreenViewModel(
    private val user: User,
    val target: Target,
    app: App,
) : ViewModel() {
    private val sessionRepository = app.sessionRepository
    private val sessionStatsRepository = app.sessionStatsRepository
    private val dartRepository = app.dartRepository
    private val throwRepository = app.throwRepository

    private lateinit var session: Session

    private val _multiplicator = MutableLiveData(1)
    val multiplicator: LiveData<Int>
        get() = _multiplicator

    private val _currentThrow = MutableLiveData<Throw>()
    val currentThrow: LiveData<Throw>
        get() = _currentThrow

    private val throws: MutableList<Throw> = mutableListOf()

    private val _lastThrow = MutableLiveData<Throw?>()
    val lastThrow: LiveData<Throw?>
        get() = _lastThrow

    private val _stats = MutableLiveData<SessionStats>()
    val stats: LiveData<SessionStats>
        get() = _stats

    init {
        viewModelScope.launch(Dispatchers.IO) {
            session = Session(
                id = 0,
                userId = user.id,
                startDateTime = DateInstance.now().asDatetimeString(),
            )

            val sessionId = sessionRepository.insert(session)

            val stats = SessionStats(
                sessionId = sessionId,
            )

            sessionStatsRepository.insert(stats)

            _currentThrow.postValue(buildNewThrow())
            _stats.postValue(stats)
        }
    }

    fun onDart(field: DartBoard.Field) {
        viewModelScope.launch(Dispatchers.IO) {
            _currentThrow.value?.let { current ->
                val currentThrow: Throw

                if (current.darts.size >= 3) {
                    currentThrow = buildNewThrow()
                    throws.add(current)
                    _lastThrow.postValue(current)
                } else {
                    currentThrow = current
                }

                val darts = currentThrow.darts.toMutableList()

                val multi = if (field.defaultMultiplication > 1) {
                    field.defaultMultiplication
                } else {
                    _multiplicator.value!!
                }

                val dart = Dart(
                    id = 0,
                    order = darts.size + 1,
                    throwId = current.id,
                    multiplicator = _multiplicator.value!!,
                    number = field.value!!,
                    sum = multi * field.value,
                )

                darts.add(dart)

                val values = darts.map { item -> item.sum }.toTypedArray()

                var copy = currentThrow.copy(
                    throwSummary = darts.sumOf { item -> item.sum },
                    dartsAverage = values.average().toInt(),
                    dartsCount = darts.size,
                    doubleCount = darts.count { item -> item.multiplicator == 2 },
                    tripleCount = darts.count { item -> item.multiplicator == 3 },
                    targetHits = darts.count { item -> item.number == target.number },
                    targetSuccess = darts.firstOrNull { item -> item.number != target.number } == null,
                    firstDartDatetime = current.firstDartDatetime ?: DateInstance.now()
                        .asDatetimeString(),
                    lastDartDatetime = DateInstance.now().asDatetimeString(),
                )

                if (copy.id > 0) {
                    throwRepository.update(copy)
                } else {
                    val id = throwRepository.insert(copy)
                    copy = copy.copy(id = id)
                }

                copy.darts = darts

                _currentThrow.postValue(copy)
                _multiplicator.postValue(1)

                if (darts.size >= 3) {
                    calculateStats(copy)
                }

                dartRepository.insert(dart)
            }
        }
    }

    private fun calculateStats(lastThrow: Throw) {
        val darts = lastThrow.darts

        val allDarts = mutableListOf<Dart>()
        throws.forEach { t ->
            allDarts.addAll(t.darts)
        }

        allDarts.addAll(darts)

        val max = darts.map { it.sum }.toTypedArray().sum()
        val average = allDarts.map { it.sum }.toTypedArray().average()
        val isFullSuccess = darts.firstOrNull { d -> d.number != target.number } == null
        val isFullMiss = darts.firstOrNull { d -> d.number == target.number } == null

        _stats.value?.let {
            var targetHits = 0
            var countDouble = 0
            var countTriple = 0
            var count100 = 0
            var count140 = 0
            var count180 = 0
            var firstIsSuccess = false
            var isFail = false

            if (lastThrow.throwSummary > 100) {
                count100++
            }

            if (lastThrow.throwSummary > 140) {
                count140++
            }

            if (lastThrow.throwSummary > 180) {
                count180++
            }

            var isOk = true

            darts.forEachIndexed { index, dart ->
                if (dart.number == target.number) {
                    targetHits++

                    if (index == 0) {
                        firstIsSuccess = true
                    }
                } else {
                    if (index == 2 && isOk) {
                        isFail = true
                    }

                    isOk = false
                }

                if (dart.multiplicator == 2) {
                    countDouble++
                }

                if (dart.multiplicator == 3) {
                    countTriple++
                }
            }

            val stats = it.copy(
                throwsCount = it.throwsCount + 1,
                dartsCount = it.dartsCount + darts.size,
                doubleCount = it.doubleCount + countDouble,
                tripleCount = it.tripleCount + countTriple,
                firstDartSuccessCount = if (firstIsSuccess) it.firstDartSuccessCount + 1 else it.firstDartSuccessCount,
                fullSuccessCount = if (isFullSuccess) it.fullSuccessCount + 1 else it.fullSuccessCount,
                fullMissCount = if (isFullMiss) it.fullMissCount + 1 else it.fullMissCount,
                targetHitsCount = it.targetHitsCount + targetHits,
                average = (average * 100).toInt(),
                max = if (max > it.max) max else it.max,
                sum180Count = it.sum180Count + count180,
                above140Count = it.above140Count + count140,
                above100Count = it.above100Count + count100,
                failCount = if (isFail) it.failCount + 1 else it.failCount,
            )

            sessionStatsRepository.update(stats)
            _stats.postValue(stats)
        }
    }

    private fun buildNewThrow() = Throw(
        id = 0,
        sessionId = session.id,
        player = user.identifier,
        target = target.label,
    )

    fun onActionButton(field: DartBoard.Field) {
        when (field.identifier) {
            "0" -> {
                onDart(field)
            }
            "double" -> {
                _multiplicator.postValue(
                    if (_multiplicator.value == 2) {
                        1
                    } else {
                        2
                    }
                )
            }
            "triple" -> {
                _multiplicator.postValue(
                    if (_multiplicator.value == 3) {
                        1
                    } else {
                        3
                    }
                )
            }
            "deleteLast" -> {
                _currentThrow.value?.let {
                    val darts = it.darts.toMutableList()

                    if (darts.removeLastOrNull() != null) {
                        val copy = it.copy(
                            throwSummary = darts.sumOf { dart -> dart.sum },
                        )

                        copy.darts = darts

                        _currentThrow.postValue(copy)
                    }
                }
            }
        }
    }
}

class GameScreenViewModelFactory(
    private val app: App,
    private val user: User,
    private val target: Target,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameScreenViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GameScreenViewModel(
                user,
                target,
                app,
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
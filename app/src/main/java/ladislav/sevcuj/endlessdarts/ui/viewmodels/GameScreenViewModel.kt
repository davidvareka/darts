package ladislav.sevcuj.endlessdarts.ui.viewmodels

import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.*
import androidx.room.withTransaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ladislav.sevcuj.endlessdarts.*
import ladislav.sevcuj.endlessdarts.db.*


class GameScreenViewModel(
    private var user: User,
    private val app: App,
    private val globalViewModel: GlobalViewModel,
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

    var saveTimer: CountDownTimer? = null

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch(Dispatchers.IO) {
            val existingSession = sessionRepository.getForDay(
                user.id,
                DateInstance.now().asDateString(),
            )

            val stats: SessionStats

            if (existingSession != null) {
                session = existingSession
                stats = sessionStatsRepository.read(session.id)
            } else {
                session = Session(
                    id = 0,
                    userId = user.id,
                    startDateTime = DateInstance.now().asDatetimeString(),
                )
                stats = SessionStats(
                    sessionId = 0,
                )
            }

            _lastThrow.postValue(null)
            _currentThrow.postValue(buildNewThrow())
            _stats.postValue(stats)
        }
    }

    fun onDart(field: DartBoard.Field) {
        var currentThrow = _currentThrow.value!!

        saveTimer?.let { timer ->
            timer.cancel()
            saveTimer = null
            storeData(currentThrow)
            throws.add(currentThrow)
            _lastThrow.postValue(currentThrow)
            currentThrow = buildNewThrow()
        }

        val gameTarget = globalViewModel.target.value!!

        val darts = currentThrow.darts.toMutableList()

        val multi = if (field.defaultMultiplication > 1) {
            field.defaultMultiplication
        } else {
            _multiplicator.value!!
        }

        val dart = Dart(
            id = 0,
            order = darts.size + 1,
            throwId = currentThrow.id,
            sessionId = session.id,
            multiplicator = if (field.defaultMultiplication > 1) field.defaultMultiplication else _multiplicator.value!!,
            number = field.value,
            sum = multi * field.value,
        )

        darts.add(dart)

        val values = darts.map { item -> item.sum }.toTypedArray()

        val newThrow = currentThrow.copy(
            throwSummary = darts.sumOf { item -> item.sum },
            dartsAverage = values.average().toInt(),
            dartsCount = darts.size,
            doubleCount = darts.count { item -> item.multiplicator == 2 },
            tripleCount = darts.count { item -> item.multiplicator == 3 },
            targetHits = darts.count { item -> item.number == gameTarget.number },
            targetSuccess = darts.firstOrNull { item -> item.number != gameTarget.number } == null,
            firstDartDatetime = currentThrow.firstDartDatetime ?: DateInstance.now()
                .asDatetimeString(),
            lastDartDatetime = DateInstance.now().asDatetimeString(),
            firstDartIsSuccess = darts.first().number == gameTarget.number,
            onlyLastDartIsFail = darts.indexOfFirst { d -> d.number != gameTarget.number } == 2,
        )

        newThrow.darts = darts

        _currentThrow.postValue(newThrow)
        _multiplicator.postValue(1)

        if (newThrow.darts.size >= 3) {
            saveTimer = object : CountDownTimer(3000, 3000) {
                override fun onTick(millisUntilFinished: Long) {
                }

                override fun onFinish() {
                    storeData(newThrow)
                    throws.add(newThrow)
                    _lastThrow.postValue(newThrow)
                    _currentThrow.postValue(buildNewThrow())
                    saveTimer = null
                }
            }.start()
        }
    }

    private fun storeData(newThrow: Throw) {
        viewModelScope.launch(Dispatchers.IO) {
            app.database.withTransaction {
                val sessionId = if (session.id == 0L) {
                    val id = sessionRepository.insert(session)
                    session = session.copy(id = id)

                    //compensate disk delay
                    Handler(Looper.getMainLooper()).postDelayed({
                        globalViewModel.onSessionCreated.postValue(id)
                    }, 200)

                    id
                } else {
                    session.id
                }

                val id = throwRepository.insert(newThrow.copy(sessionId = sessionId))

                newThrow.darts.forEachIndexed { index, newDart ->
                    dartRepository.insert(newDart.copy(throwId = id, order = index + 1))
                }

                calculateStats(newThrow)
            }
        }
    }

    private fun calculateStats(lastThrow: Throw) {
        val stats = _stats.value!!
        val statsAreStored = stats.sessionId > 0
        val darts = lastThrow.darts

        val allDarts = mutableListOf<Dart>()
        throws.forEach { t ->
            allDarts.addAll(t.darts)
        }

        allDarts.addAll(darts)

        val max = darts.map { it.sum }.toTypedArray().sum()
        val average = allDarts.map { it.sum }.toTypedArray().average()

        var count100 = 0
        var count140 = 0
        var count180 = 0

        if (lastThrow.throwSummary > 100) {
            count100++
        }

        if (lastThrow.throwSummary > 140) {
            count140++
        }

        if (lastThrow.throwSummary > 180) {
            count180++
        }

        val newStats = stats.copy(
            sessionId = session.id,
            throwsCount = stats.throwsCount + 1,
            dartsCount = stats.dartsCount + darts.size,
            doubleCount = stats.doubleCount + lastThrow.doubleCount,
            tripleCount = stats.tripleCount + lastThrow.tripleCount,
            firstDartSuccessCount = if (lastThrow.firstDartIsSuccess) stats.firstDartSuccessCount + 1 else stats.firstDartSuccessCount,
            fullSuccessCount = if (lastThrow.targetSuccess) stats.fullSuccessCount + 1 else stats.fullSuccessCount,
            fullMissCount = if (lastThrow.targetHits == 0) stats.fullMissCount + 1 else stats.fullMissCount,
            targetHitsCount = stats.targetHitsCount + lastThrow.targetHits,
            average = (average * 100).toInt(),
            max = if (max > stats.max) max else stats.max,
            sum180Count = stats.sum180Count + count180,
            above140Count = stats.above140Count + count140,
            above100Count = stats.above100Count + count100,
            failCount = if (lastThrow.onlyLastDartIsFail) stats.failCount + 1 else stats.failCount,
        )

        _stats.postValue(newStats)

        viewModelScope.launch(Dispatchers.IO) {
            if (statsAreStored) {
                sessionStatsRepository.update(newStats)
            } else {
                sessionStatsRepository.insert(newStats)
            }
        }
    }

    private fun buildNewThrow(
    ): Throw {
        val gameTarget = globalViewModel.target.value!!

        return Throw(
            id = 0,
            sessionId = session.id,
            player = user.identifier,
            target = gameTarget.number,
            isLogged = user.isTemporary,
        )
    }

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
                    saveTimer?.cancel()
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

    fun onUser(newUser: User) {
        if (user.id != newUser.id) {
            user = newUser
            load()
        }
    }
}

class GameScreenViewModelFactory(
    private val app: App,
    private val user: User,
    private val globalViewModel: GlobalViewModel,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameScreenViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GameScreenViewModel(
                user,
                app,
                globalViewModel
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
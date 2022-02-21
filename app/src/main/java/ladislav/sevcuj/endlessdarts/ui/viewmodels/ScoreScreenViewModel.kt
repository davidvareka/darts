package ladislav.sevcuj.endlessdarts.ui.viewmodels

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ladislav.sevcuj.endlessdarts.App
import ladislav.sevcuj.endlessdarts.DateInstance
import ladislav.sevcuj.endlessdarts.asDateString
import ladislav.sevcuj.endlessdarts.db.*
import ladislav.sevcuj.endlessdarts.ui.screens.score.ThrowHistoryRowData
import ladislav.sevcuj.endlessdarts.ui.widgets.StatsRowData
import java.util.*

class ScoreScreenViewModel(
    app: App,
    private var user: User,
) : ViewModel() {
    private var throwsJob: Job? = null
    private var statsJob: Job? = null

    private val throwRepository = app.throwRepository
    private val dartRepository = app.dartRepository
    private val sessionRepository = app.sessionRepository
    private val sessionStatsRepository = app.sessionStatsRepository

    private val _selectedSession = MutableLiveData<Session>()
    val selectedSession: LiveData<Session>
        get() = _selectedSession

    private val _prevSession = MutableLiveData<String>()
    val prevSession: LiveData<String>
        get() = _prevSession

    private val _nextSession = MutableLiveData<String?>()
    val nextSession: LiveData<String?>
        get() = _nextSession

    private val _throws = MutableLiveData<List<ThrowHistoryRowData>>()
    val throws: LiveData<List<ThrowHistoryRowData>>
        get() = _throws

    private val _stats = MutableLiveData<List<StatsRowData>>()
    val stats: LiveData<List<StatsRowData>>
        get() = _stats

    private var filter = ThrowFilter.NONE

    private val _filterIsActive = MutableLiveData(false)
    val filterIsActive: LiveData<Boolean>
        get() = _filterIsActive

    init {
        load()
    }

    fun load(sessionId: Long? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            val session = if (sessionId == null) {
                sessionRepository.getForDay(user.id, DateInstance.now().asDateString())
            } else {
                sessionRepository.read(sessionId)
            }

            session?.let {
                _selectedSession.postValue(it)

                val now = DateInstance.now()

                val prev = DateInstance.fromString(it.startDateTime)
                prev.add(Calendar.DAY_OF_MONTH, -1)
                _prevSession.postValue(prev.asDateString())

                val next = DateInstance.fromString(it.startDateTime)
                next.add(Calendar.DAY_OF_MONTH, 1)

                if (next > now) {
                    _nextSession.postValue("")
                } else {
                    _nextSession.postValue(next.asDateString())
                }

                loadThrows(it.id)
                loadStats(it.id)
            }
        }
    }

    fun loadForDate(date: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val session = sessionRepository.getForDay(user.id, date)
            session?.let {
                load(it.id)
            }

            if (session == null) {
                _selectedSession.postValue(
                    Session(
                        0,
                        user.id,
                        date,
                    )
                )

                val prev = DateInstance.fromString(date)
                prev.add(Calendar.DAY_OF_MONTH, -1)
                _prevSession.postValue(prev.asDateString())

                val next = DateInstance.fromString(date)
                next.add(Calendar.DAY_OF_MONTH, 1)
                _nextSession.postValue(next.asDateString())

                _throws.postValue(listOf())
                _stats.postValue(SessionStats(0).toFullData())
            }
        }
    }

    fun setFilter(newFilter: ThrowFilter) {
        filter = newFilter
        _selectedSession.value?.let {
            loadThrows(it.id)
        }
        _filterIsActive.postValue(newFilter != ThrowFilter.NONE)
    }

    private fun filterItem(item: Throw): Boolean {
        return when (filter) {
            ThrowFilter.FULL_SUCCESS -> item.targetSuccess
            ThrowFilter.FULL_MISS -> item.targetHits == 0
            ThrowFilter.SUMMARY_100 -> item.throwSummary >= 100
            ThrowFilter.SUMMARY_140 -> item.throwSummary >= 140
            ThrowFilter.SUMMARY_180 -> item.throwSummary >= 180
            ThrowFilter.TRIPLE -> item.tripleCount > 0
            ThrowFilter.DOUBLE -> item.doubleCount > 0
            ThrowFilter.START_OK -> item.firstDartIsSuccess
            ThrowFilter.FAIL -> item.onlyLastDartIsFail
            else -> true
        }
    }

    private fun loadThrows(sessionId: Long) {
        throwsJob?.cancel()
        throwsJob = viewModelScope.launch(Dispatchers.IO) {
            throwRepository.readForSession(sessionId).collect { throws ->
                throws?.let { list ->
                    _throws.postValue(list.filter { t -> filterItem(t) }.map { item -> item.toHistoryRowData(dartRepository) })
                }
            }
        }
    }

    private fun loadStats(sessionId: Long) {
        statsJob?.cancel()
        statsJob = viewModelScope.launch(Dispatchers.IO) {
            sessionStatsRepository.readFlow(sessionId).collect { stats ->
                stats?.let {
                    _stats.postValue(it.toFullData())
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

    override fun onCleared() {
        super.onCleared()

        throwsJob?.cancel()
        statsJob?.cancel()
    }
}

class ScoreScreenViewModelFactory(
    private val app: App,
    private val user: User,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ScoreScreenViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ScoreScreenViewModel(
                app,
                user,
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
package ladislav.sevcuj.endlessdarts.ui.viewmodels

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ladislav.sevcuj.endlessdarts.App
import ladislav.sevcuj.endlessdarts.DateInstance
import ladislav.sevcuj.endlessdarts.asDateString
import ladislav.sevcuj.endlessdarts.db.Session
import ladislav.sevcuj.endlessdarts.db.User
import ladislav.sevcuj.endlessdarts.ui.screens.score.ThrowHistoryRowData
import ladislav.sevcuj.endlessdarts.ui.widgets.StatsRowData

class ScoreScreenViewModel(
    app: App,
    private val user: User,
) : ViewModel() {
    private var throwsJob: Job? = null
    private var statsJob: Job? = null

    private val throwRepository = app.throwRepository
    private val dartRepository = app.dartRepository
    private val sessionRepository = app.sessionRepository
    private val sessionStatsRepository = app.sessionStatsRepository

    private var session: Session? = null

    private val _throws = MutableLiveData<List<ThrowHistoryRowData>>()
    val throws: LiveData<List<ThrowHistoryRowData>>
        get() = _throws

    private val _stats = MutableLiveData<List<StatsRowData>>()
    val stats: LiveData<List<StatsRowData>>
        get() = _stats

    init {
        load()
    }

    fun load(sessionId: Long? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            session = if (sessionId == null) {
                sessionRepository.getForDay(user.id, DateInstance.now().asDateString())
            } else {
                sessionRepository.read(sessionId)
            }
            session?.let {
                loadThrows(it.id)
                loadStats(it.id)
            }
        }
    }

    private fun loadThrows(sessionId: Long) {
        throwsJob?.cancel()
        throwsJob = viewModelScope.launch(Dispatchers.IO) {
            throwRepository.readForSession(sessionId).collect { throws ->
                _throws.postValue(throws.map { item -> item.toHistoryRowData(dartRepository) })
            }
        }
    }

    private fun loadStats(sessionId: Long) {
        statsJob?.cancel()
        statsJob = viewModelScope.launch(Dispatchers.IO) {
            sessionStatsRepository.readFlow(sessionId).collect { stats ->
                _stats.postValue(stats.toFullData())
            }
        }
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
package ladislav.sevcuj.endlessdarts.ui.viewmodels

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ladislav.sevcuj.endlessdarts.App
import ladislav.sevcuj.endlessdarts.db.User
import ladislav.sevcuj.endlessdarts.ui.screens.score.ThrowHistoryRowData
import ladislav.sevcuj.endlessdarts.ui.widgets.StatsRowData

class ScoreScreenViewModel(
    app: App,
    private val user: User,
) : ViewModel() {
    private val sessionRepository = app.sessionRepository
    private val sessionStatsRepository = app.sessionStatsRepository

    private val _throws = MutableLiveData<List<ThrowHistoryRowData>>()
    val throws: LiveData<List<ThrowHistoryRowData>>
        get() = _throws

    private val _stats = MutableLiveData<List<StatsRowData>>()
    val stats: LiveData<List<StatsRowData>>
        get() = _stats

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val session = sessionRepository.getLast(user.id)
            session?.let {
                sessionStatsRepository.read(it.id).let { sessionStats ->
                    _stats.postValue(sessionStats.toFullData())
                }
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
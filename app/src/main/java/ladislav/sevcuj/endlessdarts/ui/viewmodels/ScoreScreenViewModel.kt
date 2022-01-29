package ladislav.sevcuj.endlessdarts.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ladislav.sevcuj.endlessdarts.ui.screens.score.ThrowHistoryRowData
import ladislav.sevcuj.endlessdarts.ui.widgets.StatsRowData

class ScoreScreenViewModel : ViewModel() {

    private val _throws = MutableLiveData<List<ThrowHistoryRowData>>()
    val throws: LiveData<List<ThrowHistoryRowData>>
        get() = _throws

    private val _stats = MutableLiveData<List<StatsRowData>>()
    val stats: LiveData<List<StatsRowData>>
        get() = _stats

    init {
        _throws.postValue(
            listOf(
                ThrowHistoryRowData(
                    "20",
                    ThrowHistoryRowData.Dart("20", true),
                    ThrowHistoryRowData.Dart("15", false),
                    ThrowHistoryRowData.Dart("T20", true),
                    95,
                    31.67,
                ),
                ThrowHistoryRowData(
                    "20",
                    ThrowHistoryRowData.Dart("1", false),
                    ThrowHistoryRowData.Dart("1", false),
                    ThrowHistoryRowData.Dart("1", false),
                    3,
                    1.0,
                ),
                ThrowHistoryRowData(
                    "20",
                    ThrowHistoryRowData.Dart("5", false),
                    ThrowHistoryRowData.Dart("5", false),
                    ThrowHistoryRowData.Dart("5", false),
                    15,
                    5.0,
                ),
            )
        )

        _stats.postValue(
            listOf(
                StatsRowData("Throws", "0"),
                StatsRowData("Target full success (rate)", "0 (0%)", isSuccess = true, showDetail = true, info = "info"),
                StatsRowData("Target full miss (rate)", "0 (0%)", isFail = true, showDetail = true),
                StatsRowData("Target hits (rate)", "0 (0%)"),
                StatsRowData("Throw average", "0"),
                StatsRowData("Throw max", "0"),
                StatsRowData("140+", "0", showDetail = true, info = "info"),
                StatsRowData("100+", "0", showDetail = true),
            )
        )
    }

}

class ScoreScreenViewModelFactory() : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ScoreScreenViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ScoreScreenViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
package ladislav.sevcuj.endlessdarts.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ladislav.sevcuj.endlessdarts.DartBoard
import ladislav.sevcuj.endlessdarts.db.Dart
import ladislav.sevcuj.endlessdarts.db.Target
import ladislav.sevcuj.endlessdarts.db.Throw
import ladislav.sevcuj.endlessdarts.ui.widgets.StatsRowData

class GameScreenViewModel : ViewModel() {

    private val _currentThrow = MutableLiveData(
        Throw(
            id = 0,
            player = "Player",
            target = "20"
        )
    )
    val currentThrow: LiveData<Throw>
        get() = _currentThrow

    private val _lastThrow = MutableLiveData<Throw?>()
    val lastThrow: LiveData<Throw?>
        get() = _lastThrow

    private val _target = MutableLiveData<Target>()
    val target: LiveData<Target>
        get() = _target

    private val _targetFields = MutableLiveData<List<DartBoard.Field>>()
    val targetFields: LiveData<List<DartBoard.Field>>
        get() = _targetFields

    private val _stats = MutableLiveData<List<StatsRowData>>()
    val stats: LiveData<List<StatsRowData>>
        get() = _stats

    init {
        //TODO ID move to constructor
        _target.postValue(Target(1, "20"))

        //TODO load for target
        _targetFields.postValue(
            listOf(
                DartBoard.Field(
                    "20",
                    "20",
                ),
                DartBoard.Field(
                    "D20",
                    "D20",
                    value = 40,
                ),
                DartBoard.Field(
                    "T20",
                    "T20",
                    value = 60,
                ),
                DartBoard.Field(
                    "D1",
                    "D1",
                    value = 2,
                ),
                DartBoard.Field(
                    "T1",
                    "T1",
                    value = 3,
                ),
                DartBoard.Field(
                    "D5",
                    "D5",
                    value = 10,
                ),
                DartBoard.Field(
                    "T5",
                    "T5",
                    value = 15,
                ),
            )
        )

        _stats.postValue(
            listOf(
                StatsRowData("Throws", "0"),
                StatsRowData("Target full success (rate)", "0 (0%)"),
                StatsRowData("Target full miss (rate)", "0 (0%)"),
                StatsRowData("Target hits (rate)", "0 (0%)"),
                StatsRowData("Throw average", "0"),
                StatsRowData("Throw max", "0"),
                StatsRowData("140+", "0"),
                StatsRowData("100+", "0"),
            )
        )
    }

    fun onDart(field: DartBoard.Field) {
        _currentThrow.value?.let {
            val multiplicator = 1

            val currentThrow: Throw

            if (it.darts.size >= 3) {
                currentThrow = Throw(
                    id = 0,
                    player = "Player",
                    target = "20"
                )
                _lastThrow.postValue(it)
            } else {
                currentThrow = it
            }

            val darts = currentThrow.darts.toMutableList()

            darts.add(
                Dart(
                    id = 0,
                    order = darts.size + 1,
                    throwId = it.id,
                    multiplicator = multiplicator,
                    number = field.value!!,
                    sum = multiplicator * field.value,
                )
            )

            val copy = currentThrow.copy(
                throwSummary = darts.sumOf { dart -> dart.sum },
            )

            copy.darts = darts

            _currentThrow.postValue(copy)
        }
    }
}

class GameScreenViewModelFactory() : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameScreenViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GameScreenViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
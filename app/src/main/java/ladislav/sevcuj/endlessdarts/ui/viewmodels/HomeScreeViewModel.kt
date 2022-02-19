package ladislav.sevcuj.endlessdarts.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ladislav.sevcuj.endlessdarts.TargetProvider
import ladislav.sevcuj.endlessdarts.db.GameTarget

class HomeScreeViewModel(
    target: GameTarget,
) : ViewModel() {
    private val _target = MutableLiveData(target)
    val target: LiveData<GameTarget>
        get() = _target

    fun onTargetSelect(target: GameTarget) {
        if (target.id == TargetProvider.randomId) {
            _target.postValue(TargetProvider.random().copy(label = target.number.toString()))
        } else {
            _target.postValue(target)
        }
    }

    fun getTarget() = if (_target.value!!.id == TargetProvider.randomId) {
        TargetProvider.random().copy(label = _target.value!!.number.toString())
    } else {
        _target.value!!
    }
}

class HomeScreenViewModelFactory(
    private val target: GameTarget,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeScreeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeScreeViewModel(target) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
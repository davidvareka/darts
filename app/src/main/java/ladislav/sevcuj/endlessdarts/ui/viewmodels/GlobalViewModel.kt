package ladislav.sevcuj.endlessdarts.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ladislav.sevcuj.endlessdarts.db.GameTarget

class GlobalViewModel : ViewModel() {
    val onSessionCreated = SingleLiveEvent<Long>()
    val onTargetChange = SingleLiveEvent<GameTarget>()

    private val _target = MutableLiveData<GameTarget>()
    val target: LiveData<GameTarget>
        get() = _target

    fun changeTarget(target: GameTarget) {
        _target.postValue(target)
        onTargetChange.postValue(target)
    }
}
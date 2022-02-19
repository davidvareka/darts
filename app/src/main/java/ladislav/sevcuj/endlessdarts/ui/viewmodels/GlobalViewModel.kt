package ladislav.sevcuj.endlessdarts.ui.viewmodels

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ladislav.sevcuj.endlessdarts.App
import ladislav.sevcuj.endlessdarts.db.GameTarget
import ladislav.sevcuj.endlessdarts.db.User

class GlobalViewModel(
    user: User,
    private val app: App
) : ViewModel() {

    val onSessionCreated = SingleLiveEvent<Long>()
    val onUserChange = SingleLiveEvent<User>()
    val onTargetChange = SingleLiveEvent<GameTarget>()

    private val _user = MutableLiveData(user)
    val user: LiveData<User>
        get() = _user

    private val _target = MutableLiveData<GameTarget>()
    val target: LiveData<GameTarget>
        get() = _target

    fun changeTarget(target: GameTarget, onSet: () -> Unit) {
        viewModelScope.launch {
            _target.postValue(target)
            onTargetChange.postValue(target)
            delay(100)
            onSet()
        }
    }

    fun setUser(user: User) {
        _user.value?.let {
            if (it != user && it.isTemporary) {
                deleteTemporaryUser(it)
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            val newUser = if (user.id == 0L) {
                user.copy(id = app.userRepository.insert(user))
            } else {
                user
            }
            _user.postValue(newUser)
            onUserChange.postValue(newUser)
        }
    }

    private fun deleteTemporaryUser(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            val sessions = app.sessionRepository.getForUser(user.id)

            sessions.forEach {
                app.sessionStatsRepository.deleteForSession(it.id)
                app.throwRepository.deleteForSession(it.id)
                app.dartRepository.deleteForSession(it.id)
            }

            app.userRepository.delete(user)
        }
    }
}

class GlobalViewModelFactory(
    private val user: User,
    private val app: App
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GlobalViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GlobalViewModel(
                user,
                app
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
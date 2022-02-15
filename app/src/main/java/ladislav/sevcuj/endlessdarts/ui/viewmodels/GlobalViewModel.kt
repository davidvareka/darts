package ladislav.sevcuj.endlessdarts.ui.viewmodels

import androidx.lifecycle.ViewModel

class GlobalViewModel : ViewModel() {
    val onSessionCreated = SingleLiveEvent<Long>()
}
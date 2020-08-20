package com.okurchenko.ecocity.ui.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hadilq.liveevent.LiveEvent
import com.okurchenko.ecocity.repository.LocationListener
import com.okurchenko.ecocity.repository.StationRepositoryImpl
import org.koin.core.context.GlobalContext
import org.koin.core.context.KoinContextHandler

abstract class BaseViewModel : ViewModel() {

    //Maybe better approach will be to use https://medium.com/androiddevelopers/livedata-with-snackbar-navigation-and-other-events-the-singleliveevent-case-ac2622673150
    private val navigationCommand = LiveEvent<NavigationEvents>()
    protected val repository: StationRepositoryImpl by lazy {
        KoinContextHandler.get().get<StationRepositoryImpl>()
    }

    fun getNavigationEvents(): LiveEvent<NavigationEvents> = navigationCommand
    protected fun processNavigationEvent(event: NavigationEvents) = navigationCommand.postValue(event)
    abstract fun takeAction(action: BaseViewAction)

}

abstract class ViewModelState<State : BaseState> : BaseViewModel() {

    protected val viewState: MutableLiveData<State> = MutableLiveData()
    fun getState(): MutableLiveData<State> = viewState

}
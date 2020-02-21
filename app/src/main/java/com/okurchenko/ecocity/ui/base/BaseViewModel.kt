package com.okurchenko.ecocity.ui.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hadilq.liveevent.LiveEvent
import com.okurchenko.ecocity.repository.StationRepositoryImpl
import org.koin.core.context.GlobalContext

abstract class BaseViewModel<State : BaseState> : ViewModel() {

    protected val repository: StationRepositoryImpl by lazy { GlobalContext.get().koin.get<StationRepositoryImpl>() }
    protected val viewState: MutableLiveData<State> = MutableLiveData()
    //Maybe better approach will be to use https://medium.com/androiddevelopers/livedata-with-snackbar-navigation-and-other-events-the-singleliveevent-case-ac2622673150
    private val navigationCommand = LiveEvent<NavigationEvents>()

    fun getState(): MutableLiveData<State> = viewState

    fun getNavigationEvents(): LiveEvent<NavigationEvents> = navigationCommand

    protected fun processNavigationEvent(event: NavigationEvents) = navigationCommand.postValue(event)

    abstract fun takeAction(action: BaseViewAction)

}
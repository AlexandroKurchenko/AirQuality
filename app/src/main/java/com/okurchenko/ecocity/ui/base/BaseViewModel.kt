package com.okurchenko.ecocity.ui.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.okurchenko.ecocity.repository.StationRepositoryImpl
import org.koin.core.context.GlobalContext

abstract class BaseViewModel<State : BaseState> : ViewModel() {

    protected val repository: StationRepositoryImpl by lazy { GlobalContext.get().koin.get<StationRepositoryImpl>() }
    protected val viewState: MutableLiveData<State> = MutableLiveData()

    fun getState(): MutableLiveData<State> = viewState

    protected fun processState(state: State) {
        viewState.postValue(state)
    }

    abstract fun takeAction(action: BaseViewAction)

}
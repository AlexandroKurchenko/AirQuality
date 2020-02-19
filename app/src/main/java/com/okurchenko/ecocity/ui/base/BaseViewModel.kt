package com.okurchenko.ecocity.ui.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

abstract class BaseViewModel<State : BaseState> : ViewModel() {

    protected val viewState: MutableLiveData<State> = MutableLiveData()

    fun getState(): MutableLiveData<State> = viewState

    protected fun processState(state: State){
        viewState.postValue(state)
    }

    abstract fun takeAction(action: BaseViewAction)

}
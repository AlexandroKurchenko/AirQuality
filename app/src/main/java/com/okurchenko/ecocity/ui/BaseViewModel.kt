package com.okurchenko.ecocity.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

abstract class BaseViewModel<State> : ViewModel() {

    protected val viewState: MutableLiveData<State> = MutableLiveData()

    fun getState(): MutableLiveData<State> = viewState
    abstract fun takeAction(action: BaseViewAction)

}
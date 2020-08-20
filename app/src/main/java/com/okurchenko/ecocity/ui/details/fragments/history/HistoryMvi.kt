package com.okurchenko.ecocity.ui.details.fragments.history

import com.okurchenko.ecocity.ui.base.BaseViewAction

sealed class HistoryListViewAction : BaseViewAction {
    class DetailsClick(val timeShift: Int) : HistoryListViewAction()
    object OpenMain : HistoryListViewAction()
}

class HistoryListActor(private val emit: (HistoryListViewAction) -> Unit) {
    fun clickItem(timeShift: Int) = emit(HistoryListViewAction.DetailsClick(timeShift))
    fun openMain() = emit(HistoryListViewAction.OpenMain)
}

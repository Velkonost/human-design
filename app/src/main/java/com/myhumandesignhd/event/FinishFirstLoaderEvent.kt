package com.myhumandesignhd.event

data class FinishFirstLoaderEvent(val shouldFinish: Boolean = true, val isFirst: Boolean = false)
data class ContinueFirstLoaderEvent(val continueLoader: Boolean = true)
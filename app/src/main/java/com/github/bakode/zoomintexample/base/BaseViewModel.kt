package com.github.bakode.zoomintexample.base

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

abstract class BaseViewModel : ViewModel()
{
    private val job = Job()

    private val ctx = Dispatchers.Main + job

    val uiScope = CoroutineScope(ctx)

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}
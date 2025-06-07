package com.example.openappads.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class CountDownTimer {

    private var progress = -1
    private var max = 100
    private var delay = 80L

    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private var loopingFlowJob: Job? = null
    private var loopingFlow = flow {
        while (true) {
            emit(Unit)
            delay(delay)
        }
    }

    private var jobProgress: UpdateProgress? = null

    fun setMaxProgress(maxProgress: Int) {
        this.max = maxProgress
    }

    fun setDelay(delay: Long) {
        this.delay = delay
    }

    fun setProcessTimeSecond(second: Int) {
        this.delay = ((second * 1000) / max).toLong()
    }

    fun startJob(listener: UpdateProgress?) {
        jobProgress = listener
        stopJob()
        if (isProgressMax()) {
            jobProgress?.onUpdateProgress(progress)
            return
        }
        loopingFlowJob = coroutineScope.launch(Dispatchers.Main) {
            loopingFlow.collect {
                progress++
                jobProgress?.onUpdateProgress(progress)
                if (isProgressMax()) stopJob()
            }
        }
    }

    fun stopJob() {
        loopingFlowJob?.cancel()
        loopingFlowJob = null
    }

    fun destroy() {
        stopJob()
        jobProgress = null
    }

    fun isProgressMax(): Boolean {
        return progress >= max
    }

    interface UpdateProgress {
        fun onUpdateProgress(count: Int)
    }
}
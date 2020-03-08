package com.bytecoders.iptvservicecommunicator.executor

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor

class UiExecutor : Executor {
    private val mHandler: Handler = Handler(Looper.getMainLooper())
    override fun execute(command: Runnable?) {
        mHandler.post(command)
    }
}

inline fun <T> T.toUI(block: (T) -> Unit) {
    block(this)
}
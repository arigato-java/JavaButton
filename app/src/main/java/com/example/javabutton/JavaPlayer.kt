package com.example.javabutton

import android.content.Context
import android.net.Uri

class JavaPlayer(javaUri: Uri) {
    private val jThread: JavaPlayerThread

    init {
        jThread = JavaPlayerThread(javaUri)
        jThread.start()
    }

    fun play() {
        jThread.enqueuePlay()
    }

    fun stop() {
        jThread.enqueueStop()
    }

    fun setContext(ctx: Context) {
        jThread.enqueueChangeContext(ctx)
    }

    fun play(ctx: Context) {
        setContext(ctx)
        play()
    }
}

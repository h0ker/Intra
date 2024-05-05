package com.carbidecowboy.intra.domain

import android.app.Activity
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import javax.inject.Inject

open class NfcAdapterController @Inject constructor(
    private val nfcAdapter: NfcAdapter?
) {
    private var onTagDiscoveredListener: ((Tag?) -> Unit)? = null
    private val listenersStack = ArrayDeque<(Tag?) -> Unit>()

    fun enableNfc(activity: Activity) {
        nfcAdapter?.let { adapter ->
            val flags = NfcAdapter.FLAG_READER_NFC_V or NfcAdapter.FLAG_READER_NFC_A
            val options = Bundle()

            adapter.enableReaderMode(
                activity,
                { tag ->
                    Log.d(this@NfcAdapterController::class.simpleName,"onTagDiscoveredListener: ${onTagDiscoveredListener.hashCode()}")
                    onTagDiscoveredListener?.invoke(tag)
                },
                flags,
                options
            )
        }
    }

    fun isNfcSupported(): Boolean {
        return nfcAdapter != null
    }

    fun disableNfc(activity: Activity) {
        nfcAdapter?.disableReaderMode(activity)
    }

    fun setOnTagDiscoveredListener(
        listener: (Tag?) -> Unit
    ) {
        listenersStack.addLast(listener)
        updateListener()
        Log.d(this@NfcAdapterController::class.simpleName, "Current listenerStack: ${listenersStack.forEach { it.hashCode() }}")
    }

    fun removeOnTagDiscoveredListener() {
        listenersStack.removeLast()
        updateListener()
        Log.d(this@NfcAdapterController::class.simpleName, "Current listenerStack: ${listenersStack.forEach { it.hashCode() }}")
    }

    private fun updateListener() {
        val currentListener = if (listenersStack.isNotEmpty()) {
            listenersStack.last()
        } else {
            null
        }
        this.onTagDiscoveredListener = currentListener
    }
}
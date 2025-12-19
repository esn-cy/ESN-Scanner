package com.andymic.esnscanner

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import java.lang.ref.WeakReference

object ContextProvider {
    private var contextRef: WeakReference<Context>? = null

    fun setContext(context: Context) {
        contextRef = WeakReference(context.applicationContext)
    }

    fun getContext(): Context {
        return contextRef?.get()
            ?: throw IllegalStateException("Context has not been initialized. Call setContext() in your Application class.")
    }
}

fun createDataStore(context: Context): DataStore<Preferences> = createDataStore(
    producePath = { context.filesDir.resolve(dataStoreFileName).absolutePath }
)

actual val dataStore: DataStore<Preferences> = createDataStore(ContextProvider.getContext())
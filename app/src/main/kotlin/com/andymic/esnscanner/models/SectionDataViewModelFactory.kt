package com.andymic.esnscanner.models

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.andymic.esnscanner.data.SectionData

class SectionDataViewModelFactory(
    private val application: Application,
    private val sectionData: SectionData.SectionData
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        try {
            val constructor = modelClass.getConstructor(
                Application::class.java,
                SectionData.SectionData::class.java
            )
            return constructor.newInstance(application, sectionData)
        } catch (e: NoSuchMethodException) {
            throw IllegalArgumentException(
                "ViewModel class ${modelClass.simpleName} must have a constructor (Application, SectionData).",
                e
            )
        } catch (e: Exception) {
            throw RuntimeException("Cannot create an instance of ${modelClass.simpleName}", e)
        }
    }
}
package com.andymic.esnscanner

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.andymic.esnscanner.data.SectionData.SectionData
import com.andymic.esnscanner.data.SectionData.SectionDataSerializer

val Context.dataStore: DataStore<SectionData> by dataStore(
    fileName = "section-data.json",
    serializer = SectionDataSerializer,
)
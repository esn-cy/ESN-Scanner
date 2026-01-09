package org.esncy.esnscanner.models

data class ViewModels(
    val addViewModel: AddViewModel,
    val blacklistViewModel: StatusViewModel,
    val deliverViewModel: StatusViewModel,
    val onlineViewModel: OnlineViewModel,
    val paidViewModel: StatusViewModel,
    val produceViewModel: StatusViewModel,
    val scanViewModel: ScanViewModel,
    val sectionDataViewModel: SectionDataViewModel,
    val updateViewModel: UpdateViewModel
)
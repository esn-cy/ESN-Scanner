package org.esncy.esnscanner.models

data class ViewModels(
    val addViewModel: AddViewModel,
    val blacklistViewModel: StatusViewModel,
    val deliverViewModel: StatusViewModel,
    val onlineViewModel: OnlineViewModel,
    val paidViewModel: StatusViewModel,
    val issueViewModel: StatusViewModel,
    val scanViewModel: ScanViewModel,
    val sectionDataViewModel: SectionDataViewModel,
    val tokenViewModel: TokenViewModel,
    val updateViewModel: UpdateViewModel
)
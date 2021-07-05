package net.veldor.oblepiha_kotlin.model.view_models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import net.veldor.oblepiha_kotlin.model.selections.MembershipListItem
import net.veldor.oblepiha_kotlin.model.selections.TargetListItem

class AccrualsTargetViewModel : ViewModel() {
    companion object{
        val selectedForDetails = MutableLiveData<TargetListItem?>()
        val isLoaded= MutableLiveData<Boolean>()
    }
}
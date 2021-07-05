package net.veldor.oblepiha_kotlin.model.view_models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import net.veldor.oblepiha_kotlin.model.selections.MembershipListItem

class AccrualsMembershipViewModel : ViewModel() {
    companion object{
        val selectedForDetails = MutableLiveData<MembershipListItem?>()
        val isLoaded= MutableLiveData<Boolean>()
    }
}
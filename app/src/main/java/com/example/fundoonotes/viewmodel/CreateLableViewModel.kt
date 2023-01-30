package com.example.fundoonotes.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fundoonotes.model.Lable
import com.example.fundoonotes.model.NoteService
import com.example.fundoonotes.util.ViewState

class CreateLableViewModel(private val noteService: NoteService) : ViewModel() {
    private val _createLableStatus = MutableLiveData<ViewState<Lable>>()
    val createLableStatus = _createLableStatus as LiveData<ViewState<Lable>>

    fun createLable(lables: Lable) {
        noteService.createLable(lables) { lable, exception ->
           lable?.let {
                _createLableStatus.value = ViewState.Success(lables)
            }
        }
    }

}
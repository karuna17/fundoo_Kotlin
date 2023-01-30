package com.example.fundoonotes.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fundoonotes.model.NoteService
import com.example.fundoonotes.model.Notes
import com.example.fundoonotes.util.ViewState

class AddNoteViewModel(private val noteService: NoteService) : ViewModel() {
    private val _addNoteStatus = MutableLiveData<ViewState<Notes>>()
    val addNoteStatus = _addNoteStatus as LiveData<ViewState<Notes>>

    private val _updateNoteStatus = MutableLiveData<ViewState<Notes>>()
    val updateNoteStatus = _updateNoteStatus as LiveData<ViewState<Notes>>

    init {
        _addNoteStatus.value = ViewState.Loading(Notes())
        _updateNoteStatus.value = ViewState.Loading(Notes())
    }

    fun saveNotesToFirestore(notes: Notes,context: Context) {
        noteService.saveNotesToFirestore(notes,context) { note, exception ->
            note?.let {
                _addNoteStatus.value = ViewState.Success(notes)
            }
        }
    }

    fun updateNotesInFirestore(notes: Notes) {
        noteService.updateNotes(notes) { status, exception ->
            if (status == true)
                Log.d(TAG, "updateNotesInFirestore: $status")
                _updateNoteStatus.value = ViewState.Success(notes)
        }
    }
    companion object{
        private const val TAG = "AddNoteViewModel"
    }
}


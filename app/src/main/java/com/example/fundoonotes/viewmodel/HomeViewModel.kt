package com.example.fundoonotes.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fundoonotes.model.NoteService
import com.example.fundoonotes.model.Notes
import com.example.fundoonotes.util.ViewState

class HomeViewModel(private val noteService: NoteService) : ViewModel() {
    private var _notes = mutableListOf<Notes>()
    private var _reminderNotes = mutableListOf<Notes>()

    private val _getNoteStatus = MutableLiveData<List<Notes>>()
    val getNoteStatus = _getNoteStatus as LiveData<List<Notes>>

    private val _deleteNoteStatus = MutableLiveData<ViewState<Notes>>()
    val deleteNoteStatus = _deleteNoteStatus as LiveData<ViewState<Notes>>

    fun getNotesFromFireStore() {
        noteService.fetchNotesFromFirestore { list: List<Notes> ->
            Log.d("HomeviewModel", "getNotesFromFireStore: ${list.size}")
            _getNoteStatus.value = list
        }
    }

    fun deleteNotes(noteDetails: Notes) {
        noteService.deleteNotesFromFirestore(noteDetails) { status, exception ->
            _deleteNoteStatus.value = ViewState.Success(noteDetails)
        }
    }

}
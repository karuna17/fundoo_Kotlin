package com.example.fundoonotes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.fundoonotes.model.NoteService

class AddNoteViewModelFactory(private val noteService: NoteService) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AddNoteViewModel(noteService) as T
    }
}
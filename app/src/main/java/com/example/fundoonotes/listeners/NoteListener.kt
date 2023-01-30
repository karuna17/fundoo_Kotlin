package com.example.fundoonotes.listeners

import com.example.fundoonotes.model.Notes

data class NoteListener(val status: Boolean, val message: String = "", val noteList: MutableList<Notes>)

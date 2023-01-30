package com.example.fundoonotes.util

import com.example.fundoonotes.model.Notes

data class NoteStatus(val status: Boolean, val message: String = "", val noteList: MutableList<Notes>)

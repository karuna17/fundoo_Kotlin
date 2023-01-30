package com.example.fundoonotes.model.dao

import com.example.fundoonotes.model.Notes

interface NotesDao {
    fun saveNotesToFirestore(
        notes: Notes,
        listener: (Notes?, Exception?) -> Unit
    )
}
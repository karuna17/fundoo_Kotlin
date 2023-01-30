package com.example.fundoonotes.model

data class Notes(
    var tittle: String = "",
    var content: String = "",
    var noteId: String = "",
    var userId: String = "",
    var lastModified: String = "",
    var reminderTime: Long = 0,
    var archive: Boolean = false
)

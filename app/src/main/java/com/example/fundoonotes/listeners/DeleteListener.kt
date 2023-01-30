package com.example.fundoonotes.listeners

import com.example.fundoonotes.model.Notes

interface DeleteListener {
    fun noteDeleted(deleteOnClick:Boolean,note: Notes)
}
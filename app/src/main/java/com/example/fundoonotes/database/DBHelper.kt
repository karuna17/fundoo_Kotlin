package com.example.fundoonotes.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.fundoonotes.model.Notes

class DBHelper(private var context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, 2) {
    override fun onCreate(db: SQLiteDatabase?) {
        val createNotes =
            "CREATE TABLE $NOTES($USER_ID TEXT , $NOTE_ID TEXT PRIMARY KEY, $NOTE_TITLE TEXT, $NOTE_CONTENT TEXT, $LAST_MODIFIED TEXT, $REMINDER_TIME INTEGER)"

        val createLable =
            "CREATE TABLE $LABEL($LABLE_ID TEXT PRIMARY KEY, $LABLE_NAME TEXT)"
        db?.execSQL(createNotes)
        db?.execSQL(createLable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }

    fun addNote(note: Notes, listener: (Boolean) -> Unit) {
        val db = this.writableDatabase
        val noteDetails = ContentValues()

        noteDetails.put(USER_ID, note.userId)
        noteDetails.put(NOTE_ID, note.noteId)
        noteDetails.put(NOTE_TITLE, note.tittle)
        noteDetails.put(NOTE_CONTENT, note.content)
        noteDetails.put(LAST_MODIFIED, note.lastModified)
        noteDetails.put(REMINDER_TIME, note.reminderTime)

        val insertCheck = db.insert(NOTES, null, noteDetails)
        if (insertCheck.toInt() == -1) {
            listener(false)
        } else listener(true)
        Log.e(TAG, "addNote: inserted")
//        db.close()
    }

    fun updateNote(note: Notes, listener: (Boolean) -> Unit) {
        val db = this.writableDatabase
        val noteDetails = ContentValues()
        noteDetails.put(NOTE_TITLE, note.tittle)
        noteDetails.put(NOTE_CONTENT, note.content)
        val updateCheck = db.update(
            NOTES, noteDetails, "$NOTE_ID = \"${note.noteId}\" AND $USER_ID = \"${note.userId}\"",
            null
        )
        if (updateCheck > 0) {
            listener(true)
            Log.e("DBHelper", "Update: true")
        } else {
            listener(false)
            Log.e("DBHelper", "Update: False")
        }
         // db.close()
    }

    fun deleteNote(note: Notes, listener: (Boolean) -> Unit) {
        val db = this.writableDatabase
        val deleteCheck = db.delete(NOTES, "$NOTE_ID = \"${note.noteId}\"", null)
        if (deleteCheck > 0) {
            listener(true)
            Log.e("DBHelper", "Delete: true")
        } else {
            listener(false)
            Log.e("DBHelper", "Delete: False")
        }
        //db.close()
    }

    fun fetchNotes(uid: String, listener: (List<Notes>) -> Unit) {
        val notes: MutableList<Notes> = mutableListOf()
        val query = "SELECT * FROM $NOTES WHERE $USER_ID = \"$uid\""
        val db = this.readableDatabase
        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                Log.e("DBHelper", "fetchNotes: Inside While loop")
                notes.add(
                    Notes(
                        userId = cursor.getString(0),
                        noteId = cursor.getString(1),
                        tittle = cursor.getString(2),
                        content = cursor.getString(3),
                        lastModified = cursor.getString(4),
                        reminderTime = cursor.getLong(5)
                    )
                )
            } while (cursor.moveToNext())
            listener(notes)
        } else {
            Log.e("DBHelper", "fetchNotes: else part")
            listener(notes)
        }
        cursor.close()
        //  db.close()
        fun createLable() {

        }
    }

    companion object {
        private const val TAG = "DatabaseHelper";
        private const val DATABASE_NAME = "FundooNotes"
        private const val NOTES = "Notes";
        private const val NOTE_ID = "Note_Id";
        private const val USER_ID = "User_Id";
        private const val NOTE_TITLE = "Note_Title";
        private const val NOTE_CONTENT = "Note_Content";
        private const val LAST_MODIFIED = "lastModified";
        private const val REMINDER_TIME = "ReminderTime";
        private const val LABEL = "Label"
        private const val NOTE_LABEL = "Note_Label"
        private const val LABLE_ID = "Lable_Id";
        private const val LABLE_NAME = "Lable_Name";
    }
}
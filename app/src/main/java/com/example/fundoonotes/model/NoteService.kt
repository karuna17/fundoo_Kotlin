package com.example.fundoonotes.model

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import com.example.fundoonotes.database.DBHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class NoteService(private val dbHelper: DBHelper) {
    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var fireStore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var note: Notes
    private lateinit var documentRef: DocumentReference

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        // For 29 api or above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                    ?: return false
            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false
            }
        }
        return false
    }

    fun saveNotesToFirestore(
        notes: Notes,
        context: Context,
        listener: (Notes?, Exception?) -> Unit
    ) {
            mAuth.currentUser?.let { firebaseUser ->
                documentRef =
                    fireStore.collection(USER_COLLECTION).document(firebaseUser.uid).collection(
                        NOTES_COLLECTION
                    ).document()
                notes.noteId = documentRef.id
                notes.userId = firebaseUser.uid
                documentRef.set(notes).addOnSuccessListener {
                    dbHelper.addNote(notes) {
                        listener(notes, null)
                    }
                }.addOnFailureListener {
                    listener(null, it)
                }
            }
        }


    fun fetchNotesFromFirestore(listener: (List<Notes>) -> Unit) {
        mAuth.currentUser?.let { it ->
            fireStore.collection(USER_COLLECTION).document(it.uid).collection(NOTES_COLLECTION)
                .get().addOnCompleteListener {
                    val notes: MutableList<Notes> = mutableListOf()
                    if (it.isSuccessful && it.result != null) {
                        for (document in it.result!!) {
                            note = Notes(
                                document.getString(TITTLE).toString(),
                                document.getString(CONTENT).toString(),
                                document.getString("noteId").toString(),
                                document.getString("userId").toString(),
                                document.getString("lastModified").toString(),
                                document.getLong("reminderTime")!!
                            )
                            notes.add(note)
                        }
                        Log.d(TAG, "fetchNotesFromFirestore: ${notes.size}")

                        dbHelper.fetchNotes(note.userId) {
                            listener(notes)
                        }
                    } else {
                        listener(emptyList())
                    }
                }
        }
    }

    fun updateNotes(notes: Notes, listener: (Boolean, Exception?) -> Unit) {
        Log.d(TAG, "NoteId: ${notes.noteId}  title${notes.tittle}   lastModi${notes.lastModified}")
        val note: MutableMap<String, Any> = HashMap()
        note[TITTLE] = notes.tittle
        note[CONTENT] = notes.content
        mAuth.currentUser?.let {
            fireStore.collection(USER_COLLECTION).document(it.uid)
                .collection(NOTES_COLLECTION).document(notes.noteId).update(note)
                .addOnSuccessListener {
                    Log.d(TAG, "updateNotes: Note ID: ${notes.noteId}")
                    dbHelper.updateNote(notes) {
                        listener(it, null)
                    }
                }.addOnFailureListener {
                    listener(false, it)
                }
        }
    }

    fun deleteNotesFromFirestore(note: Notes, listener: (Boolean?, Exception?) -> Unit) {
        fireStore.collection(USER_COLLECTION).document(note.userId)
            .collection(NOTES_COLLECTION).document(note.noteId).delete().addOnSuccessListener {
                dbHelper.deleteNote(note) {
                    listener(it, null)
                }
            }.addOnFailureListener {
                listener(null, it)
            }
    }

    fun createLable(label: Lable, listener: (Lable?,Exception?) -> Unit) {
        mAuth.currentUser.let {
            if (it != null) {
                documentRef = fireStore.collection(USER_COLLECTION).document(it.uid).collection(
                    LABELS
                ).document()
                label.labelId = documentRef.id
                documentRef.set(label).addOnSuccessListener {
                    listener(label,null)
                }.addOnFailureListener {
                    listener(null,it)
                }
            }
        }
    }

    fun addLable(label: Lable, user: User): Lable {
        val userId = user.userId.toString()
        val firebaseLable = FirebaseLable(label.labelName)
        val autoId =
            fireStore.collection(USER_COLLECTION).document(userId).collection(LABELS).document().id
        fireStore.collection(USER_COLLECTION).document(userId).collection(LABELS)
            .document(autoId).set(firebaseLable).addOnCompleteListener {

            }
        return label
    }

    companion object {
        private const val TITTLE = "tittle"
        private const val CONTENT = "content"
        private const val LABELS = "labels"
        private const val REMINDER_TIME = "reminder_time"
        private const val USER_COLLECTION = "users"
        private const val NOTES_COLLECTION = "notes"
        private const val TAG = "NotesService"
    }
}
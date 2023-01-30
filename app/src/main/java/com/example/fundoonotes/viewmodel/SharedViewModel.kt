package com.example.fundoonotes.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fundoonotes.model.*
import com.example.fundoonotes.util.ViewState

class SharedViewModel(val userAuthService: UserAuthService) : ViewModel() {

    private val _goToLoginPageStatus = MutableLiveData<Boolean>()
    val goToLoginPageStatus = _goToLoginPageStatus as LiveData<Boolean>

    private val _goToRegisterPageStatus = MutableLiveData<Boolean>()
    val goToRegisterPageStatus = _goToRegisterPageStatus as LiveData<Boolean>

    private val _goToHomePageStatus = MutableLiveData<Boolean>()
    val goToHomePageStatus = _goToHomePageStatus as LiveData<Boolean>

    private val _userDetails = MutableLiveData<User>()
    val userDetails = _userDetails as LiveData<User>

    private val _imageUri = MutableLiveData<Uri>()
    val imageUri = _imageUri as LiveData<Uri>

    private val _addNotesWidgetsStatus = MutableLiveData<Boolean>()
    val addNotesWidgetsStatus = _addNotesWidgetsStatus as LiveData<Boolean>

    private val _addNoteStatus = MutableLiveData<ViewState<Notes>>()
    val addNoteStatus = _addNoteStatus as LiveData<ViewState<Notes>>

    private val _updateNoteStatus = MutableLiveData<ViewState<Notes>>()
    val updateNoteStatus = _updateNoteStatus as LiveData<ViewState<Notes>>

    private val _notesDisplayType = MutableLiveData<ViewType>()
    val notesDisplayType = _notesDisplayType as LiveData<ViewType>

    private val _writeNote = MutableLiveData<NotesOperation>()
    val writeNote = _writeNote as LiveData<NotesOperation>

    private val _queryText = MutableLiveData<String>()
    val queryText = _queryText as LiveData<String>

    fun setGoToLoginPageStatus(status: Boolean) {
        _goToLoginPageStatus.value = status
    }

    fun setGoToHomePageStatus(status: Boolean) {
        _goToHomePageStatus.value = status
    }

    fun setGoToRegisterationPage(status: Boolean) {
        _goToRegisterPageStatus.value = status
    }

    fun fetchUserDetails(localId: String, idToken: String) {
        userAuthService.getUserDetailsfromfirestore(localId, idToken) {
            _userDetails.value = it
        }
    }

    fun setImageUri(uri: Uri) {
        _imageUri.value = uri
    }

    fun setAddNoteStatus(state: ViewState<Notes>?) {
        _addNoteStatus.value = state
    }

    fun setUpdateNoteStatus(state: ViewState<Notes>?) {
        _updateNoteStatus.value = state
    }

    fun setNoteToWrite(operation: NotesOperation?) {
        _writeNote.value = operation
    }

    fun setQueryText(string: String?) {
        _queryText.value = string
    }

    fun setNotesDisplayType(viewType: ViewType) {
        _notesDisplayType.value = viewType
    }
}
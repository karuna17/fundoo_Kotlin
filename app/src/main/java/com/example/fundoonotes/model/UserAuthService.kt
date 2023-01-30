package com.example.fundoonotes.model

import android.net.Uri
import android.util.Log
import com.example.fundoonotes.api.LoginLoader
import com.example.fundoonotes.api.LoginResponse
import com.example.fundoonotes.listeners.AuthListener
import com.example.fundoonotes.listeners.LoginListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class UserAuthService {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var fireStore: FirebaseFirestore
    private lateinit var mStorageRef: StorageReference
    private lateinit var user: User

    init {
        initService();
    }

    private fun initService() {
        mAuth = FirebaseAuth.getInstance()
        fireStore = FirebaseFirestore.getInstance()
        mStorageRef = FirebaseStorage.getInstance().reference
    }

    fun userLogin(email: String, password: String, listener: (AuthListener) -> Unit) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful)
                listener(AuthListener(status = it.isSuccessful))
        }
    }

    fun userRegister(user: User, listener: (Boolean) -> Unit) {
        mAuth.createUserWithEmailAndPassword(user.email, user.password).addOnCompleteListener {
            if (it.isSuccessful) {
                addUserDetailsToFireStore(user, listener)
                listener(it.isSuccessful)
                Log.d(TAG, "userRegister: Save User Detatils status: ${it.isSuccessful}")
            }
        }
    }

    fun resetPassword(email: String, listener: (Boolean) -> Unit) {
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener {
            if (it.isSuccessful) {
                listener(it.isSuccessful)
            }
        }
    }

    fun getLoginStatus(listener: (Boolean) -> Unit) {
        mAuth.addAuthStateListener {
            listener(it.currentUser != null)
        }
    }

    fun logOut() {
        mAuth.signOut()
    }

    fun addUserDetailsToFireStore(user: User, listener: (Boolean) -> Unit) {
        val userDB: MutableMap<String, Any> = HashMap()
        userDB[NAME] = user.name
        userDB[EMAIL] = user.email
        userDB[PASSWORD] = user.password
        userDB[PROFILE_IMAGE_URL] = user.imageUrl
        fireStore.collection(USER_COLLECTION).document(mAuth.currentUser!!.uid).set(userDB)
            .addOnCompleteListener {
                listener(it.isSuccessful)
                Log.d(TAG, "USerDetails: Save User Detatils status: ${it.isSuccessful}")
            }
    }

    fun getUserDetailsfromfirestore(localId: String, idToken: String, listener: (User) -> Unit) {
        mAuth.currentUser?.let {
            fireStore.collection(USER_COLLECTION).document(it.uid)
                .addSnapshotListener { value: DocumentSnapshot?, error: FirebaseFirestoreException? ->
                    listener(
                        User(
                            name = value?.getString(NAME).toString(),
                            email = value?.getString(EMAIL).toString(),
                            imageUrl = value?.getString(PROFILE_IMAGE_URL).toString()
                        )
                    )
                }
        }
    }

    fun uploadImageToFirebase(uri: Uri) {
        val fileRef =
            mStorageRef.child("$USER_COLLECTION/${mAuth.currentUser?.uid}/profile.jpg")
        fileRef.putFile(uri)
            .addOnCompleteListener {
                fileRef.downloadUrl.addOnSuccessListener { uri ->
                    fireStore.collection(USER_COLLECTION).document(mAuth.currentUser!!.uid)
                        .update(
                            PROFILE_IMAGE_URL, uri.toString()
                        )
                }
            }
    }

    fun loginWithRestApi(
        email: String,
        password: String,
        listener: (AuthListener) -> Unit
    ) {
        val loginLoader = LoginLoader()
        loginLoader.getLoginDone(object : LoginListener {
            override fun onLogin(response: LoginResponse?, status: Boolean) {
                if (status) {
                    if (response != null) {
                        Constant.getInstance()?.setUserId(response.localId)
                        Log.d(TAG, "onLogin: local Id: ${response.localId}")
                        listener(
                            AuthListener(
                                status = status,
                                localId = response.localId,
                                idToken = response.idToken
                            )
                        )
                    }
                }
            }

        }, email, password)
    }

    companion object {
        private const val NAME = "name"
        private const val EMAIL = "email"
        private const val PASSWORD = "password"
        private const val PROFILE_IMAGE_URL = "profileImageUrl"
        private const val TAG = "UserAuthService"
        private const val USER_COLLECTION = "users"
    }
}


package com.example.fundoonotes.model

import com.example.fundoonotes.listeners.AuthListener
import com.example.fundoonotes.listeners.UserAuthListener
import com.google.firebase.auth.FirebaseAuth

class UserAuthService {
    private lateinit var mAuth: FirebaseAuth

    init {
        initService();
    }

    private fun initService() {
        mAuth = FirebaseAuth.getInstance()
//        fireStore = FirebaseFirestore.getInstance()
    }

    fun userLogin(email: String, password: String, listener: (AuthListener) -> Unit) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful)
                listener(AuthListener(status = it.isSuccessful))
        }
    }

    fun userRegister(user: User, listener: (Boolean) -> Unit) {
        mAuth.createUserWithEmailAndPassword(user.email, user.password).addOnCompleteListener {
            if (it.isSuccessful)
                listener(it.isSuccessful)
        }
    }

    fun resetPassword(email: String, listener: (Boolean)->Unit) {
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
}


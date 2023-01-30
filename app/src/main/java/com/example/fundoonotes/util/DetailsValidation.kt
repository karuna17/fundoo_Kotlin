package com.example.fundoonotes.util

import android.widget.EditText
import java.util.*

class DetailsValidation {
    companion object {
        fun validateEmail(email_value: String, email_field: EditText): Boolean {
            if (email_value.isEmpty()) {
                email_field.error = "Email Id is required"
                email_field.requestFocus()
                return false
            }
            return true
        }

        fun validatePassword(password_value: String, pass_field: EditText): Boolean {
            if (password_value.isEmpty()) {
                pass_field.error = "Password is required"
                pass_field.requestFocus()
                return false
            }
            if (password_value.length < 6) {
                pass_field.error = "Password must be greater than 6 characters"
                pass_field.requestFocus()
                return false;
            }
            return true
        }
    }

}
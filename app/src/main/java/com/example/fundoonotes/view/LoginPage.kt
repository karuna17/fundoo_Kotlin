package com.example.fundoonotes.view

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.fundoonotes.R
import com.example.fundoonotes.model.User
import com.example.fundoonotes.model.UserAuthService
import com.example.fundoonotes.util.Failed
import com.example.fundoonotes.util.FailingReason
import com.example.fundoonotes.util.Loading
import com.example.fundoonotes.util.Success
import com.example.fundoonotes.viewmodel.LoginViewModel
import com.example.fundoonotes.viewmodel.LoginViewModelFactory
import com.example.fundoonotes.viewmodel.SharedViewModel
import com.example.fundoonotes.viewmodel.SharedViewModelFactory

class LoginPage : Fragment() {
    companion object {
        internal const val TAG = "LoginFragment"
    }

    private lateinit var user: User
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var loginButton: Button
    private lateinit var createNewAcc: TextView
    private lateinit var resetPassword: TextView
    private lateinit var progBar: ProgressBar
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var passwordResetDialog: AlertDialog.Builder

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var view: View = inflater.inflate(R.layout.fragment_login_page, container, false)
        email = view.findViewById(R.id.inputEmail);
        password = view.findViewById(R.id.inputPassword);
        loginButton = view.findViewById(R.id.loginBtn);
        createNewAcc = view.findViewById(R.id.create_new_acc);
        resetPassword = view.findViewById(R.id.forgot_password);
        progBar = view.findViewById(R.id.progress_bar);
        loginViewModel = ViewModelProvider(
            this,
            LoginViewModelFactory(UserAuthService())
        ).get(LoginViewModel::class.java)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progBar.visibility = View.VISIBLE
        sharedViewModel = ViewModelProvider(
            requireActivity(),
            SharedViewModelFactory(UserAuthService())
        )[SharedViewModel::class.java]

        login()
        creatingNewAcc()
        resetPassword(view)
    }

    private fun login() {
        loginButton.setOnClickListener {
            val mEmail = email.text.toString().trim()
            val mPassword = password.text.toString().trim()
            user = User(email = mEmail, password = mPassword)
            loginViewModel.loginToFundoo(mEmail, mPassword)
//            loginViewModel.loginWithApi(mEmail,mPassword)
            loginViewModel.loginStatus.observe(viewLifecycleOwner, Observer {
                Log.e(TAG, "checkLoginDetails: ${it}")
                when (it) {
                    is Failed -> {
                        when (it.reason) {
                            FailingReason.EMAIL -> {
                                progBar.visibility = View.GONE
                                email.error = it.message
                                email.requestFocus()
                            }
                            FailingReason.PASSWORD -> {
                                progBar.visibility = View.GONE
                                password.error = it.message
                                password.requestFocus()
                            }
                            FailingReason.OTHER -> {
                                progBar.visibility = View.GONE
                                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                            }
                            else -> Toast.makeText(context, "Loading...", Toast.LENGTH_SHORT).show()
                        }
                    }
                    is Success -> loginUser(it)
                    Loading -> progBar.visibility = View.VISIBLE
                }
            })
        }
    }

    private fun loginUser(status: Success) {
        sharedViewModel.fetchUserDetails(status.localId, status.idToken)
        sharedViewModel.userDetails.observe(viewLifecycleOwner, Observer {
            progBar.visibility = View.GONE
            sharedViewModel.setGoToHomePageStatus(true)
            Toast.makeText(context, status.message, Toast.LENGTH_SHORT).show()
        })
    }

    private fun creatingNewAcc() {
        createNewAcc.setOnClickListener {
            sharedViewModel.setGoToRegisterationPage(true)
        }
    }

    private fun resetPassword(view: View) {
        resetPassword.setOnClickListener {
            val mEmail = EditText(view.context)
            passwordResetDialog = AlertDialog.Builder(view.context).apply {
                setTitle("Reset Password?")
                setMessage("Enter Your Email To Reset The Password Link")
                setView(mEmail)
            }
            passwordResetDialog.setPositiveButton("Reset") { dialogInterface: DialogInterface, _: Int ->
                loginViewModel.resetPassword(email.text.toString())
                dialogInterface.dismiss()
            }.setCancelable(false)

            passwordResetDialog.setNegativeButton("Cancel") { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
            }
            passwordResetDialog.create().show()

            loginViewModel.resetPasswordStatus.observe(viewLifecycleOwner, Observer {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            })
        }

    }

    private fun setUser() {
        loginViewModel.checkUserExistence()
        loginViewModel.isUserLoggedIn.observe(this, {
            when (it) {
                true -> {
                    sharedViewModel.setGoToHomePageStatus(true)
                }
                false -> {
                    sharedViewModel.setGoToLoginPageStatus(true)
                }
            }
        })
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity).supportActionBar?.show()
    }
}

//    private fun login() {
//        loginButton.setOnClickListener {
//            var mEmail = email.text.toString().trim()
//            var mPassword = password.text.toString().trim()
//            /*          val validEmail = DetailsValidation.validateEmail(mEmail, email)
//                      val validPassword = DetailsValidation.validatePassword(mPassword, password)
//                      var userAuthService = UserAuthService()
//                      if (validEmail && validPassword) {
//                          userAuthService.userLogin(mEmail, mPassword, userAuthListener)
//                      } */
//        }
//    }
//
//    private fun creatingNewAcc() {
//        createNewAcc.setOnClickListener {
//            activity?.let {
//                it.supportFragmentManager.beginTransaction()
//                    .replace(R.id.fragment_container, RegistrationPage()).commit()
//            }
//        }
//    }

//    private fun resetPassword(view: View) {
//        resetPassword.setOnClickListener {
//            val resetEmail = EditText(view.context)
//            passwordResetDialog = AlertDialog.Builder(view.context).apply {
//                setTitle("Reset Password?")
//                setMessage("Enter Your Email To Reset The Password Link")
//                setView(resetEmail)
//            }
//            passwordResetDialog.setPositiveButton(
//                "Reset",
//                DialogInterface.OnClickListener { dialogInterface: DialogInterface, _: Int ->
//                    var mEmail = resetEmail.text.toString();
//                    userAuthService.resetPassword(mEmail, userAuthListener)
//                    activity?.let {
//                        it.supportFragmentManager.beginTransaction()
//                            .replace(R.id.fragment_container, LoginPage()).commit()
//                    }
//                    dialogInterface.dismiss()
//                }).setCancelable(false)
//            passwordResetDialog.setNegativeButton("Cancel") { dialogInterface: DialogInterface, _: Int ->
//                dialogInterface.dismiss()
//            }
//            passwordResetDialog.create().show()
//        }
//    }
//
//    private val userAuthListener = object : UserAuthListener {
//        override fun onAuthCompleteListener(status: Boolean, message: String) {
//            activity?.let {
//                if (status) {
//                    Toast.makeText(it, message, Toast.LENGTH_SHORT).show()
//                    it.supportFragmentManager.beginTransaction()
//                        .replace(R.id.fragment_container, HomePage()).commit()
//                } else {
//                    Toast.makeText(it, message, Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//    }



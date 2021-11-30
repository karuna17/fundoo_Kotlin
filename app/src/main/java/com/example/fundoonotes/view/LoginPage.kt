package com.example.fundoonotes.view

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.fundoonotes.R
import com.example.fundoonotes.databinding.FragmentLoginPageBinding
import com.example.fundoonotes.listeners.UserAuthListener
import com.example.fundoonotes.model.UserAuthService
import com.example.fundoonotes.util.*
import com.example.fundoonotes.viewmodel.LoginViewModel
import com.example.fundoonotes.viewmodel.LoginViewModelFactory
import com.example.fundoonotes.viewmodel.SharedViewModel
import com.example.fundoonotes.viewmodel.SharedViewModelFactory


class LoginPage : Fragment() {
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var loginButton: Button
    private lateinit var createNewAcc: TextView
    private lateinit var resetPassword: TextView
    private lateinit var progBar: ProgressBar

    //    private lateinit var userAuthService: UserAuthService
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var passwordResetDialog: AlertDialog.Builder

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        var view: View = inflater.inflate(R.layout.fragment_login_page, container, false)
        //        userAuthService = UserAuthService()
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
            loginViewModel.loginToFundoo(mEmail, mPassword)
            loginViewModel.loginStatus.observe(viewLifecycleOwner, Observer {
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
        sharedViewModel.setGoToHomePageStatus(true)
        Toast.makeText(context, status.message, Toast.LENGTH_SHORT).show()
    }

    private fun creatingNewAcc() {
        createNewAcc.setOnClickListener {
            sharedViewModel.setGoToRegisterationPage(true)
            sharedViewModel.setGoToLoginPageStatus(false)
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
}


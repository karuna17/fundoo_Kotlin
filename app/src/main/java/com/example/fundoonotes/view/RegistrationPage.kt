package com.example.fundoonotes.view

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
import com.example.fundoonotes.viewmodel.RegisterViewModel
import com.example.fundoonotes.viewmodel.RegisterViewModelFactory
import com.example.fundoonotes.viewmodel.SharedViewModel
import com.example.fundoonotes.viewmodel.SharedViewModelFactory

class RegistrationPage : Fragment() {
    companion object {
        internal const val TAG = "RegistrationPage"
    }

    //private lateinit var userAuthService: UserAuthService
    private lateinit var name: EditText
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var registerButton: Button
    private lateinit var alreadyHaveAcc: TextView
    private lateinit var progBar: ProgressBar
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var registerViewModel: RegisterViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view: View = inflater.inflate(R.layout.fragment_registration_page, container, false)
        name = view.findViewById(R.id.inputName)
        email = view.findViewById(R.id.inputEmail);
        password = view.findViewById(R.id.inputPassword);
        registerButton = view.findViewById(R.id.registerBtn);
        alreadyHaveAcc = view.findViewById(R.id.already_acc);
        progBar = view.findViewById(R.id.progress_bar);
        //userAuthService = UserAuthService()

        registerViewModel =
            ViewModelProvider(this, RegisterViewModelFactory(UserAuthService())).get(
                RegisterViewModel::class.java
            )

        sharedViewModel = ViewModelProvider(
            requireActivity(),
            SharedViewModelFactory(UserAuthService())
        )[SharedViewModel::class.java]
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        register()
        gotoLoginPage()
    }

    private fun register() {
        registerButton.setOnClickListener {
            val mName = name.text.toString().trim()
            val mEmail = email.text.toString().trim()
            val mPassword = password.text.toString().trim()
            val user = User(
                name = mName,
                email = mEmail,
                password = mPassword,
                imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/3/34/PICA.jpg/900px-PICA.jpg"
            )
            registerViewModel.registerUser(user)
            registerViewModel.userRegistrationStatus.observe(viewLifecycleOwner, Observer {
                Log.e(TAG, "checkRegisterDetails: ${it}")

                when (it) {
                    is Failed -> {
                        when (it.reason) {
                            FailingReason.NAME -> {
                                progBar.visibility = View.GONE
                                name.error = it.message
                                name.requestFocus()
                            }
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
                        }
                    }
                    is Success -> registerUser(it.message)
                    Loading -> progBar.visibility = View.VISIBLE
                }
            })
        }
    }

    private fun registerUser(message: String) {
        progBar.visibility = View.GONE
        sharedViewModel.setGoToHomePageStatus(true)
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun gotoLoginPage() {
        alreadyHaveAcc.setOnClickListener {
            sharedViewModel.setGoToLoginPageStatus(true)
            sharedViewModel.setGoToRegisterationPage(false)
        }
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
    //    private fun register() {
//        registerButton.setOnClickListener {
//            val mName = name.text.toString().trim()
//            val mEmail = email.text.toString().trim()
//            val mPassword = password.text.toString().trim()
//            val validEmail = DetailsValidation.validateEmail(mEmail, email)
//            val validPassword = DetailsValidation.validatePassword(mPassword, password)
//            val user = User(name = mName, email = mEmail, password = mPassword)
//            if (validEmail && validPassword) {
//                userAuthService.userRegister(user, userAuthListener)
//            }
//        }
//    }
//
//    private val userAuthListener = object : UserAuthListener {
//        override fun onAuthCompleteListener(status: Boolean, message: String) {
//            activity?.let {
//                if (status) {
//                    Toast.makeText(it, message, Toast.LENGTH_SHORT).show()
//                    val transaction = it.supportFragmentManager.beginTransaction()
//                    transaction.replace(R.id.fragment_container, HomePage()).commit()
//                } else {
//                    Toast.makeText(it, message, Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//    }


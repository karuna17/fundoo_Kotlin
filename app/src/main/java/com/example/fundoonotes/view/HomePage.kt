package com.example.fundoonotes.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.ViewModelProvider
import com.example.fundoonotes.R
import com.example.fundoonotes.model.UserAuthService
import com.example.fundoonotes.viewmodel.LoginViewModel
import com.example.fundoonotes.viewmodel.LoginViewModelFactory
import com.example.fundoonotes.viewmodel.SharedViewModel
import com.example.fundoonotes.viewmodel.SharedViewModelFactory


class HomePage : Fragment() {
    //    private lateinit var userAuthService: UserAuthService
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var signOutButton: Button


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view: View = inflater.inflate(R.layout.fragment_home_page, container, false)
        signOutButton = view.findViewById(R.id.signOutBtn)
        loginViewModel = ViewModelProvider(
            this,
            LoginViewModelFactory(UserAuthService())
        ).get(LoginViewModel::class.java)

//        userAuthService = UserAuthService()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel = ViewModelProvider(
            requireActivity(),
            SharedViewModelFactory(UserAuthService())
        )[SharedViewModel::class.java]
        logOut()
    }

    private fun logOut() {
        signOutButton.setOnClickListener {
            activity?.let {
                loginViewModel.userLogOut()
                sharedViewModel.setGoToLoginPageStatus(true)
            }
        }
    }
}
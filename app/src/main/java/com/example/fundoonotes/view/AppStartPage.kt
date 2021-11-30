package com.example.fundoonotes.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.fundoonotes.R
import com.example.fundoonotes.model.UserAuthService
import com.example.fundoonotes.viewmodel.AppStartViewModel
import com.example.fundoonotes.viewmodel.AppStartViewModelFactory
import com.example.fundoonotes.viewmodel.SharedViewModel
import com.example.fundoonotes.viewmodel.SharedViewModelFactory

class AppStartPage : Fragment() {
    private lateinit var appStartViewModel: AppStartViewModel
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view: View = inflater.inflate(R.layout.fragment_app_start_page, container, false)
        progressBar = view.findViewById(R.id.progress_bar)
        appStartViewModel =
            ViewModelProvider(this, AppStartViewModelFactory(UserAuthService())).get(
                AppStartViewModel::class.java
            )
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel = ViewModelProvider(
            requireActivity(),
            SharedViewModelFactory(UserAuthService())
        )[SharedViewModel::class.java]
        progressBar.visibility = View.VISIBLE
    }

    override fun onStart() {
        super.onStart()
        setUser()
    }

    private fun setUser() {
        appStartViewModel.checkUserExistence()
        appStartViewModel.isUserLoggedIn.observe(viewLifecycleOwner, Observer {
            when (it) {
                true -> {
                    sharedViewModel.setGoToHomePageStatus(true)
                    progressBar.visibility = View.GONE
                }
                false -> {
                    sharedViewModel.setGoToLoginPageStatus(true)
                    progressBar.visibility = View.GONE
                }
            }
        })
    }
}
package com.example.fundoonotes.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.fundoonotes.R
import com.example.fundoonotes.model.UserAuthService
import com.example.fundoonotes.viewmodel.SharedViewModel
import com.example.fundoonotes.viewmodel.SharedViewModelFactory

class MainActivity : AppCompatActivity() {
    private lateinit var sharedViewModel: SharedViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        goToStartAppPage()
        initActivity()
        observeAppNavigation()
    }

//    private fun loadLoginPage() {
//        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, LoginPage())
//            .commit();
//    }

    private fun initActivity() {
        sharedViewModel = ViewModelProvider(
            this, SharedViewModelFactory(UserAuthService())
        )[SharedViewModel::class.java]
    }

    private fun observeAppNavigation() {
        sharedViewModel.goToHomePageStatus.observe(this, {
            if (it == true) goToHomePage()
        })
        sharedViewModel.goToRegisterPageStatus.observe(this, {
            if (it == true) goToRegisterUserPage()
        })
        sharedViewModel.goToLoginPageStatus.observe(this, {
            if (it == true) goToLoginUserPage()
        })
    }

    private fun goToRegisterUserPage() {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, RegistrationPage())
            commit()
        }
    }

    private fun goToLoginUserPage() {
//        binding.contentLayout.addNotesFab.hide()
//        binding.contentLayout.appBarLayout.visibility = View.GONE
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, LoginPage())
            commit()
        }
    }

    private fun goToHomePage() {
//        binding.contentLayout.addNotesFab.show()
//        binding.contentLayout.appBarLayout.visibility = View.VISIBLE
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, HomePage())
            commit()
        }
    }

    private fun goToStartAppPage() {
//        binding.contentLayout.addNotesFab.hide()
//        binding.contentLayout.appBarLayout.visibility = View.GONE
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, AppStartPage())
            commit()
        }
    }

}
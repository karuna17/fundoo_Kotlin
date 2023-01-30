package com.example.fundoonotes.view

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.MenuItemCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.fundoonotes.R
import com.example.fundoonotes.model.NotesOperation
import com.example.fundoonotes.model.UserAuthService
import com.example.fundoonotes.model.ViewType
import com.example.fundoonotes.viewmodel.LoginViewModel
import com.example.fundoonotes.viewmodel.LoginViewModelFactory
import com.example.fundoonotes.viewmodel.SharedViewModel
import com.example.fundoonotes.viewmodel.SharedViewModelFactory
import com.google.android.material.navigation.NavigationView
import de.hdodenhof.circleimageview.CircleImageView

class MainActivity : AppCompatActivity() {
    private lateinit var profileImage: CircleImageView
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var toolBar: Toolbar
    private lateinit var drawer: DrawerLayout
    private lateinit var navigationDrawer: NavigationView
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var loginViewModel: LoginViewModel
    private var menuStatus = true
    private var viewType = ViewType.LIST

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toolBar = findViewById(R.id.main_toolbar)
        drawer = findViewById(R.id.drawer)
        navigationDrawer = findViewById(R.id.navigation_drawer)
        setSupportActionBar(toolBar)
        toolBar.showOverflowMenu()

        loginViewModel = ViewModelProvider(
            this,
            LoginViewModelFactory(UserAuthService())
        ).get(LoginViewModel::class.java)

        sharedViewModel = ViewModelProvider(
            this, SharedViewModelFactory(UserAuthService())
        )[SharedViewModel::class.java]

        sharedViewModel.setGoToLoginPageStatus(true)
        observeAppNavigation()
    }

//    private fun loadLoginPage() {
//        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, LoginPage())
//            .commit();
//    }

    private fun setNavigationDrawer() {
        toggle = ActionBarDrawerToggle(this, drawer, toolBar, R.string.open, R.string.close)
        drawer.addDrawerListener(toggle)
        toggle.isDrawerIndicatorEnabled = true
        toggle.syncState()
        navigationDrawer.setNavigationItemSelectedListener(NavigationView.OnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.notes -> Toast.makeText(this, "note selected", Toast.LENGTH_SHORT).show()
                R.id.reminder_list -> Toast.makeText(this, "reminder selected", Toast.LENGTH_SHORT)
                    .show()
            }
            return@OnNavigationItemSelectedListener true
        })
    }

    private fun observeAppNavigation() {
        sharedViewModel.goToHomePageStatus.observe(this, {
            Log.d(TAG, "observeAppNavigation: goto home page $it")
            if (it == true) goToHomePage()
        })
        sharedViewModel.goToRegisterPageStatus.observe(this, {
            Log.d(TAG, "observeAppNavigation: goto registration $it")
            if (it == true) goToRegisterUserPage()
        })
        sharedViewModel.goToLoginPageStatus.observe(this, {
            Log.d(TAG, "observeAppNavigation:goto loginpage $it")
            if (it == true) goToLoginUserPage()
        })
        sharedViewModel.writeNote.observe(this, Observer {
            it?.apply {
              goToNotesPage(it)
            }
        })
    }

    private fun goToRegisterUserPage() {
        toolBar.visibility = View.GONE
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, RegistrationPage())
            addToBackStack(null)
            commit()
        }
    }

    private fun goToLoginUserPage() {
        toolBar.visibility = View.GONE
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, LoginPage())
            addToBackStack(null)
            commit()
        }
    }

    private fun goToHomePage() {
        toolBar.visibility = View.VISIBLE
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, HomePage())
            addToBackStack(null)
            commit()
        }
    }

    private fun goToNotesPage(operation: NotesOperation) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, AddNotes(operation))
            addToBackStack(null)
            commit()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (menuStatus) {
            menuInflater.inflate(R.menu.home_toolbar_menu, menu)
            homeToolBarMenu(menu)
        }
        return super.onCreateOptionsMenu(menu)
    }

    private fun homeToolBarMenu(menu: Menu?) {
        setNavigationDrawer()
        supportActionBar?.title = "Fundoo"
        var profileItem = menu?.findItem(R.id.profile_menu)
        val view = MenuItemCompat.getActionView(profileItem)
        profileImage = view.findViewById(R.id.toolbar_profile_img)
        profileImage.setOnClickListener {
            ProfileFragment().show(supportFragmentManager, "User Profile")
        }
        sharedViewModel.imageUri.observe(this, {
            if (it != null) Glide.with(this).load(it).into(profileImage)
        })
        sharedViewModel.userDetails.observe(this, {
            if (it.imageUrl != "")
                Glide.with(this).load(it.imageUrl).into(profileImage)
        })

        val search = menu?.findItem(R.id.search)
        val searchView = search?.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                sharedViewModel.setQueryText(newText)
                return false
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.notesView ->{
                viewType = when(viewType){
                    ViewType.LIST -> {
                        item.setIcon(R.drawable.ic_grid_layout)
                        ViewType.GRID
                    }
                    ViewType.GRID -> {
                        item.setIcon(R.drawable.ic_linear_layout)
                        ViewType.LIST
                    }
                }
                sharedViewModel.setNotesDisplayType(viewType)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
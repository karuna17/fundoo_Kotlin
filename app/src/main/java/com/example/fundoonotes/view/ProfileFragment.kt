package com.example.fundoonotes.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.fundoonotes.R
import com.example.fundoonotes.databinding.FragmentProfileBinding
import com.example.fundoonotes.model.UserAuthService
import com.example.fundoonotes.viewmodel.ProfileViewModel
import com.example.fundoonotes.viewmodel.ProfileViewModelFactory
import com.example.fundoonotes.viewmodel.SharedViewModel
import com.example.fundoonotes.viewmodel.SharedViewModelFactory

class ProfileFragment : DialogFragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var profileViewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_profile, container, false)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)
        profileViewModel = ViewModelProvider(
            this,
            ProfileViewModelFactory(UserAuthService())
        ).get(ProfileViewModel::class.java)

        sharedViewModel = ViewModelProvider(
            requireActivity(),
            SharedViewModelFactory(UserAuthService())
        )[SharedViewModel::class.java]
        binding.profilePageViewModel = profileViewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.profileCloseIcon.setOnClickListener { dismiss() }
        binding.SignOutButton.setOnClickListener {
            profileViewModel.logout()
            profileViewModel.logoutStatus.observe(viewLifecycleOwner, Observer {
                sharedViewModel.setGoToLoginPageStatus(true)
            })
            dismiss()
        }
        binding.profileImageView.setOnClickListener {
            val openGalleryIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(openGalleryIntent, 200)
        }
        setProfileDetails()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 200 && resultCode == Activity.RESULT_OK) {
            data?.data?.let {
                profileViewModel.uploadImageToFirebase(it)
                Glide.with(this).load(it).into(binding.profileImageView)
                sharedViewModel.setImageUri(it)
            }
        }
    }

    private fun setProfileDetails() {
        sharedViewModel.userDetails.observe(viewLifecycleOwner, {
            binding.profileName.text = it.name
            binding.profileEmail.text = it.email
            if (it.imageUrl != "")
                Glide.with(this).load(it.imageUrl).into(binding.profileImageView)
        })
    }
}


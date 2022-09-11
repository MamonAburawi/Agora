package com.example.agora.screens.home

import android.Manifest
import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.PermissionChecker.checkSelfPermission

import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.example.agora.R
import com.example.agora.databinding.HomeBinding

class Home : Fragment() {


    private lateinit var binding: HomeBinding
    private lateinit var viewModel: HomeViewModel


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater,R.layout.home, container, false)
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]




        setViews()

        return binding.root
    }


    override fun onResume() {
        super.onResume()

        binding.btnCall.visibility = View.VISIBLE
    }

    private fun setViews() {
        binding.apply {


            // button call
            btnCall.setOnClickListener {
                findNavController().navigate(R.id.action_home_to_encounter)
            }


        }
    }


}
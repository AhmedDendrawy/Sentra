package com.example.sentra.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.sentra.api.RetrofitClient
import com.example.sentra.data.repo.SettingsRepository
import com.example.sentra.databinding.FragmentSettingsBinding
import com.example.sentra.ui.auth.ChangePassword
import com.example.sentra.ui.auth.LoginActivity
import com.example.sentra.ui.camera.ManageCamerasActivity

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: SettingsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        initViews()
        setupObservers()

        viewModel.loadUserData()
    }

    private fun setupViewModel() {
        val apiService = RetrofitClient.getApiService(requireContext())
        val repository = SettingsRepository(requireContext(), apiService)
        val factory = SettingsViewModel.Factory(repository)
        viewModel = ViewModelProvider(this, factory)[SettingsViewModel::class.java]
    }

    private fun initViews() {
        binding.btnManageCameras.setOnClickListener {
            startActivity(Intent(requireContext(), ManageCamerasActivity::class.java))
        }

        binding.btnChangePassword.setOnClickListener {
            startActivity(Intent(requireContext(), ChangePassword::class.java))
        }

        binding.btnLogOut.setOnClickListener {
            Toast.makeText(requireContext(), "Logging Out...", Toast.LENGTH_SHORT).show()
            viewModel.logout()
        }

        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            val msg = if (isChecked) "Notifications Enabled" else "Notifications Disabled"
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupObservers() {
        viewModel.userName.observe(viewLifecycleOwner) { name ->
            binding.tvUserName.text = name
        }

        viewModel.userEmail.observe(viewLifecycleOwner) { email ->
            binding.tvUserEmail.text = email
        }

        viewModel.logoutEvent.observe(viewLifecycleOwner) { isLoggedOut ->
            if (isLoggedOut) {
                val intent = Intent(requireActivity(), LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
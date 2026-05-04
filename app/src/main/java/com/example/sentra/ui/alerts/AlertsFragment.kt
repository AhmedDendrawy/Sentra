package com.example.sentra.ui.alerts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sentra.adapters.AlertsAdapter
import com.example.sentra.api.RetrofitClient
import com.example.sentra.data.repository.AlertsRepository
import com.example.sentra.databinding.FragmentAlertsBinding

class AlertsFragment : Fragment() {

    private var _binding: FragmentAlertsBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: AlertsAdapter
    private lateinit var viewModel: AlertsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAlertsBinding.inflate(inflater, container, false)

        binding.rvAlerts.layoutManager = LinearLayoutManager(context)

        adapter = AlertsAdapter(mutableListOf()) { clickedAlert ->

        }
        binding.rvAlerts.adapter = adapter

        val apiService = RetrofitClient.getApiService(requireContext())
        val repository = AlertsRepository(apiService)
        val factory = AlertsViewModel.Factory(repository)
        viewModel = ViewModelProvider(this, factory)[AlertsViewModel::class.java]

        setupObservers()

        viewModel.fetchIncidents()

        return binding.root
    }

    private fun setupObservers() {
        viewModel.alertsList.observe(viewLifecycleOwner) { alerts ->
            adapter.updateData(alerts)
            updateEmptyState(alerts.isEmpty())
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
                binding.rvAlerts.visibility = View.GONE
                binding.layoutEmptyState.visibility = View.GONE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMsg ->
            errorMsg?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                updateEmptyState(adapter.itemCount == 0)
            }
        }
    }

    private fun updateEmptyState(isEmpty: Boolean) {
        if (isEmpty) {
            binding.rvAlerts.visibility = View.GONE
            binding.layoutEmptyState.visibility = View.VISIBLE
        } else {
            binding.rvAlerts.visibility = View.VISIBLE
            binding.layoutEmptyState.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
package com.example.sentra.ui.alerts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sentra.R
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

        setupRecyclerView()
        setupViewModel()
        setupSwipeRefresh()
        setupObservers()

        // بنطلب الداتا أول ما الشاشة تفتح
        viewModel.fetchIncidents()

        return binding.root
    }

    private fun setupRecyclerView() {
        binding.rvAlerts.layoutManager = LinearLayoutManager(context)
        adapter = AlertsAdapter(mutableListOf()) { clickedAlert ->
            // هنا تقدر تبرمج ضغطة اليوزر على الإشعار من جوه الشاشة لو حابب
        }
        binding.rvAlerts.adapter = adapter
    }

    private fun setupViewModel() {
        val apiService = RetrofitClient.getApiService(requireContext())
        val repository = AlertsRepository(apiService)
        val factory = AlertsViewModel.Factory(repository)
        viewModel = ViewModelProvider(this, factory)[AlertsViewModel::class.java]
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setColorSchemeResources(
            R.color.blue,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light
        )
        // 🌟 هنا بنلقط سحبة اليوزر للشاشة من فوق
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.fetchIncidents(isSwipeRefresh = true)
        }
    }

    private fun setupObservers() {
        viewModel.alertsList.observe(viewLifecycleOwner) { alerts ->
            adapter.updateData(alerts)
            updateEmptyState(alerts.isEmpty())
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
                binding.layoutEmptyState.visibility = View.GONE
                // 🌟 سيبنا الـ RecyclerView ظاهر عشان حركة السحب متقفش
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }

        // 🌟 بنقفل علامة التحميل بتاعت السحب (الدايرة اللي بتنزل من فوق) لما الداتا توصل
        viewModel.isRefreshing.observe(viewLifecycleOwner) { isRefreshing ->
            binding.swipeRefreshLayout.isRefreshing = isRefreshing
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
            // 🌟 لو مفيش داتا، بنظهر شاشة الفراغ (Lottie) بس بنسيب الـ RecyclerView موجود ومخفي تحته عشان السحب يشتغل
            binding.rvAlerts.visibility = View.VISIBLE
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
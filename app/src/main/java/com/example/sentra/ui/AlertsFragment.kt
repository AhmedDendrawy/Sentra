package com.example.sentra.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sentra.R
import com.example.sentra.adapters.AlertsAdapter
import com.example.sentra.api.RetrofitClient
import com.example.sentra.model.AlertItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AlertsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var layoutEmptyState: LinearLayout
    private lateinit var progressBar: ProgressBar // 🌟 تعريف عجلة التحميل
    private lateinit var adapter: AlertsAdapter
    private var alertsList = mutableListOf<AlertItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_alerts, container, false)

        recyclerView = view.findViewById(R.id.rvAlerts)
        layoutEmptyState = view.findViewById(R.id.layoutEmptyState)
        progressBar = view.findViewById(R.id.progressBar) // 🌟 ربط التحميل

        recyclerView.layoutManager = LinearLayoutManager(context)

        adapter = AlertsAdapter(alertsList) { clickedAlert ->
            // Click action
        }
        recyclerView.adapter = adapter

        fetchIncidents()

        return view
    }

    private fun fetchIncidents() {
        // 🌟 إظهار التحميل وإخفاء باقي الشاشة قبل الريكويست
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        layoutEmptyState.visibility = View.GONE

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.getApiService(requireContext()).getIncidents()

                withContext(Dispatchers.Main) {
                    // 🌟 إخفاء التحميل أول ما الداتا توصل
                    progressBar.visibility = View.GONE

                    if (response.isSuccessful && response.body() != null) {
                        alertsList.clear()
                        alertsList.addAll(response.body()!!)

                        adapter.updateData(alertsList)
                        updateEmptyState()
                    } else {
                        updateEmptyState()
                        if(response.code() == 401) {
                            Toast.makeText(context, "Session expired, please login again", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    // 🌟 إخفاء التحميل لو حصل خطأ في النت
                    progressBar.visibility = View.GONE
                    updateEmptyState()
                }
            }
        }
    }

    private fun updateEmptyState() {
        if (alertsList.isEmpty()) {
            recyclerView.visibility = View.GONE
            layoutEmptyState.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            layoutEmptyState.visibility = View.GONE
        }
    }
}
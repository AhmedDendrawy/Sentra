package com.example.sentra.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
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
    private lateinit var adapter: AlertsAdapter
    private var alertsList = mutableListOf<AlertItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_alerts, container, false)

        recyclerView = view.findViewById(R.id.rvAlerts)
        layoutEmptyState = view.findViewById(R.id.layoutEmptyState)

        recyclerView.layoutManager = LinearLayoutManager(context)

        // 1. تعريف الأدابتر وحل الإيرور (ضفنا الأقواس بتاعت الـ Click Listener)
        adapter = AlertsAdapter(alertsList) { clickedAlert ->
            // الكود هنا هيتنفذ لو اليوزر داس على الكارت نفسه (مش الصورة)
            // هنبقى نخليه يفتح شاشة تفاصيل لو حبيت
        }
        recyclerView.adapter = adapter

        // 2. جلب الحوادث من السيرفر فوراً أول ما الشاشة تفتح
        fetchIncidents()

        return view
    }

    private fun fetchIncidents() {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                // بنكلم الـ API اللي إنت لسه ضايفه
                val response = RetrofitClient.getApiService(requireContext()).getIncidents()

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        alertsList.clear()
                        alertsList.addAll(response.body()!!)

                        // بنبعت الداتا الجديدة للأدابتر
                        adapter.updateData(alertsList)
                        updateEmptyState()
                    } else {
                        updateEmptyState()
                        // لو التوكن خلصان أو فيه مشكلة
                        if(response.code() == 401) {
                            Toast.makeText(context, "Session expired, please login again", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    updateEmptyState()
                    // Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // دالة التحكم في الرسمة الباهتة (Empty State)
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
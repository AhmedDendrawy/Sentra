import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sentra.AddCameraActivity
import com.example.sentra.CameraAdapter
import com.example.sentra.CameraItem
import com.example.sentra.CameraStreamActivity
import com.example.sentra.R
import com.google.android.material.button.MaterialButton

class HomeFragment : Fragment() {

    private lateinit var adapter: CameraAdapter

    // قائمة قابلة للتعديل عشان نضيف فيها
    private val camerasList = ArrayList<CameraItem>()

    // --- (أ) إنشاء المستقبل (Receiver) ---
    // ده الكود اللي بيشتغل لما ترجع من صفحة الإضافة
    private val addCameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // استخراج الكاميرا من الشنطة
            val newCamera = result.data?.getParcelableExtra<CameraItem>("NEW_CAMERA")

            if (newCamera != null) {
                // إضافة الكاميرا للقائمة
                camerasList.add(newCamera)
                // تحديث الـ RecyclerView عشان يظهر العنصر الجديد
                adapter.notifyItemInserted(camerasList.size - 1)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val recyclerView: RecyclerView = view.findViewById(R.id.rvCameras)
        val btnAdd = view.findViewById<MaterialButton>(R.id.btnAddCamera)

        // إضافة بيانات تجريبية لو القائمة فاضية
        if (camerasList.isEmpty()) {
            camerasList.add(CameraItem("Front Door", "Main Entrance", "2 hours ago", true))
            camerasList.add(CameraItem("Parking Lot", "Outside", "No incidents", true))
        }

        // إعداد الـ Adapter
        adapter = CameraAdapter(camerasList) { clickedCamera ->
            // الكود ده هيشتغل لما تضغط على أي كاميرا
            val intent = Intent(requireContext(), CameraStreamActivity::class.java)

            // نبعت بيانات الكاميرا للصفحة الجديدة
            intent.putExtra("CAMERA_DATA", clickedCamera)

            startActivity(intent)
        }

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
        // --- (ب) تشغيل صفحة الإضافة ---
        btnAdd.setOnClickListener {
            val intent = Intent(requireContext(), AddCameraActivity::class.java)
            // لازم نستخدم الـ Launcher اللي عملناه فوق، مش startActivity العادية
            addCameraLauncher.launch(intent)
        }

        return view
    }
}
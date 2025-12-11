import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sentra.CameraItem
import com.example.sentra.R

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_home, container, false)


        val recyclerView: RecyclerView = view.findViewById(R.id.rvCameras)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // تجهيز بيانات وهمية (Dummy Data)
        val cameras = listOf(
            CameraItem("Front Door", "Main Entrance", "Last incident: 2 hours ago", true),
            CameraItem("Parking Lot", "Outside", "Last incident: No incidents", true),
            CameraItem("Back Yard", "Garden Area", "Last incident: Yesterday", false), // دي هتظهر Offline
            CameraItem("Garage", "Side Entrance", "Last incident: 5 mins ago", true)
        )

        val adapter = CameraAdapter(cameras)
        recyclerView.adapter = adapter

        return view
    }
}
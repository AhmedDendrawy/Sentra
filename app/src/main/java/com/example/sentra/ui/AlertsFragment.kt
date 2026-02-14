import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sentra.model.AlertItem
import com.example.sentra.model.AlertType
import com.example.sentra.R

class AlertsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_alerts, container, false)

        val recyclerView: RecyclerView = view.findViewById(R.id.rvAlerts)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // بيانات وهمية للتجربة (نفس الصورة)
        val alertsList = listOf(
            AlertItem("Fire Detected", "Front Door", "2 hours ago", AlertType.FIRE),
            AlertItem("Violence Detected", "Parking Lot", "5 hours ago", AlertType.VIOLENCE),
            AlertItem("Accident Detected", "Front Door", "1 day ago", AlertType.ACCIDENT)
        )

        recyclerView.adapter = AlertsAdapter(alertsList)

        return view
    }
}
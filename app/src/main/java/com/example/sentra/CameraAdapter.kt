import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sentra.CameraItem
import com.example.sentra.R

class CameraAdapter(private val cameraList: List<CameraItem>) :
    RecyclerView.Adapter<CameraAdapter.CameraViewHolder>() {

    class CameraViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvCameraName)
        val tvLocation: TextView = itemView.findViewById(R.id.tvLocation)
        val tvLastIncident: TextView = itemView.findViewById(R.id.tvLastIncident)
        val imgStatus: ImageView = itemView.findViewById(R.id.imgStatus) // تأكد إنك غيرت ID في XML لـ imgStatus
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CameraViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_camera, parent, false)
        return CameraViewHolder(view)
    }

    override fun onBindViewHolder(holder: CameraViewHolder, position: Int) {
        val item = cameraList[position]

        holder.tvName.text = item.name
        holder.tvLocation.text = item.location
        holder.tvLastIncident.text = item.lastIncident

        // تغيير الصورة بناءً على الحالة
        if (item.isOnline) {
            holder.imgStatus.setImageResource(R.drawable.statues_online) // لازم تكون ضفت الصورة دي
        } else {
            holder.imgStatus.setImageResource(R.drawable.statues_offline) // لازم تكون ضفت الصورة دي
        }
    }

    override fun getItemCount() = cameraList.size
}
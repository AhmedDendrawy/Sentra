import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.sentra.model.AlertItem
import com.example.sentra.model.AlertType
import com.example.sentra.R

class AlertsAdapter(private val alerts: List<AlertItem>) :
    RecyclerView.Adapter<AlertsAdapter.AlertViewHolder>() {

    class AlertViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvAlertTitle)
        val tvCamera: TextView = itemView.findViewById(R.id.tvCameraName)
        val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        val imgIcon: ImageView = itemView.findViewById(R.id.imgAlertIcon)
        val viewIndicator: View = itemView.findViewById(R.id.viewIndicator)
        // تم حذف viewIconBg
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_alert, parent, false)
        return AlertViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlertViewHolder, position: Int) {
        val item = alerts[position]
        val context = holder.itemView.context

        holder.tvTitle.text = item.title
        holder.tvCamera.text = "Camera: ${item.cameraName}"
        holder.tvTime.text = item.time

        // التعديل هنا: شيلنا باراميتر لون الخلفية
        when (item.type) {
            AlertType.FIRE -> {
                // افترضت اسم الايقونة الملونة عندك ic_fire_colored
                setColors(context, holder, R.color.alert_red_main, R.drawable.fire)
            }
            AlertType.VIOLENCE -> {
                setColors(context, holder, R.color.alert_orange_main, R.drawable.violence)
            }
            AlertType.ACCIDENT -> {
                setColors(context, holder, R.color.alert_yellow_main, R.drawable.accident)
            }
        }
    }

    // الدالة بقت أبسط بكتير
    private fun setColors(context: Context, holder: AlertViewHolder, mainColorRes: Int, iconRes: Int) {
        val mainColor = ContextCompat.getColor(context, mainColorRes)

        // 1. تلوين الشريط الجانبي فقط
        holder.viewIndicator.setBackgroundColor(mainColor)

        // 2. تغيير الصورة (الصورة نفسها ملونة وجاهزة)
        holder.imgIcon.setImageResource(iconRes)
    }

    override fun getItemCount() = alerts.size
}
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.plantwise.R

class PlantDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plant_detail)

        val plantName = intent.getStringExtra("plantName")
        val plantImage = intent.getStringExtra("plantImage")

        val plantNameTextView: TextView = findViewById(R.id.plant_detail_name)
        val plantImageView: ImageView = findViewById(R.id.plant_detail_image)

        plantNameTextView.text = plantName
        Glide.with(this).load(plantImage).into(plantImageView)
    }
}

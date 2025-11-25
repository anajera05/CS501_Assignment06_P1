package com.example.a06_01

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a06_01.ui.theme._06_01Theme
import kotlin.math.pow

class MainActivity : ComponentActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var barometer: Sensor? = null

    private var _pressure by mutableFloatStateOf(0f)
    private var _accuracy by mutableStateOf("Unknown")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Sensor Manager
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        // Use the pressure sensor
        barometer = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE)

        enableEdgeToEdge()
        setContent {
            _06_01Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BarometerScreen(pressure = _pressure, accuracy = _accuracy)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        barometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            _pressure = it.values[0]
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        _accuracy = when (accuracy) {
            SensorManager.SENSOR_STATUS_ACCURACY_HIGH -> "High"
            SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM -> "Medium"
            SensorManager.SENSOR_STATUS_ACCURACY_LOW -> "Low"
            SensorManager.SENSOR_STATUS_UNRELIABLE -> "Unreliable"
            else -> "Unknown"
        }
    }

}


@Composable
fun BarometerScreen(pressure: Float, accuracy: String) {
    // Convert pressure readings using the following formula where h is the altitude P0 = 1013.25.
    val altitude = 44330f * (1f - (pressure / 1013.25f).pow(1f / 5.255f))
    val maxAltitude = 4000f
    val normalized = (altitude / maxAltitude).coerceIn(0f, 1f)

    // Simulate pressure changes to test and update the screen in real time. Also change the background color using darker colors at higher altitudes
    val bgColor = Color((173 + (0 - 173) * normalized).toInt(), (216 + (0 - 216) * normalized).toInt(), (230 + (139 - 230) * normalized).toInt())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Altitude",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(16.dp))

        SensorValue(value = altitude)

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Sensor Accuracy: $accuracy",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Composable
fun SensorValue(value: Float) {
    Text(
        text = "Altitude: ${"%.2f".format(value)} m",
        fontSize = 18.sp,
        fontWeight = FontWeight.Medium,
        color = Color.White
    )
}

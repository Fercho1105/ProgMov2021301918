package com.example.apppingpong

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlin.math.abs


class MainActivity : ComponentActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private lateinit var mediaPlayer: MediaPlayer
    private var imageState = mutableStateOf(R.drawable.raqueta1)


    private var lastX = 0f
    private var lastY = 0f
    private var lastTimestamp: Long = 0
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        mediaPlayer = MediaPlayer.create(this, R.raw.ping_pong_hit)

        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }

        setContent {
            PingPongGame(imageState)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            val currentTime = System.currentTimeMillis()
            val x = it.values[0]
            val y = it.values[1]

            if (lastTimestamp > 0) {
                val deltaTime = (currentTime - lastTimestamp).toFloat() / 1000 // en segundos
                val velocityX = abs(x - lastX) / deltaTime
                val velocityY = abs(y - lastY) / deltaTime

                val velocidadGolpe = maxOf(velocityX, velocityY)

                val golpeThreshold = 300f // Ajusta según pruebas
                if (velocidadGolpe > golpeThreshold) {
                    val volumen = calcularVolumen(velocidadGolpe)
                    playSound(volumen)
                    changeImage()
                }
            }

            lastX = x
            lastY = y
            lastTimestamp = currentTime
        }
    }

    private fun playSound(volumen: Float) {
        if (!mediaPlayer.isPlaying) {
            mediaPlayer.setVolume(volumen, volumen)
            mediaPlayer.start()
            Log.d("VOL", "Volumen: $volumen")
        }
    }

    private fun calcularVolumen(velocidad: Float): Float {
        val minVelocidad = 10f  // Mínimo para considerar golpe
        val maxVelocidad = 50f  // Velocidad máxima esperada
        
        return ((velocidad - minVelocidad) / (maxVelocidad - minVelocidad))
            .coerceIn(0f, 1f)
    }

    private fun changeImage() {
        imageState.value = R.drawable.raqueta2

        handler.postDelayed({
            imageState.value = R.drawable.raqueta1
        }, 500)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
        mediaPlayer.release()
    }
}

@Composable
fun PingPongGame(imageState: MutableState<Int>) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = imageState.value),
            contentDescription = "Raqueta",
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        )
    }
}
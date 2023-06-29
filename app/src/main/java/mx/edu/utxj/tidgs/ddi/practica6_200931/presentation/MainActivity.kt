/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */


package mx.edu.utxj.tidgs.ddi.practica6_200931.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope

import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import mx.edu.utxj.tidgs.ddi.practica6_200931.R
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import mx.edu.utxj.tidgs.ddi.practica6_200931.presentation.WeatherData




class MainActivity : ComponentActivity() { // Reemplaza con el nombre de tu Activity
    private lateinit var clockTextView: TextView
    private lateinit var saludoTextView: TextView
    private lateinit var handler: Handler
    private lateinit var updateTimeRunnable: Runnable
    private var mHandler: Handler? = null
    private var mRunnable: Runnable? = null
    private val LOCATION_PERMISSION_REQUEST_CODE = 1



    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var locationCallback: LocationCallback? = null

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://api.openweathermap.org/data/2.5/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val weatherApi: WeatherApi = retrofit.create(WeatherApi::class.java)
    private val apiKey = "9a0695fc78bfb244df31e9b3287d71e0"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                if (locationResult == null) {
                    return
                }
                for (location in locationResult.locations) {
                    // Aquí obtienes la ubicación actual
                    val latitude = location.latitude
                    val longitude = location.longitude
                    // Haz lo que necesites con la ubicación
                    val saludosTextView = findViewById<TextView>(mx.edu.utxj.tidgs.ddi.practica6_200931.R.id.saludo)
                    val lolTextView = findViewById<TextView>(mx.edu.utxj.tidgs.ddi.practica6_200931.R.id.clockTextView)

                    saludosTextView.text = "Latitud: $latitude"
                    lolTextView.text = "Longitud: $longitude"

                    // Llamar a la función getWeather dentro de una coroutine
                    lifecycleScope.launch {
                        getWeatherData(latitude, longitude)
                    }
                }
            }
        }

        mHandler = Handler()
        mRunnable = Runnable {
            /*updateTime()
            mRunnable?.let { mHandler?.postDelayed(it, 1000) }*/ // Actualiza cada segundo
        }




    }

    override fun onResume() {
        super.onResume();
        startLocationUpdates();
        mRunnable?.let { mHandler?.post(it) }
    }

    override fun onPause() {
        super.onPause();
        stopLocationUpdates();
        mRunnable?.let { mHandler?.removeCallbacks(it) }
    }

    private fun startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            val locationRequest: LocationRequest = LocationRequest.create()
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            locationRequest.setInterval(5000) // Intervalo en milisegundos para obtener actualizaciones de ubicación
            locationCallback?.let {
                fusedLocationClient!!.requestLocationUpdates(locationRequest,
                    it, null)
            }
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun stopLocationUpdates() {
        locationCallback?.let { fusedLocationClient!!.removeLocationUpdates(it) }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates()
            }
        }
    }

    private suspend fun getWeatherData(latitude: Double, longitude: Double) {
        val response = weatherApi.getWeather(latitude, longitude, apiKey)
        if (response.isSuccessful) {
            val weatherData = response.body()
            val temperature = weatherData.getTemperature()

            // Actualiza la vista con la temperatura
            updateTemperatureView(temperature)
        } else {
            // Manejar error de solicitud a la API
        }
    }

    private fun WeatherData?.getTemperature(): Float {
        val defaultTemperature = 0f
        return this?.tiempo?.getOrNull(0)?.main?.temp ?: defaultTemperature
    }



    private fun updateTemperatureView(temperature: Float) {
        val temperatureTextView = findViewById<TextView>(R.id.temperatureTextView)
        temperatureTextView.text = getString(R.string.temperature_format, temperature)
    }

}
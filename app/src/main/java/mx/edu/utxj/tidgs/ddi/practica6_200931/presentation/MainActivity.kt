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
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import mx.edu.utxj.tidgs.ddi.practica6_200931.R
import mx.edu.utxj.tidgs.ddi.practica6_200931.presentation.theme.Practica6_200931Theme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

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
}

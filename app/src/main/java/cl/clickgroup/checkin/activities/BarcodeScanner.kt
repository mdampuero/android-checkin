package cl.clickgroup.checkin.activities

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cl.clickgroup.checkin.R
import kotlinx.coroutines.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class BarcodeScanner : AppCompatActivity() {

    private var scannedData: StringBuilder = StringBuilder()
    private lateinit var scanLine: View
    private var scanJob: Job? = null // Variable para la coroutine de la animación

    override fun onResume() {
        super.onResume()
        // Reiniciar la animación cuando se vuelve a la actividad
        startScanAnimation()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_barcode_scanner)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }

        scanLine = findViewById(R.id.scanLine) // Asegúrate de inicializar el `scanLine`
        startScanAnimation()
    }

    private fun startScanAnimation() {
        // Crear la coroutine para la animación
        scanJob = CoroutineScope(Dispatchers.Main).launch {
            // Obtener la altura de la imagen del código de barras
            val barcodeImage = findViewById<ImageView>(R.id.barcodeImage)
            val imageHeight = barcodeImage.height

            // Asegúrate de que la línea de escaneo comience desde la parte superior de la imagen
            scanLine.translationY = -scanLine.height.toFloat() // Mover hacia arriba para que empiece desde arriba de la imagen

            // Bucle indefinido para mantener la animación corriendo
            while (isActive) {
                // Animación hacia abajo
                val downAnimator = ObjectAnimator.ofFloat(scanLine, "translationY", (-imageHeight.toFloat() / 2), (imageHeight.toFloat() / 2 ))
                downAnimator.duration = 1000 // Duración del escaneo hacia abajo (1 segundo)
                downAnimator.start()

                // Esperar a que termine la animación hacia abajo
                downAnimator.awaitCompletion()

                // Animación hacia arriba
                val upAnimator = ObjectAnimator.ofFloat(scanLine, "translationY", (imageHeight.toFloat() / 2), (-imageHeight.toFloat() / 2))
                upAnimator.duration = 1000 // Duración del escaneo hacia arriba (1 segundo)
                upAnimator.start()

                // Esperar a que termine la animación hacia arriba
                upAnimator.awaitCompletion()
            }
        }
    }

    // Extensión para esperar a que una animación termine
    private suspend fun ObjectAnimator.awaitCompletion() {
        suspendCoroutine<Unit> { continuation ->
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    continuation.resume(Unit)
                }
            })
        }
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN) {
            val char = event.unicodeChar.toChar()

            if (event.keyCode == KeyEvent.KEYCODE_TAB) {
                processScannedData()
                return true
            } else if (event.unicodeChar != 0) {
                scannedData.append(char)
            }
        }

        return super.dispatchKeyEvent(event)
    }

    private fun processScannedData() {
        val scannedCode = scannedData.toString().trim()
        if (scannedCode.isNotEmpty()) {
            Toast.makeText(this, "Código escaneado: $scannedCode", Toast.LENGTH_SHORT).show()
            Log.d("BarcodeReaderActivity", "Código escaneado: $scannedCode")
        }

        // Limpiar el buffer para la siguiente lectura
        scannedData.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Cancelar cualquier coroutine activa cuando la Activity sea destruida
        scanJob?.cancel()
    }
}
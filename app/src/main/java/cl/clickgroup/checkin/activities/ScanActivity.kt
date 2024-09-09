package cl.clickgroup.checkin.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.common.InputImage
import cl.clickgroup.checkin.R
import com.google.mlkit.vision.barcode.BarcodeScanner
import java.util.concurrent.Executors

class ScanActivity : AppCompatActivity() {

    private val executor = Executors.newSingleThreadExecutor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)

        // Cambiar la orientación a vertical en tiempo de ejecución
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        // Solicitar permisos y comenzar la cámara
        startCamera()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(findViewById<PreviewView>(R.id.previewView).surfaceProvider)
            }

            // Configurar opciones para leer PDF417 y QR Code
            val barcodeScannerOptions = BarcodeScannerOptions.Builder()
                .setBarcodeFormats(
                    Barcode.FORMAT_QR_CODE
                    //Barcode.FORMAT_PDF417
                )
                .build()

            val barcodeScanner = BarcodeScanning.getClient(barcodeScannerOptions)

            val imageAnalysis = ImageAnalysis.Builder().build().also {
                it.setAnalyzer(executor, { imageProxy ->
                    processImage(imageProxy, barcodeScanner)
                })
            }

            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build()

            cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis)

        }, ContextCompat.getMainExecutor(this))
    }

    private fun processImage(imageProxy: ImageProxy, barcodeScanner: BarcodeScanner) {
        val image = InputImage.fromMediaImage(imageProxy.image!!, imageProxy.imageInfo.rotationDegrees)

        barcodeScanner.process(image)
            .addOnSuccessListener { barcodes ->
                for (barcode in barcodes) {
                    val rawValue = barcode.rawValue
                    val resultIntent = Intent().apply {
                        putExtra("SCAN_RESULT", rawValue)
                    }
                    setResult(Activity.RESULT_OK, resultIntent)
                    finish()
                }
            }
            .addOnFailureListener { e ->
                Log.e("ScanActivity", "Error: ${e.message}")
                setResult(Activity.RESULT_CANCELED)
                finish()
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }
}
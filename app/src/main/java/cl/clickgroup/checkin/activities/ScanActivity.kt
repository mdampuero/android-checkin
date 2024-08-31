package cl.clickgroup.checkin.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cl.clickgroup.checkin.R
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult

class ScanActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)

        startQRCodeScanner()
    }

    private fun startQRCodeScanner() {
        val integrator = IntentIntegrator(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE, IntentIntegrator.PDF_417)
        integrator.setPrompt(getString(R.string.SCAN_TO_CODE_QR))
        integrator.setCameraId(0)
        integrator.setBeepEnabled(true)
        integrator.setOrientationLocked(true)
        integrator.setBarcodeImageEnabled(true)
        integrator.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result: IntentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents != null) {
                val resultIntent = Intent()
                resultIntent.putExtra("SCAN_RESULT", result.contents)
                setResult(Activity.RESULT_OK, resultIntent)
            } else {
                setResult(Activity.RESULT_CANCELED)
            }
        }
        finish()
        super.onActivityResult(requestCode, resultCode, data)
    }
}

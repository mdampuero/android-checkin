package cl.clickgroup.checkin.utils

import cl.clickgroup.checkin.R

import android.app.Dialog
import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

object DialogUtils {

    fun showCustomDialog(context: Context, type: String, message: String) {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_custom)

        val tvMessage = dialog.findViewById<TextView>(R.id.tvMessage)
        val btnOK = dialog.findViewById<Button>(R.id.BT_accept)
        val ivSuccess = dialog.findViewById<ImageView>(R.id.IV_success)
        val ivWarning = dialog.findViewById<ImageView>(R.id.IV_warning)
        val ivError = dialog.findViewById<ImageView>(R.id.IV_error)

        when (type) {
            "success" -> {
                ivSuccess.visibility = View.VISIBLE
                ivWarning.visibility = View.GONE
                ivError.visibility = View.GONE
            }
            "warning" -> {
                ivSuccess.visibility = View.GONE
                ivWarning.visibility = View.VISIBLE
                ivError.visibility = View.GONE
            }
            "error" -> {
                ivSuccess.visibility = View.GONE
                ivWarning.visibility = View.GONE
                ivError.visibility = View.VISIBLE
            }
            else -> {
                ivSuccess.visibility = View.GONE
                ivWarning.visibility = View.GONE
                ivError.visibility = View.GONE
            }
        }
        tvMessage.text = message

        btnOK.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}

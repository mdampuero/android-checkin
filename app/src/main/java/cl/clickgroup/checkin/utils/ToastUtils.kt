package cl.clickgroup.checkin.utils

import android.content.Context
import android.view.Gravity
import android.widget.Toast

object ToastUtils {

    fun showCenteredToast(context: Context, message: String) {
        val toast = Toast.makeText(context, message, Toast.LENGTH_LONG)
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.show()
    }
}
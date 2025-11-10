package cl.clickgroup.checkin.utils

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.IBinder
import android.os.ParcelFileDescriptor
import android.os.RemoteException
import android.util.Log
import com.sunmi.peripheral.printer.InnerPrinterCallback
import com.sunmi.peripheral.printer.InnerPrinterManager
import com.sunmi.peripheral.printer.InnerResultCallback
import com.sunmi.peripheral.printer.SunmiPrinterService
import woyou.aidlservice.jiuiv5.ICallback
import woyou.aidlservice.jiuiv5.IWoyouService
import java.io.File

object SunmiPrinterHelper {
    private const val TAG = "SunmiPrinterHelper"
    private var woyouService: IWoyouService? = null
    private var isBound = false

    private val callback = object : ICallback.Stub(){
        override fun onRunResult(isSuccess: Boolean) {
            Log.d(TAG, "Print run result: $isSuccess")
        }

        override fun onReturnString(result: String?) {
            Log.d(TAG, "Print return string: $result")
        }

        override fun onRaiseException(code: Int, msg: String?) {
            Log.e(TAG, "Print exception: $code - $msg")
        }

        override fun onPrintResult(code: Int, msg: String?) {
            TODO("Not yet implemented")
        }
    }

    private val connService = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            woyouService = IWoyouService.Stub.asInterface(p1)
            isBound = true
            Log.d(TAG, "Service connected")
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            woyouService = null
            isBound = false
            Log.d(TAG, "Service disconnected")
        }

    }

    fun initPrinterService(context: Context){
        if (!isBound){
            val intent = Intent()
            intent.setPackage("woyou.aidlservice.jiuiv5")
            intent.setAction("woyou.aidlservice.jiuiv5.IWoyouService")
            context.bindService(intent, connService, Context.BIND_AUTO_CREATE)
        }
    }

    fun releasePritnerService(context: Context){
        if (isBound){
            context.unbindService(connService)
            isBound = false
        }
    }

    fun printBitmap(bitmap: Bitmap, constext: Context){
        if (!isBound){
            initPrinterService(constext)
        }

        woyouService?.let { service ->
            try {
                service.printerInit(callback)
                service.printBitmap(bitmap, callback)
                service.lineWrap(3, callback)
                service.printerInit(callback)

            }catch (e: RemoteException){
                Log.e(TAG, "Error printing bitmap", e)
            }
        } ?: Log.e(TAG, "Service not bound")
    }

    fun getPrinterStatus(): Int {
        return woyouService?.updatePrinterState() ?: -1
    }
}
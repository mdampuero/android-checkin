package cl.clickgroup.checkin.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.core.content.FileProvider
import cl.clickgroup.checkin.data.repositories.PersonDB
import cl.clickgroup.checkin.fragments.PrintField
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileOutputStream
import android.print.PrintManager
import android.print.pdf.PrintedPdfDocument
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import cl.clickgroup.checkin.adapters.PdfDocumentAdapter

object PdfGeneratorUtils {
    private var useAlternateHeight = false

    private fun wrapText(text: String, maxLength: Int): List<String> {
        val lines = mutableListOf<String>()
        if (text.isBlank()) return lines

        val words = text.split(' ')
        var currentLine = StringBuilder()

        for (word in words) {
            if (currentLine.isNotEmpty() && currentLine.length + word.length + 1 > maxLength) {
                lines.add(currentLine.toString())
                currentLine = StringBuilder(word)
            } else {
                if (currentLine.isNotEmpty()) {
                    currentLine.append(' ')
                }
                currentLine.append(word)
            }
        }

        if (currentLine.isNotEmpty()) {
            lines.add(currentLine.toString())
        }
        return lines
    }

    fun generatePersonPdf(
        context: Context,
        person: PersonDB?,
        printFieldsJson: String,
        onSuccess: (uri: Uri) -> Unit,
        onError: (message: String) -> Unit
    ) {


        val gson = Gson()
        val type = object : TypeToken<List<PrintField>>() {}.type
        val printFields: List<PrintField> = gson.fromJson(printFieldsJson, type) ?: emptyList()

        // Dimensions for bitmap (adjusted for 58mm paper, ~384 pixels wide at 203dpi)
        val pageWidth = 384 // Adjust based on printer: 58mm paper, effective 48mm, 384 dots
        val pageHeight = if (useAlternateHeight) 204 else 203 // Dynamic height, can calculate based on content
        useAlternateHeight = !useAlternateHeight

        val bitmap = Bitmap.createBitmap(pageWidth, pageHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE) // White background

        val paint = Paint().apply {
            color = Color.BLACK
            textSize = 16f
            isAntiAlias = true
        }

        var yText = 22f
        val xText = 0f
        val xQr = pageWidth - 130f // QR on right, assuming qrSize=120
        val qrSize = 120

        // Mapeo de campos (same as before)
        val fieldMap = mapOf(
            "fullname" to "${person?.first_name} ${person?.last_name}".trim().uppercase(),
            "email" to person?.email?.trim()?.uppercase(),
            "c_4392417" to person?.rut?.trim()?.uppercase(),
            "company" to person?.company?.trim()?.uppercase(),
            "job_title" to person?.job_title?.trim()?.uppercase(),
            "external_id" to person?.external_id?.toString()?.trim()?.uppercase()
        )

        // Dibujar textos a la izquierda
        for (field in printFields) {
            if (field.field != "qr_code") {
                val value = fieldMap[field.field]
                if (!value.isNullOrBlank()) {
                    paint.textSize = when (field.style) {
                        "big" -> 30f
                        "small" -> 20f
                        else -> 25f
                    }
                    val lines = wrapText(value, 22)
                    for (line in lines) {
                        canvas.drawText(line, xText, yText, paint)
                        yText += paint.textSize
                    }

                    if (lines.isNotEmpty()) {
                        // After drawing all lines for a field, add a small margin before the next field
                        yText += 10f
                    }
                }
            }
        }

        // Dibujar QR a la derecha si corresponde
        val qrField = printFields.find { it.field == "qr_code" }
        val qrData = fieldMap["external_id"]

        if (qrField != null && !qrData.isNullOrBlank()) {
            try {
                val writer = com.google.zxing.qrcode.QRCodeWriter()
                val bitMatrix = writer.encode(
                    qrData,
                    com.google.zxing.BarcodeFormat.QR_CODE,
                    qrSize,
                    qrSize
                )

                val qrBitmap = Bitmap.createBitmap(qrSize, qrSize, Bitmap.Config.RGB_565)
                for (x in 0 until qrSize) {
                    for (y in 0 until qrSize) {
                        qrBitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                    }
                }

                val yQr = 30f  // alineado arriba a la derecha
                canvas.drawBitmap(qrBitmap, xQr, yQr, null)

            } catch (e: Exception) {
                e.printStackTrace()
                onError("Error generating QR code")
                return
            }
        }

        // Now, print the bitmap using Sunmi
        SunmiPrinterHelper.printBitmap(bitmap, context)

        // If you still need a URI for some reason, save the bitmap to file and provide URI, but since we're printing directly, maybe not needed
        // onSuccess(someUri) // Optional


        /*val gson = Gson()
        val type = object : TypeToken<List<PrintField>>() {}.type
        val printFields: List<PrintField> = gson.fromJson(printFieldsJson, type) ?: emptyList()

        // Tamaño de la página
        val pageWidth = 300
        val pageHeight = 400  // fijo o ajustable si lo preferís
        val pdfDocument = PdfDocument()
        //val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        val pageInfo = PdfDocument.PageInfo.Builder(300, 180, 1).create()

        val page = pdfDocument.startPage(pageInfo)

        val canvas = page.canvas
        val paint = Paint().apply {
            color = Color.BLACK
            textSize = 16f
        }

        var yText = 30f
        val xText = 10f
        val xQr = 170f
        val qrSize = 120

        // Mapeo de campos
        val fieldMap = mapOf(
            "fullname" to "${person?.first_name} ${person?.last_name}".trim(),
            "email" to person?.email?.trim(),
            "c_4392417" to person?.rut?.trim(),
            "company" to person?.company?.trim(),
            "job_title" to person?.job_title?.trim(),
            "external_id" to person?.external_id?.toString()?.trim()
        )

        // Dibujar textos a la izquierda
        for (field in printFields) {
            if (field.field != "qr_code") {
                val value = fieldMap[field.field]
                if (!value.isNullOrBlank()) {
                    paint.textSize = when (field.style) {
                        "big" -> 20f
                        "small" -> 12f
                        else -> 16f
                    }
                    canvas.drawText(value, xText, yText, paint)
                    yText += paint.textSize + 10f
                }
            }
        }

        // Dibujar QR a la derecha si corresponde
        val qrField = printFields.find { it.field == "qr_code" }
        val qrData = fieldMap["external_id"]

        if (qrField != null && !qrData.isNullOrBlank()) {
            try {
                val writer = com.google.zxing.qrcode.QRCodeWriter()
                val bitMatrix = writer.encode(
                    qrData,
                    com.google.zxing.BarcodeFormat.QR_CODE,
                    qrSize,
                    qrSize
                )

                val bmp = Bitmap.createBitmap(qrSize, qrSize, Bitmap.Config.RGB_565)
                for (x in 0 until qrSize) {
                    for (y in 0 until qrSize) {
                        bmp.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                    }
                }

                val yQr = 30f  // alineado arriba a la derecha
                canvas.drawBitmap(bmp, xQr, yQr, null)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        pdfDocument.finishPage(page)

        try {
            val file = File(context.getExternalFilesDir(null), "detalle_persona_${person?.id}.pdf")
            FileOutputStream(file).use { outputStream ->
                pdfDocument.writeTo(outputStream)
            }
            pdfDocument.close()

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )
            //onSuccess(uri)
            //SE COMENTA PARA TEST INTEGRACION SUNMI
            val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
            val printAdapter = PdfDocumentAdapter(context, file.path)
            printManager.print("Comprobante", printAdapter, null)


        } catch (e: Exception) {
            e.printStackTrace()
            pdfDocument.close()
            onError("Error al generar el PDF")
        }*/
    }
}

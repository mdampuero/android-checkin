package cl.clickgroup.checkin.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import cl.clickgroup.checkin.R
import cl.clickgroup.checkin.data.repositories.PersonRepository
import cl.clickgroup.checkin.utils.CheckInUtils
import cl.clickgroup.checkin.utils.SharedPreferencesUtils

class DetailFragment : Fragment() {

    private var personId: Int? = null
    private lateinit var personRepository: PersonRepository
    private var checkInBySearch : Boolean = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el layout para este fragmento
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkInBySearch = SharedPreferencesUtils.getDataBoolean(requireContext(), "extraOption")
        arguments?.let {
            personId = it.getInt("person_id")
            loadPersonDetails(personId!!)
        }

        val btnBack = view.findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

    }

    private fun loadPersonDetails(personId: Int) {
        val view = requireView()
        personRepository = PersonRepository(requireContext())
        val person = personRepository.getPersonById(personId)
        val tvFullName = view.findViewById<TextView>(R.id.TV_fullName_value)
        val tvEmail = view.findViewById<TextView>(R.id.TV_email_value)
        val tvDocument = view.findViewById<TextView>(R.id.ET_document_value)
        val tvExternalId = view.findViewById<TextView>(R.id.ET_phone_value)

        val ivCheckIn: ImageView = view.findViewById(R.id.IV_checkin)
        val ivCheckInLocal: ImageView = view.findViewById(R.id.IV_checkinLocal)
        val ivNoCheckIn: ImageView = view.findViewById(R.id.IV_noCheckin)

        val btCheckIn: Button = view.findViewById(R.id.BT_checkIn)
        val btPdf: Button = view.findViewById(R.id.BT_pdf)

        if (person != null) {
            tvFullName.text = "${person.first_name} ${person.last_name}"
            tvEmail.text = "${person.email}"
            tvDocument.text = "${person.rut}"
            tvExternalId.text = "${person.external_id}"
            if(!person.scanned.isNullOrEmpty()){
                if(person.scanned == "SERVER"){
                    ivCheckIn.visibility = View.VISIBLE
                    ivCheckInLocal.visibility = View.GONE
                    ivNoCheckIn.visibility = View.GONE
                    btCheckIn.visibility = View.GONE
                }else{
                    ivCheckInLocal.visibility = View.VISIBLE
                    ivCheckIn.visibility = View.GONE
                    ivNoCheckIn.visibility = View.GONE
                    btCheckIn.visibility = View.GONE
                }
            }else{
                ivNoCheckIn.visibility = View.VISIBLE
                ivCheckInLocal.visibility = View.GONE
                ivCheckIn.visibility = View.GONE
                if(checkInBySearch){
                    btCheckIn.visibility = View.VISIBLE
                }else{
                    btCheckIn.visibility = View.GONE
                }
                Log.d("DetailFragment", checkInBySearch.toString())
            }
            btCheckIn.setOnClickListener {
                CheckInUtils.checkInByRut(requireContext(), person.rut)
                requireActivity().supportFragmentManager.popBackStack()
            }

            btPdf.setOnClickListener {
                val pdfDocument = android.graphics.pdf.PdfDocument()
                val pageInfo = android.graphics.pdf.PdfDocument.PageInfo.Builder(300, 600, 1).create()
                val page = pdfDocument.startPage(pageInfo)
                val canvas = page.canvas
                val paint = android.graphics.Paint()
                paint.color = android.graphics.Color.BLACK
                paint.textSize = 12f

                var y = 25f
                canvas.drawText("Ficha de Persona", 10f, y, paint)
                y += 20f
                canvas.drawText("Nombre: ${person.first_name} ${person.last_name}", 10f, y, paint)
                y += 20f
                canvas.drawText("Email: ${person.email}", 10f, y, paint)
                y += 20f
                canvas.drawText("Documento: ${person.rut}", 10f, y, paint)
                y += 20f
                canvas.drawText("ID Externo: ${person.external_id}", 10f, y, paint)

                pdfDocument.finishPage(page)

                try {
                    val file = java.io.File(
                        requireContext().getExternalFilesDir(null),
                        "detalle_persona_${person.id}.pdf"
                    )
                    val outputStream = java.io.FileOutputStream(file)
                    pdfDocument.writeTo(outputStream)
                    outputStream.close()
                    pdfDocument.close()

                    //android.widget.Toast.makeText(requireContext(), "PDF guardado en ${file.absolutePath}", android.widget.Toast.LENGTH_LONG).show()

                    // Abrir el PDF con FileProvider
                    val uri = androidx.core.content.FileProvider.getUriForFile(
                        requireContext(),
                        "${requireContext().packageName}.provider",
                        file
                    )

                    val intent = android.content.Intent(android.content.Intent.ACTION_VIEW).apply {
                        setDataAndType(uri, "application/pdf")
                        flags = android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION or android.content.Intent.FLAG_ACTIVITY_NO_HISTORY
                    }

                    try {
                        startActivity(intent)
                    } catch (e: android.content.ActivityNotFoundException) {
                        android.widget.Toast.makeText(requireContext(), "No hay ninguna aplicación para abrir PDF", android.widget.Toast.LENGTH_SHORT).show()
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                    android.widget.Toast.makeText(requireContext(), "Error al generar el PDF", android.widget.Toast.LENGTH_SHORT).show()
                    pdfDocument.close()
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(personId: Int) = DetailFragment().apply {
            arguments = Bundle().apply {
                putInt("person_id", personId)
            }
        }
    }
}

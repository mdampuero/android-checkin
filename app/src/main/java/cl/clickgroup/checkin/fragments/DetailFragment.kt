package cl.clickgroup.checkin.fragments

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import cl.clickgroup.checkin.R
import cl.clickgroup.checkin.data.repositories.PersonRepository
import cl.clickgroup.checkin.utils.CheckInUtils
import cl.clickgroup.checkin.utils.PdfGeneratorUtils
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
        val print = SharedPreferencesUtils.getDataBoolean(requireContext(), "print")
        personRepository = PersonRepository(requireContext())
        val person = personRepository.getPersonById(personId)
        val tvFullName = view.findViewById<TextView>(R.id.TV_fullName_value)
        val tvEmail = view.findViewById<TextView>(R.id.TV_email_value)
        val tvDocument = view.findViewById<TextView>(R.id.ET_document_value)
        val tvExternalId = view.findViewById<TextView>(R.id.ET_externalId_value)
        val tvCompany = view.findViewById<TextView>(R.id.ET_company_value)
        val tvJobTitle = view.findViewById<TextView>(R.id.ET_job_title_value)

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
            tvCompany.text = "${person.company}"
            tvJobTitle.text = "${person.job_title}"
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
            if(print && btCheckIn.visibility != View.VISIBLE){
                btPdf.visibility = View.VISIBLE
            }
            btPdf.setOnClickListener {
                val json = SharedPreferencesUtils.getData(requireContext(), "print_fields")
                PdfGeneratorUtils.generatePersonPdf(
                    context = requireContext(),
                    person = person,
                    printFieldsJson = json.toString(),
                    onSuccess = { uri ->
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            setDataAndType(uri, "application/pdf")
                            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NO_HISTORY
                        }

                        try {
                            startActivity(intent)
                        } catch (e: ActivityNotFoundException) {
                            Toast.makeText(requireContext(), "No hay ninguna aplicación para abrir PDF", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onError = { message ->
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    }
                )
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

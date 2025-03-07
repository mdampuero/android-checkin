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

package cl.clickgroup.checkin.fragments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import retrofit2.Call
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cl.clickgroup.checkin.R
import cl.clickgroup.checkin.activities.FormPerson
import cl.clickgroup.checkin.adapters.PersonAdapter
import cl.clickgroup.checkin.data.repositories.PersonDB
import cl.clickgroup.checkin.data.repositories.PersonRepository
import cl.clickgroup.checkin.network.RetrofitClient
import cl.clickgroup.checkin.network.requests.CheckInByRegistrantIDsRequest
import cl.clickgroup.checkin.network.responses.IntegrationsRegistrantsResponse
import cl.clickgroup.checkin.network.responses.Person
import cl.clickgroup.checkin.utils.SharedPreferencesUtils
import cl.clickgroup.checkin.utils.ToastUtils
import retrofit2.Response


class CheckInFragment : Fragment() {

    private lateinit var progressBar: ProgressBar
    private lateinit var progressText: TextView
    private lateinit var personRepository: PersonRepository
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PersonAdapter
    private lateinit var searchInput: EditText
    private var allPersons: List<PersonDB> = emptyList()
    private var needSync: Boolean = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_checkin, container, false)
        val btRefresh = view.findViewById<ImageView>(R.id.BT_refresh)
        val btAdd = view.findViewById<ImageView>(R.id.BT_add)

        progressBar = view.findViewById(R.id.progressBar)
        progressText = view.findViewById(R.id.progressText)
        searchInput = view.findViewById(R.id.searchInput)

        // Init el RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        arguments?.let {
            needSync = it.getBoolean("needSync", false)
        }

        val integration_type = SharedPreferencesUtils.getData(requireContext(), "integration_type")
        if(integration_type == "REGISTER") {
            btAdd.visibility = View.GONE
        }

        btAdd.setOnClickListener {
            val intent = Intent(requireActivity(), FormPerson::class.java)
            startActivity(intent)
            /*val container = view?.findViewById<FrameLayout>(R.id.container)

            container?.let {
                val inflater = layoutInflater
                val newLayout = inflater.inflate(R.layout.fragment_add_person, container, false)

                container.removeAllViews()
                container.addView(newLayout)

                val inputFirstName = newLayout.findViewById<EditText>(R.id.input_first_name)
                val inputLastName = newLayout.findViewById<EditText>(R.id.input_last_name)
                val inputEmail = newLayout.findViewById<EditText>(R.id.input_email)
                val btnSave = newLayout.findViewById<Button>(R.id.btn_save)

                btnSave.setOnClickListener {
                    val firstName = inputFirstName.text.toString()
                    val lastName = inputLastName.text.toString()
                    val email = inputEmail.text.toString()

                   // if (firstName.isNotEmpty() && lastName.isNotEmpty() && email.isNotEmpty()) {
                        /*val newPerson = PersonDB(
                            first_name = firstName,
                            last_name = lastName,
                            email = email,
                            external_id = 0,  // ejemplo
                            rut = "some_rut",
                            scanned = ""
                        )
                        personRepository.insertPerson(newPerson)*/

                        showList()

                        parentFragmentManager.popBackStack()
                  /*  } else {
                        ToastUtils.showCenteredToast(requireContext(), "Por favor, completa todos los campos")
                    }*/
                }
            }*/
        }
        btRefresh.setOnClickListener {
            fetchData()
        }

        init()

        showList()

        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterList(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        return view
    }

    private fun init() {
        personRepository = PersonRepository(requireContext())
        if(needSync){
            fetchData()
        }
    }

    fun fetchData() {
        progressBar.visibility = View.VISIBLE
        progressText.visibility = View.VISIBLE
        val integrationId = SharedPreferencesUtils.getData(requireContext(), "integration_id")
        val sessionID = SharedPreferencesUtils.getData(requireContext(), "session_id")
        val registrantIDs = personRepository.getAllExternalIdsWhereScannedIsApp()
        val results = personRepository.getAllExternalIdsAndRequestScannedIsApp()
        val call = RetrofitClient.apiService.getRegistrant(integrationId.toString(),sessionID.toString(), CheckInByRegistrantIDsRequest(registrantIDs, results))
        call.enqueue(object : retrofit2.Callback<IntegrationsRegistrantsResponse> {
            override fun onResponse(
                call: Call<IntegrationsRegistrantsResponse>,
                response: Response<IntegrationsRegistrantsResponse>
            ) {
                progressBar.visibility = View.GONE
                progressText.visibility = View.GONE
                try {
                    if (!response.isSuccessful) {
                        Log.d("CheckInFragment", "${getString(R.string.SERVICE_RESPONSE_FAIL)}")
                        ToastUtils.showCenteredToast(
                            requireContext(),
                            requireContext().getString(R.string.SERVICE_RESPONSE_FAIL)
                        )
                    } else {
                        syncByServer(response.body()?.data ?: emptyList())
                    }
                } catch (e: Exception) {
                    Log.d("CheckInFragment", "${getString(R.string.SYNC_FAIL)} : ${e.message}")
                    ToastUtils.showCenteredToast(
                        requireContext(),
                        requireContext().getString(R.string.SYNC_FAIL)
                    )
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<IntegrationsRegistrantsResponse>, t: Throwable) {
                progressBar.visibility = View.GONE
                progressText.visibility = View.GONE
                t.printStackTrace()
                Log.d("CheckInFragment", "${getString(R.string.SERVICE_FAIL)}: ${t.message}")
                ToastUtils.showCenteredToast(
                    requireContext(),
                    requireContext().getString(R.string.SERVICE_FAIL)
                )
            }
        })
    }


    private fun syncByServer(persons: List<Person>) {
        try {
            val existingPersons = personRepository.getAllPersons()
            val existingPersonIds = existingPersons.map { it.external_id }.toSet()
            for (personApi in persons) {
                if (personApi.id !in existingPersonIds) {
                    val personDB = PersonDB(
                        first_name = personApi.first_name,
                        last_name = personApi.last_name,
                        email = personApi.email,
                        external_id = personApi.id,
                        rut = personApi.rut,
                        scanned = personApi.scanned,
                        request_value = "",
                        company = personApi.company,
                        job_title = personApi.job_title
                    )
                    personRepository.insertPerson(personDB)
                }else{
                    /**
                     * Update if checkin in SERVER
                      */
                    val personDB = personRepository.getPersonByExternalId(personApi.id)
                    if (personDB != null) {
                        if (personApi.scanned != personDB.scanned) {
                            val personDBUpdate = PersonDB(
                                id = personDB?.id ?: 0,
                                first_name = personApi.first_name,
                                last_name = personApi.last_name,
                                email = personApi.email,
                                external_id = personApi.id,
                                rut = personApi.rut,
                                scanned = personApi.scanned,
                                request_value = personDB.request_value,
                                company = personDB.company,
                                job_title = personDB.job_title
                            )
                            personRepository.updatePerson(personDBUpdate)
                        }
                    }
                }
            }
            showList()
            Log.d("CheckInFragment", getString(R.string.SYNC_SUCCESS))
            ToastUtils.showCenteredToast(requireContext(), getString(R.string.SYNC_SUCCESS))

        } catch (e: Exception) {
            Log.d("CheckInFragment", getString(R.string.SYNC_FAIL))
            ToastUtils.showCenteredToast(requireContext(), getString(R.string.SYNC_FAIL))
            e.printStackTrace()
        }
    }

    private fun showList() {
        allPersons = personRepository.getAllPersons()
        adapter = PersonAdapter(allPersons)
        recyclerView.adapter = adapter
    }

    private fun filterList(query: String) {
        val filteredList = allPersons.filter { person ->
            person.first_name.contains(query, ignoreCase = true) ||
                    person.last_name.contains(query, ignoreCase = true) ||
                    person.external_id.toString().contains(query, ignoreCase = true) ||
                    person.rut.contains(query, ignoreCase = true)
        }
        adapter.updateList(filteredList)
    }
}
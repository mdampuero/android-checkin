package cl.clickgroup.checkin.fragments

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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cl.clickgroup.checkin.R
import cl.clickgroup.checkin.adapters.PersonAdapter
import cl.clickgroup.checkin.data.repositories.PersonDB
import cl.clickgroup.checkin.data.repositories.PersonRepository
import cl.clickgroup.checkin.network.RetrofitClient
import cl.clickgroup.checkin.network.requests.CheckInByRegistrantIDsRequest
import cl.clickgroup.checkin.network.requests.CheckInByRutRequest
import cl.clickgroup.checkin.network.responses.CheckInByRegistrantIDsResponse
import cl.clickgroup.checkin.network.responses.CheckInByRutResponse
import cl.clickgroup.checkin.network.responses.IntegrationsRegistrantsResponse
import cl.clickgroup.checkin.network.responses.Person
import cl.clickgroup.checkin.utils.SharedPreferencesUtils
import cl.clickgroup.checkin.utils.ToastUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
        val btSearch = view.findViewById<ImageView>(R.id.BT_refresh)

        progressBar = view.findViewById(R.id.progressBar)
        progressText = view.findViewById(R.id.progressText)
        searchInput = view.findViewById(R.id.searchInput)

        // Init el RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        arguments?.let {
            needSync = it.getBoolean("needSync", false)
        }

        btSearch.setOnClickListener {
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

    private fun fetchData() {
        progressBar.visibility = View.VISIBLE
        progressText.visibility = View.VISIBLE
        val integrationId = SharedPreferencesUtils.getData(requireContext(), "integration_id")
        val sessionID = SharedPreferencesUtils.getData(requireContext(), "session_id")
        val registrantIDs = personRepository.getAllExternalIdsWhereScannedIsApp()
        val call = RetrofitClient.apiService.getRegistrant(integrationId.toString(),sessionID.toString(), CheckInByRegistrantIDsRequest(registrantIDs))
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
                        scanned = personApi.scanned
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
                                scanned = personApi.scanned
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
                    person.rut.contains(query, ignoreCase = true)
        }
        adapter.updateList(filteredList)
    }
}
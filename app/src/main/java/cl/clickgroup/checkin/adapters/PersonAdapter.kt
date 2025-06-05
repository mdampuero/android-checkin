package cl.clickgroup.checkin.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import cl.clickgroup.checkin.R
import cl.clickgroup.checkin.data.repositories.PersonDB
import cl.clickgroup.checkin.fragments.DetailFragment

class PersonAdapter(private var personList: List<PersonDB>) :

    RecyclerView.Adapter<PersonAdapter.PersonViewHolder>() {

    class PersonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.TV_fullName)
        val tvRut: TextView = itemView.findViewById(R.id.TV_rut)
        val ivCheckIn: ImageView = itemView.findViewById(R.id.IV_checkin)
        val ivCheckInLocal: ImageView = itemView.findViewById(R.id.IV_checkinLocal)
        val ivNoCheckIn: ImageView = itemView.findViewById(R.id.IV_noCheckin)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_person, parent, false)
        return PersonViewHolder(view)
    }

    override fun onBindViewHolder(holder: PersonViewHolder, job_title: Int) {
        val person = personList[job_title]
        holder.tvName.text = "${person.first_name} ${person.last_name}"
        holder.tvRut.text = "RUT: ${person.rut} - ID: ${person.external_id}"
        if(!person.scanned.isNullOrEmpty()){
            if(person.scanned == "SERVER"){
                holder.ivCheckIn.visibility = View.VISIBLE
                holder.ivCheckInLocal.visibility = View.GONE
                holder.ivNoCheckIn.visibility = View.GONE
            }else{
                holder.ivCheckInLocal.visibility = View.VISIBLE
                holder.ivCheckIn.visibility = View.GONE
                holder.ivNoCheckIn.visibility = View.GONE
            }
        }else{
            holder.ivNoCheckIn.visibility = View.VISIBLE
            holder.ivCheckInLocal.visibility = View.GONE
            holder.ivCheckIn.visibility = View.GONE
        }
        holder.itemView.setOnClickListener {
            val detailFragment = DetailFragment.newInstance(person.id)

            val fragmentTransaction = (holder.itemView.context as AppCompatActivity).supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragment_container, detailFragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }
    }

    fun updateList(newList: List<PersonDB>) {
        personList = newList
        notifyDataSetChanged()
    }
    override fun getItemCount() = personList.size
}

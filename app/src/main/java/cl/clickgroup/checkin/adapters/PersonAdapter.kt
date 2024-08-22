package cl.clickgroup.checkin.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cl.clickgroup.checkin.R
import cl.clickgroup.checkin.data.repositories.PersonDB

class PersonAdapter(private var personList: List<PersonDB>) :
    RecyclerView.Adapter<PersonAdapter.PersonViewHolder>() {

    class PersonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.TV_fullName)
        val tvRut: TextView = itemView.findViewById(R.id.TV_rut)
        val ivCheckIn: ImageView = itemView.findViewById(R.id.IV_checkin)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_person, parent, false)
        return PersonViewHolder(view)
    }

    override fun onBindViewHolder(holder: PersonViewHolder, position: Int) {
        val person = personList[position]
        holder.tvName.text = "${person.first_name} ${person.last_name}"
        holder.tvRut.text = person.rut
        if(!person.scanned.isNullOrEmpty()){
            holder.ivCheckIn.visibility = View.VISIBLE
        }else{
            holder.ivCheckIn.visibility = View.GONE
        }
    }

    fun updateList(newList: List<PersonDB>) {
        personList = newList
        notifyDataSetChanged()
    }
    override fun getItemCount() = personList.size
}

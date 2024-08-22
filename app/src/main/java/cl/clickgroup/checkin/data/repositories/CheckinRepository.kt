package cl.clickgroup.checkin.data.repositories

import android.content.ContentValues
import android.content.Context
import cl.clickgroup.checkin.data.DatabaseHelper

data class Checkin(val id: Int = 0, val personId: Int, val checkinTime: String)

class CheckinRepository(context: Context) {

    private val dbHelper = DatabaseHelper(context)

    fun insertCheckin(checkin: Checkin): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("personId", checkin.personId)
            put("checkinTime", checkin.checkinTime)
        }
        return db.insert("checkins", null, values)
    }

    fun getAllCheckins(): List<Checkin> {
        val db = dbHelper.readableDatabase
        val cursor = db.query("checkins", null, null, null, null, null, null)
        val checkins = mutableListOf<Checkin>()

        with(cursor) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow("id"))
                val personId = getInt(getColumnIndexOrThrow("personId"))
                val checkinTime = getString(getColumnIndexOrThrow("checkinTime"))
                checkins.add(Checkin(id, personId, checkinTime))
            }
        }

        cursor.close()
        return checkins
    }

    fun getCheckinsByPersonId(personaId: Int): List<Checkin> {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            "checkins",
            null,
            "personId = ?",
            arrayOf(personaId.toString()),
            null,
            null,
            null
        )
        val checkins = mutableListOf<Checkin>()

        with(cursor) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow("id"))
                val checkinTime = getString(getColumnIndexOrThrow("checkinTime"))
                checkins.add(Checkin(id, personaId, checkinTime))
            }
        }

        cursor.close()
        return checkins
    }

    fun deleteCheckinById(id: Int) {
        val db = dbHelper.writableDatabase
        db.delete("checkins", "id = ?", arrayOf(id.toString()))
    }
}
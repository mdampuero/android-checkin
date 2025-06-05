package cl.clickgroup.checkin.data.repositories

import android.content.ContentValues
import android.content.Context
import cl.clickgroup.checkin.data.DatabaseHelper
import cl.clickgroup.checkin.network.requests.ExternalIdWithRequestValue

data class PersonDB(
    val id: Int = 0,
    val first_name: String,
    val last_name: String,
    val email: String,
    val external_id: Int,
    val rut: String,
    val scanned: String?,
    var request_value: String?,
    val company: String?,
    val job_title: String?
)

class PersonRepository(context: Context) {

    private val dbHelper = DatabaseHelper(context)

    fun insertPerson(person: PersonDB): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("first_name", person.first_name)
            put("last_name", person.last_name)
            put("email", person.email)
            put("external_id", person.external_id)
            put("rut", person.rut.uppercase())
            put("scanned", person.scanned)
            put("request_value", person.request_value)
            put("company", person.company)
            put("job_title", person.job_title)
        }
        return db.insert("persons", null, values)
    }

    fun updatePerson(person: PersonDB): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("first_name", person.first_name)
            put("last_name", person.last_name)
            put("email", person.email)
            put("external_id", person.external_id)
            put("rut", person.rut.uppercase())
            put("scanned", person.scanned)
            put("request_value", person.request_value)
            put("company", person.company)
            put("job_title", person.job_title)
        }
        return db.update("persons", values, "id = ?", arrayOf(person.id.toString()))
    }

    private fun fromCursor(cursor: android.database.Cursor): PersonDB {
        return PersonDB(
            id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
            first_name = cursor.getString(cursor.getColumnIndexOrThrow("first_name")),
            last_name = cursor.getString(cursor.getColumnIndexOrThrow("last_name")),
            email = cursor.getString(cursor.getColumnIndexOrThrow("email")),
            external_id = cursor.getInt(cursor.getColumnIndexOrThrow("external_id")),
            rut = cursor.getString(cursor.getColumnIndexOrThrow("rut")),
            scanned = cursor.getString(cursor.getColumnIndexOrThrow("scanned")),
            request_value = cursor.getString(cursor.getColumnIndexOrThrow("request_value")),
            company = cursor.getString(cursor.getColumnIndexOrThrow("company")),
            job_title = cursor.getString(cursor.getColumnIndexOrThrow("job_title"))
        )
    }

    fun getPersonByRut(rut: String): PersonDB? {
        val db = dbHelper.readableDatabase
        val cursor = db.query("persons", null, "rut = ?", arrayOf(rut), null, null, null)
        val person = if (cursor.moveToFirst()) fromCursor(cursor) else null
        cursor.close()
        return person
    }

    fun getPersonByExternalID(external_id: Int): PersonDB? {
        val db = dbHelper.readableDatabase
        val cursor = db.query("persons", null, "external_id = ?", arrayOf(external_id.toString()), null, null, null)
        val person = if (cursor.moveToFirst()) fromCursor(cursor) else null
        cursor.close()
        return person
    }

    fun getPersonById(id: Int): PersonDB? {
        val db = dbHelper.readableDatabase
        val cursor = db.query("persons", null, "id = ?", arrayOf(id.toString()), null, null, null)
        val person = if (cursor.moveToFirst()) fromCursor(cursor) else null
        cursor.close()
        return person
    }

    fun updateScannedFieldByRut(rut: String, scanned: String): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("scanned", scanned)
        }
        return db.update("persons", values, "rut = ?", arrayOf(rut))
    }

    fun updateResponseValue(id: Int, requestValue: String): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("request_value", requestValue)
        }
        return db.update("persons", values, "id = ?", arrayOf(id.toString()))
    }

    fun getAllPersons(): List<PersonDB> {
        val db = dbHelper.readableDatabase
        val cursor = db.query("persons", null, null, null, null, null, null)
        val persons = mutableListOf<PersonDB>()
        with(cursor) {
            while (moveToNext()) {
                persons.add(fromCursor(this))
            }
        }
        cursor.close()
        return persons
    }

    fun getAllExternalIdsAndRequestScannedIsApp(): List<ExternalIdWithRequestValue> {
        val db = dbHelper.readableDatabase
        val results = mutableListOf<ExternalIdWithRequestValue>()
        val cursor = db.query(
            "persons",
            arrayOf("external_id", "request_value"),
            "scanned = ?",
            arrayOf("APP"),
            null,
            null,
            null
        )
        cursor?.use {
            while (it.moveToNext()) {
                val externalId = it.getInt(it.getColumnIndexOrThrow("external_id"))
                val requestValue = it.getString(it.getColumnIndexOrThrow("request_value"))
                results.add(ExternalIdWithRequestValue(externalId, requestValue))
            }
        }
        return results
    }

    fun getAllExternalIdsWhereScannedIsApp(): List<Int> {
        val db = dbHelper.readableDatabase
        val cursor = db.query("persons", arrayOf("external_id"), "scanned = ?", arrayOf("APP"), null, null, null)
        val externalIds = mutableListOf<Int>()
        with(cursor) {
            while (moveToNext()) {
                externalIds.add(getInt(getColumnIndexOrThrow("external_id")))
            }
        }
        cursor.close()
        return externalIds
    }

    fun truncatePersonsTable() {
        val db = dbHelper.writableDatabase
        db.execSQL("DELETE FROM persons")
        db.execSQL("DELETE FROM sqlite_sequence WHERE name = 'persons'")
    }

    fun getPersonByExternalId(externalId: Int): PersonDB? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            "persons", null, "external_id = ?", arrayOf(externalId.toString()), null, null, null
        )
        return if (cursor.moveToFirst()) {
            val person = fromCursor(cursor)
            cursor.close()
            person
        } else {
            cursor.close()
            null
        }
    }
}

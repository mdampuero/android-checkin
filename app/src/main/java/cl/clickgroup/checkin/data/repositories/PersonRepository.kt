package cl.clickgroup.checkin.data.repositories

import android.content.ContentValues
import cl.clickgroup.checkin.data.DatabaseHelper
import android.content.Context
import cl.clickgroup.checkin.network.responses.Person

data class PersonDB(
    val id: Int = 0,
    val first_name: String,
    val last_name: String,
    val email: String,
    val external_id: Int,
    val rut: String,
    val scanned: String?,
    val request_value: String?,
    )

class PersonRepository(context: Context) {

    private val dbHelper = DatabaseHelper(context)

    public fun insertPerson(person: PersonDB): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("first_name", person.first_name)
            put("last_name", person.last_name)
            put("email", person.email)
            put("external_id", person.external_id)
            put("rut", person.rut.uppercase())
            put("scanned", person.scanned)
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
        }
        return db.update(
            "persons",
            values,
            "id = ?", // Selección basada en el ID de la base de datos
            arrayOf(person.id.toString())
        )
    }

    fun getPersonByRut(rut: String): PersonDB? {
        val db = dbHelper.readableDatabase
        var person: PersonDB? = null
        val cursor = db.query(
            "persons",
            null,
            "rut = ?",
            arrayOf(rut),
            null,
            null,
            null
        )

        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            val firstName = cursor.getString(cursor.getColumnIndexOrThrow("first_name"))
            val lastName = cursor.getString(cursor.getColumnIndexOrThrow("last_name"))
            val email = cursor.getString(cursor.getColumnIndexOrThrow("email"))
            val externalId = cursor.getInt(cursor.getColumnIndexOrThrow("external_id"))
            val scanned = cursor.getString(cursor.getColumnIndexOrThrow("scanned"))
            val request_value = cursor.getString(cursor.getColumnIndexOrThrow("request_value"))

            person = PersonDB(
                id = id,
                first_name = firstName,
                last_name = lastName,
                email = email,
                external_id = externalId,
                rut = rut,
                scanned = scanned,
                request_value = request_value
            )
        }
        cursor.close()
        return person
    }

    fun getPersonByExternalID(external_id: Int): PersonDB? {
        val db = dbHelper.readableDatabase
        var person: PersonDB? = null
        val cursor = db.query(
            "persons",
            null,
            "external_id = ?",
            arrayOf(external_id.toString()),
            null,
            null,
            null
        )

        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            val firstName = cursor.getString(cursor.getColumnIndexOrThrow("first_name"))
            val lastName = cursor.getString(cursor.getColumnIndexOrThrow("last_name"))
            val email = cursor.getString(cursor.getColumnIndexOrThrow("email"))
            val rut = cursor.getString(cursor.getColumnIndexOrThrow("rut"))
            val scanned = cursor.getString(cursor.getColumnIndexOrThrow("scanned"))
            val request_value = cursor.getString(cursor.getColumnIndexOrThrow("request_value"))

            person = PersonDB(
                id = id,
                first_name = firstName,
                last_name = lastName,
                email = email,
                external_id = external_id,
                rut = rut,
                scanned = scanned,
                request_value = request_value
            )
        }
        cursor.close()
        return person
    }

    fun getPersonById(id: Int): PersonDB? {
        val db = dbHelper.readableDatabase
        var person: PersonDB? = null
        val cursor = db.query(
            "persons",
            null,
            "id = ?", // Condición para buscar por ID
            arrayOf(id.toString()), // Convertimos el ID a String
            null,
            null,
            null
        )

        if (cursor.moveToFirst()) {
            val personId = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            val firstName = cursor.getString(cursor.getColumnIndexOrThrow("first_name"))
            val lastName = cursor.getString(cursor.getColumnIndexOrThrow("last_name"))
            val email = cursor.getString(cursor.getColumnIndexOrThrow("email"))
            val externalId = cursor.getInt(cursor.getColumnIndexOrThrow("external_id"))
            val rut = cursor.getString(cursor.getColumnIndexOrThrow("rut"))
            val scanned = cursor.getString(cursor.getColumnIndexOrThrow("scanned"))
            val request_value = cursor.getString(cursor.getColumnIndexOrThrow("request_value"))

            person = PersonDB(
                id = personId,
                first_name = firstName,
                last_name = lastName,
                email = email,
                external_id = externalId,
                rut = rut,
                scanned = scanned,
                request_value = request_value
            )
        }
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
                val id = getInt(getColumnIndexOrThrow("id"))
                val first_name = getString(getColumnIndexOrThrow("first_name"))
                val last_name = getString(getColumnIndexOrThrow("last_name"))
                val email = getString(getColumnIndexOrThrow("email"))
                val external_id = getInt(getColumnIndexOrThrow("external_id"))
                val rut = getString(getColumnIndexOrThrow("rut"))
                val scanned = getString(getColumnIndexOrThrow("scanned"))
                val request_value = getString(getColumnIndexOrThrow("request_value"))
                persons.add(PersonDB(id, first_name, last_name, email, external_id, rut, scanned,request_value))
            }
        }

        cursor.close()
        return persons
    }

    fun getAllExternalIdsWhereScannedIsApp(): List<Int> {
        val db = dbHelper.readableDatabase
        val externalIds = mutableListOf<Int>()

        val columns = arrayOf("external_id")

        val selection = "scanned = ?"
        val selectionArgs = arrayOf("APP")

        val cursor = db.query(
            "persons",
            columns,
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        with(cursor) {
            while (moveToNext()) {
                val external_id = getInt(getColumnIndexOrThrow("external_id"))
                externalIds.add(external_id)
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
            "persons",
            null, // Columns - null selects all columns
            "external_id = ?", // Selection
            arrayOf(externalId.toString()), // Selection args
            null, // Group by
            null, // Having
            null // Order by
        )

        return if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            val first_name = cursor.getString(cursor.getColumnIndexOrThrow("first_name"))
            val last_name = cursor.getString(cursor.getColumnIndexOrThrow("last_name"))
            val email = cursor.getString(cursor.getColumnIndexOrThrow("email"))
            val rut = cursor.getString(cursor.getColumnIndexOrThrow("rut"))
            val scanned = cursor.getString(cursor.getColumnIndexOrThrow("scanned"))
            val request_value = cursor.getString(cursor.getColumnIndexOrThrow("request_value"))
            cursor.close()
            PersonDB(id, first_name, last_name, email, externalId, rut, scanned, request_value)
        } else {
            cursor.close()
            null
        }
    }


   /*fun getPersonaById(id: Int): Persona? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            "personas",
            null,
            "id = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )
        return if (cursor.moveToFirst()) {
            val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
            val edad = cursor.getInt(cursor.getColumnIndexOrThrow("edad"))
            Persona(id, nombre, edad)
        } else {
            null
        }.also {
            cursor.close()
        }
    }

    fun deletePersonaById(id: Int) {
        val db = dbHelper.writableDatabase
        db.delete("personas", "id = ?", arrayOf(id.toString()))
    }*/
}
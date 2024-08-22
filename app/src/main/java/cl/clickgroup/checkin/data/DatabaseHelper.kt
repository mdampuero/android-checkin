package cl.clickgroup.checkin.data

import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.Context

class DatabaseHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null, // factory
    DATABASE_VERSION
) {

    override fun onCreate(db: SQLiteDatabase) {
        val createPersonsTable = """
            CREATE TABLE persons (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                external_id INT,
                first_name TEXT NOT NULL,
                last_name TEXT NOT NULL,
                email TEXT,
                rut NOT NULL,
                scanned TEXT
            );
        """.trimIndent()

        val createCheckinsTable = """
            CREATE TABLE checkins (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                personId INTEGER,
                checkinTime TEXT NOT NULL,
                FOREIGN KEY(personId) REFERENCES persons(id)
            );
        """.trimIndent()

        val createIndexPersons = """
            CREATE INDEX idx_external_id ON persons(external_id);
        """.trimIndent()
        db.execSQL(createPersonsTable)
        db.execSQL(createCheckinsTable)
        db.execSQL(createIndexPersons)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS checkins")
        db.execSQL("DROP TABLE IF EXISTS personas")
        db.execSQL("DROP TABLE IF EXISTS persons")
        onCreate(db)
    }

    companion object {
        private const val DATABASE_NAME = "mydatabase.db"
        private const val DATABASE_VERSION = 4
    }
}

package com.example.spacius

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// NOTA: Asegúrate de que este modelo de datos sea global y accesible
data class User(
    val id: String,
    val name: String,
    val birthDate: String,
    val identification: String, // Campo añadido para la DB
    val email: String,
    val passwordHash: String
)

class UserDatabaseHelper(context: Context) : SQLiteOpenHelper(context, "spacius.db", null, 2) { // Versión incrementada a 2

    // Campos de columna (ayuda a prevenir errores tipográficos)
    companion object {
        private const val TABLE_NAME = "usuarios"
        private const val COL_ID = "id"
        private const val COL_NOMBRE = "nombre"
        private const val COL_IDENTIFICACION = "identificacion"
        private const val COL_FECHA_NACIMIENTO = "fechaNacimiento"
        private const val COL_EMAIL = "email"
        private const val COL_PASSWORD = "passwordHash" // Renombrado en la lógica
    }

    override fun onCreate(db: SQLiteDatabase?) {
        // TABLA ACTUALIZADA
        db?.execSQL(
            "CREATE TABLE $TABLE_NAME (" +
                    "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$COL_NOMBRE TEXT, " +
                    "$COL_IDENTIFICACION TEXT UNIQUE, " + // Identificación única
                    "$COL_FECHA_NACIMIENTO TEXT, " +
                    "$COL_EMAIL TEXT UNIQUE, " +
                    "$COL_PASSWORD TEXT)" // La contraseña debe ser hasheada en la práctica real
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // En una app real, aquí migrarías datos. Para simplificar, la destruiremos.
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    // Método que registra un usuario con todos los nuevos campos
    fun registrarUsuario(
        nombre: String,
        identificacion: String,
        fechaNacimiento: String,
        email: String,
        passwordHash: String // Recibe el hash (o el texto, si se hashea aquí)
    ): Boolean {
        val db = writableDatabase
        val values = ContentValues()
        values.put(COL_NOMBRE, nombre)
        values.put(COL_IDENTIFICACION, identificacion)
        values.put(COL_FECHA_NACIMIENTO, fechaNacimiento)
        values.put(COL_EMAIL, email)
        values.put(COL_PASSWORD, passwordHash)

        val result = db.insert(TABLE_NAME, null, values)
        return result != -1L
    }

    /**
     * Devuelve el ID del usuario si el login es exitoso.
     * Reemplaza el antiguo 'loginUsuario'.
     */
    fun getUserIdByCredentials(email: String, password: String): String? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT $COL_ID FROM $TABLE_NAME WHERE $COL_EMAIL=? AND $COL_PASSWORD=?",
            arrayOf(email, password)
        )
        var userId: String? = null
        if (cursor.moveToFirst()) {
            userId = cursor.getString(cursor.getColumnIndexOrThrow(COL_ID))
        }
        cursor.close()
        // NOTA: Si usas hashing, debes verificar el hash aquí, no el texto plano.
        return userId
    }

    /**
     * Devuelve los datos completos del perfil del usuario usando su ID.
     */
    fun getUserById(userId: String): User? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_NAME WHERE $COL_ID=?",
            arrayOf(userId)
        )
        var user: User? = null
        if (cursor.moveToFirst()) {
            user = User(
                id = cursor.getString(cursor.getColumnIndexOrThrow(COL_ID)),
                name = cursor.getString(cursor.getColumnIndexOrThrow(COL_NOMBRE)),
                identification = cursor.getString(cursor.getColumnIndexOrThrow(COL_IDENTIFICACION)),
                birthDate = cursor.getString(cursor.getColumnIndexOrThrow(COL_FECHA_NACIMIENTO)),
                email = cursor.getString(cursor.getColumnIndexOrThrow(COL_EMAIL)),
                passwordHash = cursor.getString(cursor.getColumnIndexOrThrow(COL_PASSWORD))
            )
        }
        cursor.close()
        return user
    }

    /**
     * Actualiza los campos de nombre, correo y, opcionalmente, la contraseña.
     */
    fun updateProfile(user: User): Boolean {
        val db = writableDatabase
        val values = ContentValues()
        values.put(COL_NOMBRE, user.name)
        values.put(COL_EMAIL, user.email)
        values.put(COL_PASSWORD, user.passwordHash) // Siempre guardar el hash

        val rowsAffected = db.update(
            TABLE_NAME,
            values,
            "$COL_ID = ?",
            arrayOf(user.id)
        )
        return rowsAffected > 0
    }
}
package com.example.spacius.data

import android.content.Context
import androidx.room.*

@Entity(tableName = "lugares")
data class Lugar(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val descripcion: String,
    val latitud: Double,
    val longitud: Double,
    val imagenUrl: String,
    val fechaDisponible: String,
    val horaDisponible: String
)

@Entity(tableName = "reservas")
data class Reserva(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val idLugar: Int,
    val fecha: String,
    val horaInicio: String,
    val horaFin: String,
    val nombreUsuario: String
)

@Dao
interface LugarDao {
    @Query("SELECT * FROM lugares")
    suspend fun getAllLugares(): List<Lugar>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(lugares: List<Lugar>)

    @Query("SELECT COUNT(*) FROM lugares")
    suspend fun countLugares(): Int

    // Función agregada para obtener un lugar específico por su id
    @Query("SELECT * FROM lugares WHERE id = :idLugar LIMIT 1")
    suspend fun getLugarById(idLugar: Int): Lugar?
}

@Dao
interface ReservaDao {
    @Query("SELECT * FROM reservas")
    suspend fun getAllReservas(): List<Reserva>

    @Insert
    suspend fun insertReserva(reserva: Reserva)

    @Query("DELETE FROM reservas WHERE id = :reservaId")
    suspend fun deleteReservaById(reservaId: Int)

    @Query("SELECT * FROM reservas WHERE id = :reservaId LIMIT 1")
    suspend fun getReservaById(reservaId: Int): Reserva?
}

@Database(entities = [Lugar::class, Reserva::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun lugarDao(): LugarDao
    abstract fun reservaDao(): ReservaDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "spacius_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}


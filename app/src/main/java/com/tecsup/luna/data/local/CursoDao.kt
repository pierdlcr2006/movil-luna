package com.tecsup.luna.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CursoDao {
    @Query("SELECT * FROM Curso")
    fun getAllCursos(): Flow<List<Curso>>

    @Query("SELECT * FROM Curso WHERE nombreCurso LIKE '%' || :query || '%' OR docente LIKE '%' || :query || '%'")
    fun searchCursos(query: String): Flow<List<Curso>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurso(curso: Curso)

    @Update
    suspend fun updateCurso(curso: Curso)

    @Delete
    suspend fun deleteCurso(curso: Curso)

    @Query("SELECT * FROM Curso WHERE codigoCurso = :codigo LIMIT 1")
    suspend fun getCursoByCodigo(codigo: String): Curso?
}

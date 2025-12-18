package com.tecsup.luna.data.repository

import com.tecsup.luna.data.local.Curso
import com.tecsup.luna.data.local.CursoDao
import kotlinx.coroutines.flow.Flow

class CursoRepository(private val cursoDao: CursoDao) {
    val allCursos: Flow<List<Curso>> = cursoDao.getAllCursos()

    fun searchCursos(query: String): Flow<List<Curso>> {
        return cursoDao.searchCursos(query)
    }

    suspend fun insert(curso: Curso) {
        cursoDao.insertCurso(curso)
    }

    suspend fun update(curso: Curso) {
        cursoDao.updateCurso(curso)
    }

    suspend fun delete(curso: Curso) {
        cursoDao.deleteCurso(curso)
    }

    suspend fun getCursoByCodigo(codigo: String): Curso? {
        return cursoDao.getCursoByCodigo(codigo)
    }
}

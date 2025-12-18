package com.tecsup.luna.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Curso")
data class Curso(
    @PrimaryKey(autoGenerate = true)
    val idCurso: Int = 0,
    val nombreCurso: String,
    val creditos: Int,
    val docente: String?,
    val horasSemanales: Int,
    val ciclo: String,
    val codigoCurso: String
)

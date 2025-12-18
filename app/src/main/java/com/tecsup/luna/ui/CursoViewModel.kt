package com.tecsup.luna.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tecsup.luna.data.local.Curso
import com.tecsup.luna.data.repository.CursoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CursoViewModel(private val repository: CursoRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    val cursos: StateFlow<List<Curso>> = _searchQuery
        .combine(repository.allCursos) { query, cursos ->
            if (query.isBlank()) {
                cursos
            } else {
                cursos.filter {
                    it.nombreCurso.contains(query, ignoreCase = true) ||
                            (it.docente?.contains(query, ignoreCase = true) == true)
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun insertCurso(curso: Curso, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val existingCurso = repository.getCursoByCodigo(curso.codigoCurso)
            if (existingCurso != null) {
                onError("El código del curso ya existe")
            } else {
                repository.insert(curso)
                onSuccess()
            }
        }
    }

    fun updateCurso(curso: Curso, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.update(curso)
            onSuccess()
        }
    }

    fun deleteCurso(curso: Curso) {
        viewModelScope.launch {
            repository.delete(curso)
        }
    }
}

class CursoViewModelFactory(private val repository: CursoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CursoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CursoViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

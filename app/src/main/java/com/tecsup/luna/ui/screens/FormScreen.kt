package com.tecsup.luna.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.tecsup.luna.data.local.Curso
import com.tecsup.luna.ui.CursoViewModel
import com.tecsup.luna.ui.components.CustomTextField
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormScreen(
    viewModel: CursoViewModel,
    cursoId: Int?,
    cursoToEdit: Curso? = null,
    onNavigateBack: () -> Unit
) {
    var nombreCurso by remember { mutableStateOf(cursoToEdit?.nombreCurso ?: "") }
    var creditos by remember { mutableStateOf(cursoToEdit?.creditos?.toString() ?: "") }
    var docente by remember { mutableStateOf(cursoToEdit?.docente ?: "") }
    var horasSemanales by remember { mutableStateOf(cursoToEdit?.horasSemanales?.toString() ?: "") }
    var ciclo by remember { mutableStateOf(cursoToEdit?.ciclo ?: "") }
    var codigoCurso by remember { mutableStateOf(cursoToEdit?.codigoCurso ?: "") }

    var nombreError by remember { mutableStateOf<String?>(null) }
    var creditosError by remember { mutableStateOf<String?>(null) }
    var codigoError by remember { mutableStateOf<String?>(null) }
    var cicloError by remember { mutableStateOf<String?>(null) }
    var horasError by remember { mutableStateOf<String?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (cursoId == null) "Nuevo Curso" else "Editar Curso") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CustomTextField(
                value = codigoCurso,
                onValueChange = {
                    if (it.length <= 6) codigoCurso = it
                    codigoError = null
                },
                label = "Código (6 caracteres)",
                isError = codigoError != null,
                errorMessage = codigoError
            )

            CustomTextField(
                value = nombreCurso,
                onValueChange = {
                    nombreCurso = it
                    nombreError = null
                },
                label = "Nombre del Curso",
                isError = nombreError != null,
                errorMessage = nombreError
            )

            CustomTextField(
                value = creditos,
                onValueChange = {
                    if (it.all { char -> char.isDigit() }) creditos = it
                    creditosError = null
                },
                label = "Créditos",
                keyboardType = KeyboardType.Number,
                isError = creditosError != null,
                errorMessage = creditosError
            )

            CustomTextField(
                value = horasSemanales,
                onValueChange = {
                    if (it.all { char -> char.isDigit() }) horasSemanales = it
                    horasError = null
                },
                label = "Horas Semanales",
                keyboardType = KeyboardType.Number,
                isError = horasError != null,
                errorMessage = horasError
            )

            CustomTextField(
                value = ciclo,
                onValueChange = {
                    ciclo = it
                    cicloError = null
                },
                label = "Ciclo (I, II, ...)",
                isError = cicloError != null,
                errorMessage = cicloError
            )

            CustomTextField(
                value = docente,
                onValueChange = { docente = it },
                label = "Docente (Opcional)"
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    // Validation
                    var isValid = true
                    if (nombreCurso.isBlank()) {
                        nombreError = "El nombre es obligatorio"
                        isValid = false
                    }
                    if (codigoCurso.length != 6) {
                        codigoError = "El código debe tener 6 caracteres"
                        isValid = false
                    }
                    if (creditos.isBlank() || (creditos.toIntOrNull() ?: 0) < 1) {
                        creditosError = "Mínimo 1 crédito"
                        isValid = false
                    }
                    if (horasSemanales.isBlank()) {
                        horasError = "Requerido"
                        isValid = false
                    }
                    if (ciclo.isBlank()) {
                        cicloError = "Requerido"
                        isValid = false
                    }

                    if (isValid) {
                        val curso = Curso(
                            idCurso = cursoId ?: 0,
                            nombreCurso = nombreCurso,
                            creditos = creditos.toInt(),
                            docente = docente,
                            horasSemanales = horasSemanales.toInt(),
                            ciclo = ciclo,
                            codigoCurso = codigoCurso
                        )

                        if (cursoId == null) {
                            viewModel.insertCurso(
                                curso,
                                onSuccess = {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Curso registrado")
                                        onNavigateBack()
                                    }
                                },
                                onError = { error ->
                                    scope.launch {
                                        snackbarHostState.showSnackbar(error)
                                    }
                                }
                            )
                        } else {
                            viewModel.updateCurso(curso) {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Curso actualizado")
                                    onNavigateBack()
                                }
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (cursoId == null) "Registrar" else "Guardar Cambios")
            }
        }
    }
}

package com.example.guiaconsumoapis.ui.formulario

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guiaconsumoapis.data.PublicacionesRepository
import com.example.guiaconsumoapis.data.Resultado
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FormularioUiState(
    val titulo: String = "",
    val cuerpo: String = "",
    val enviando: Boolean = false,
    val idDevuelto: Int? = null,
    val error: String? = null
)

class FormularioViewModel(
    private val repositorio: PublicacionesRepository = PublicacionesRepository()
) : ViewModel() {
    private val _uiState = MutableStateFlow(FormularioUiState())
    val uiState: StateFlow<FormularioUiState> = _uiState.asStateFlow()

    fun cambiarTitulo(texto: String) = _uiState.update { it.copy(titulo = texto) }
    fun cambiarCuerpo(texto: String) = _uiState.update { it.copy(cuerpo = texto) }

    /** POST /posts con lo escrito en el formulario. */
    fun enviar() {
        val estado = _uiState.value
        if (estado.titulo.isBlank()) {
            _uiState.update { it.copy(error = "Escribe un título.") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(enviando = true, error = null, idDevuelto = null) }
            when (val resultado = repositorio.crear(estado.titulo, estado.cuerpo, usuarioId = 1)) {
                is Resultado.Exito -> _uiState.update { it.copy(enviando = false, idDevuelto = resultado.datos.id) }
                is Resultado.Error -> _uiState.update { it.copy(enviando = false, error = resultado.mensaje) }
            }
        }
    }
}

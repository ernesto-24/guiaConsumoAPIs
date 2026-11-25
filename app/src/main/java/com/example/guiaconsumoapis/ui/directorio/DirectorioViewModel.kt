package com.example.guiaconsumoapis.ui.directorio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guiaconsumoapis.data.DirectorioRepository
import com.example.guiaconsumoapis.data.Resultado
import com.example.guiaconsumoapis.data.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DirectorioUiState(
    val texto: String = "",
    val cargando: Boolean = false,
    val usuarios: List<Usuario> = emptyList(),
    val error: String? = null
)

class DirectorioViewModel(
    private val repositorio: DirectorioRepository = DirectorioRepository()
) : ViewModel() {
    private val _uiState = MutableStateFlow(DirectorioUiState())
    val uiState: StateFlow<DirectorioUiState> = _uiState.asStateFlow()

    fun cambiarTexto(texto: String) {
        _uiState.update { it.copy(texto = texto) }
    }

    fun buscar() {
        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, error = null) }
            when (val resultado = repositorio.buscar(texto = _uiState.value.texto)) {
                is Resultado.Exito -> _uiState.update { it.copy(cargando = false, usuarios = resultado.datos) }
                is Resultado.Error -> _uiState.update { it.copy(cargando = false, error = resultado.mensaje) }
            }
        }
    }
}

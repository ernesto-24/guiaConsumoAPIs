package com.example.guiaconsumoapis.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guiaconsumoapis.data.PokemonRepository
import com.example.guiaconsumoapis.data.Resultado
import com.example.guiaconsumoapis.data.remote.PokemonDetalleDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DetalleUiState(
    val cargando: Boolean = false,
    val pokemon: PokemonDetalleDto? = null,
    val error: String? = null
)

class DetallePokemonViewModel(
    private val repositorio: PokemonRepository = PokemonRepository()
) : ViewModel() {
    private val _uiState = MutableStateFlow(DetalleUiState())
    val uiState: StateFlow<DetalleUiState> = _uiState.asStateFlow()

    fun cargar(nombre: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, error = null) }
            when (val resultado = repositorio.obtenerPokemon(nombre)) {
                is Resultado.Exito -> _uiState.update { it.copy(cargando = false, pokemon = resultado.datos) }
                is Resultado.Error -> _uiState.update { it.copy(cargando = false, error = resultado.mensaje) }
            }
        }
    }
}

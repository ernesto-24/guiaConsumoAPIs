package com.example.guiaconsumoapis.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guiaconsumoapis.data.PokemonRepository
import com.example.guiaconsumoapis.data.Resultado
import com.example.guiaconsumoapis.data.remote.PokemonResumen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
data class ListaUiState(
    val cargando: Boolean = false,
    val cargandoMas: Boolean = false, // pidiendo la pagina siguiente
    val pokemones: List<PokemonResumen> = emptyList(),
    val hayMas: Boolean = true,
    val error: String? = null
)

class ListaPokemonViewModel(
    private val repositorio: PokemonRepository = PokemonRepository()
    ) : ViewModel() {
    private val _uiState = MutableStateFlow(ListaUiState())
    val uiState: StateFlow<ListaUiState> = _uiState.asStateFlow()

    private val tamanoPagina = 20

    init {
        cargar()
    }

    /** Primera pagina (offset = 0). */
    fun cargar() {
        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, error = null) }
            when (val resultado = repositorio.obtenerPokemones(limite = tamanoPagina, desplazamiento = 0)) {
                is Resultado.Exito -> _uiState.update { it.copy(cargando = false, pokemones =
                    resultado.datos, hayMas = resultado.datos.size == tamanoPagina) }
                is Resultado.Error -> _uiState.update { it.copy(cargando = false, error =
                    resultado.mensaje) }
            }
        }
    }

    /** Pagina siguiente: el offset es la cantidad que ya tenemos. */
    fun cargarMas() {
        val estado = _uiState.value
        if (estado.cargandoMas || !estado.hayMas) return
        viewModelScope.launch {
            _uiState.update { it.copy(cargandoMas = true, error = null) }
            val desplazamiento = estado.pokemones.size
            when (val resultado = repositorio.obtenerPokemones(limite = tamanoPagina, desplazamiento = desplazamiento)) {
                is Resultado.Exito -> _uiState.update { it.copy(cargandoMas = false, pokemones =
                    it.pokemones + resultado.datos, hayMas = resultado.datos.size == tamanoPagina) }
                is Resultado.Error -> _uiState.update { it.copy(cargandoMas = false, error =
                    resultado.mensaje) }
            }
        }
    }
}

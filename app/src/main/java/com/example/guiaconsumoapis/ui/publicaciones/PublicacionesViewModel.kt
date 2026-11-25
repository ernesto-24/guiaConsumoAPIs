package com.example.guiaconsumoapis.ui.publicaciones

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guiaconsumoapis.data.PublicacionesRepository
import com.example.guiaconsumoapis.data.Resultado
import com.example.guiaconsumoapis.data.remote.PublicacionDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PublicacionesUiState(
    val cargando: Boolean = false,
    val publicaciones: List<PublicacionDto> = emptyList(),
    val error: String? = null,
    val mensaje: String? = null // resultado de la ultima accion (crear, editar, eliminar)
)

/**
 * OJO: JSONPlaceholder no guarda los cambios. Por eso la lista se actualiza aqui
 * con lo que responde la API, para que se vea el efecto de cada peticion.
 */
class PublicacionesViewModel(
    private val repositorio: PublicacionesRepository = PublicacionesRepository()
) : ViewModel() {
    private val _uiState = MutableStateFlow(PublicacionesUiState())
    val uiState: StateFlow<PublicacionesUiState> = _uiState.asStateFlow()

    private val usuarioId = 1

    init {
        cargar()
    }

    fun cargar() {
        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, error = null) }
            when (val resultado = repositorio.listar(usuarioId)) {
                is Resultado.Exito -> _uiState.update { it.copy(cargando = false, publicaciones = resultado.datos) }
                is Resultado.Error -> _uiState.update { it.copy(cargando = false, error = resultado.mensaje) }
            }
        }
    }

    fun crear() {
        viewModelScope.launch {
            when (val resultado = repositorio.crear("Nueva publicación", "Creada desde la app", usuarioId)) {
                is Resultado.Exito -> _uiState.update {
                    it.copy(
                        publicaciones = listOf(resultado.datos) + it.publicaciones,
                        mensaje = "POST: creada con id ${resultado.datos.id}"
                    )
                }
                is Resultado.Error -> _uiState.update { it.copy(mensaje = resultado.mensaje) }
            }
        }
    }

    fun editar(publicacion: PublicacionDto) {
        viewModelScope.launch {
            val nueva = publicacion.copy(title = "${publicacion.title} (editado)")
            when (val resultado = repositorio.reemplazar(nueva)) {
                is Resultado.Exito -> _uiState.update { estado ->
                    estado.copy(
                        publicaciones = estado.publicaciones.map { if (it.id == publicacion.id) resultado.datos else it },
                        mensaje = "PUT: publicación ${publicacion.id} reemplazada"
                    )
                }
                is Resultado.Error -> _uiState.update { it.copy(mensaje = resultado.mensaje) }
            }
        }
    }

    fun eliminar(publicacion: PublicacionDto) {
        viewModelScope.launch {
            when (val resultado = repositorio.eliminar(publicacion.id)) {
                is Resultado.Exito -> _uiState.update { estado ->
                    estado.copy(
                        publicaciones = estado.publicaciones.filterNot { it.id == publicacion.id },
                        mensaje = "DELETE: publicación ${publicacion.id} eliminada (código ${resultado.datos})"
                    )
                }
                is Resultado.Error -> _uiState.update { it.copy(mensaje = resultado.mensaje) }
            }
        }
    }
}

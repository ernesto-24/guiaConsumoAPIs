package com.example.guiaconsumoapis.data

import com.example.guiaconsumoapis.data.remote.NuevaPublicacionRequest
import com.example.guiaconsumoapis.data.remote.PublicacionDto
import com.example.guiaconsumoapis.data.remote.PublicacionesClient
import com.example.guiaconsumoapis.data.remote.PublicacionesService

class PublicacionesRepository(private val api: PublicacionesService = PublicacionesClient.api) {

    suspend fun listar(usuarioId: Int): Resultado<List<PublicacionDto>> =
        llamadaSegura { api.listar(usuarioId) }

    suspend fun crear(titulo: String, cuerpo: String, usuarioId: Int): Resultado<PublicacionDto> =
        llamadaSegura { api.crear(NuevaPublicacionRequest(title = titulo, body = cuerpo, userId = usuarioId)) }

    suspend fun reemplazar(publicacion: PublicacionDto): Resultado<PublicacionDto> =
        llamadaSegura { api.reemplazar(publicacion.id, publicacion) }

    /** Como DELETE no tiene cuerpo, revisamos el codigo HTTP a mano. */
    suspend fun eliminar(id: Int): Resultado<Int> =
        when (val resultado = llamadaSegura { api.eliminar(id) }) {
            is Resultado.Exito ->
                if (resultado.datos.isSuccessful) Resultado.Exito(resultado.datos.code())
                else Resultado.Error("No se pudo eliminar (código ${resultado.datos.code()}).")
            is Resultado.Error -> resultado
        }
}

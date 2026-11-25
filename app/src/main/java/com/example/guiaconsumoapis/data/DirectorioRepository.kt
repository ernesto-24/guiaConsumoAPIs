package com.example.guiaconsumoapis.data

import com.example.guiaconsumoapis.BuildConfig
import com.example.guiaconsumoapis.data.remote.BuscarRequest
import com.example.guiaconsumoapis.data.remote.ContactoDto
import com.example.guiaconsumoapis.data.remote.DirectorioClient
import com.example.guiaconsumoapis.data.remote.DirectorioService
import com.example.guiaconsumoapis.data.remote.UsuarioDto
import com.example.guiaconsumoapis.data.remote.listaDesdeTexto
import com.google.gson.Gson

/** Modelo para la pantalla: los contactos ya vienen convertidos en lista. */
data class Usuario(
    val id: Int,
    val nombreCompleto: String,
    val cargo: String?,
    val area: String?,
    val contactos: List<ContactoDto>
)

class DirectorioRepository(
    private val api: DirectorioService = DirectorioClient.api,
    private val apiKey: String = BuildConfig.API_KEY,
    private val gson: Gson = Gson()
) {
    suspend fun buscar(texto: String, area: String = "", limite: Int = 20, desplazamiento: Int = 0): Resultado<List<Usuario>> {
        if (apiKey.isBlank()) {
            return Resultado.Error("Falta la API key del directorio. Agrégala en local.properties.")
        }
        val cuerpo = BuscarRequest(
            apikey = apiKey,
            q = texto,
            area = area,
            limit = limite.toString(),
            offset = desplazamiento.toString()
        )
        return when (val resultado = llamadaSegura { api.buscar(cuerpo) }) {
            is Resultado.Exito -> Resultado.Exito(resultado.datos.data.map { it.aUsuario() })
            is Resultado.Error -> resultado
        }
    }

    private fun UsuarioDto.aUsuario() = Usuario(
        id = id,
        nombreCompleto = nombreCompleto,
        cargo = cargo,
        area = areaCodigo,
        contactos = gson.listaDesdeTexto<ContactoDto>(contactos) // segunda lectura del JSON
    )
}

package com.example.guiaconsumoapis.data.remote

import com.example.guiaconsumoapis.BuildConfig
import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import retrofit2.http.Body
import retrofit2.http.POST

data class BuscarRequest(
    val apikey: String,
    val q: String,
    val area: String,
    val limit: String,
    val offset: String
)

fun cuerpoDeBusqueda(texto: String, area: String = "", pagina: Int = 0): BuscarRequest =
    BuscarRequest(
        apikey = BuildConfig.API_KEY,
        q = texto,
        area = area,
        limit = "20",
        offset = (pagina * 20).toString()
    )

interface DirectorioService {
    // La URL completa es baseUrl + "directorio_buscar"
    @POST("directorio_buscar")
    suspend fun buscar(@Body cuerpo: BuscarRequest): RespuestaApi<UsuarioDto>
}

data class RespuestaApi<T>(
    val data: List<T>
)
data class UsuarioDto(
    val id: Int,
    @SerializedName("nombre_completo") val nombreCompleto: String,
    val cargo: String?,
    @SerializedName("area_codigo") val areaCodigo: String?,
    val contactos: String? // OJO: llega como TEXTO que contiene un JSON, no como lista
)
data class ContactoDto(
    val tipo: String,
    val valor: String,
    val principal: Int
)
/** Segunda lectura: convierte un TEXTO con JSON en una lista de objetos. Si el texto no sirve,
devuelve lista vacia. */
inline fun <reified T> Gson.listaDesdeTexto(texto: String?): List<T> {
    if (texto.isNullOrBlank()) return emptyList()
    val tipo = TypeToken.getParameterized(List::class.java, T::class.java).type
    return try {
        fromJson<List<T>>(texto, tipo) ?: emptyList()
    } catch (e: JsonParseException) {
        emptyList()
    }
}

object DirectorioClient {
    private const val BASE_URL = "https://clgqxx5idk.g4.sqlite.cloud:443/v2/functions/"

    val api: DirectorioService by lazy {
        FabricaRetrofit.crear(BASE_URL).create(DirectorioService::class.java)
    }
}

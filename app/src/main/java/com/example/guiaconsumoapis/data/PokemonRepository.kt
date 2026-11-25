package com.example.guiaconsumoapis.data

import com.example.guiaconsumoapis.data.remote.PokeApiService
import com.example.guiaconsumoapis.data.remote.PokemonDetalleDto
import com.example.guiaconsumoapis.data.remote.PokemonResumen
import com.example.guiaconsumoapis.data.remote.RetrofitClient
import com.example.guiaconsumoapis.data.remote.aResumen
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import java.io.IOException

sealed class Resultado<out T> {
    data class Exito<out T>(val datos: T) : Resultado<T>()
    data class Error(val mensaje: String) : Resultado<Nothing>()
}

class PokemonRepository(private val api: PokeApiService = RetrofitClient.api) {
    suspend fun obtenerPokemones(limite: Int, desplazamiento: Int): Resultado<List<PokemonResumen>> =
        try {
            val respuesta = api.obtenerPokemones(limite, desplazamiento)
            Resultado.Exito(respuesta.results.map { it.aResumen() })
        } catch (e: CancellationException) {
            throw e // cancelar la corrutina no es un error
        } catch (e: HttpException) {
            Resultado.Error("El servidor respondió con un error (código ${e.code()}).")
        } catch (e: IOException) {
            Resultado.Error("No hay conexión a internet. Revisa tu red e inténtalo de nuevo.")
        } catch (e: Exception) {
            Resultado.Error("Ocurrió un error inesperado.")
        }

    /** Detalle con @Path: /pokemon/{nombre} */
    suspend fun obtenerPokemon(nombre: String): Resultado<PokemonDetalleDto> =
        llamadaSegura { api.obtenerPokemon(nombre) }
}

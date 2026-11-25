package com.example.guiaconsumoapis.data

import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import java.io.IOException

/**
 * El mismo manejo de errores de PokemonRepository, pero reutilizable:
 * ejecuta la llamada y convierte cualquier fallo en Resultado.Error.
 */
suspend fun <T> llamadaSegura(bloque: suspend () -> T): Resultado<T> =
    try {
        Resultado.Exito(bloque())
    } catch (e: CancellationException) {
        throw e // cancelar la corrutina no es un error
    } catch (e: HttpException) {
        Resultado.Error("El servidor respondió con un error (código ${e.code()}).")
    } catch (e: IOException) {
        Resultado.Error("No hay conexión a internet. Revisa tu red e inténtalo de nuevo.")
    } catch (e: Exception) {
        Resultado.Error("Ocurrió un error inesperado.")
    }

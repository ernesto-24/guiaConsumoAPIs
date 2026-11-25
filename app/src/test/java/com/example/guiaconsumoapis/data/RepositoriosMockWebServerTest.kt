package com.example.guiaconsumoapis.data

import com.example.guiaconsumoapis.data.remote.DirectorioService
import com.example.guiaconsumoapis.data.remote.FabricaRetrofit
import com.example.guiaconsumoapis.data.remote.PokeApiService
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class RepositoriosMockWebServerTest {
    private lateinit var servidor: MockWebServer

    @Before
    fun preparar() {
        servidor = MockWebServer()
        servidor.start()
    }

    @After
    fun cerrar() {
        servidor.shutdown()
    }

    private inline fun <reified T> crearApi(): T =
        FabricaRetrofit.crear(servidor.url("/").toString()).create(T::class.java)

    @Test
    fun respuesta404_devuelveErrorConElCodigo() = runBlocking {
        servidor.enqueue(MockResponse().setResponseCode(404).setBody("Not Found"))
        val repositorio = PokemonRepository(crearApi<PokeApiService>())

        val resultado = repositorio.obtenerPokemon("noexiste")

        assertEquals(Resultado.Error("El servidor respondió con un error (código 404)."), resultado)
        assertEquals("/pokemon/noexiste", servidor.takeRequest().path) // el @Path armó bien la ruta
    }

    @Test
    fun servidorCaido_devuelveErrorDeConexion() = runBlocking {
        val api = crearApi<PokeApiService>()
        servidor.shutdown() // nadie escucha en ese puerto
        val repositorio = PokemonRepository(api)

        val resultado = repositorio.obtenerPokemones(limite = 20, desplazamiento = 0)

        assertEquals(Resultado.Error("No hay conexión a internet. Revisa tu red e inténtalo de nuevo."), resultado)
    }

    @Test
    fun listaVaciaEnData_devuelveExitoSinUsuarios() = runBlocking {
        servidor.enqueue(MockResponse().setResponseCode(200).setBody("""{"data": []}"""))
        val repositorio = DirectorioRepository(api = crearApi<DirectorioService>(), apiKey = "clave-de-prueba")

        val resultado = repositorio.buscar("nadie")

        assertEquals(Resultado.Exito(emptyList<Usuario>()), resultado)
    }
}

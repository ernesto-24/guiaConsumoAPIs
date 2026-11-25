package com.example.guiaconsumoapis

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.guiaconsumoapis.ui.directorio.DirectorioScreen
import com.example.guiaconsumoapis.ui.formulario.FormularioScreen
import com.example.guiaconsumoapis.ui.list.PokemonNavegacion
import com.example.guiaconsumoapis.ui.publicaciones.PublicacionesScreen
import com.example.guiaconsumoapis.ui.theme.GuiaConsumoAPIsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                PantallaPrincipal()
            }
        }
    }
}

/** Pestañas para cambiar entre las APIs de la guia. */
@Composable
fun PantallaPrincipal() {
    val pestanas = listOf("Pokémon", "Publicaciones", "Formulario", "Directorio")
    var seleccionada by rememberSaveable { mutableIntStateOf(0) }
    Scaffold { relleno ->
        Column(Modifier.fillMaxSize().padding(relleno)) {
            PrimaryTabRow(selectedTabIndex = seleccionada) {
                pestanas.forEachIndexed { indice, titulo ->
                    Tab(
                        selected = seleccionada == indice,
                        onClick = { seleccionada = indice },
                        text = { Text(titulo) }
                    )
                }
            }
            Box(Modifier.fillMaxSize()) {
                when (seleccionada) {
                    0 -> PokemonNavegacion()
                    1 -> PublicacionesScreen()
                    2 -> FormularioScreen()
                    else -> DirectorioScreen()
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    GuiaConsumoAPIsTheme {
        Greeting("Android")
    }
}

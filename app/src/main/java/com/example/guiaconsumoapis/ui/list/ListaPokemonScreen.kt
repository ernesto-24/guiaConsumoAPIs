package com.example.guiaconsumoapis.ui.list

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

/** Navegacion simple: si hay un nombre elegido se muestra el detalle, si no la lista. */
@Composable
fun PokemonNavegacion() {
    var nombreElegido by rememberSaveable { mutableStateOf<String?>(null) }
    val nombre = nombreElegido
    if (nombre == null) {
        ListaPokemonScreen(onPokemonClick = { nombreElegido = it })
    } else {
        BackHandler { nombreElegido = null }
        DetallePokemonScreen(nombre = nombre, onVolver = { nombreElegido = null })
    }
}

@Composable
fun ListaPokemonScreen(
    onPokemonClick: (String) -> Unit = {},
    viewModel: ListaPokemonViewModel = viewModel()
) {
    val estado by viewModel.uiState.collectAsState()
    val error = estado.error
    when {
        estado.cargando -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        error != null && estado.pokemones.isEmpty() ->
            MensajeDeError(mensaje = error, onReintentar = viewModel::cargar)
        else -> LazyColumn {
            items(estado.pokemones, key = { it.id }) { pokemon ->
                Text(
                    "#${pokemon.id} ${pokemon.nombre}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onPokemonClick(pokemon.nombre) }
                        .padding(16.dp)
                )
            }
            item {
                Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                    when {
                        estado.cargandoMas -> CircularProgressIndicator()
                        error != null -> MensajeDeError(mensaje = error, onReintentar = viewModel::cargarMas)
                        estado.hayMas -> Button(onClick = viewModel::cargarMas) { Text("Cargar más") }
                    }
                }
            }
        }
    }
}

@Composable
fun MensajeDeError(mensaje: String, onReintentar: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = mensaje, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onReintentar) { Text("Reintentar") }
    }
}
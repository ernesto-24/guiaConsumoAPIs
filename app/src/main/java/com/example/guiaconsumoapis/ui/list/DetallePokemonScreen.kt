package com.example.guiaconsumoapis.ui.list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

/** Recibe el nombre como argumento y pide /pokemon/{nombre}. */
@Composable
fun DetallePokemonScreen(
    nombre: String,
    onVolver: () -> Unit,
    viewModel: DetallePokemonViewModel = viewModel(key = "detalle-$nombre")
) {
    LaunchedEffect(nombre) { viewModel.cargar(nombre) }
    val estado by viewModel.uiState.collectAsState()
    val error = estado.error
    val pokemon = estado.pokemon
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        TextButton(onClick = onVolver) { Text("← Volver") }
        when {
            estado.cargando -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            error != null -> MensajeDeError(mensaje = error, onReintentar = { viewModel.cargar(nombre) })
            pokemon != null -> {
                Text("#${pokemon.id} ${pokemon.name}", style = MaterialTheme.typography.headlineMedium)
                Spacer(Modifier.height(8.dp))
                Text("Altura: ${pokemon.height / 10.0} m")
                Text("Peso: ${pokemon.weight / 10.0} kg")
                Text("Tipos: ${pokemon.types.joinToString { it.type.name }}")
            }
        }
    }
}

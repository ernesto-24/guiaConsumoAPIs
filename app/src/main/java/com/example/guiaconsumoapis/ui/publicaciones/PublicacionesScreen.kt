package com.example.guiaconsumoapis.ui.publicaciones

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.guiaconsumoapis.data.remote.PublicacionDto
import com.example.guiaconsumoapis.ui.list.MensajeDeError

@Composable
fun PublicacionesScreen(viewModel: PublicacionesViewModel = viewModel()) {
    val estado by viewModel.uiState.collectAsState()
    val error = estado.error
    when {
        estado.cargando -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        error != null -> MensajeDeError(mensaje = error, onReintentar = viewModel::cargar)
        else -> Column(Modifier.fillMaxSize()) {
            Button(onClick = viewModel::crear, modifier = Modifier.padding(16.dp)) {
                Text("Crear publicación (POST)")
            }
            estado.mensaje?.let {
                Text(it, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(horizontal = 16.dp))
            }
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Sin `key`: JSONPlaceholder devuelve siempre id 101 al crear, y las claves repetidas cierran la app.
                items(estado.publicaciones) { publicacion ->
                    TarjetaPublicacion(
                        publicacion = publicacion,
                        onEditar = { viewModel.editar(publicacion) },
                        onEliminar = { viewModel.eliminar(publicacion) }
                    )
                }
            }
        }
    }
}

@Composable
private fun TarjetaPublicacion(publicacion: PublicacionDto, onEditar: () -> Unit, onEliminar: () -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text("#${publicacion.id} ${publicacion.title}", style = MaterialTheme.typography.titleMedium)
            Text(publicacion.body, style = MaterialTheme.typography.bodyMedium)
            Row {
                TextButton(onClick = onEditar) { Text("Editar (PUT)") }
                TextButton(onClick = onEliminar) { Text("Eliminar (DELETE)") }
            }
        }
    }
}

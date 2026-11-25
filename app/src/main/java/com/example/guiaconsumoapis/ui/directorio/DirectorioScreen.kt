package com.example.guiaconsumoapis.ui.directorio

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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.guiaconsumoapis.data.Usuario
import com.example.guiaconsumoapis.ui.list.MensajeDeError

@Composable
fun DirectorioScreen(viewModel: DirectorioViewModel = viewModel()) {
    val estado by viewModel.uiState.collectAsState()
    val error = estado.error
    Column(Modifier.fillMaxSize()) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = estado.texto,
                onValueChange = viewModel::cambiarTexto,
                label = { Text("Buscar persona") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            Button(onClick = viewModel::buscar, modifier = Modifier.padding(start = 8.dp)) { Text("Buscar") }
        }
        when {
            estado.cargando -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            error != null -> MensajeDeError(mensaje = error, onReintentar = viewModel::buscar)
            else -> LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(estado.usuarios, key = { it.id }) { usuario -> TarjetaUsuario(usuario) }
            }
        }
    }
}

@Composable
private fun TarjetaUsuario(usuario: Usuario) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(usuario.nombreCompleto, style = MaterialTheme.typography.titleMedium)
            usuario.cargo?.let { Text(it, style = MaterialTheme.typography.bodyMedium) }
            usuario.area?.let { Text("Área: $it", style = MaterialTheme.typography.bodySmall) }
            usuario.contactos.forEach { contacto ->
                val marca = if (contacto.principal == 1) " ★" else ""
                Text("${contacto.tipo}: ${contacto.valor}$marca", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

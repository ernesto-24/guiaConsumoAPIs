package com.example.guiaconsumoapis.ui.formulario

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun FormularioScreen(viewModel: FormularioViewModel = viewModel()) {
    val estado by viewModel.uiState.collectAsState()
    Column(
        Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = estado.titulo,
            onValueChange = viewModel::cambiarTitulo,
            label = { Text("Título") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = estado.cuerpo,
            onValueChange = viewModel::cambiarCuerpo,
            label = { Text("Contenido") },
            minLines = 3,
            modifier = Modifier.fillMaxWidth()
        )
        Button(onClick = viewModel::enviar, enabled = !estado.enviando) { Text("Enviar (POST)") }
        if (estado.enviando) CircularProgressIndicator()
        estado.idDevuelto?.let {
            Text("La API devolvió el id $it", color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleMedium)
        }
        estado.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
    }
}

package com.example.guiaconsumoapis.data.remote
import com.google.gson.annotations.SerializedName
// GET https://pokeapi.co/api/v2/pokemon?limit=20&offset=0
data class PokemonListResponse(
    val count: Int,
    val next: String?,
    val results: List<PokemonItemDto>
)
data class PokemonItemDto(
    val name: String,
    val url: String
)

fun PokemonItemDto.aResumen(): PokemonResumen {
    val id = url.trimEnd('/').substringAfterLast('/').toInt() // ".../pokemon/25/" -> 25
    return PokemonResumen(id = id, nombre = name, imagenUrl = "...")
}

data class PokemonResumen(val id: Int, val nombre: String, val imagenUrl: String)

// GET https://pokeapi.co/api/v2/pokemon/{nombre} (solo los campos que usamos)
data class PokemonDetalleDto(
    val id: Int,
    val name: String,
    val height: Int, // en decimetros
    val weight: Int, // en hectogramos
    val types: List<TipoSlotDto>
)
data class TipoSlotDto(val type: TipoDto)
data class TipoDto(val name: String)

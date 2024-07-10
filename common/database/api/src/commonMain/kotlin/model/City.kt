package model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

data class City(
    val id: Int,
    val name: String,
    val languageId: Int,
    val location: Location,
)


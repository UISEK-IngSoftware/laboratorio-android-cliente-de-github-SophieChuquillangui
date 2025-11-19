package ec.edu.uisek.githubclient.models

import com.google.gson.annotations.SerializedName

/**
 * Representa la RESPUESTA JSON de un repositorio que recibimos de la API de GitHub.
 */
data class Repo(
    @SerializedName("id")
    val id: Long,

    @SerializedName("name")
    val name: String,

    @SerializedName("description")
    val description: String?,

    @SerializedName("language")
    val language: String?,

    @SerializedName("owner")
    val owner: RepoOwner
)
data class RepoRequest(
    val name: String,
    val description: String
)

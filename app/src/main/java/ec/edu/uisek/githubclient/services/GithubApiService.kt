package ec.edu.uisek.githubclient.services

import ec.edu.uisek.githubclient.models.Repo
import ec.edu.uisek.githubclient.models.RepoPatchRequest
import ec.edu.uisek.githubclient.models.RepoRequest

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface GithubApiService {

    @GET("users/{user}/repos")
    fun getRepos(@Path("user") user: String): Call<List<Repo>>

    @POST("user/repos")
    fun createRepo(
        @Body body: RepoRequest
    ): Call<Repo>

    @PATCH("repos/{owner}/{repo}")
    fun updateRepo(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Body body: RepoPatchRequest
    ): Call<Void>

    @DELETE("repos/{owner}/{repo}")
    fun deleteRepo(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): Call<Void>
}

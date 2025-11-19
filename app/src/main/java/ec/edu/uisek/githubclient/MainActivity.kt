package ec.edu.uisek.githubclient

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ec.edu.uisek.githubclient.databinding.ActivityMainBinding
import ec.edu.uisek.githubclient.models.Repo
import ec.edu.uisek.githubclient.services.GithubApiService
import ec.edu.uisek.githubclient.services.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    companion object {
        private const val GITHUB_USER = "SophieChuquillangui"
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var reposAdapter: ReposAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()

        binding.newRepoFab.setOnClickListener {
            displayNewRepoForm()
        }

    }

    override fun onResume() {
        super.onResume()
        fetchRepositories()
    }

    private fun setupRecyclerView() {
        reposAdapter = ReposAdapter(
            onDelete = { repo -> showDeleteDialog(repo) },
            onEdit = { repo -> displayEditRepoForm(repo) }
        )
        binding.reposRecyclerView.adapter = reposAdapter
    }

    private fun fetchRepositories() {
        val apiService: GithubApiService = RetrofitClient.gitHubApiService
        val call = apiService.getRepos(GITHUB_USER)

        call.enqueue(object : Callback<List<Repo>> {
            override fun onResponse(call: Call<List<Repo>>, response: Response<List<Repo>>) {
                if (response.isSuccessful) {
                    val repos = response.body()
                    if (repos != null && repos.isNotEmpty()) {
                        reposAdapter.updateRepositories(repos)
                    } else {
                        showMessage("No se encontraron repositorios")
                    }
                } else {
                    val errorMessage = when (response.code()) {
                        401 -> "No autorizado (Verifica tu token)"
                        403 -> "Prohibido"
                        404 -> "No encontrado"
                        else -> "Error ${response.code()}"
                    }
                    showMessage("Error: $errorMessage")
                }
            }

            override fun onFailure(call: Call<List<Repo>>, t: Throwable) {
                showMessage("No se pudieron cargar los repositorios. Error: ${t.message}")
            }
        })
    }

    private fun showDeleteDialog(repo: Repo) {
        val dialog = ConfirmDeleteDialog(repo) {
            deleteRepo(it)
        }
        dialog.show(supportFragmentManager, "deleteDialog")
    }

    private fun deleteRepo(repo: Repo) {
        RetrofitClient.gitHubApiService
            .deleteRepo(owner = GITHUB_USER, repo = repo.name)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        reposAdapter.removeRepo(repo)
                        showMessage("Repositorio eliminado")
                    } else {
                        showMessage("No se pudo eliminar (código ${response.code()})")
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    showMessage("Error eliminando repo: ${t.message}")
                }
            })
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun displayNewRepoForm() {
        Intent(this, RepoForm::class.java).apply {
            startActivity(this)
        }
    }

    private fun displayEditRepoForm(repo: Repo) {
        val intent = Intent(this, EditRepoForm::class.java)
        intent.putExtra("repo_name", repo.name)
        intent.putExtra("repo_description", repo.description)
        startActivity(intent)
    }
}

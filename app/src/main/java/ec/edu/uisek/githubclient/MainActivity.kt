package ec.edu.uisek.githubclient

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import ec.edu.uisek.githubclient.databinding.ActivityMainBinding
import ec.edu.uisek.githubclient.models.Repo
import ec.edu.uisek.githubclient.services.GithubApiService
import ec.edu.uisek.githubclient.services.RetrofitClient
import retrofit2.Call
import retrofit2.Response
import retrofit2.Callback

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
            onEdit = { repo ->
                val intent = Intent(this, RepoForm::class.java).apply {
                    putExtra("IS_EDIT_MODE", true)
                    putExtra("REPO_NAME", repo.name)
                    putExtra("REPO_OWNER", repo.owner.login)
                    putExtra("REPO_DESCRIPTION", repo.description)
                }
                startActivity(intent)
            },
            onDelete = { repo ->
                showDeleteConfirmationDialog(repo)
            }
        )
        binding.reposRecyclerView.adapter = reposAdapter
    }

    private fun showDeleteConfirmationDialog(repo: Repo) {
        AlertDialog.Builder(this)
            .setTitle("Confirmar eliminación")
            .setMessage("¿Estás seguro de que quieres eliminar el repositorio '${repo.name}'?")
            .setPositiveButton("Aceptar") { _, _ ->
                deleteRepositoryFromApi(repo)
            }
            .setNegativeButton("Cancelar", null)
            // --- CORRECCIÓN: Usar el drawable que sí existe ---
            .setIcon(R.drawable.baseline_delete_24)
            .show()
    }

    private fun deleteRepositoryFromApi(repo: Repo) {
        val owner = repo.owner.login
        val repoName = repo.name

        // --- CORRECCIÓN: Usar Callback<Void> en lugar de Callback<Unit> ---
        RetrofitClient.getApiService().deleteRepo(owner, repoName).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    showMessage("Repositorio '${repo.name}' eliminado correctamente")
                    fetchRepositories()
                } else {
                    showMessage("Error al eliminar: ${response.code()} - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                showMessage("Fallo de conexión al intentar eliminar: ${t.message}")
            }
        })
    }

    private fun fetchRepositories() {
        val apiService: GithubApiService = RetrofitClient.getApiService()
        val call = apiService.getRepos(GITHUB_USER)

        call.enqueue(object : Callback<List<Repo>> {
            override fun onResponse(call: Call<List<Repo>>, response: Response<List<Repo>>) {
                if (response.isSuccessful) {
                    val repos = response.body()
                    if (repos != null) {
                        reposAdapter.updateRepositories(repos)
                    } else {
                        showMessage("No se encontraron repositorios")
                    }
                } else {
                    val errorMessage = when (response.code()) {
                        401 -> "No autorizado (Revisa tu Token)"
                        403 -> "Prohibido"
                        404 -> "No encontrado"
                        else -> "Error ${response.code()}"
                    }
                    showMessage("Error: $errorMessage")
                }
            }

            override fun onFailure(call: Call<List<Repo>>, t: Throwable) {
                showMessage("No se pudieron cargar repositorios")
            }
        })

    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun displayNewRepoForm() {
        val intent = Intent(this, RepoForm::class.java).apply {
            putExtra("IS_EDIT_MODE", false)
        }
        startActivity(intent)
    }
}

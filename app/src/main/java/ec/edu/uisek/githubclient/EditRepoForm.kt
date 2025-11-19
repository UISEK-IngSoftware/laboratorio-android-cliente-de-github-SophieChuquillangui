package ec.edu.uisek.githubclient

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ec.edu.uisek.githubclient.databinding.ActivityRepoFormBinding
import ec.edu.uisek.githubclient.models.RepoPatchRequest
import ec.edu.uisek.githubclient.services.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditRepoForm : AppCompatActivity() {
    companion object {
        private const val GITHUB_USER = "SophieChuquillangui"
    }

    private lateinit var binding: ActivityRepoFormBinding
    private lateinit var repoName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRepoFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repoName = intent.getStringExtra("repo_name")!!
        val description = intent.getStringExtra("repo_description") ?: ""

        binding.repoNameInput.setText(repoName)
        binding.repoNameInput.isEnabled = false // El nombre del repo no se puede cambiar

        binding.repoDescriptionInput.setText(description)

        binding.cancelButton.setOnClickListener { finish() }
        binding.saveButton.setOnClickListener { updateRepo() }
    }

    private fun updateRepo() {
        val newDescription = binding.repoDescriptionInput.text.toString()
        val request = RepoPatchRequest(description = newDescription)

        RetrofitClient.gitHubApiService.updateRepo(
            owner = GITHUB_USER,
            repo = repoName,
            body = request
        ).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@EditRepoForm, "Repositorio actualizado", Toast.LENGTH_SHORT).show()
                    finish() // Cierra la actividad y vuelve a la lista
                } else {
                    Toast.makeText(this@EditRepoForm, "Error al actualizar: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@EditRepoForm, "Error de red: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

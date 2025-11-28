package ec.edu.uisek.githubclient

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ec.edu.uisek.githubclient.databinding.ActivityRepoFormBinding
import ec.edu.uisek.githubclient.models.Repo
import ec.edu.uisek.githubclient.models.RepoPatchRequest
import ec.edu.uisek.githubclient.models.RepoRequest
import ec.edu.uisek.githubclient.services.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RepoForm : AppCompatActivity() {

    private lateinit var binding: ActivityRepoFormBinding

    // Propiedades para manejar el modo de edición
    private var isEditMode = false
    private var repoName: String? = null
    private var repoOwner: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRepoFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        isEditMode = intent.getBooleanExtra("IS_EDIT_MODE", false)

        if (isEditMode) {
            repoName = intent.getStringExtra("REPO_NAME")
            repoOwner = intent.getStringExtra("REPO_OWNER")
            val repoDescription = intent.getStringExtra("REPO_DESCRIPTION")

            binding.repoNameInput.setText(repoName)
            binding.repoNameInput.isEnabled = false
            binding.repoDescriptionInput.setText(repoDescription)

            binding.saveButton.text = "Actualizar"
            binding.saveButton.setOnClickListener { updateRepo() }
        } else {
            binding.saveButton.setOnClickListener { createRepo() }
        }

        binding.cancelButton.setOnClickListener { finish() }
    }

    private fun createRepo() {
        if (!validateForm()) return

        val name = binding.repoNameInput.text.toString().trim()
        val description = binding.repoDescriptionInput.text.toString().trim()
        val request = RepoRequest(name, description)

        RetrofitClient.getApiService().createRepo(request).enqueue(object : Callback<Repo> {
            override fun onResponse(call: Call<Repo>, response: Response<Repo>) {
                if (response.isSuccessful) {
                    showMessage("Repositorio creado con éxito")
                    finish()
                } else {
                    val errorMsg = when (response.code()) {
                        401 -> "No autorizado"
                        422 -> "El repositorio ya existe"
                        else -> "Error: ${response.code()}"
                    }
                    showMessage(errorMsg)
                }
            }

            override fun onFailure(call: Call<Repo>, t: Throwable) {
                showMessage("Error de conexión al crear: ${t.message}")
            }
        })
    }

    private fun updateRepo() {
        val description = binding.repoDescriptionInput.text.toString().trim()
        val request = RepoPatchRequest(description)

        // --- CORRECCIÓN: Usar Callback<Void> en lugar de Callback<Unit> ---
        RetrofitClient.getApiService().updateRepo(repoOwner!!, repoName!!, request).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    showMessage("Repositorio actualizado con éxito")
                    finish()
                } else {
                    showMessage("Error al actualizar: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                showMessage("Error de conexión al actualizar: ${t.message}")
            }
        })
    }

    private fun validateForm(): Boolean {
        val repoName = binding.repoNameInput.text.toString()
        if (repoName.isBlank()) {
            binding.repoNameInput.error = "El nombre es requerido"
            return false
        }
        if (repoName.contains(" ")) {
            binding.repoNameInput.error = "No se permiten espacios"
            return false
        }
        return true
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}

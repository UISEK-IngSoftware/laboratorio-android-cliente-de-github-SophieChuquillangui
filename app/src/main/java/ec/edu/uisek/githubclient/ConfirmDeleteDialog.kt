package ec.edu.uisek.githubclient

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import ec.edu.uisek.githubclient.models.Repo

class ConfirmDeleteDialog(
    private val repo: Repo,
    private val onConfirm: (Repo) -> Unit
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle("Eliminar repositorio")
            .setMessage("¿Seguro que deseas eliminar '${repo.name}'?")
            .setPositiveButton("Eliminar") { _, _ -> onConfirm(repo) }
            .setNegativeButton("Cancelar", null)
            .create()
    }
}



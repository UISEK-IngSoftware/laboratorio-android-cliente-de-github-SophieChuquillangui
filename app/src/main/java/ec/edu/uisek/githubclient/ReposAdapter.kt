// ReposAdapter.kt

package ec.edu.uisek.githubclient

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ec.edu.uisek.githubclient.databinding.FragmentRepoItemBinding
import ec.edu.uisek.githubclient.models.Repo

class ReposAdapter(
    private val onDelete: (Repo) -> Unit,
    private val onEdit: (Repo) -> Unit
) : RecyclerView.Adapter<ReposAdapter.RepoViewHolder>() {

    private var repos: MutableList<Repo> = mutableListOf()

    inner class RepoViewHolder(private val binding: FragmentRepoItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(repo: Repo) {
            binding.repoName.text = repo.name
            binding.repoDescription.text = repo.description ?: "Sin descripción"

            Glide.with(binding.root.context)
                .load(repo.owner.avatarUrl)
                .into(binding.repoOwnerImage)

            binding.deleteFab.setOnClickListener {
                onDelete(repo)
            }

            binding.editFab.setOnClickListener {
                onEdit(repo)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepoViewHolder {
        val binding = FragmentRepoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RepoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RepoViewHolder, position: Int) {
        holder.bind(repos[position])
    }

    override fun getItemCount(): Int {
        return repos.size
    }


    fun updateRepositories(newRepos: List<Repo>) {
        repos.clear()
        repos.addAll(newRepos)
        notifyDataSetChanged()
    }

    fun removeRepo(repo: Repo) {
        val position = repos.indexOf(repo)
        if (position > -1) {
            repos.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}

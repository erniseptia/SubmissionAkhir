package com.github.githubusersearch


import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.githubusersearch.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: GithubUserAdapter


    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = GithubUserAdapter()
        adapter.notifyDataSetChanged()

        adapter.setOnItemClickCallback(object : GithubUserAdapter. OnItemClickCallback {
        override fun onItemClicked(data: User){
            showLoading(true)
            Intent(this@MainActivity, DetailActivity::class.java).also {
                it.putExtra(DetailActivity.EXTRA_USERNAME, data.login)
                startActivity(it)
                showLoading(false)
            }

        }
        })
        viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(MainViewModel::class.java)

        binding.rvUser.layoutManager = LinearLayoutManager(this)
        binding.rvUser.setHasFixedSize(true)
        binding.rvUser.adapter = adapter

        binding.btnSearch.setOnClickListener {
            searchUser()
        }

        binding.etQuery.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                searchUser()
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }

        viewModel.getSearchUsers().observe(this)
        {
            if (it!= null) {
                adapter.setList(it)
                showLoading(false)
            }
        }
    }

       private fun searchUser() {
        val query = binding.etQuery.text.toString()
        if (query.isEmpty()) return
        showLoading(true)
        viewModel. setSearchUsers(query)
    }


    private fun showLoading(state: Boolean) {
        if (state
        ) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }
}

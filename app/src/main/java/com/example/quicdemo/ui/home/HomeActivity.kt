package com.example.quicdemo.ui.home

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quic_data.response.SampleResponse
import com.example.quicdemo.R
import com.example.quicdemo.databinding.ActivityMainBinding
import com.example.quic_data.Result
import com.example.quicdemo.ui.BaseActivity
import com.google.android.material.snackbar.Snackbar

class HomeActivity : BaseActivity() {
    private lateinit var mBinding: ActivityMainBinding
    private val mHomeViewModel by viewModels<HomeViewModel>()
    private val userDetailsAdapter by lazy { UserListAdapter(emptyList(), mImageLoader) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        fetchDataFromService()
        setUpAdapter()
    }

    private fun setUpAdapter() {
        val mLinearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mBinding.rvUserList.layoutManager = mLinearLayoutManager
        mBinding.rvUserList.adapter = userDetailsAdapter
    }

    private fun fetchDataFromService() {
        mHomeViewModel.getSampleResult(mQuicDemoRepo)
            .observe(this, Observer {
                updateUI(it)
            })
    }

    private fun updateUI(it: Result<SampleResponse>) {
        when (it) {
            is Result.Loading -> {
                mBinding.progressBar.visibility = View.VISIBLE
            }
            is Result.Success -> {
                mBinding.progressBar.visibility = View.GONE
                userDetailsAdapter.updateList(it.data.data)
            }
            is Result.Error -> {
                mBinding.progressBar.visibility = View.GONE
                Snackbar.make(
                    mBinding.root,
                    it.exception.message ?: "Something went wrong",
                    Snackbar.LENGTH_SHORT
                ).show()
                it.exception
            }
        }
    }
}
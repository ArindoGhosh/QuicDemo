package com.example.quicdemo.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.quic_data.response.SampleResponse
import com.example.quicdemo.R
import com.example.quicdemo.databinding.ItemUserDetailsBinding
import com.example.quicdemo.utility.ImageLoader

class UserListAdapter(
    private var userList: List<SampleResponse.Datum>
    , private val mImageLoader: ImageLoader
) :
    RecyclerView.Adapter<UserListAdapter.UserListViewHolder>() {
    private lateinit var mBinding: ItemUserDetailsBinding

    inner class UserListViewHolder(mItemBinding: ItemUserDetailsBinding) :
        RecyclerView.ViewHolder(mItemBinding.root) {
        fun onBind(userData: SampleResponse.Datum) {
            mBinding.tvUserDetails.text =
                "${userData.firstName} ${userData.lastName}\n${userData.email}"
            mImageLoader.loadInImageView(mBinding.imgUser, userData.avatar)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserListViewHolder {
        mBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_user_details,
            parent,
            false
        )
        return UserListViewHolder(mBinding)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: UserListViewHolder, position: Int) {
        holder.onBind(userList[position])
    }

    fun updateList(userList: List<SampleResponse.Datum>) {
        this.userList = userList
        notifyDataSetChanged()
    }
}
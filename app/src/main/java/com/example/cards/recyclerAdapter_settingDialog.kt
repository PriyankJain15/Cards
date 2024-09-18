package com.example.cards

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import de.hdodenhof.circleimageview.CircleImageView

class recyclerAdapter_settingDialog (var list:MutableList<Int>,var context: Context, val onImageClick:(Int)->Unit):
    RecyclerView.Adapter<recyclerAdapter_settingDialog.imageViewHolder>() {

    class imageViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        var img:ImageView = itemView.findViewById(R.id.recycle_profile)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): imageViewHolder {
        return imageViewHolder(LayoutInflater.from(context).inflate(R.layout.dialog_recyclerview,parent,false))
    }

    override fun getItemCount(): Int {
        return  list.size
    }

    override fun onBindViewHolder(holder: imageViewHolder, position: Int) {
        var check = list.get(position)
        holder.img.setImageResource(check)

        holder.img.setOnClickListener(View.OnClickListener {
            onImageClick(check)
        })
    }
}
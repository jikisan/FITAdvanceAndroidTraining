package com.example.day1stackoverflowapi

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.example.day1stackoverflowapi.databinding.RowBinding
import com.squareup.picasso.Picasso

class CustomAdapter(val allQuestions :AllQuestions, val context :Context) : BaseAdapter() {

    override fun getCount(): Int {
        return allQuestions.items.size
    }

    override fun getItem(position: Int): Any {
        return allQuestions.items[position].link
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val binding = RowBinding.inflate(inflater, parent, false)
        val questions = allQuestions.items[position]

        binding.lblTitle.text = questions.title
        binding.lblOwner.text = questions.owner.display_name
        Picasso
            .get()
            .load(questions.owner.profile_image)
            .placeholder(R.drawable.ic_launcher_foreground)
            .into(binding.imageView)

        return binding.root
    }

}
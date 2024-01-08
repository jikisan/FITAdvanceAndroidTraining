package com.example.day3syncactivity

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.example.day3syncactivity.databinding.RowBinding

class CustomAdapter(val context: Context, val allItems: ArrayList<Item>) : BaseAdapter() {


    override fun getCount(): Int {
        return allItems.size
    }

    override fun getItem(p0: Int): Any {
        return allItems[p0].item_name
    }

    override fun getItemId(p0: Int): Long {
        return allItems[p0].id.toLong()
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val layoutInflter = context.getSystemService((Context.LAYOUT_INFLATER_SERVICE)) as LayoutInflater
        val binding = RowBinding.inflate(layoutInflter, p2, false)
        val item = allItems[p0]
        var image = 0

        if(item.sync > 0) image = R.drawable.check
        else image = R.drawable.sync

        binding.imageView.setImageResource(image)
        binding.lblItem.text = item.item_name

        return binding.root
    }
}
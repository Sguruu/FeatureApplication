package com.weather.featuretesting.presentation.mainscreen.basic.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.weather.featuretesting.R
import com.weather.featuretesting.presentation.mainscreen.basic.model.IBasicFeatureModel

class BaseFeatureListAdapter<T : IBasicFeatureModel>(
    private val dataSet: Array<T>,
    private val callback: (IBasicFeatureModel) -> Unit
) : RecyclerView.Adapter<BaseFeatureListAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val button: Button

        init {
            button = view.findViewById(R.id.buttonViewItem)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.main_recycler_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.button.text = dataSet[position].valueName
        Log.d("MyTest", "$position BaseFeatureListAdapter onBindViewHolder")
        clickHandler(holder)
    }

    override fun getItemCount(): Int {
        Log.d("MyTest", "${dataSet.size} BaseFeatureListAdapter getItemCount")
        return dataSet.size
    }

    private fun clickHandler(holder: ViewHolder) {
        holder.button.setOnClickListener {
            callback.invoke(dataSet[holder.adapterPosition])
        }
    }
}

package com.weather.featuretesting.presentation.mainscreen.view.recycler

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.weather.featuretesting.R
import com.weather.featuretesting.presentation.mainscreen.model.FeatureModel

class FeatureListAdapter(
    private val dataSet: Array<FeatureModel>,
    private val callback: (FeatureModel) -> Unit
) : RecyclerView.Adapter<FeatureListAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val button: Button

        init {
            button = view.findViewById(R.id.buttonViewItem)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.main_recycler_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.button.text = dataSet[position].valueName
        Log.d("MyTest", "$position onBindViewHolder")
        clickHandler(holder)
    }

    override fun getItemCount(): Int {
        Log.d("MyTest", "${dataSet.size} getItemCount")
        return dataSet.size
    }

    private fun clickHandler(holder: ViewHolder) {
        holder.button.setOnClickListener {
            callback.invoke(dataSet[holder.adapterPosition])
        }
    }
}

package com.weather.featuretesting.presentation.mainscreen.basic.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.weather.featuretesting.presentation.mainscreen.basic.adapter.BaseFeatureListAdapter
import com.weather.featuretesting.presentation.mainscreen.basic.model.IBasicFeatureModel
import com.weather.featuretesting.presentation.mainscreen.viewmodel.MainActivityViewModel

open class FragmentRecyclerFeature(@LayoutRes contentLayoutId: Int) : FragmentLog(contentLayoutId) {
    private val viewModelActivity: MainActivityViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    fun <T : RecyclerView.ViewHolder> createRecyclerView(
        adapterList: RecyclerView.Adapter<T>,
        idRecyclerView: Int
    ) {
        val recyclerView: RecyclerView = requireView().findViewById(idRecyclerView)
        recyclerView.adapter = adapterList
        recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
    }

    fun <T : IBasicFeatureModel> createAdapter(
        listFeature: Array<T>,
        fragmentManager: FragmentManager = childFragmentManager,
        @IdRes containerView: Int
    ): BaseFeatureListAdapter<T> {
        return BaseFeatureListAdapter(listFeature) {
            clickItemList(
                value = it,
                listValue = listFeature,
                fragmentManager,
                containerView = containerView
            )
        }
    }

    private fun <T : IBasicFeatureModel> clickItemList(
        value: IBasicFeatureModel,
        listValue: Array<T>,
        fragmentManager: FragmentManager,
        @IdRes containerView: Int
    ) {
        for (item in listValue) {
            if (item === value) {
                fragmentManager.commit {
                    this.replace(containerView, value.fragmentClass)
                    addToBackStack(null)
                }
                viewModelActivity.titleActionBar.value = value.valueName
                return
            }
        }
    }
}

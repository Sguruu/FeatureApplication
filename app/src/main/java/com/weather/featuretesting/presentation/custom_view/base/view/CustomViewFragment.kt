package com.weather.featuretesting.presentation.custom_view.base.view

import android.os.Bundle
import android.view.View
import com.weather.featuretesting.R
import com.weather.featuretesting.presentation.custom_view.base.model.CustomViewModel
import com.weather.featuretesting.presentation.mainscreen.basic.fragment.FragmentRecyclerFeature

class CustomViewFragment : FragmentRecyclerFeature(R.layout.fragment_base_list) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = createAdapter(
            CustomViewModel.values(),
            containerView = R.id.fragment_container_view,
            fragmentManager = parentFragmentManager
        )

        createRecyclerView(adapter, R.id.recyclerView)
    }
}

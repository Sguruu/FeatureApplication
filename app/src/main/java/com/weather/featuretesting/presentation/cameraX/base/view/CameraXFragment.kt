package com.weather.featuretesting.presentation.cameraX.base.view

import android.os.Bundle
import android.view.View
import com.weather.featuretesting.R
import com.weather.featuretesting.presentation.cameraX.base.model.CameraXModel
import com.weather.featuretesting.presentation.mainscreen.basic.fragment.FragmentRecyclerFeature


class CameraXFragment : FragmentRecyclerFeature(R.layout.fragment_base_list) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = createAdapter(
            listFeature = CameraXModel.values(),
            containerView = R.id.fragment_container_view,
            fragmentManager = parentFragmentManager
        )
        createRecyclerView(
            adapterList = adapter,
            idRecyclerView = R.id.recyclerView
        )

        createRecyclerView(adapter, R.id.recyclerView)
    }
}

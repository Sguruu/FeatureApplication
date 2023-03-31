package com.weather.featuretesting.presentation.mainscreen.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.weather.featuretesting.R
import com.weather.featuretesting.presentation.cameraX.base.view.CameraXFragment
import com.weather.featuretesting.presentation.checkvpn.view.CheckVpnFragment
import com.weather.featuretesting.presentation.custom_view.base.view.CustomViewFragment
import com.weather.featuretesting.presentation.mainscreen.basic.fragment.FragmentLog
import com.weather.featuretesting.presentation.mainscreen.model.FeatureModel
import com.weather.featuretesting.presentation.mainscreen.view.recycler.FeatureListAdapter
import com.weather.featuretesting.presentation.mainscreen.viewmodel.MainActivityViewModel

class MainFragment : FragmentLog(R.layout.fragment_main) {
    private val viewModelActivity: MainActivityViewModel by activityViewModels()
    private val titleActionBar = "FeatureTesting"

    override fun onStart() {
        super.onStart()
        viewModelActivity.titleActionBar.value = titleActionBar
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listFeature = FeatureModel.values()

        val adapterList = FeatureListAdapter(listFeature) {
            clickItemList(it)
        }

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerViewMain)

        recyclerView.adapter = adapterList
        recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
    }

    private fun clickItemList(value: FeatureModel) {
        when (value) {
            FeatureModel.CHECK_VPN -> {
                parentFragmentManager.commit {
                    replace<CheckVpnFragment>(R.id.fragment_container_view)
                    addToBackStack(null)
                }
            }
            FeatureModel.CAMERAX -> {
                parentFragmentManager.commit {
                    replace<CameraXFragment>(R.id.fragment_container_view)
                    addToBackStack(null)
                }
            }
            FeatureModel.CUSTOM_VIEW -> {
                parentFragmentManager.commit {
                    replace<CustomViewFragment>(R.id.fragment_container_view)
                    addToBackStack(null)
                }
            }
        }
        viewModelActivity.titleActionBar.value = value.valueName
    }
}

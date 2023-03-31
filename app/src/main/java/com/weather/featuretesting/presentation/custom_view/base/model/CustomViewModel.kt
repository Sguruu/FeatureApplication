package com.weather.featuretesting.presentation.custom_view.base.model

import androidx.fragment.app.Fragment
import com.weather.featuretesting.presentation.custom_view.feature.view_finder_view.ViewFinderViewFragment
import com.weather.featuretesting.presentation.mainscreen.basic.model.IBasicFeatureModel

enum class CustomViewModel(override val valueName: String, override val fragmentClass: Fragment) :
    IBasicFeatureModel {
    VIEW_FINDER_VIEW("VIEW_FINDER_VIEW", ViewFinderViewFragment())
}

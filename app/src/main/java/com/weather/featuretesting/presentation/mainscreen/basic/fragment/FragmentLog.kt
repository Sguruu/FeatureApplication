package com.weather.featuretesting.presentation.mainscreen.basic.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment

private const val TAG = "FeatureTesting"

open class FragmentLog(@LayoutRes contentLayoutId: Int) : Fragment(contentLayoutId) {
    private val outMsg: String = this.javaClass.name

    /* Activity State Created */
    override fun onAttach(context: Context) {
        Log.d(TAG, "$outMsg onAttach")
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "$outMsg onCreate")
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "$outMsg onCreateView")
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "$outMsg onViewCreated")
    }

    /* Activity State Started */
    override fun onStart() {
        Log.d(TAG, "$outMsg onStart")
        super.onStart()
    }

    /* Activity Resume Started */
    override fun onResume() {
        Log.d(TAG, "$outMsg onResume")
        super.onResume()
    }

    /* Activity Pause Started */
    override fun onPause() {
        Log.d(TAG, "$outMsg onPause")
        super.onPause()
    }

    /* Activity Stopped Started */
    override fun onStop() {
        Log.d(TAG, "$outMsg onStop")
        super.onStop()
    }

    /* Destroyed */
    override fun onDestroyView() {
        Log.d(TAG, "$outMsg onDestroyView")
        super.onDestroyView()
    }

    override fun onDestroy() {
        Log.d(TAG, "$outMsg onDestroy")
        super.onDestroy()
    }

    override fun onDetach() {
        Log.d(TAG, "$outMsg onDetach")
        super.onDetach()
    }
}

package com.weather.featuretesting.presentation.mainscreen.basic

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

private const val TAG = "FeatureTesting"

open class AppCompatActivityLog : AppCompatActivity() {
    private val outMsg: String = this.javaClass.name

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "$outMsg onCreate")
        super.onCreate(savedInstanceState)
    }

    override fun onStart() {
        Log.d(TAG, "$outMsg onStart")
        super.onStart()
    }

    override fun onResume() {
        Log.d(TAG, "$outMsg onResume")
        super.onResume()
    }

    override fun onPause() {
        Log.d(TAG, "$outMsg onPause")
        super.onPause()
    }

    override fun onStop() {
        Log.d(TAG, "$outMsg onStop")
        super.onStop()
    }

    override fun onDestroy() {
        Log.d(TAG, "$outMsg onDestroy")
        super.onDestroy()
    }
}

package com.weather.featuretesting.presentation.mainscreen.view

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import com.weather.featuretesting.R
import com.weather.featuretesting.presentation.cameraX.feature.cameracapture.CameraCaptureFragment
import com.weather.featuretesting.presentation.mainscreen.basic.AppCompatActivityLog
import com.weather.featuretesting.presentation.mainscreen.viewmodel.MainActivityViewModel

class MainActivity : AppCompatActivityLog() {
    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<MainFragment>(R.id.fragment_container_view)
            }
        }

        val newTitleActionBarObserver = Observer<String> {
            supportActionBar?.title = it
        }

        viewModel.titleActionBar.observe(this, newTitleActionBarObserver)
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 0) {
            finish()
        } else {
            supportFragmentManager.popBackStack()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        checkPermissionsCardScanWindow(requestCode)
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun checkPermissionsCardScanWindow(requestCode: Int) {
        if (requestCode == CameraCaptureFragment.REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                // можем стартовать камеру
                Toast.makeText(
                    this,
                    "Пермишены на камеру есть",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    this,
                    "Permissions not granted by the user",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun allPermissionsGranted(): Boolean {
        return CameraCaptureFragment.REQUIRED_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(
                this,
                it
            ) == PackageManager.PERMISSION_GRANTED
        }
    }
}

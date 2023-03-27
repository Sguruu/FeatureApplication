package com.weather.featuretesting.presentation.cameraX.feature.cameracapture

import android.Manifest.permission.*
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.google.common.util.concurrent.ListenableFuture
import com.weather.featuretesting.R
import com.weather.featuretesting.presentation.mainscreen.basic.fragment.FragmentLog
import com.weather.featuretesting.presentation.mainscreen.viewmodel.MainActivityViewModel
import java.util.concurrent.ExecutorService

/*
 Дока : https://developer.android.com/training/camerax/preview
 Пример обучалка : https://developer.android.com/codelabs/camerax-getting-started#1
 */

class CameraCaptureFragment : FragmentLog(R.layout.fragment_capture_fragment) {

    private val viewModelActivity: MainActivityViewModel by activityViewModels()

    private var imageCapture: ImageCapture? = null

    private var videoCapture: VideoCapture<Recorder>? = null
    private var recording: Recording? = null

    private lateinit var cameraExecutor: ExecutorService
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>

    private lateinit var cameraPreview: PreviewView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        // Запросить разрешение камеры
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
    }

    private fun initView() {
        view?.let {
            cameraPreview = it.findViewById(R.id.cameraPreview)
        }
    }


    private fun startCamera() {
        /*
         Создайте экземпляр ProcessCameraProvider.
         Это используется для привязки жизненного цикла камер к владельцу жизненного цикла.
         Это устраняет задачу открытия и закрытия камеры, так как CameraX работает на жизненном цикле.
          */
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        /*
          Добавьте слушателя в cameraProviderFuture. Добавьте Runnable в качестве одного аргумента.
          Мы заполним его позже. Добавьте ContextCompat.getMainExecutor() в качестве второго аргумента.
          Это возвращает Executor, который работает в основном потоке.
           */
        cameraProviderFuture.addListener({
            // Используется для привязки жизненного цикла камер к владельцу жизненного цикла.
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            /*
            Инициализируйте наш объект предварительного просмотра, вызовите сборку на нем, получите
             поставщика поверхности из видоискателя, а затем установите его в предварительном просмотре.
             */
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(cameraPreview.surfaceProvider)
                }

            // Выберите заднюю камеру по умолчанию
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            /*
            Создайте пробный блок. Внутри этого блока убедитесь, что ничего не привязано к
            cameraProvider, а затем привяжите наш cameraSelector и объект предварительного просмотра
            к cameraProvider.
             */
            try {
                // Отменить привязку вариантов использования перед повторной привязкой
                cameraProvider.unbindAll()

                // Привязать варианты использования к камере
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview
                )
            }
            /*
            Есть несколько способов, которыми этот код может выйти из строя, например, если
            приложение больше не находится в фокусе. Заверните этот код в блок catch, чтобы войти
            в систему в случае сбоя.
             */
            catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun allPermissionsGranted(): Boolean {
        return REQUIRED_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(
                requireContext(),
                it
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    companion object {
        private const val TAG = "CameraXApp"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        const val REQUEST_CODE_PERMISSIONS = 10
        val REQUIRED_PERMISSIONS =
            mutableListOf(
                CAMERA,
                RECORD_AUDIO
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }
}

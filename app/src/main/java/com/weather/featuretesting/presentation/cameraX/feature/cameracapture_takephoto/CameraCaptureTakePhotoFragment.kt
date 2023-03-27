package com.weather.featuretesting.presentation.cameraX.feature.cameracapture_takephoto

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.weather.featuretesting.R
import com.weather.featuretesting.presentation.mainscreen.basic.fragment.FragmentLog
import java.text.SimpleDateFormat
import java.util.*

/*
 Дока : https://developer.android.com/training/camerax/preview
 Пример обучалка : https://developer.android.com/codelabs/camerax-getting-started#1
 */

class CameraCaptureTakePhotoFragment : FragmentLog(R.layout.fragment_camera_capture_take_photo) {

    private var imageCapture: ImageCapture? = null

    private lateinit var cameraPreview: PreviewView
    private lateinit var buttonTakePhoto: Button

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
        // слушатели
        initListener()
    }

    private fun initView() {
        view?.let {
            cameraPreview = it.findViewById(R.id.cameraPreview)
            buttonTakePhoto = it.findViewById(R.id.image_capture_button)
        }
    }

    private fun initListener() {
        buttonTakePhoto.setOnClickListener { takePhoto() }
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

            // Для предзахвата
            imageCapture = ImageCapture.Builder()
                .build()

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
                    preview,
                    imageCapture
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

    private fun takePhoto() {
        // Получите стабильную ссылку на изменяемый сценарий использования захвата изображений
        /*
        Сначала получите ссылку на сценарий использования ImageCapture. Если сценарий использования
        пуст, выйдите из функции. Это будет равно нулю, если мы нажмем кнопку фотографии перед
        настройкой захвата изображения. Без оператора return приложение выйдет из строя, если оно
        будет нулевым
         */
        val imageCapture = imageCapture ?: return

        // Создайте имя с отметкой времени и запись MediaStore
        /*
        Затем создайте значение содержимого MediaStore для хранения изображения.
        Используйте метку времени, чтобы отображаемое имя в MediaStore было уникальным.
         */
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
            }
        }

        // Создайте объект параметров вывода, содержащий файл + метаданные
        /*
        Создайте объект OutputFileOptions. В этом объекте мы можем указать, каким должен быть наш
        вывод. Мы хотим, чтобы выходные данные сохранялись в MediaStore, чтобы другие приложения
        могли их отображать, поэтому добавьте нашу запись MediaStore.
         */
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(
                requireActivity().contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
            .build()

        // Настройте прослушиватель захвата изображений, который запускается после того, как фотография
        // будет сделана
        /*
        Вызовите takePicture() для объекта imageCapture. Передайте outputOptions, исполнитель и
        обратный вызов при сохранении изображения. Далее вы заполните обратный вызов.
         */
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                /**
                 * Если захват не удался, фотография была сделана успешно! Сохраните фотографию в
                 * файл, который мы создали ранее, произнесите тост, чтобы сообщить пользователю,
                 * что все прошло успешно, и распечатайте отчет журнала.
                 */
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val msg = "Photo capture succeeded: ${outputFileResults.savedUri}"
                    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, msg)
                }

                /**
                 * В случае сбоя захвата изображения или сбоя сохранения захвата изображения
                 * добавьте в случае ошибки, чтобы зарегистрировать, что он не удался.
                 */
                override fun onError(exception: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exception.message}", exception)
                }
            }
        )
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
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }
}

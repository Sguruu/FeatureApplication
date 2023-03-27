package com.weather.featuretesting.presentation.cameraX.feature.cameracapture_makevideo

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.*
import androidx.camera.video.VideoCapture
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import com.google.common.util.concurrent.ListenableFuture
import com.weather.featuretesting.R
import com.weather.featuretesting.presentation.mainscreen.basic.fragment.FragmentLog
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraCaptureMakeVideoFragment : FragmentLog(R.layout.fragment_camera_capture_make_video) {

    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService

    private lateinit var cameraPreview: PreviewView
    private lateinit var buttonTakeVideo: Button

    private var videoCapture: VideoCapture<Recorder>? = null
    private var recording: Recording? = null

    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>

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
            buttonTakeVideo = it.findViewById(R.id.video_capture_button)
        }
    }

    private fun initListener() {
        buttonTakeVideo.setOnClickListener { makeVideo() }
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    @SuppressLint("SetTextI18n")
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

            // Это создаст сценарий использования VideoCapture.
            val recorder = Recorder.Builder()
                .setQualitySelector(QualitySelector.from(Quality.HIGHEST))
                .build()
            videoCapture = VideoCapture.withOutput(recorder)

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
                    videoCapture
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

    private fun makeVideo() {
        // Проверьте, был ли создан сценарий использования VideoCapture: если нет, ничего не делайте
        val videoCapture = this.videoCapture ?: return

        buttonTakeVideo.isEnabled = false

        /*
        Если в процессе выполняется активная запись, остановите ее и отпустите текущую запись.
        Мы будем уведомлены, когда записанный видеофайл будет готов к использованию нашим приложением.
         */
        val curRecording = recording
        if (curRecording != null) {
            // Остановить текущий сеанс видео записи
            curRecording.stop()
            recording = null
            return
        }

        // Создать и начать новый сеанс записи
        /*
        Чтобы начать запись, мы создаем новую сессию записи. Сначала мы создаем предполагаемый
        объект видеоконтента MediaStore с системной меткой времени в качестве отображаемого имени
        (чтобы мы могли снимать несколько видео).
         */
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, "Movies/CameraX-Video")
            }
        }

        // Создайте MediaStoreOutputOptions.Builder с опцией внешнего контента.
        val mediaStoreOutputOptions = MediaStoreOutputOptions
            .Builder(
                requireActivity().contentResolver,
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            )
            // Установите созданные значение видеоконтента в MediaStoreOutputOptions.Builder
            // и создайте наш экземпляр MediaStoreOutputOptions.
            .setContentValues(contentValues)
            .build()

        // Настройте опцию вывода в Recorder of VideoCapture<Recorder> и включите аудиозапись:
        recording = videoCapture.output
            .prepareRecording(requireContext(), mediaStoreOutputOptions)
            .also {
                if (PermissionChecker.checkSelfPermission(
                        requireActivity(),
                        Manifest.permission.RECORD_AUDIO
                    ) == PermissionChecker.PERMISSION_GRANTED
                ) {
                    // Включите аудио в этой записи
                    it.withAudioEnabled()
                }
            }
            // Начните эту новую запись и зарегистрируйте лямбда-слушателя VideoRecordEvent.
            .start(ContextCompat.getMainExecutor(requireContext())) { recordEvent ->
                when (recordEvent) {
                    /*
                    Когда запись запроса начнется устройством камеры, переключите текст кнопки
                    "Начать захват" на "Остановить захват".
                     */
                    is VideoRecordEvent.Start -> {
                        buttonTakeVideo.apply {
                            text = "Остановить запись"
                            isEnabled = true
                        }
                    }
                    /*
                    Когда активная запись будет завершена, сообщите пользователю тостом, переключите
                    кнопку "Остановить захват" обратно в "Начать захват" и снова включите его:
                     */
                    is VideoRecordEvent.Finalize -> {
                        if (!recordEvent.hasError()) {
                            val msg =
                                "Video capture succeeded: ${recordEvent.outputResults.outputUri}"
                            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                            Log.d(TAG, msg)
                        } else {
                            recording?.close()
                            recording = null
                            Log.e(TAG, "Video capture ends with error: ${recordEvent.error}")
                        }
                        buttonTakeVideo.apply {
                            text = resources.getString(R.string.start_capture)
                            isEnabled = true
                        }
                    }
                }
            }
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

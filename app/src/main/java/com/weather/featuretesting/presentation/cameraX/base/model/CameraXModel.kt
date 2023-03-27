package com.weather.featuretesting.presentation.cameraX.base.model

import androidx.fragment.app.Fragment
import com.weather.featuretesting.presentation.cameraX.feature.cameracapture.CameraCaptureFragment
import com.weather.featuretesting.presentation.cameraX.feature.cameracapture_makevideo.CameraCaptureMakeVideoFragment
import com.weather.featuretesting.presentation.cameraX.feature.cameracapture_photoanalysis.view.CameraCapturePhotoAnalysisFragment
import com.weather.featuretesting.presentation.cameraX.feature.cameracapture_takephoto.CameraCaptureTakePhotoFragment
import com.weather.featuretesting.presentation.cameraX.feature.cameracapture_videocombineusecase.CameraCaptureVideoCombineUseCaseFragment
import com.weather.featuretesting.presentation.mainscreen.basic.model.IBasicFeatureModel

enum class CameraXModel(override val valueName: String, override val fragmentClass: Fragment) :
    IBasicFeatureModel {
    CAMERA_CAPTURE("Захват камеры", CameraCaptureFragment()),
    CAMERA_CAPTURE_TAKE_A_PHOTO(
        "Захватить камеру + сделать фотографию",
        CameraCaptureTakePhotoFragment()
    ),
    CAMERA_CAPTURE_PHOTO_ANALYSIS(
        "Захватить камеру + проанализировать фотографию",
        CameraCapturePhotoAnalysisFragment()
    ),
    CAMERA_CAPTURE_MAKE_A_VIDEO(
        "Захватить камеру + сделать видео",
        CameraCaptureMakeVideoFragment()
    ),
    CAMERA_CAPTURE_VIDEO_COMBINE_USE_CASE(
        "Комбинация VideoCapture с другими вариантами использования",
        CameraCaptureVideoCombineUseCaseFragment()
    );
}

package com.josetoanto.subastas.core.hardware.data

import android.content.Context
import android.content.pm.PackageManager
import com.josetoanto.subastas.core.hardware.domain.FeatureManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AndroidFeatureManager @Inject constructor(
    @ApplicationContext private val context: Context
) : FeatureManager {

    override fun hasCameraFeature(): Boolean =
        context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
}

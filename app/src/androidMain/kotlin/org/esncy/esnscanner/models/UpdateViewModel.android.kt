package org.esncy.esnscanner.models

import android.app.Activity
import android.content.Context
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import kotlinx.coroutines.tasks.await

actual class UpdateChecker {

    actual suspend fun checkForUpdate(context: Any?): UpdateResult {
        val appUpdateManager: AppUpdateManager = AppUpdateManagerFactory.create(context as Context)

        val info = appUpdateManager.appUpdateInfo.await()

        val isUpdateReady = info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                info.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)

        return UpdateResult(
            isUpdateAvailable = isUpdateReady,
            updateInfo = info
        )
    }

    actual fun startUpdateFlow(updateResult: UpdateResult, context: Any?) {
        val activity = context as? Activity ?: return
        val androidInfo = updateResult.updateInfo as? AppUpdateInfo ?: return
        val appUpdateManager = AppUpdateManagerFactory.create(activity)

        appUpdateManager.startUpdateFlow(
            androidInfo,
            activity,
            AppUpdateOptions.defaultOptions(AppUpdateType.IMMEDIATE),
        )
    }
}
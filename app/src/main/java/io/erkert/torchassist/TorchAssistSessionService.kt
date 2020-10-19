package io.erkert.torchassist

import android.content.pm.PackageManager
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.service.voice.VoiceInteractionSession
import android.service.voice.VoiceInteractionSessionService

class TorchAssistSessionService : VoiceInteractionSessionService() {
    enum class CameraState {
        On,
        Off,
        Unavailable,
    }

    class TorchAssistTorchCallback(private var context: TorchAssistSessionService) : CameraManager.TorchCallback() {

        override fun onTorchModeChanged(cameraId: String, enabled: Boolean) {
            context.onTorchModeChanged(cameraId, enabled)
        }

        override fun onTorchModeUnavailable(cameraId: String) {
            context.onTorchModeUnavailable(cameraId)
        }
    }

    private var cameraManager: CameraManager? = null;
    private var cameraId: String? = null;
    private var cameraState = CameraState.Off;

    private fun init() {
        var isFlashAvailable = applicationContext.packageManager
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)

        if (!isFlashAvailable) {
            return;
        }

        try {
            cameraManager = getSystemService(CAMERA_SERVICE) as CameraManager;

            cameraManager?.let {
                cameraId = it.cameraIdList[0];
                it.registerTorchCallback(TorchAssistTorchCallback(this), null)
            }

        } catch (e : CameraAccessException) {
            isFlashAvailable = false;
        }
    }

    private fun onTorchModeChanged(cameraId: String, enabled: Boolean) {
        if (cameraId != this.cameraId){
            return;
        }

        cameraState = if (enabled) { CameraState.On } else { CameraState.Off }
    }

    private fun onTorchModeUnavailable(cameraId: String) {
        if (cameraId != this.cameraId){
            return;
        }

        cameraState = CameraState.Unavailable;
    }

    override fun onNewSession(p0: Bundle?): VoiceInteractionSession {
        init()

        return TorchAssistSession(this)
    }

    public fun toggleTorch() : Int {
        try {
            when (cameraState) {
                CameraState.On -> {
                    cameraManager?.let { it.setTorchMode(cameraId!!, false) }

                    return R.string.msg_off;
                }
                CameraState.Off -> {
                    cameraManager?.let { it.setTorchMode(cameraId!!, true) }

                    return R.string.msg_on;
                }
                else -> return R.string.msg_unavailable;
            }
        } catch(e : CameraAccessException) {
            return R.string.msg_unavailable;
        }
    }
}

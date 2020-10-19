package io.erkert.torchassist

import android.app.assist.AssistContent
import android.app.assist.AssistStructure
import android.content.Context
import android.os.Bundle
import android.service.voice.VoiceInteractionSession
import android.widget.Toast

class TorchAssistSession(context: Context?) : VoiceInteractionSession(context) {
    override fun onHandleAssist(
        data: Bundle?,
        structure: AssistStructure?,
        content: AssistContent?
    ) {
        super.onHandleAssist(data, structure, content)

        val msg = (context as TorchAssistSessionService).toggleTorch()

        Toast
            .makeText(
                context, msg,
                Toast.LENGTH_LONG
            )
            .show()

        super.finish()
    }
}

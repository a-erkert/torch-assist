package io.erkert.torchassist

import android.app.ListActivity
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast


class MainActivity : ListActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val assistant = Settings.Secure.getString(contentResolver,
                "voice_interaction_service")
        var areWeGood = false
        if (assistant != null) {
            val cn = ComponentName.unflattenFromString(assistant)
            if (cn!!.packageName == packageName) {
                areWeGood = true
            }
        }
        if (areWeGood) {
            Toast
                    .makeText(this, R.string.msg_active, Toast.LENGTH_LONG)
                    .show()
        } else {
            Toast
                    .makeText(this, R.string.msg_activate, Toast.LENGTH_LONG)
                    .show()
            startActivity(Intent(Settings.ACTION_VOICE_INPUT_SETTINGS))
        }
        finish()
    }
}
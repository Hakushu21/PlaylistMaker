package com.example.playlistmaker

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        sharedPreferences = getSharedPreferences("theme_prefs", MODE_PRIVATE)

        val backButton = findViewById<ImageView>(R.id.back_button)
        backButton.setOnClickListener {
            finish()
        }

        val themeSwitch = findViewById<SwitchMaterial>(R.id.theme_switch)
        themeSwitch.isChecked = isDarkThemeEnabled()
        themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            switchTheme(isChecked)
            saveThemeState(isChecked)
        }

        val shareAppLayout = findViewById<LinearLayout>(R.id.share_app_layout)
        shareAppLayout.setOnClickListener {
            shareApp()
        }

        val supportLayout = findViewById<LinearLayout>(R.id.support_layout)
        supportLayout.setOnClickListener {
            sendSupportEmail()
        }

        val userAgreementLayout = findViewById<LinearLayout>(R.id.user_agreement_layout)
        userAgreementLayout.setOnClickListener {
            openUserAgreement()
        }
    }

    private fun isDarkThemeEnabled(): Boolean {
        return AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
    }

    private fun switchTheme(isDarkTheme: Boolean) {
        val mode = if (isDarkTheme) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    private fun saveThemeState(isDarkTheme: Boolean) {
        sharedPreferences.edit()
            .putBoolean("dark_theme", isDarkTheme)
            .apply()
    }

    private fun shareApp() {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_app_text))

        val chooserIntent = Intent.createChooser(shareIntent, getString(R.string.share_app))
        startActivity(chooserIntent)
    }

    private fun sendSupportEmail() {
        val emailIntent = Intent(Intent.ACTION_SENDTO)
        emailIntent.data = Uri.parse("mailto:")
        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support_email)))
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_subject))
        emailIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.support_body))

        try {
            startActivity(emailIntent)
        } catch (e: Exception) {
            val fallbackIntent = Intent(Intent.ACTION_SEND)
            fallbackIntent.type = "message/rfc822"
            fallbackIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support_email)))
            fallbackIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_subject))
            fallbackIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.support_body))

            try {
                startActivity(fallbackIntent)
            } catch (ex: Exception) {

            }
        }
    }

    private fun openUserAgreement() {
        val agreementIntent = Intent(Intent.ACTION_VIEW)
        agreementIntent.data = Uri.parse(getString(R.string.user_agreement_url))

        try {
            startActivity(agreementIntent)
        } catch (e: Exception) {

        }
    }
}
package com.example.playlistmaker.presentation.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.App
import com.example.playlistmaker.R
import com.example.playlistmaker.presentation.Creator
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {

    private val themeInteractor = Creator.provideThemeInteractor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        setupBackButton()
        setupThemeSwitch()
        setupShareApp()
        setupSupport()
        setupUserAgreement()
    }

    private fun setupBackButton() {
        val backButton = findViewById<ImageView>(R.id.back_button)
        backButton.setOnClickListener {
            finish()
        }
    }

    private fun setupThemeSwitch() {
        val themeSwitch = findViewById<SwitchMaterial>(R.id.theme_switch)

        themeSwitch.isChecked = themeInteractor.isDarkTheme()

        themeSwitch.setOnCheckedChangeListener { _, checked ->
            (application as App).switchTheme(checked)
        }
    }

    private fun setupShareApp() {
        val shareAppLayout = findViewById<LinearLayout>(R.id.share_app_layout)
        shareAppLayout.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getString(R.string.share_app_text))
            }
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_app)))
        }
    }

    private fun setupSupport() {
        val supportLayout = findViewById<LinearLayout>(R.id.support_layout)
        supportLayout.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support_email)))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_subject))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.support_body))
            }
            startActivity(emailIntent)
        }
    }

    private fun setupUserAgreement() {
        val userAgreementLayout = findViewById<LinearLayout>(R.id.user_agreement_layout)
        userAgreementLayout.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.user_agreement_url)))
            startActivity(browserIntent)
        }
    }
}
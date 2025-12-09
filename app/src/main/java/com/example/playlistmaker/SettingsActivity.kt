package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {

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

        // Устанавливаем текущее состояние переключателя
        themeSwitch.isChecked = (application as App).darkTheme

        // Обработка изменения состояния переключателя
        themeSwitch.setOnCheckedChangeListener { _, checked ->
            (application as App).switchTheme(checked)
        }
    }

    private fun setupShareApp() {
        val shareAppLayout = findViewById<LinearLayout>(R.id.share_app_layout) // Изменено на LinearLayout
        shareAppLayout.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getString(R.string.share_app_text))
            }
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_app)))
        }
    }

    private fun setupSupport() {
        val supportLayout = findViewById<LinearLayout>(R.id.support_layout) // Изменено на LinearLayout
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
        val userAgreementLayout = findViewById<LinearLayout>(R.id.user_agreement_layout) // Изменено на LinearLayout
        userAgreementLayout.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.user_agreement_url)))
            startActivity(browserIntent)
        }
    }
}
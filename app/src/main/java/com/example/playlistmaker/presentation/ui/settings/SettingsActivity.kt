package com.example.playlistmaker.presentation.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.App
import com.example.playlistmaker.R
import com.google.android.material.switchmaterial.SwitchMaterial
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {

    private val viewModel: SettingsViewModel by viewModel()
    private lateinit var themeSwitch: SwitchMaterial

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        setupBackButton()
        setupThemeSwitch()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupBackButton() {
        val backButton = findViewById<ImageView>(R.id.back_button)
        backButton.setOnClickListener {
            finish()
        }
    }

    private fun setupThemeSwitch() {
        themeSwitch = findViewById(R.id.theme_switch)

        themeSwitch.setOnCheckedChangeListener { _, checked ->
            (application as App).switchTheme(checked)
            viewModel.onThemeSwitched(checked)
        }
    }

    private fun setupClickListeners() {
        val shareAppLayout = findViewById<LinearLayout>(R.id.share_app_layout)
        shareAppLayout.setOnClickListener {
            viewModel.onShareAppClicked()
        }

        val supportLayout = findViewById<LinearLayout>(R.id.support_layout)
        supportLayout.setOnClickListener {
            viewModel.onSupportClicked()
        }

        val userAgreementLayout = findViewById<LinearLayout>(R.id.user_agreement_layout)
        userAgreementLayout.setOnClickListener {
            viewModel.onUserAgreementClicked()
        }
    }

    private fun observeViewModel() {
        viewModel.screenState.observe(this) { state ->
            themeSwitch.isChecked = state.isDarkTheme

            if (state.navigateToShare) {
                shareApp()
                viewModel.onNavigateHandled("share")
            }

            if (state.navigateToSupport) {
                openSupport()
                viewModel.onNavigateHandled("support")
            }

            if (state.navigateToAgreement) {
                openUserAgreement()
                viewModel.onNavigateHandled("agreement")
            }
        }
    }

    private fun shareApp() {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, getString(R.string.share_app_text))
        }
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_app)))
    }

    private fun openSupport() {
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support_email)))
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_subject))
            putExtra(Intent.EXTRA_TEXT, getString(R.string.support_body))
        }
        startActivity(emailIntent)
    }

    private fun openUserAgreement() {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.user_agreement_url)))
        startActivity(browserIntent)
    }
}
package com.okurchenko.ecocity.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.okurchenko.ecocity.ui.main.MainActivity


class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Intent(this@SplashActivity, MainActivity::class.java)
            .also {
                startActivity(it)
                this@SplashActivity.finish()
            }
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}

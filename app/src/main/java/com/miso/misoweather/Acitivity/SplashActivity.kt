package com.miso.misoweather.Acitivity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.miso.misoweather.common.MisoActivity
import com.miso.misoweather.databinding.ActivitySplashBinding
import com.miso.misoweather.Acitivity.home.HomeActivity
import com.miso.misoweather.Acitivity.login.LoginActivity
import com.miso.misoweather.Acitivity.selectRegion.SelectRegionActivity

class SplashActivity : MisoActivity() {
    lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        moveToLogin()
    }

    fun moveToLogin() {
        Handler().postDelayed({
            checkKakaoTokenAndLogin()
            finish()
        }, 2000)
    }

    fun checkKakaoTokenAndLogin() {
        if (getPreference("accessToken").equals("")) {
            startActivity(Intent(this, LoginActivity::class.java))
        } else {
            var intent: Intent
            if (getPreference("misoToken").equals(""))
                intent = Intent(this, SelectRegionActivity::class.java)
            else
                intent = Intent(this, HomeActivity::class.java)


            startActivity(intent)
        }
    }
}
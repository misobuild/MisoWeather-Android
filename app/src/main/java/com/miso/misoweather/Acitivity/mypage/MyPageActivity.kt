package com.miso.misoweather.Acitivity.mypage

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.kakao.sdk.user.UserApiClient
import com.miso.misoweather.Acitivity.home.HomeActivity
import com.miso.misoweather.Acitivity.login.LoginActivity
import com.miso.misoweather.Dialog.GeneralConfirmDialog
import com.miso.misoweather.common.MisoActivity
import com.miso.misoweather.databinding.ActivityMypageBinding
import com.miso.misoweather.model.DTO.GeneralResponseDto
import com.miso.misoweather.model.DTO.LoginRequestDto
import com.miso.misoweather.model.MisoRepository
import com.miso.misoweather.model.TransportManager
import com.miso.misoweather.model.interfaces.MisoWeatherAPI
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception

class MyPageActivity : MisoActivity() {
    lateinit var binding: ActivityMypageBinding
    lateinit var btn_back: ImageButton
    lateinit var btn_logout: Button
    lateinit var btn_unregister: Button
    lateinit var btn_version: Button
    lateinit var txt_version: TextView
    lateinit var txt_emoji: TextView
    lateinit var txt_nickname: TextView
    lateinit var viewModel: MyPageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMypageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeView()
    }

    fun getVersionString(): String {
        return this.packageManager.getPackageInfo(this.packageName, 0).versionName
    }

    fun initializeView() {
        viewModel = MyPageViewModel(MisoRepository.getInstance(applicationContext))
        btn_back = binding.imgbtnBack
        btn_logout = binding.btnLogout
        btn_unregister = binding.btnUnregister
        btn_version = binding.btnVersion
        txt_version = binding.txtVersion
        txt_emoji = binding.txtEmoji
        txt_nickname = binding.txtNickname

        txt_emoji.text = getPreference("emoji")
        txt_nickname.text = getPreference("nickname")
        txt_version.text = getVersionString()
        btn_version.setOnClickListener()
        {
            val dialog = GeneralConfirmDialog(
                this,
                null,
                "?????? ${getVersionString()}\n\n" + "\uD83D\uDC65?????????\n" +
                        "-\uD83E\uDD16??????????????? ??????: ?????????\n" +
                        "-\uD83C\uDF4EiOS ??????: ?????????,?????????\n" +
                        "-\uD83D\uDCE6?????? ??????: ?????????\n" +
                        "-\uD83C\uDFA8UI/UX ?????????: ?????????",
                "??????",
                0.8f,
                0.4f
            )
            dialog.show(supportFragmentManager, "generalConfirmDialog")
        }
        btn_back.setOnClickListener()
        {
            doBack()
        }
        btn_unregister.setOnClickListener()
        {
            val dialog = GeneralConfirmDialog(
                this,
                {
                    unregister()
                },
                "????????? ????????? ???????????????? \uD83D\uDE22",
                "??????"
            )
            dialog.show(supportFragmentManager, "generalConfirmDialog")
        }
        btn_logout.setOnClickListener()
        {
            val dialog = GeneralConfirmDialog(
                this,
                {
                    logout()
                },
                "???????????? ??????????????????? \uD83D\uDD13",
                "????????????"
            )
            dialog.show(supportFragmentManager, "generalConfirmDialog")
        }
    }

    override fun doBack() {
        startActivity(Intent(this, HomeActivity::class.java))
        transferToBack()
        finish()
    }

    fun goToLoginActivity() {
        startActivity(Intent(this, LoginActivity::class.java))
        transferToBack()
        finish()
    }

    fun unregister() {
        viewModel.unRegister(makeLoginRequestDto())
        viewModel.unRegisterResponse.observe(this, {
            try {
                if (it is Response<*>) {
                    if (it.isSuccessful) {
                        goToLoginActivity()
                        Log.i("unregister", "??????")
                    } else {
                        goToLoginActivity()
                        throw Exception(it.errorBody()!!.source().toString())
                    }
                } else {
                    if (it is String) {
                        throw Exception(it)
                    } else {
                        if (it is Throwable)
                            throw it
                        else
                            throw Exception()
                    }
                }
            } catch (e: Exception) {
                Log.e("unregister", e.stackTraceToString())
                Log.e("unregister", e.message.toString())
                Toast.makeText(this, "????????? ?????? ?????? ????????? ?????????????????????.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun makeLoginRequestDto(): LoginRequestDto {
        var loginRequestDto = LoginRequestDto(
            getPreference("socialId"),
            getPreference("socialType")
        )
        return loginRequestDto
    }

    fun logout() {
        UserApiClient.instance.logout { error ->
            if (error != null) {
                Log.e("kakaoLogout", "???????????? ??????. SDK?????? ?????? ?????????", error)
                UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
                    if (error != null) {
                        Log.e("", "?????? ?????? ?????? ??????", error)
                    } else if (tokenInfo != null) {
                        Log.i(
                            "", "?????? ?????? ?????? ??????" +
                                    "\n????????????: ${tokenInfo.id}" +
                                    "\n????????????: ${tokenInfo.expiresIn} ???"
                        )
                    }
                }
            } else {
                Log.i("kakaoLogout", "???????????? ??????. SDK?????? ?????? ?????????")
                removePreference("accessToken", "socialId", "socialType", "misoToken")
                savePreferences()
                goToLoginActivity()
            }
        }
    }

}
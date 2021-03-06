package com.miso.misoweather.Acitivity.login

import android.animation.Animator
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import androidx.viewpager2.widget.ViewPager2
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.kakao.sdk.user.model.AccessTokenInfo
import com.miso.misoweather.Acitivity.home.HomeActivity
import com.miso.misoweather.Acitivity.login.viewPagerFragments.*
import com.miso.misoweather.Acitivity.selectRegion.SelectRegionActivity
import com.miso.misoweather.Dialog.GeneralConfirmDialog
import com.miso.misoweather.common.MisoActivity
import com.miso.misoweather.databinding.ActivityLoginBinding
import com.miso.misoweather.model.DTO.GeneralResponseDto
import com.miso.misoweather.model.DTO.LoginRequestDto
import com.miso.misoweather.model.MisoRepository
import com.miso.misoweather.model.TransportManager
import com.rd.PageIndicatorView
import retrofit2.Response


class LoginActivity : MisoActivity() {
    lateinit var binding: ActivityLoginBinding
    lateinit var viewpager_onboarding: ViewPager2
    lateinit var pageIndicatorView: PageIndicatorView
    lateinit var viewModel: LoginViewModel
    lateinit var socialId: String
    lateinit var socialType: String
    lateinit var accessToken: String
    val onBoardFragmentList =
        listOf(
            OnBoardInitFragment(),
            OnBoardApparellFragment(),
            OnBoardFoodFragment(),
            OnBoardLocationFragment(),
            OnBoardChatFragment()
        )
    var isCheckValid = false
    var currentPosition = 0
    var isAllInitialized = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeProperties()
    }

    fun initializeProperties() {
        fun checkinitializedAll() {
            if (!isAllInitialized) {
                if (
                    this::accessToken.isInitialized &&
                    this::socialId.isInitialized &&
                    this::socialType.isInitialized
                ) {
                    initializeView()
                    isAllInitialized = true
                }
            }
        }
        viewModel = LoginViewModel(MisoRepository.getInstance(applicationContext))
        viewModel.updateProperties()
        viewModel.accessToken.observe(this, {
            accessToken = it!!
            checkinitializedAll()
        })
        viewModel.socialType.observe(this, {
            socialType = it!!
            checkinitializedAll()
        })
        viewModel.socialId.observe(this, {
            socialId = it!!
            checkinitializedAll()
        })
    }

    @SuppressLint("LongLogTag")
    fun initializeView() {
        fun initializePageIndicatorView() {
            try {
                pageIndicatorView = binding.pageIndicatorView
                pageIndicatorView.setCount(onBoardFragmentList.size)
            } catch (e: Exception) {
                Log.i("initializePageIndicatorView", e.stackTraceToString())
                throw Exception("pageIndicatorView is null")
            }
        }

        fun matchPagerIndicator() {
            try {
                initializePageIndicatorView()
                viewpager_onboarding.registerOnPageChangeCallback(object :
                    ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        try {
                            super.onPageSelected(position)
                            pageIndicatorView.setSelected(position)
                        } catch (e: Exception) {
                            Log.i("viewpate_onboarding.onPageSelected", e.stackTraceToString())
                        }
                    }
                })
            } catch (e: Exception) {
                Log.i("matchPagerIndicator", e.stackTraceToString())
            }
        }

        fun setupViewPagerAndIndicator() {
            fun initializeViewPager() {
                viewpager_onboarding = binding.viewPagerOnBoarding
                viewpager_onboarding.adapter =
                    ViewPagerFragmentAdapter(
                        this,
                        onBoardFragmentList
                    )
                viewpager_onboarding.orientation = ViewPager2.ORIENTATION_HORIZONTAL
            }
            try {
                initializeViewPager()
                Thread(PagerRunnable()).start()
                matchPagerIndicator()
            } catch (e: Exception) {
                Log.i("initializeViewPager", e.stackTraceToString())
            }
        }

        checkTokenValid()
        setupViewPagerAndIndicator()
        binding.clBtnKakaoLogin.setOnClickListener {
            if (hasValidToken())
                checkRegistered()
            else
                kakaoLogin()
        }
    }

    fun hasValidToken(): Boolean {
        return (isCheckValid &&
                !socialId.isNullOrBlank() &&
                !socialType.isNullOrBlank())
    }

    fun checkTokenValid() {
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(this@LoginActivity)) {
            UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
                if (error != null) {
                    Log.i("token", "?????? ?????? ?????? ??????", error)
                    isCheckValid = false
                } else if (tokenInfo != null) {
                    Log.i(
                        "token", "?????? ?????? ?????? ??????" +
                                "\n????????????:${tokenInfo.id}"
                    )
                    isCheckValid = true
                }
            }
        } else
            isCheckValid = false
    }

    fun showDialogForInstallingKakaoTalk() {
        fun goToStoreForInstallingKakaoTalk(generalConfirmDialog: GeneralConfirmDialog) {
            try {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.addCategory(Intent.CATEGORY_DEFAULT)
                intent.data = Uri.parse("market://details?id=com.kakao.talk")
                startActivity(intent)
            } catch (e: Exception) {
                Log.e("instaillingKakaoTalk", e.stackTraceToString())
                Toast.makeText(this, "???????????? ????????? ??????????????????.", Toast.LENGTH_SHORT).show()
                generalConfirmDialog.dismiss()
            }
        }

        lateinit var generalConfirmDialog: GeneralConfirmDialog

        generalConfirmDialog = GeneralConfirmDialog(
            this,
            {
                goToStoreForInstallingKakaoTalk(generalConfirmDialog)
            }, "?????????????????? ???????????? ????????? ???????????????.\n?????????????????????????"
        )

        generalConfirmDialog.show(supportFragmentManager, "generalConfirmDialog")
    }

    fun showDialogForLoginKakaoTalk() {
        fun launchKakaoTalk() {
            try {
                val intent =
                    packageManager.getLaunchIntentForPackage("com.kakao.talk")
                intent!!.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            } catch (e: Exception) {

            }
        }
        GeneralConfirmDialog(
            this,
            {
                launchKakaoTalk()
            },
            "??????????????? ??????????????? ???????????????.\n?????????????????????????"
        ).show(supportFragmentManager, "generalConfirmDialog")
    }

    fun kakaoLogin() {
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(this@LoginActivity)) {
            loginWithKakaoTalk()
        } else {
            showDialogForInstallingKakaoTalk()
        }
    }

    fun loginWithKakaoTalk() {
        UserApiClient.instance.loginWithKakaoTalk(this@LoginActivity) { token, error ->
            try {
                if (error != null) {
                    Log.e("miso", "????????? ??????", error)
                    showDialogForLoginKakaoTalk()
                } else if (token != null) {
                    Log.i("miso", "????????? ?????? ${token.accessToken}")
                    UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
                        if (error != null) {
                            Log.i("token", "?????? ?????? ?????? ??????", error)
                            Toast.makeText(this, "????????? ?????? ??? ????????? ?????????????????????.", Toast.LENGTH_SHORT).show()
                        } else if (tokenInfo != null) {
                            Log.i("token", "?????? ?????? ?????? ??????" + "\n????????????:${tokenInfo.id}")
                            viewModel.saveTokenInfo(token, tokenInfo)
                            issueMisoToken()
                        }
                    }
                }
            } catch (e: Exception) {
                Log.i("kakaoLogin", e.stackTraceToString())
                Toast.makeText(this, "????????? ?????? ??? ????????? ?????????????????????.", Toast.LENGTH_SHORT).show()
            }
        }
    }


    fun startRegionActivity() {
        startActivity(Intent(this, SelectRegionActivity::class.java))
        transferToNext()
        finish()
    }

    fun startHomeActivity() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

    fun issueMisoToken() {
        viewModel.issueMisoToken()
        viewModel.issueMisoTokenResponse.observe(this, {
            if (it is Response<*>) {
                if (it.isSuccessful) {
                    startHomeActivity()
                } else {
                    Log.i("issueMisoToken", "??????")
                    var errorString = it.errorBody()!!.source().toString()
                    if (errorString.contains("UNAUTHORIZED"))
                        kakaoLogin()
                    else if (errorString.contains("NOT_FOUND")) {
                        startRegionActivity()
                    } else {
                        Log.i("issueMisoToken", errorString)
                        Toast.makeText(this, "????????? ?????? ?????? ??? ????????? ?????????????????????.", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                startRegionActivity()
            }
        })
    }


    fun checkRegistered() {
        viewModel.checkRegistered()
        viewModel.checkRegistered.observe(this, {
            if (it!!)
                issueMisoToken()
            else
                startRegionActivity()
        })
    }


    inner class PagerRunnable : Runnable {
        override fun run() {
            while (true) {
                Thread.sleep(3000)
                Handler(Looper.getMainLooper()) {
                    fun setPage() {
                        fun ViewPager2.setCurrentItem(
                            item: Int,
                            duration: Long,
                            interpolator: TimeInterpolator = AccelerateDecelerateInterpolator(),
                            pagePxWidth: Int = width, // Default value taken from getWidth() from ViewPager2 view
                            pagePxHeight: Int = height
                        ) {
                            val pxToDrag: Int =
                                if (orientation == ViewPager2.ORIENTATION_HORIZONTAL)
                                    pagePxWidth * (item - currentItem)
                                else
                                    pagePxHeight * (item - currentItem)

                            val animator = ValueAnimator.ofInt(0, pxToDrag)
                            var previousValue = 0
                            animator.addUpdateListener { valueAnimator ->
                                val currentValue = valueAnimator.animatedValue as Int
                                val currentPxToDrag = (currentValue - previousValue).toFloat()
                                fakeDragBy(-currentPxToDrag)
                                previousValue = currentValue
                            }
                            animator.addListener(object : Animator.AnimatorListener {
                                override fun onAnimationStart(animation: Animator?) {
                                    beginFakeDrag()
                                }

                                override fun onAnimationEnd(animation: Animator?) {
                                    endFakeDrag()
                                }

                                override fun onAnimationCancel(animation: Animator?) { /* Ignored */
                                }

                                override fun onAnimationRepeat(animation: Animator?) { /* Ignored */
                                }
                            })
                            animator.interpolator = interpolator
                            animator.duration = duration
                            animator.start()
                        }
                        if (currentPosition == 5) currentPosition = 0
                        viewpager_onboarding.setCurrentItem(currentPosition, 500)
                        currentPosition += 1
                    }
                    setPage()
                    true
                }.sendEmptyMessage(0)
            }
        }
    }
}
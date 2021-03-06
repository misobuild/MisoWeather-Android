package com.miso.misoweather.model.interfaces

import com.miso.misoweather.model.DTO.*
import com.miso.misoweather.model.DTO.CommentList.CommentListResponseDto
import com.miso.misoweather.model.DTO.Forecast.Brief.ForecastBriefResponseDto
import com.miso.misoweather.model.DTO.Forecast.CurrentAir.CurrentAirResponseDto
import com.miso.misoweather.model.DTO.Forecast.Daily.DailyForecastResponseDto
import com.miso.misoweather.model.DTO.Forecast.Hourly.HourlyForecastResponseDto
import com.miso.misoweather.model.DTO.MemberInfoResponse.MemberInfoResponseDto
import com.miso.misoweather.model.DTO.NicknameResponse.NicknameResponseDto
import com.miso.misoweather.model.DTO.RegionListResponse.RegionListResponseDto
import com.miso.misoweather.model.DTO.SurveyAddMyAnswer.SurveyAddMyAnswerRequestDto
import com.miso.misoweather.model.DTO.SurveyAddMyAnswer.SurveyAddMyAnswerResponseDto
import com.miso.misoweather.model.DTO.SurveyMyAnswer.SurveyMyAnswerResponseDto
import com.miso.misoweather.model.DTO.SurveyResponse.SurveyAnswerResponseDto
import com.miso.misoweather.model.DTO.SurveyResultResponse.SurveyResultResponseDto
import retrofit2.Call
import retrofit2.http.*

interface MisoWeatherAPI {
    @GET("api/member/nickname")
    fun getNickname(): Call<NicknameResponseDto>

    @POST("api/member")
    fun registerMember(
        @Body body: SignUpRequestDto,
        @Query("socialToken") socialToken: String
    ): Call<GeneralResponseDto>

    @GET("api/region/{bigScaleRegion}")
    fun getCity(@Path("bigScaleRegion") bigScaleRegion: String): Call<RegionListResponseDto>

    @GET("api/region/{bigScaleRegion}/{midScaleRegion}")
    fun getArea(
        @Path("bigScaleRegion") bigScaleRegion: String,
        @Path("midScaleRegion") midScaleRegion: String
    ): Call<RegionListResponseDto>

    @POST("api/member/token")
    fun reIssueMisoToken(
        @Body body: LoginRequestDto,
        @Query("socialToken") socialToken: String
    ): Call<GeneralResponseDto>

    @HTTP(method = "DELETE", path = "api/member/", hasBody = true)
    fun unregisterMember(
        @Header("serverToken") serverToken: String,
        @Body body: LoginRequestDto
    ): Call<GeneralResponseDto>

    @GET("api/member")
    fun getUserInfo(@Header("serverToken") serverToken: String): Call<MemberInfoResponseDto>

    @GET("api/new-forecast/{regionId}")
    fun getBriefForecast(@Path("regionId") regionId: Int): Call<ForecastBriefResponseDto>

    @GET("api/forecast/{regionId}/detail")
    fun getDetailForecast(@Path("regionId") regionId: Int): Call<DailyForecastResponseDto>

    @GET("api/comment")
    fun getCommentList(
        @Query("commentId") commentId: Int?,
        @Query("size") size: Int
    ): Call<CommentListResponseDto>

    @POST("api/comment")
    fun addComment(
        @Header("serverToken") serverToken: String,
        @Body body: CommentRegisterRequestDto
    ): Call<GeneralResponseDto>

    @GET("api/survey/answers/{surveyId}")
    fun getSurveyAnswers(@Path("surveyId") surveyId: Int): Call<SurveyAnswerResponseDto>

    @GET("api/survey")
    fun getSurveyResults(@Query("shortBigScale") shortBigScale: String?=null): Call<SurveyResultResponseDto>

    @GET("api/survey/member")
    fun getSurveyMyAnswers(@Header("serverToken") serverToken: String): Call<SurveyMyAnswerResponseDto>

    @POST("api/survey")
    fun putSurveyMyAnser(
        @Header("serverToken") serverToken: String,
        @Body answerSurveyDto: SurveyAddMyAnswerRequestDto
    ): Call<SurveyAddMyAnswerResponseDto>

    @PUT("api/member-region-mapping/default")
    fun updateRegion(
        @Header("serverToken") serverToken: String,
        @Query("regionId") regionId: Int
    ): Call<GeneralResponseDto>

    @GET("api/member/existence")
    fun checkRegistered(
        @Query("socialId") socialId: String,
        @Query("socialType") socialType: String
    ): Call<GeneralResponseDto>

    @GET("/api/new-forecast/update/{regionId}")
    fun loadWeatherInfo(@Path("regionId") regionId: Int): Call<GeneralResponseDto>

    @GET("/api/new-forecast/daily/{regionId}")
    fun getDailyForecast(@Path("regionId") regionId: Int): Call<DailyForecastResponseDto>

    @GET("/api/new-forecast/hourly/{regionId}")
    fun getHourlyForecast(@Path("regionId") regionId: Int): Call<HourlyForecastResponseDto>

    @GET("/api/new-forecast/airdust/{regionId}")
    fun getCurrentAir(@Path("regionId") regionId: Int): Call<CurrentAirResponseDto>
}
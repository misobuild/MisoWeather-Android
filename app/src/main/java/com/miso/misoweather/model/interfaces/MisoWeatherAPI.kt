package com.miso.misoweather.model.interfaces

import com.miso.misoweather.model.DTO.*
import com.miso.misoweather.model.DTO.CommentList.CommentListResponseDto
import com.miso.misoweather.model.DTO.Forecast.Brief.ForecastBriefResponseDto
import com.miso.misoweather.model.DTO.Forecast.Detail.ForecastDetailResponseDto
import com.miso.misoweather.model.DTO.MemberInfoResponse.MemberInfoResponseDto
import com.miso.misoweather.model.DTO.NicknameResponse.NicknameResponseDto
import com.miso.misoweather.model.DTO.RegionListResponse.RegionListResponseDto
import com.miso.misoweather.model.DTO.SurveyResponse.SurveyAnswerResponseDto
import retrofit2.Call
import retrofit2.http.*

interface MisoWeatherAPI {
 @GET("api/member/nickname")
 fun getNickname(): Call<NicknameResponseDto>

 @POST("api/member")
 fun registerMember(@Body body:SignUpRequestDto,@Query("socialToken") socialToken:String):Call<GeneralResponseDto>

 @GET("api/region/{bigScaleRegion}")
 fun getCity(@Path("bigScaleRegion") bigScaleRegion:String):Call<RegionListResponseDto>

 @GET("api/region/{bigScaleRegion}/{midScaleRegion}")
 fun getArea(@Path("bigScaleRegion") bigScaleRegion:String,
 @Path("midScaleRegion")midScaleRegion:String):Call<RegionListResponseDto>

 @POST("api/member/token")
 fun reIssueMisoToken(@Body body: LoginRequestDto, @Query("socialToken")socialToken: String):Call<GeneralResponseDto>

 @HTTP(method = "DELETE", path="api/member/",hasBody = true)
 fun unregisterMember(@Header("serverToken") serverToken:String,@Body body: LoginRequestDto):Call<GeneralResponseDto>

 @GET("api/member")
 fun getUserInfo(@Header("serverToken") serverToken:String):Call<MemberInfoResponseDto>

 @GET("api/forecast/{regionId}")
 fun getBriefForecast(@Path("regionId")regionId:Int):Call<ForecastBriefResponseDto>

 @GET("api/forecast/{regionId}/detail")
 fun getDetailForecast(@Path("regionId")regionId:Int):Call<ForecastDetailResponseDto>

@GET("api/comment")
fun getCommentList(@Query("commentId")commentId:Int?,@Query("size")size:Int):Call<CommentListResponseDto>

 @POST("api/comment")
 fun addComment(@Header("serverToken") serverToken: String,@Body body: CommentRegisterRequestDto):Call<GeneralResponseDto>

 @GET("api/survey/answers/{surveyId}")
 fun getSurveyAnswers(@Path("surveyId")surveyId:Int):Call<SurveyAnswerResponseDto>

}
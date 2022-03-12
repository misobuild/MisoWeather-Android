package com.miso.misoweather.Acitivity.chatmain

import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.miso.misoweather.R
import com.miso.misoweather.common.MisoActivity
import com.miso.misoweather.model.DTO.CommentList.CommentListResponseDto
import com.miso.misoweather.model.DTO.CommentRegisterRequestDto
import com.miso.misoweather.model.DTO.Forecast.Brief.ForecastBriefResponseDto
import com.miso.misoweather.model.DTO.GeneralResponseDto
import com.miso.misoweather.model.DTO.MemberInfoResponse.MemberInfoResponseDto
import com.miso.misoweather.model.DTO.NicknameResponse.NicknameResponseDto
import com.miso.misoweather.model.DTO.SurveyMyAnswer.SurveyMyAnswerData
import com.miso.misoweather.model.DTO.SurveyMyAnswer.SurveyMyAnswerDto
import com.miso.misoweather.model.DTO.SurveyMyAnswer.SurveyMyAnswerResponseDto
import com.miso.misoweather.model.DTO.SurveyResponse.SurveyAnswerDto
import com.miso.misoweather.model.DTO.SurveyResultResponse.SurveyResultData
import com.miso.misoweather.model.DTO.SurveyResultResponse.SurveyResultResponseDto
import com.miso.misoweather.model.MisoRepository
import retrofit2.Response
import java.lang.Exception

class ChatMainViewModel(private val repository: MisoRepository) : ViewModel() {
    val commentListResponse: MutableLiveData<Response<CommentListResponseDto>?> = MutableLiveData()
    val addCommentResponse: MutableLiveData<Response<GeneralResponseDto>?> = MutableLiveData()
    lateinit var surveyQuestions: Array<String>
    var surveyAnswerMap: HashMap<Int, List<SurveyAnswerDto>> = HashMap()
    var surveyResultResponseDto = SurveyResultResponseDto(
        SurveyResultData(
            listOf()
        ), "", ""
    )
    var surveyMyAnswerResponseDto = SurveyMyAnswerResponseDto(SurveyMyAnswerData(listOf()), "", "")
    var surveyItems: MutableLiveData<ArrayList<SurveyItem>> = MutableLiveData(ArrayList())

    fun getCommentList(commentId: Int?, size: Int) {
        repository.getCommentList(
            commentId,
            size,
            { call, response ->
                commentListResponse.value = response
            },
            { call, response ->
                commentListResponse.value = response
            },
            { call, throwable -> }
        )
    }

    fun addComment(
        shortBigScale: String,
        commentRegisterRequestDto: CommentRegisterRequestDto,
    ) {
        repository.addComment(
            shortBigScale,
            commentRegisterRequestDto,
            { call, response ->
                addCommentResponse.value = response
            },
            { call, response ->
                addCommentResponse.value = response
            },
            { call, t ->
//                    Log.i("결과", "실패 : $t")
            },
        )
    }

    fun setupSurveyAnswerList(activity: ChatMainActivity) {
        surveyQuestions = activity.resources.getStringArray(R.array.survey_questions)
        surveyQuestions.forEachIndexed { i, item ->
            Log.i("surveyAnswer", "surveyId:" + (i + 1).toString())
            getSurveyAnswer(i + 1)
        }
    }

    fun getSurveyAnswer(surveyId: Int) {
        repository.getSurveyAnswers(
            surveyId,
            { call, response ->
                initializeDataAndSetupRecycler {
                    surveyAnswerMap.put(surveyId, (response.body()!!).data.responseList)
                }
            },
            { call, response -> },
            { call, t -> },
        )
    }

    fun getSurveyResult(shortBigScale: String) {
        repository.getSurveyResults(
            shortBigScale,
            { call, reponse ->
                initializeDataAndSetupRecycler {
                    surveyResultResponseDto = reponse.body()!!
                }
            },
            { call, reponse ->
            },
            { call, t ->

            },
        )
    }

    fun getSurveyMyAnswers(serverToken: String) {
        repository.getSurveyMyAnswers(
            serverToken,
            { call, reponse ->
                initializeDataAndSetupRecycler {
                    surveyMyAnswerResponseDto = reponse.body()!!
                }
            },
            { call, response ->
            },
            { call, throwable -> }
        )
    }

    private fun makeSurveyItems() {
        surveyItems.value = ArrayList()
        val comparator: Comparator<SurveyMyAnswerDto> = compareBy { it.surveyId }
        var sortedMyanswerList = surveyMyAnswerResponseDto.data.responseList.sortedWith(comparator)

        surveyQuestions.forEachIndexed { index, s ->
            val surveyItem = SurveyItem(
                index + 1,
                s,
                sortedMyanswerList[index],
                surveyAnswerMap.get(index + 1)!!,
                surveyResultResponseDto.data.responseList[index]
            )
            var tempList = surveyItems.value!!
            tempList.add(surveyItem)
            surveyItems.value = tempList
        }
    }

    fun initializeDataAndSetupRecycler(func: () -> Unit) {
        func()
        if (isAllSurveyResponseInitialized())
            makeSurveyItems()
    }

    fun isAllSurveyResponseInitialized(): Boolean {
        return ((surveyAnswerMap.size >= surveyQuestions.size) &&
                (surveyMyAnswerResponseDto.data.responseList.size>= surveyQuestions.size)&&
                (surveyResultResponseDto.data.responseList.size>= surveyQuestions.size))
    }
}
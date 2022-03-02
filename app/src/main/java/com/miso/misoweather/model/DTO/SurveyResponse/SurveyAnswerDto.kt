package com.miso.misoweather.model.DTO.SurveyResponse

import java.io.Serializable

data class SurveyAnswerDto(
    val answer: String="",
    val answerDescription: String="",
    val answerId: Int=-1,
    val surveyId: Int=-1,
    val surveyDescription: String="",
    val surveyTitle: String="",
) : Serializable
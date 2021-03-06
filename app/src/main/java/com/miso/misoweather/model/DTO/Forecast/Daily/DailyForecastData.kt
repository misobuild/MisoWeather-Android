package com.miso.misoweather.model.DTO.Forecast.Daily

import com.miso.misoweather.model.DTO.Region
import java.io.Serializable

data class DailyForecastData(
    val dailyForecastList: List<DailyForecast>,
    val region: Region,
    val pop:String,
    val popIcon:String,
    val rain:String,
    val snow:String,
):Serializable
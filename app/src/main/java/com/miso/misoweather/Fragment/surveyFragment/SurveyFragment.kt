package com.miso.misoweather.Fragment.surveyFragment

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.miso.misoweather.Acitivity.chatmain.ChatMainActivity
import com.miso.misoweather.Acitivity.chatmain.ChatMainViewModel
import com.miso.misoweather.Acitivity.chatmain.SurveyItem
import com.miso.misoweather.R
import com.miso.misoweather.common.MisoActivity
import com.miso.misoweather.databinding.FragmentSurveyBinding
import com.miso.misoweather.model.DTO.SurveyMyAnswer.SurveyMyAnswerDto
import com.miso.misoweather.model.DTO.SurveyMyAnswer.SurveyMyAnswerResponseDto
import com.miso.misoweather.model.DTO.SurveyResponse.SurveyAnswerDto
import com.miso.misoweather.model.DTO.SurveyResponse.SurveyAnswerResponseDto
import com.miso.misoweather.model.DTO.SurveyResultResponse.SurveyResultResponseDto
import com.miso.misoweather.model.MisoRepository
import com.miso.misoweather.model.TransportManager

@RequiresApi(Build.VERSION_CODES.O)
class SurveyFragment : Fragment() {
    lateinit var binding: FragmentSurveyBinding
    lateinit var recyclerSurvey: RecyclerView
    lateinit var recyclerSurveysAdapter: RecyclerSurveysAdapter
    lateinit var currentLocation: String
    lateinit var bigShortScale: String
    lateinit var activity: ChatMainActivity
    lateinit var viewModel: ChatMainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentSurveyBinding.inflate(layoutInflater)
        activity = getActivity() as ChatMainActivity
        viewModel = activity.viewModel
        bigShortScale = activity.selectedRegion
        initializeView()
        setupData()
    }

    fun setupData() {
        if (viewModel.surveyItems.value.isNullOrEmpty()) {
            viewModel.setupSurveyAnswerList(activity)
            setupSurveyResult(bigShortScale)
            setupSurveyMyAnswer()
        }
    }

    fun initializeView() {
        currentLocation = activity.selectedRegion
        recyclerSurvey = binding.recyclerSurveys
        viewModel.surveyItems.observe(this, {
            if (it.size > 0)
                setupRecyclerSurveys(it)
        })
    }


    fun setupRecyclerSurveys(surveyItems: ArrayList<SurveyItem>) {
        try {
            recyclerSurveysAdapter = RecyclerSurveysAdapter(activity, surveyItems)
            recyclerSurvey.adapter = recyclerSurveysAdapter
            recyclerSurvey.layoutManager = LinearLayoutManager(activity.baseContext)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = binding.root
        // ??????
        return view
    }

    fun setupSurveyResult(bigShortScale: String) {
        viewModel.getSurveyResult(bigShortScale)
    }

    fun setupSurveyMyAnswer() {
        viewModel.getSurveyMyAnswers(
            (requireActivity() as MisoActivity).getPreference("misoToken")!!
        )
    }

}
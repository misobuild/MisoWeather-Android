package com.miso.misoweather.Fragment.commentFragment

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.miso.misoweather.Acitivity.chatmain.ChatMainViewModel
import com.miso.misoweather.Acitivity.home.RecyclerChatsAdapter
import com.miso.misoweather.common.MisoActivity
import com.miso.misoweather.databinding.FragmentCommentBinding
import com.miso.misoweather.model.DTO.CommentList.CommentListResponseDto
import com.miso.misoweather.model.DTO.CommentRegisterRequestDto
import java.lang.Exception

@RequiresApi(Build.VERSION_CODES.O)
class CommentsFragment(val viewModel: ChatMainViewModel) : Fragment() {
    lateinit var binding: FragmentCommentBinding
    lateinit var recyclerChatAdapter: RecyclerChatsAdapter
    lateinit var refreshLayout: SwipeRefreshLayout
    lateinit var recyclerChat: RecyclerView
    lateinit var edtComment: EditText
    lateinit var btnSubmit: Button
    lateinit var activity: MisoActivity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentCommentBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = binding.root
        initializeViews()
        getCommentList(null)

        return view
    }

    fun initializeViews() {
        activity = getActivity() as MisoActivity
        recyclerChat = binding.recyclerChat
        refreshLayout = binding.refreshLayout
        refreshLayout.setOnRefreshListener {
            getCommentList(null)
            refreshLayout.isRefreshing = false
        }
        edtComment = binding.edtComment
        edtComment.hint = "?????? ????????? ?????? ${activity.getPreference("nickname")!!}?????? ????????? ????????????????"
        btnSubmit = binding.btnSubmit
        btnSubmit.setOnClickListener()
        {
            addComent()
        }
        recyclerChat.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                var lastVisibleItemPosition =
                    ((recyclerChat.layoutManager) as LinearLayoutManager).findLastVisibleItemPosition()
                var itemTotalCount = recyclerChat.adapter!!.itemCount - 1
                if (lastVisibleItemPosition == itemTotalCount) {
                    Log.i("Paging", "?????????")
                }
            }
        })
        viewModel.commentListResponse.observe(activity,{
            try {
                Log.i("??????", "??????")
                setRecyclerChats(it!!.body()!!)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        })
        viewModel.addCommentResponse.observe(activity,{
            try {
                Log.i("??????", "??????")
                getCommentList(null)
                edtComment.text.clear()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        })
    }

    fun addComent() {
        if (edtComment.text.toString().length < 2) {
            Toast.makeText(context, "???????????? 2??? ?????? ??????????????????.", Toast.LENGTH_SHORT).show()
        } else {
            viewModel.addComment(
                activity.getPreference("misoToken")!!,
                CommentRegisterRequestDto(edtComment.text.toString()))
        }
    }

    fun getCommentList(commentId: Int?) {
        viewModel.getCommentList(
            commentId,
            5
        )
    }

    fun setRecyclerChats(commentListResponseDto: CommentListResponseDto) {
        try {
            recyclerChatAdapter = RecyclerChatsAdapter(
                activity.baseContext,
                commentListResponseDto.data.commentList,
                true
            )
            recyclerChat.adapter = recyclerChatAdapter
            recyclerChat.layoutManager = LinearLayoutManager(activity.baseContext)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
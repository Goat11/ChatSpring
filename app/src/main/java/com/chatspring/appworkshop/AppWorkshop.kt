package com.chatspring.appworkshop

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import com.chatspring.R
import com.chatspring.bmob_data.workshop
import com.chatspring.*
import com.google.android.material.snackbar.Snackbar


class AppWorkshop : Fragment(), CardAdapter.OnItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var cardAdapter: CardAdapter
    private var cardDataList = mutableListOf<CardData>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.review_app_workshop, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        cardAdapter = CardAdapter(cardDataList, this)
        recyclerView.adapter = cardAdapter

        //请求BMOB云，将获取的加到cardDataList中
        val bmobQuery = BmobQuery<workshop>()
        bmobQuery.findObjects(object : FindListener<workshop>() {
            override fun done(applist: List<workshop>, e: BmobException?) {

                if (e == null) {
                    if (applist.isEmpty()) {
                        val dialog = AlertDialog.Builder(activity)
                        dialog.setTitle("查询应用工作失败")
                        dialog.setMessage("很抱歉,暂时没有可查询的应用工坊信息!")
                        dialog.setPositiveButton("确定") { _, _ -> }
                        dialog.show()
                    } else {
                        for (APP in applist) {
                            val dialog = AlertDialog.Builder(activity)
                            val appname = APP.appname
                            val appintro = APP.appintro
                            val appprompt = APP.appprompt
                            var iconname = APP.icon
                            var resourceId: Int = 0
                            // 如果没有设置图标，则使用默认的"chat"图标
                            if (iconname == null){
                                iconname = "chat"

                            }
                            // 如果设置了图标，则尝试获取资源ID
                            else{
                                resourceId = resources.getIdentifier(iconname, "drawable", activity?.packageName)
                                // 如果找不到则使用默认的"chat"资源ID
                                if (resourceId == 0){
                                    var temp =  resources.getIdentifier("chat", "drawable", activity?.packageName)
                                    resourceId = temp
                                }
                            }
                            if(appname !=null&&appintro !=null&&appprompt !=null){
                                cardDataList.add(CardData(resourceId, appname,appintro,appprompt,iconname))
                            }
                        }
                        // 通知适配器数据已更改
                        cardAdapter.notifyDataSetChanged()
                    }
                } else {
                    val dialog = AlertDialog.Builder(activity)
                    dialog.setTitle("查询应用工坊失败")
                    dialog.setMessage("服务器错误,请稍后再试!")
                    dialog.setPositiveButton("确定") { _, _ -> }
                    dialog.show()
                }

            }
        })


        // 设置布局管理器
        recyclerView.layoutManager = GridLayoutManager(context, 2)

        return view
    }

    // 处理卡片点击事件
    override fun onItemClick(position: Int) {

        // 获取点击的卡片数据
        val cardData = cardDataList[position]
        val appname = cardData.title
        val appintro = cardData.description
        val appprompt = cardData.prompt
        val icon=cardData.iconname

        // 跳转到ImportApp Fragment，参数为app名
        val fragment = ImportApp.newInstance(appname,appintro,appprompt,icon)
        activity?.supportFragmentManager?.beginTransaction()?.apply {
            val transaction = activity?.supportFragmentManager?.beginTransaction()

            //设置转场动画
            transaction?.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)

            transaction?.replace(R.id.fragment_main, fragment)?.commit()
//            replace(R.id.fragment_main, fragment)
//            addToBackStack(null)
//            commit()
        }
    }
}
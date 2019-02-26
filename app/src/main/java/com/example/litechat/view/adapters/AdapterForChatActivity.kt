package com.example.litechat.view.adapters

import android.arch.persistence.room.Room
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.litechat.R
import com.example.litechat.model.AllChatDataModel
import com.example.litechat.model.MessageModel
import com.example.litechat.model.UserProfileData
import com.example.litechat.model.contactsRoom.AppDatabse

class AdapterForChatActivity(private var dataset:ArrayList<MessageModel>,private var context:Context): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class MyViewHolderMe(val view: View): RecyclerView.ViewHolder(view){


        var myName:TextView=view.findViewById(R.id.myName)
        var myMessage:TextView=view.findViewById(R.id.myMessage)

    }

    class MyViewHolderYou(val view: View): RecyclerView.ViewHolder(view){


        var youName:TextView=view.findViewById(R.id.youName)
        var youMessage:TextView=view.findViewById(R.id.youMessage)

    }


    override fun getItemViewType(position: Int): Int {
        if(dataset[position].sentBy.equals(AllChatDataModel.userNumberIdPM))

        {
            return 0
        }
        else{

            return 1
        }

//        return super.getItemViewType(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): RecyclerView.ViewHolder {
        var view:View?
        //Here user no. is to be placed
        if(position==0)

        {

             view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_chat_me, parent,false) as View

            return MyViewHolderMe(view!!)
        }
       else{

             view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_chat_you, parent,false) as View
            return MyViewHolderYou(view!!)
        }


    } // create a new view


    override fun getItemCount(): Int {


        return dataset.size
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) =
        if(holder.itemViewType==0) {
            Log.d("Position",position.toString())
             var holderMe: MyViewHolderMe= holder as MyViewHolderMe
              holderMe.myMessage.text=(dataset[position].message)
              holderMe.myName.text=(UserProfileData.UserName)
        }
        else
        {   Log.d("Positione",position.toString())
            var holderYou: MyViewHolderYou=holder as MyViewHolderYou
            holderYou.youMessage.text=(dataset[position].message)
            holderYou.youName.text=(searchContactName(dataset[position].sentBy))
        }

    private fun searchContactName(number: String): String {
        var name : String
        val db = Room.databaseBuilder(context, AppDatabse::class.java, "Contact_Database")
            .allowMainThreadQueries().build()
        // if condition
     //   Log.d("check",db.userDao().getName("91"+AllChatDataModel.otherUserNumber))
        if(db.userDao().getName("91"+AllChatDataModel.otherUserNumber)!=null){

            name= db.userDao().getName("91"+AllChatDataModel.otherUserNumber)
            return name
        }
        else if(db.userDao().getName("0"+AllChatDataModel.otherUserNumber)!=null){
            name= db.userDao().getName("0"+AllChatDataModel.otherUserNumber)
            return name
        }

        else if(db.userDao().getName(AllChatDataModel.otherUserNumber)!=null){
            name= db.userDao().getName(AllChatDataModel.otherUserNumber)
            return name
        }


        return number

    }

}
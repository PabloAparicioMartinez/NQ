package com.example.nq.listHelpQuestions

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.nq.R

class HelpAdapter(
    private val context: Context,
    private val listData: List<HelpData>
) : BaseExpandableListAdapter() {

    override fun getGroupCount(): Int {
        return listData.size
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return listData[groupPosition].answers.size
    }

    override fun getGroup(groupPosition: Int): Any {
        return listData[groupPosition]
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return listData[groupPosition].answers[childPosition]
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        var view = convertView
        if (view == null) {
            val inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.item_help_list_group, null)
        }

        val groupTitleTextView: TextView? = view?.findViewById(R.id.itemHelp_listGroup)
        groupTitleTextView?.text = listData[groupPosition].question

        val groupImageView: ImageView? = view?.findViewById(R.id.itemHelp_listGroupImage)
        groupImageView?.rotation = 90f

        if (isExpanded) groupImageView?.rotation = -90f
        else groupImageView?.rotation = 90f

        return view!!
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        var view = convertView
        if (view == null) {
            val inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.item_help_list_child, null)
        }

        val childItemTextView: TextView? = view?.findViewById(R.id.itemHelp_listChild)
        childItemTextView?.text = listData[groupPosition].answers[childPosition]

        val groupTitleImage: ImageView? = view?.findViewById(R.id.itemHelp_listGroupImage)
        println(groupTitleImage)
        groupTitleImage?.setImageResource(R.drawable.ic_error_01)

        return view!!
    }
}


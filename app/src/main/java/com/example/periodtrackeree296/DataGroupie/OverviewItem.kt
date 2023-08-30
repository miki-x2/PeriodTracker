package com.example.periodtrackeree296.DataGroupie

import android.content.ClipData

class OverviewItem(val string: String): ClipData.Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int){
        viewHolder.cycle_overview.text = string
    }
}
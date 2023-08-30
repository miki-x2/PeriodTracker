package com.example.periodtrackeree296.Cycle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceGroupAdapter
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import com.example.periodtrackeree296.R
import com.example.periodtrackeree296.getCycleInfo
import com.example.periodtrackeree296.getPeriodDaysDecend
import io.realm.Realm
import java.util.*



class CycleHistory : Fragment(){
    data class CycleData(val cycleStarts: List<Calendar>, val periodEnds: List<Calendar>)
    private val realm = Realm.getDefaultInstance()
    private val periodDates = realm.getPeriodDaysDecend()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val cycleData = findCycleStartsAndPeriodEnds(periodDates)
        val cycleLengths = findCycleLengths(cycleData.cycleStarts)
        val periodLengths = findPeriodLengths(cycleData)

        if(cycleLengths.isNotEmpty())
            avg_cycle_length.text = cycleLengths.average().roundToInt().toString()
        else
            avg_cycle_length.text = realm.getCycleInfo().cycleLength.toString()
        if(periodLengths.isNotEmpty())
            avg_period_length.text = periodLengths.average().roundToInt().toString()
        else
            avg_period_length.text = realm.getCycleInfo().periodLength.toString()

        setPreviousCycles(cycleData.cycleStarts, periodLengths, cycleLengths)
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    private fun setPreviousCycles(cycleStarts: List<Calendar>, periodLengths: List<Int>, cycleLengths: List<Int>){
        val layout = GridLayoutManager(context, 3)
        val dividerItem = DividerItemDecoration(context, layout.orientation)
        val groupAdapter = GroupAdapter
    }
}
package com.example.periodtrackeree296.Cycle

import android.content.Context
import android.os.Bundle
import android.provider.Settings
import android.provider.Settings.Global.getString
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.periodtrackeree296.*
import io.realm.Realm
import io.realm.RealmResults
import java.time.Duration
import java.time.Period
import java.time.temporal.ChronoUnit
import java.util.*


class CycleOverview: Fragment(){
    private var realm = Realm.getDefaultInstance()
    private var periodDates = realm.getPeriodDaysDecend()
    private var dayData = realm.getDataByDate(Calendar.getInstance())
    private var cycleInfo = realm.getCycleInfo()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cycleInfo.removeAllChangeListeners()
        periodDates.removeAllChangeListeners()
        dayData.removeAllChangeListeners()
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    override fun onResume() {
        super.onResume()
        calculateNextCycle(findCycleStart(periodDates))
        if (dayData.date != Calendar.getInstance().formatDate())
            dayData= realm.getDataByDate(Calendar.getInstance())

        setupLoggedToday()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)
        if(!dayData.isValid) dayData = realm.getDataByDate(Calendar.getInstance())

        periodDates.addChangeListener {
            results, changeset -> calculateNextCycle(findCycleStart(results))
        }

        cycleInfo.addChangeListener<CycleInfo> {
            _, _ -> calculateNextCycle(findCycleStart(periodDates))
        }

        dayData.addChangeListener<DayData> {
            _, changeSet -> if(changeSet != null) setupLoggedToday()
        }

        val layout = LinearLayoutManager(context)
        val dividerItem = DividerItemDecoration(context, layout.orientation)

        today_log_list.apply{
            layoutManager = layout
            adapter = groupAdapter
            this.addItemDecoration(dividerItem)
        }

        addDate.setOnClickListener{
            (this.activity as MainActivity).navToDayView(Calendar.getInstance())
        }
    }

    private fun setupLoggedToday(){
        if(!dayData.isValid)
            return

        groupAdapter.clear()
         if (dayData.symptoms.isEmpty() && dayData.notes.isBlank())
             logged_today.setText(R.string.nothing_logged)
        else{
            logged_today.setText(R.string.logged_today)
             dayData.symptoms.forEach{
                 groupAdapter.add(OverviewItem(it.name))
             }
             if(dayData.notes.isNotBlank())
                 groupAdapter.add(OverviewItem(context?.resources!!.getString(R.string.notes_prefix, dayData.notes)))
         }
    }

    private fun findCycleStart(periodDate: RealmResults<DayData>): Long{
        periodDates.forEachIndexed { index, dayData ->
            val previousDay = dayData.date.toCalendar()
            previousDay.add(Calendar.DAY_OF_MONTH, -1)

            if (index == periodDates.lastIndex || previousDay.formatDate() != periodDates[index+1]?.date){
                return dayData.date
            }
        }
        return 0
    }

    private fun calculateNextCycle(cycleStartDate: Long){
        if (cycleStartDate == 0L)
            return
        val cycleStart = cycleStartDate.toCalendar()



        val cycleDays =  compareValues(cycleStart, Calendar.getInstance())

        if (cycleDays < cycleInfo.periodLength){
            days_until_next_period_text.text = Settings.Global.getString(R.string.days_left)
            days_until_next_period_num.text = (cycleInfo.periodDuration - cycleDays).toString()
        }
        else if (cycleDays <= cycleInfo.cycleLength){
            days_until_next_period_text.text = Settings.Global.getString(R.string.days_until_next)
            days_until_next_period_num.text = (cycleInfo.periodDuration - cycleDays).toString()
        }
        else{
            days_until_next_period_text.text = Settings.Global.getString(R.string.days_late)
            days_until_next_period_num.text = (cycleDay - cycleInfo.cycleDuration).toString()
        }

        cycle_view.setCycleData(cycleInfo.cycleLength, cycleInfo.periodDuration, cycleDays)
        cycle_view.invalidate()
    }
    companion object {
        const val TAG = "OVERVIEW"
        fun newInstance() = CycleOverview()
    }
}



package com.example.periodtrackeree296

import android.content.Context
import android.icu.util.Calendar
import androidx.preference.PreferenceManager
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmResults
import io.realm.Sort
import java.util.*

fun Realm.firstPeriodDay(firstDay: Calendar, context: Context){
    val periodDuration = PreferenceManager.getDefaultSharedPreferences(context).getString("period_duration", "7")?.toInt()?:7

    this.executeTransactionAsync {
        localRealm -> localRealm.where(DayData::class.java).findAll().forEach{
            it.symptoms.clear()
    }
        val blood = localRealm.where(Symptoms::class.java).equalTo("name", "Bleeding").findFirst()!!

        for(i in 0 until periodDuration){
            val day = localRealm.where(DayData::class.java).equalTo("date", firstDay.formatDate()).findFirst()?: localRealm.createObject(DayData::class.java, firstDay.formatDate())
            day.symptoms = RealmList(blood)

            if(firstDay.isToday())
                break
            firstDay.add(Calendar.DAY_OF_MONTH, 1)
        }
    }
}

fun Realm.getDataByDate(queryDate: java.util.Calendar): DayData{
    this.beginTransaction()
    val daydata = this.where(DayData::class.java).equalTo("date", queryDate.formatDate()).findFirst()?:
    this.createObject(DayData::class.java, queryDate.formatDate())
    this.commitTransaction()

    return daydata
}

fun Realm.getPeriodDaysDecend(): RealmResults<DayData>{
    return this.where(DayData::class.java).equalTo("symptons.name", "Bleeding").sort("date", Sort.DESCENDING).findAll()
}

fun Realm.getSymptomsActive(): RealmResults<Symptoms>{
    return this.where(Symptoms::class.java).equalTo("active", true).findAll()
}

fun Realm.getSymptoms(): RealmResults<Symptoms>{
    return this.where(Symptoms::class.java).findAll()
}
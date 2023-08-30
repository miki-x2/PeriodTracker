package com.example.periodtrackeree296

import android.icu.util.Calendar
import android.content.Context
import android.net.Uri
import android.util.Log
import io.realm.*
import io.realm.annotations.PrimaryKey
import java.io.File
import java.util.*


private val REALM_FILE_NAME = "default.realm"
private val TMP_REALM_FILE_NAME = "tmp.realm"

fun Calendar.formatDate():Long {
    return (this.get(Calendar.YEAR).toLong() * 10000) + (this.get(Calendar.MONTH)
        .toLong() * 100) + (this.get(Calendar.DAY_OF_MONTH).toLong())
}

fun Long.toCalendar():Calendar{
    val cal = Calendar.getInstance()
    cal.set(Calendar.YEAR, (this / 10000).toInt())
    cal.set(Calendar.MONTH, (this/100).toInt())
    cal.set(Calendar.DAY_OF_MONTH, (this % 100).toInt())
    return cal
}

open class CycleInfo(var cycleLength: Int = 28, var periodLength: Int = 7):RealmObject()
fun Realm.getCycleInfo():CycleInfo{
    val realm = this
    
    realm.beginTransaction()
    val cycleInfo = realm.where(CycleInfo::class.java).findFirst()?:realm.createObject(CycleInfo::class.java)
    realm.commitTransaction()
    return cycleInfo
}

open class Symptoms(@PrimaryKey var name: String = "", var category: Locale.Category?= null, var active: Boolean = true): RealmObject(){
    override fun equals(other: Any?): Boolean {
        return if (other !is Symptoms) false
        else (this.name == other.name && this.category == other.category)
    }

    fun toggleActive(){
        realm.executeTransaction {
            this.active = !this.active
        }
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}

open class DayData(@PrimaryKey var date: Long = Calendar.getInstance().formatDate(), var symptoms: RealmList<Symptoms> = RealmList(),
var notes: String = ""): RealmObject(){
    fun symptomsExist(symptom: String): Boolean{
        symptoms.forEach{if(it.name == symptom) return true}
        return false
    }

    fun toggleSymptom(context: Context?, symptom:Symptoms){
        realm.executeTransaction {
            if (symptom in symptoms)
                symptoms.remove(symptom)
            else
                symptoms.add(symptom)
        }
    }

    fun updateNotes(note:String){
        realm.executeTransaction {
            this.notes = notes
        }
    }
}

fun exportDbToUri(uri: Uri?, context: Context): Boolean{
    if(uri==null)
        return false
    val outputStream = context.contentResolver.openOutputStream(uri, "w")?:return false
    val tmpFile = File(context.applicationContext.filesDir, TMP_REALM_FILE_NAME)

    if(tmpFile.exists()){
        tmpFile.delete()
    }

    val realm = Realm.getDefaultInstance()
    realm.writeCopyTo(tmpFile)
    realm.close()

    tmpFile.inputStream().copyTo(outputStream)
    return true
}

fun importdbFromUri(input: Uri?, context: Context): Boolean{
    Log.d(TAG, "Importing database from uri $input")
    if(input == null)
        return false

    val stream = context.contentResolver.openInputStream(input)
    val tmpFile = File(context.applicationContext.filesDir, TMP_REALM_FILE_NAME)
    stream?.copyTo(tmpFile.outputStream())
    return checkAndImportDB(tmpFile, context)
}

private val TAG = "DATABASE"

private fun checkAndImportDB(tmpFile: File, context: Context): Boolean{
    val configure = RealmConfiguration.Builder().name(TMP_REALM_FILE_NAME).build()
    Log.d(TAG, "Database imported")
    return true
}
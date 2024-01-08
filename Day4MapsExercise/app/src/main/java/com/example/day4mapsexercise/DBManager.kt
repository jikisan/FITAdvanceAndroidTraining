package com.example.day4mapsexercise

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBManager(val context: Context, ) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        val DB_NAME = "locationDB"
        val DB_VERSION = 1
        val TBL_NAME = "locationTbl"
        val ID = "ID"
        val ADDRESS_NAME = "address_name"
        val LATITUDE = "latitude"
        val LONGITUDE = "longitude"

        val CREATE_TBL_LOCATIONS = "CREATE TABLE $TBL_NAME (" +
                "$ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                " $ADDRESS_NAME TEXT NOT NULL," +
                " $LATITUDE TEXT NOT NULL," +
                " $LONGITUDE TEXT NOT NULL);"
    }

    val db = this.writableDatabase

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_TBL_LOCATIONS)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TBL_NAME")
        onCreate(db)
    }

    fun insert(addressName: String, latitude: Double, longitude: Double) : Boolean{

        val values = ContentValues()
        values.put(ADDRESS_NAME, addressName)
        values.put(LATITUDE, latitude)
        values.put(LONGITUDE, longitude)

        val result = db.insert(TBL_NAME, null, values)

        if(result > 0) return true
        return false
    }

    fun viewAllLocation() : ArrayList<Location> {

        val locations: ArrayList<Location> = ArrayList()

        val cursor = db.rawQuery("Select * from $TBL_NAME", null)
        var location: Location

        while (cursor.moveToNext()) {

            val id = cursor.getInt(0)
            val addressName = cursor.getString(cursor.getColumnIndexOrThrow(ADDRESS_NAME))
            val latitude = cursor.getString(cursor.getColumnIndexOrThrow(LATITUDE)).toDouble()
            val longitude = cursor.getString(cursor.getColumnIndexOrThrow(LONGITUDE)).toDouble()
            location = Location(addressName, latitude, longitude, id)
            locations.add(location)

        }

        return locations
    }


}
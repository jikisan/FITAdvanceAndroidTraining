package com.example.day2sqliteactivity

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBManager(val context: Context, ) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        val DB_NAME = "userDB"
        val DB_VERSION = 1
        val TBL_NAME = "userTbl"
        val USERID = "id"
        val USERNAME = "username"
        val FULLNAME = "fullname"
        val PASSWORD = "password"

        val CREATE_TBL_USERS = "CREATE TABLE $TBL_NAME (" +
                "$USERID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                " $USERNAME TEXT NOT NULL," +
                " $PASSWORD TEXT NOT NULL," +
                " $FULLNAME TEXT NOT NULL);"
    }

    val db = this.writableDatabase

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_TBL_USERS)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TBL_NAME")
        onCreate(db)
    }

    fun insertUser(username: String, password: String, fullname: String) : Boolean{

        val values = ContentValues()
        values.put(USERNAME, username)
        values.put(PASSWORD, password)
        values.put(FULLNAME, fullname)

        val result = db.insert(TBL_NAME, null, values)

        if(result > 0) return true
        return false
    }

    fun checkUserIfExists(username : String ) : Boolean {
        val searchUser = "SELECT * from $TBL_NAME where $USERNAME=?"
        val query = db.rawQuery(searchUser, arrayOf(username))

        if(query.moveToNext()) return true
        return false
    }

    fun login(username : String, password: String ) : Boolean {
        val searchUser = "SELECT * from $TBL_NAME where $USERNAME=? AND $PASSWORD=?"
        val query = db.rawQuery(searchUser, arrayOf(username, password))

        if(query.moveToNext()) return true
        return false
    }

    fun viewAllUsers() : ArrayList<User> {


        val users: ArrayList<User> = ArrayList()

        val cursor = db.rawQuery("Select * from $TBL_NAME", null)
        var user: User

        while (cursor.moveToNext()) {

            val id = cursor.getInt(0)
            val username = cursor.getString(cursor.getColumnIndexOrThrow(USERNAME))
            val password = cursor.getString(cursor.getColumnIndexOrThrow(PASSWORD))
            val fullname = cursor.getString(cursor.getColumnIndexOrThrow(FULLNAME))
            user = User(username, password, fullname, id)
            users.add(user)

        }

        return users
    }

    fun searchUsers(keyword: String) : ArrayList<User> {

        val users: ArrayList<User> = ArrayList()

        val cursor = db.rawQuery("Select * from $TBL_NAME where $USERNAME LIKE '$keyword' or $FULLNAME LIKE '$keyword'" , null)
        var user: User

        while (cursor.moveToNext()) {

            val id = cursor.getInt(0)
            val username = cursor.getString(cursor.getColumnIndexOrThrow(USERNAME))
            val password = cursor.getString(cursor.getColumnIndexOrThrow(PASSWORD))
            val fullname = cursor.getString(cursor.getColumnIndexOrThrow(FULLNAME))
            user = User(username, password, fullname, id)
            users.add(user)

        }

        return users
    }

    fun deleteUser(id: String) : Boolean {

        val deleted = db.delete(TBL_NAME, "id=?", arrayOf(id))

        if (deleted > 0) return true
        return false
    }

    fun updateUser(username: String, password: String, fullname: String, id: String): Boolean {

        val cv = ContentValues()
        cv.put(USERNAME, username)
        cv.put(PASSWORD, password)
        cv.put(FULLNAME, fullname)

        val updated = db.update(TBL_NAME, cv, "id=?", arrayOf(id))

        if(updated > 0) return true

        return false
    }
    
}
package com.example.day3syncactivity

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class DBManager(val context: Context) : SQLiteOpenHelper(context, DBNAME, null, DBVERSION) {

    companion object{
        val DBNAME = "itemDB"
        val DBVERSION = 1
        val ITEM_ID = "id"
        val TBL_NAME = "items"
        val ITEM_NAME = "item_name"
        val ITEM_SYNC = "item_sync"

        val CREATE_TABLE = "CREATE TABLE $TBL_NAME (" +
                "$ITEM_ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                "$ITEM_NAME TEXT NOT NULL," +
                "$ITEM_SYNC INTEGER NOT NULL);"



    }



    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TBL_NAME")
        onCreate(db)
    }

    private val db = this.writableDatabase
    private val helper = HelperClass(context)
    private var insertToApi = "";

    fun inserItem(item_name: String) : String {

        var sync = 0
        if(helper.isNetworkAvailable()) { sync = 1 }

        val cv = ContentValues()
        cv.put(ITEM_NAME, item_name)
        cv.put(ITEM_SYNC, sync)

        val inserted = db.insert(TBL_NAME, null, cv)

        if(inserted > 0 && sync > 0) {
            //Save to API
            saveToAPI(item_name)
            return insertToApi
        }
        else {
            //SQLite
            if(inserted > 0) { return "Item has been saved in SQLite only." }
            else { return "Error in saving to SQLite." }
        }

        return "Error"
    }

    private fun saveToAPI(item_name: String){
        val api = "${helper.api}/insert.php"
        val stringRequest = object : StringRequest(Request.Method.POST, api,
            {response ->
                insertToApi = response
            },
            {error ->

            })
            {
                override fun getParams(): MutableMap<String, String>? {
                    val params: MutableMap<String, String> = HashMap()
                    params.put(ITEM_NAME, item_name)
                    return params
                }

            }
        Volley.newRequestQueue(context).add(stringRequest)
    }

    fun syncToAPI(): Boolean{


        val cursor = db.rawQuery("SELECT * from $TBL_NAME where $ITEM_SYNC=?", arrayOf("0"))

        if (cursor.count > 0){
            while (cursor.moveToNext()){

                val itemName = cursor.getString(cursor.getColumnIndexOrThrow(ITEM_NAME))
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(ITEM_ID))
                saveToAPI(itemName)

                val cv = ContentValues()
                cv.put("item_sync", 1)
                db.update(TBL_NAME, cv, "id=?", arrayOf("$id"))
            }

            return true
        }

        return false
    }

    fun viewAllItems() : ArrayList<Item> {
        val items : ArrayList<Item> = ArrayList()

        val cursor = db.rawQuery( "SELECT * from $TBL_NAME", null)
        var item : Item

        while(cursor.moveToNext()){
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(ITEM_ID))
            val item_name = cursor.getString(cursor.getColumnIndexOrThrow(ITEM_NAME))
            val sync = cursor.getInt(cursor.getColumnIndexOrThrow(ITEM_SYNC))

            item = Item(sync, item_name, id)
            items.add(item)
        }


        return items
    }

}
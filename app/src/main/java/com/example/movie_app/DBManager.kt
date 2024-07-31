package com.example.movie_app

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBManager(context: Context, name: String, factory: SQLiteDatabase.CursorFactory?, version: Int) :
    SQLiteOpenHelper(context, name, factory, version) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE personnel (" +
                    "name TEXT, " +
                    "id TEXT PRIMARY KEY, " +
                    "password TEXT, " +
                    "email TEXT, " +
                    "gender TEXT, " +
                    "genres TEXT)" // 장르 정보를 저장할 필드 추가
        )
    }


    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS personnel")
        onCreate(db)
    }
}
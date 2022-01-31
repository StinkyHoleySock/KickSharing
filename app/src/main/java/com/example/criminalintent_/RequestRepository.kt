package com.example.criminalintent_

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.example.criminalintent_.database.RequestDatabase
import com.example.criminalintent_.database.migration_1_2
import java.io.File
import java.lang.IllegalStateException
import java.util.*
import java.util.concurrent.Executors

private const val DATABASE_NAME = "request-database"

class RequestRepository private constructor(context: Context) {

    private val database: RequestDatabase = Room.databaseBuilder(
        context.applicationContext, // БД обращается к файловой системе, поэтому нужен объект Context
        RequestDatabase::class.java, // класс базы данных, которую должен создать Room
        DATABASE_NAME // Имя файла базы данных, которую создст Room
    ).addMigrations(migration_1_2)
        .build()

    private val requestDao = database.requestDao()
    private val executor = Executors.newSingleThreadExecutor()
    private val filesDir = context.applicationContext.filesDir

    fun getRequests(): LiveData<List<Request>> = requestDao.getRequests()

    fun getRequest(id: UUID): LiveData<Request?> = requestDao.getRequest(id)

    fun updateRequest(request: Request){
        executor.execute {
            requestDao.updateRequest(request)
        }
    }

    fun addRequest(request: Request){
        executor.execute {
            requestDao.addRequest(request)
        }
    }


    companion object{
        private var INSTANCE: RequestRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null){
                INSTANCE = RequestRepository(context)
            }
        }

        fun get(): RequestRepository {
            return INSTANCE ?:
            throw IllegalStateException("RequestRepository must be initialized")
        }
    }
}
package com.example.criminalintent_.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.criminalintent_.Request
import java.util.*

// аннотация @Dao обозначет, что RequestDao - один из наших объектов доступа к данным
@Dao
interface RequestDao {
    // аннотация @Query обозначает, что функция предназначена для ИЗВЛЕЧЕНИЯ информации из БД
    @Query("SELECT * FROM request")
    fun getRequests(): LiveData<List<Request>>

    @Query("SELECT * FROM request WHERE id=(:id)")
    fun getRequest(id: UUID): LiveData<Request>

    @Update
    fun updateRequest(Request: Request)

    @Insert
    fun addRequest(Request: Request)
}
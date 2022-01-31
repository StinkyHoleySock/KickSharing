package com.example.criminalintent_

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Request (@PrimaryKey val id: UUID = UUID.randomUUID(),
                  var title: String = "",
                  var currentDate: Date = Date(),
                  var isSolved: Boolean = false,
                  var employee: String = "") {
}
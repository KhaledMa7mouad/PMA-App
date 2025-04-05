package com.example.pmaapp.database

import android.os.Parcelable
import androidx.room.*
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "player")
data class Player(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,

    // Core info
    @ColumnInfo(name = "name")           val name: String,
    @ColumnInfo(name = "position")       val position: String,
    @ColumnInfo(name = "age")            val age: Int,

    // Physical
    @ColumnInfo(name = "height")         val height: Float,
    @ColumnInfo(name = "weight")         val weight: Float,

    // Play style
    @ColumnInfo(name = "best_position")          val bestPosition: String,
    @ColumnInfo(name = "weak_foot")              val weakFoot: String,
    @ColumnInfo(name = "skill_moves")            val skillMoves: Int,
    @ColumnInfo(name = "attacking_work_rate")    val attackingWorkRate: String,
    @ColumnInfo(name = "defensive_work_rate")    val defensiveWorkRate: String,

    // Technical
    @ColumnInfo(name = "crossing")         val crossing: Int,
    @ColumnInfo(name = "finishing")        val finishing: Int,
    @ColumnInfo(name = "heading_accuracy") val headingAccuracy: Int,
    @ColumnInfo(name = "short_passing")    val shortPassing: Int,
    @ColumnInfo(name = "volleys")          val volleys: Int,
    @ColumnInfo(name = "dribbling")        val dribbling: Int,
    @ColumnInfo(name = "curve")            val curve: Int,
    @ColumnInfo(name = "fk_accuracy")      val fkAccuracy: Int,
    @ColumnInfo(name = "long_passing")     val longPassing: Int,
    @ColumnInfo(name = "ball_control")     val ballControl: Int,

    // Pace & Movement
    @ColumnInfo(name = "acceleration") val acceleration: Int,
    @ColumnInfo(name = "sprint_speed") val sprintSpeed: Int,
    @ColumnInfo(name = "agility")       val agility: Int,
    @ColumnInfo(name = "reactions")     val reactions: Int,
    @ColumnInfo(name = "balance")       val balance: Int,

    // Power & Endurance
    @ColumnInfo(name = "shot_power")  val shotPower: Int,
    @ColumnInfo(name = "jumping")     val jumping: Int,
    @ColumnInfo(name = "stamina")     val stamina: Int,
    @ColumnInfo(name = "strength")    val strength: Int,
    @ColumnInfo(name = "long_shots")  val longShots: Int,
    @ColumnInfo(name = "aggression")  val aggression: Int,

    // Defending
    @ColumnInfo(name = "interceptions")      val interceptions: Int,
    @ColumnInfo(name = "positioning")        val positioning: Int,
    @ColumnInfo(name = "vision")             val vision: Int,
    @ColumnInfo(name = "penalties")          val penalties: Int,
    @ColumnInfo(name = "composure")          val composure: Int,
    @ColumnInfo(name = "marking")            val marking: Int,
    @ColumnInfo(name = "standing_tackle")    val standingTackle: Int,
    @ColumnInfo(name = "sliding_tackle")     val slidingTackle: Int,

    // Goalkeeping
    @ColumnInfo(name = "gk_diving")       val gkDiving: Int,
    @ColumnInfo(name = "gk_handling")     val gkHandling: Int,
    @ColumnInfo(name = "gk_kicking")      val gkKicking: Int,
    @ColumnInfo(name = "gk_positioning")  val gkPositioning: Int,
    @ColumnInfo(name = "gk_reflexes")     val gkReflexes: Int
) : Parcelable

package ladislav.sevcuj.endlessdarts.db

import androidx.room.*
import ladislav.sevcuj.endlessdarts.DartBoard
import ladislav.sevcuj.endlessdarts.toDecimalString
import ladislav.sevcuj.endlessdarts.ui.widgets.StatsRowData

@Entity
data class User(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val identifier: String,
    val isTemporary: Boolean,
)

@Dao
interface UserDao {
    @Query("SELECT * FROM user WHERE id = :id")
    fun get(id: Long): User

    @Insert
    fun insert(entity: User): Long

    @Delete
    fun delete(entity: User)
}

@Entity
data class Session(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val userId: Long,
    val startDateTime: String,
    val endDateTime: String? = null,
)

@Entity(tableName = "session_stats")
data class SessionStats(
    @PrimaryKey val sessionId: Long,
    val throwsCount: Int = 0,
    val dartsCount: Int = 0,
    val targetHitsCount: Int = 0,
    val doubleCount: Int = 0,
    val tripleCount: Int = 0,
    val firstDartSuccessCount: Int = 0,
    val fullSuccessCount: Int = 0,
    val fullMissCount: Int = 0,
    val average: Int = 0,
    val max: Int = 0,
    val above140Count: Int = 0,
    val above100Count: Int = 0,
    val sum180Count: Int = 0,
    val failCount: Int = 0,
) {
    fun toSimpleData(): List<StatsRowData> {
        val fullSuccessPercent = getPercent(fullMissCount, throwsCount)
        val fullMissPercent = getPercent(fullMissCount, throwsCount)
        val targetHitsPercent = getPercent(targetHitsCount, dartsCount)

        return listOf(
            StatsRowData("Throws", throwsCount.toString()),
            StatsRowData(
                "Target full success (rate)",
                "$fullSuccessCount (${fullSuccessPercent.toDecimalString()}%)"
            ),
            StatsRowData("Target full miss (rate)", "$fullMissCount (${fullMissPercent.toDecimalString()}%)"),
            StatsRowData("Target hits (rate)",  "$targetHitsCount (${targetHitsPercent.toDecimalString()}%)"),
            StatsRowData("Throw average", (average / 100).toString()),
            StatsRowData("Throw max", max.toString()),
            StatsRowData("140+", above140Count.toString()),
            StatsRowData("100+", above100Count.toString()),
        )
    }

    fun toFullData(): List<StatsRowData> {
        val simpleList = toSimpleData().toMutableList()

        val startOkPercent = getPercent(firstDartSuccessCount, dartsCount)
        val failPercent = getPercent(failCount, throwsCount)
        val potentialCount = fullSuccessCount + failCount
        val potentialPercent = getPercent(potentialCount, throwsCount)

        simpleList.add(StatsRowData("Double", doubleCount.toString()),)
        simpleList.add(StatsRowData("Triple", tripleCount.toString()),)
        simpleList.add(StatsRowData("Start OK",  "$targetHitsCount (${startOkPercent.toDecimalString()}%)"))
        simpleList.add(StatsRowData("Fail",  "$failCount (${failPercent.toDecimalString()}%)"))
        simpleList.add(StatsRowData("Potential",  "$potentialCount (${potentialPercent.toDecimalString()}%)"))

        return simpleList.toList()
    }

    private fun getPercent(value: Int, total: Int): Double {
        return if (value == 0) 0.0 else (value.toDouble() / total.toDouble()) * 100
    }
}

@Dao
interface SessionStatsDao {
    @Query("SELECT * FROM session_stats WHERE sessionId = :id")
    fun get(id: Long): SessionStats

    @Insert
    fun insert(entity: SessionStats)

    @Update
    fun update(entity: SessionStats)

    @Delete
    fun delete(entity: SessionStats)
}

@Dao
interface SessionDao {
    @Query("SELECT * FROM session WHERE id = :id")
    fun get(id: Long): Session

    @Query("SELECT * FROM session WHERE userId = :userId ORDER BY id DESC LIMIT 1")
    fun getLast(userId: Long): Session?

    @Insert
    fun insert(entity: Session): Long

    @Update
    fun update(entity: Session)

    @Delete
    fun delete(entity: Session)
}

data class Target(
    val id: Long,
    val label: String,
    val number: Int,
) {
    fun getPreferredFields(): List<DartBoard.Field> {
        return listOf(
            DartBoard.Field(
                "20",
                "20",
                maxMultiplication = 1,
            ),
            DartBoard.Field(
                "20",
                "D20",
                value = 20,
                maxMultiplication = 1,
                defaultMultiplication = 2,
            ),
            DartBoard.Field(
                "20",
                "T20",
                value = 20,
                maxMultiplication = 1,
                defaultMultiplication = 3,
            ),
            DartBoard.Field(
                "1",
                "D1",
                value = 1,
                maxMultiplication = 1,
                defaultMultiplication = 2,
            ),
            DartBoard.Field(
                "1",
                "T1",
                value = 1,
                maxMultiplication = 1,
                defaultMultiplication = 3,
            ),
            DartBoard.Field(
                "5",
                "D5",
                value = 5,
                maxMultiplication = 1,
                defaultMultiplication = 2,
            ),
            DartBoard.Field(
                "5",
                "T5",
                value = 5,
                maxMultiplication = 1,
                defaultMultiplication = 3,
            ),
        )
    }
}

@Entity(
    ignoredColumns = ["darts"],
)
data class Throw(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val sessionId: Long,
    val player: String,
    val throwSummary: Int = 0,
    val dartsAverage: Int = 0,
    val dartsCount: Int = 0,
    val doubleCount: Int = 0,
    val tripleCount: Int = 0,
    val target: String,
    val targetHits: Int = 0,
    val targetSuccess: Boolean = false,
    val firstDartDatetime: String? = null,
    val lastDartDatetime: String? = null,
    val isLogged: Boolean = false,
) {
    var darts: List<Dart> = listOf()
}

@Dao
interface ThrowDao {
    @Query("SELECT * FROM throw WHERE id = :id")
    fun get(id: Long): Throw

    @Insert
    fun insert(entity: Throw): Long

    @Update
    fun update(entity: Throw)

    @Delete
    fun delete(entity: Throw)
}

@Entity
data class Dart(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val throwId: Long,
    val order: Int,
    val multiplicator: Int,
    val number: Int,
    val sum: Int,
)

@Dao
interface DartDao {
    @Query("SELECT * FROM dart WHERE id = :id")
    fun get(id: Long): Dart

    @Insert
    fun insert(entity: Dart): Long

    @Update
    fun update(entity: Dart)

    @Delete
    fun delete(entity: Dart)
}
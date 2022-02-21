package ladislav.sevcuj.endlessdarts.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ladislav.sevcuj.endlessdarts.DartBoard
import ladislav.sevcuj.endlessdarts.TargetProvider
import ladislav.sevcuj.endlessdarts.toDecimalString
import ladislav.sevcuj.endlessdarts.ui.screens.score.ThrowHistoryRowData
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
    suspend fun get(id: Long): User?

    @Insert
    suspend fun insert(entity: User): Long

    @Delete
    suspend fun delete(entity: User)
}

@Entity
data class Session(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val userId: Long,
    val startDateTime: String,
    val endDateTime: String? = null,
)

enum class ThrowFilter {
    NONE,
    FULL_SUCCESS,
    FULL_MISS,
    SUMMARY_100,
    SUMMARY_140,
    SUMMARY_180,
    TRIPLE,
    DOUBLE,
    START_OK,
    FAIL
}

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
                "$fullSuccessCount (${fullSuccessPercent.toDecimalString()}%)",
                filter = ThrowFilter.FULL_SUCCESS,
            ),
            StatsRowData(
                "Target full miss (rate)",
                "$fullMissCount (${fullMissPercent.toDecimalString()}%)",
                filter = ThrowFilter.FULL_MISS,
            ),
            StatsRowData(
                "Target hits (rate)",
                "$targetHitsCount (${targetHitsPercent.toDecimalString()}%)"
            ),
            StatsRowData("Throw average", (average / 100).toString()),
            StatsRowData("Throw max", max.toString()),
            StatsRowData("140+", above140Count.toString(), filter = ThrowFilter.SUMMARY_140),
            StatsRowData("100+", above100Count.toString(), filter = ThrowFilter.SUMMARY_100),
        )
    }

    fun toFullData(): List<StatsRowData> {
        val simpleList = toSimpleData().toMutableList()

        val startOkPercent = getPercent(firstDartSuccessCount, dartsCount)
        val failPercent = getPercent(failCount, throwsCount)
        val potentialCount = fullSuccessCount + failCount
        val potentialPercent = getPercent(potentialCount, throwsCount)

        simpleList.add(StatsRowData("Double", doubleCount.toString(), filter = ThrowFilter.DOUBLE))
        simpleList.add(StatsRowData("Triple", tripleCount.toString(), filter = ThrowFilter.TRIPLE))
        simpleList.add(
            StatsRowData(
                "Start OK",
                "$targetHitsCount (${startOkPercent.toDecimalString()}%)",
                filter = ThrowFilter.START_OK
            )
        )
        simpleList.add(
            StatsRowData(
                "Fail",
                "$failCount (${failPercent.toDecimalString()}%)",
                filter = ThrowFilter.FAIL
            )
        )
        simpleList.add(
            StatsRowData(
                "Potential",
                "$potentialCount (${potentialPercent.toDecimalString()}%)"
            )
        )

        return simpleList.toList()
    }

    private fun getPercent(value: Int, total: Int): Double {
        return if (value == 0) 0.0 else (value.toDouble() / total.toDouble()) * 100
    }
}

@Dao
interface SessionStatsDao {
    @Query("SELECT * FROM session_stats WHERE sessionId = :id")
    suspend fun get(id: Long): SessionStats

    @Query("SELECT * FROM session_stats WHERE sessionId = :id")
    fun getFlow(id: Long): Flow<SessionStats?>

    @Insert
    suspend fun insert(entity: SessionStats)

    @Update
    suspend fun update(entity: SessionStats)

    @Delete
    suspend fun delete(entity: SessionStats)

    @Query("DELETE FROM session_stats WHERE sessionId = :id")
    suspend fun deleteForSession(id: Long)
}

@Dao
interface SessionDao {
    @Query("SELECT * FROM session WHERE id = :id")
    suspend fun get(id: Long): Session

    @Query("SELECT * FROM session WHERE userId = :userId")
    suspend fun getForUser(userId: Long): List<Session>

    @Query("SELECT * FROM session WHERE userId = :userId ORDER BY id DESC LIMIT 1")
    suspend fun getLast(userId: Long): Session?

    @Query("SELECT * FROM session WHERE userId = :userId AND substr(startDateTime, 1, 10) = :date ORDER BY id DESC LIMIT 1")
    suspend fun getForDate(userId: Long, date: String): Session?

    @Insert
    suspend fun insert(entity: Session): Long

    @Update
    suspend fun update(entity: Session)

    @Delete
    suspend fun delete(entity: Session)
}

data class GameTarget(
    val id: Long,
    val label: String,
    val number: Int,
    val prefferedFields: List<DartBoard.Field>,
) {
    fun label(): String {
        return if (id == TargetProvider.randomId) {
            "random"
        } else {
            label
        }
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
    val target: Int,
    val targetHits: Int = 0,
    val targetSuccess: Boolean = false,
    val firstDartDatetime: String? = null,
    val lastDartDatetime: String? = null,
    val isLogged: Boolean = false,
    val firstDartIsSuccess: Boolean = false,
    val onlyLastDartIsFail: Boolean = false,
) {
    var darts: List<Dart> = listOf()

    suspend fun toHistoryRowData(
        dartRepository: DartRepository,
        order: Int,
    ): ThrowHistoryRowData {
        val throwTarget = TargetProvider.get(target)
        val darts = dartRepository.readForThrow(id)

        return ThrowHistoryRowData(
            throwId = id,
            order = order,
            target = target.toString(),
            dart1 = ThrowHistoryRowData.Dart(
                value = darts[0].sum.toString(),
                isSuccess = darts[0].number == throwTarget.number
            ),
            dart2 = ThrowHistoryRowData.Dart(
                value = darts[1].sum.toString(),
                isSuccess = darts[1].number == throwTarget.number
            ),
            dart3 = ThrowHistoryRowData.Dart(
                value = darts[2].sum.toString(),
                isSuccess = darts[2].number == throwTarget.number
            ),
            sum = throwSummary,
            average = dartsAverage,
        )
    }
}

@Dao
interface ThrowDao {
    @Query("SELECT * FROM throw WHERE id = :id")
    suspend fun get(id: Long): Throw

    @Query("SELECT * FROM throw")
    suspend fun readAll(): List<Throw>

    @Query("SELECT * FROM throw WHERE sessionId = :sessionId AND (SELECT COUNT(*) FROM Dart WHERE Dart.throwId = throw.id) = 3 ORDER BY id DESC")
    fun readForSession(sessionId: Long): Flow<List<Throw>?>

    @Insert
    suspend fun insert(entity: Throw): Long

    @Update
    suspend fun update(entity: Throw)

    @Delete
    suspend fun delete(entity: Throw)

    @Query("DELETE FROM throw WHERE sessionId = :id")
    suspend fun deleteForSession(id: Long)
}

@Entity
data class Dart(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val throwId: Long,
    val sessionId: Long,
    val order: Int,
    val multiplicator: Int,
    val number: Int,
    val sum: Int,
)

@Dao
interface DartDao {
    @Query("SELECT * FROM dart WHERE id = :id")
    suspend fun get(id: Long): Dart

    @Query("SELECT * FROM dart WHERE throwId = :throwId")
    suspend fun readForThrow(throwId: Long): List<Dart>

    @Insert
    suspend fun insert(entity: Dart): Long

    @Update
    suspend fun update(entity: Dart)

    @Delete
    suspend fun delete(entity: Dart)

    @Query("DELETE FROM dart WHERE sessionId = :id")
    suspend fun deleteForSession(id: Long)
}
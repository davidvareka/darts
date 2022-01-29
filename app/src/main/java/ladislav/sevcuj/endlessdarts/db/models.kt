package ladislav.sevcuj.endlessdarts.db

import androidx.room.*

@Entity
data class User(
    @PrimaryKey val id: Long,
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
    @PrimaryKey val id: Long,
    val startDateTime: String,
    val endDateTime: String,
)

@Dao
interface SessionDao {
    @Query("SELECT * FROM session WHERE id = :id")
    fun get(id: Long): Session

    @Insert
    fun insert(entity: Session): Long

    @Update
    fun update(entity: Session)

    @Delete
    fun delete(entity: Session)
}

@Entity
data class Target(
    @PrimaryKey val id: Long,
    val label: String,
)

@Dao
interface TargetDao {
    @Query("SELECT * FROM target WHERE id = :id")
    fun get(id: Long): Target

    @Insert
    fun insert(entity: Target): Long

    @Update
    fun update(entity: Target)

    @Delete
    fun delete(entity: Target)
}

@Entity(
    ignoredColumns = ["darts"],
)
data class Throw(
    @PrimaryKey val id: Long,
    val dartsAverage: Int = 0,
    val dartsCount: Int = 0,
    val doubleCount: Int = 0,
    val firstDartDatetime: String? = null,
    val isLogged: Boolean = false,
    val lastDartDatetime: String? = null,
    val player: String,
    val target: String,
    val targetHits: Int = 0,
    val targetSuccess: Boolean = false,
    val throwSummary: Int = 0,
    val tripleCount: Int = 0,
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
    @PrimaryKey val id: Long,
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
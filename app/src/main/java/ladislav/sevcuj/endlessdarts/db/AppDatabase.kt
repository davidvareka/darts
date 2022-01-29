package ladislav.sevcuj.endlessdarts.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import ladislav.sevcuj.endlessdarts.db.*

@Database(
    entities = [
        User::class,
        Session::class,
        Throw::class,
        Target::class,
        Dart::class,
    ], version = 1, exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun sessionDao(): SessionDao
    abstract fun targetDao(): TargetDao
    abstract fun throwDao(): ThrowDao
    abstract fun dartDao(): DartDao

    private class DatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
            }
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    "EndlessDarts"
                )
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance

                instance
            }
        }
    }
}
package ladislav.sevcuj.endlessdarts

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import ladislav.sevcuj.endlessdarts.db.*

class App : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob())

    // Using by lazy so the database and the repository are only created when they're needed
    // rather than when the application starts
    val database by lazy { AppDatabase.getDatabase(this, applicationScope) }
    val useRepository by lazy { UserRepository(database.userDao()) }
    val sessionRepository by lazy { SessionRepository(database.sessionDao()) }
    val sessionStatsRepository by lazy { SessionStatsRepository(database.sessionStatsDao()) }
    val throwRepository by lazy { ThrowRepository(database.throwDao()) }
    val dartRepository by lazy { DartRepository(database.dartDao()) }
}
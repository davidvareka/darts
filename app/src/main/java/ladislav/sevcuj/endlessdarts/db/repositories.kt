package ladislav.sevcuj.endlessdarts.db

import kotlinx.coroutines.flow.Flow

class UserRepository(
    private val dao: UserDao,
) {
    suspend fun insert(entity: User): Long {
        return dao.insert(entity)
    }

    suspend fun delete(entity: User) {
        dao.delete(entity)
    }
}

class SessionRepository(
    private val dao: SessionDao,
) {
    suspend fun insert(entity: Session): Long {
        return dao.insert(entity)
    }

    suspend fun read(id: Long): Session {
        return dao.get(id)
    }

    suspend fun getForUser(userId: Long): List<Session> {
        return dao.getForUser(userId)
    }

    suspend fun getForDay(userId: Long, date: String): Session? {
        return dao.getForDate(userId, date)
    }
}

class SessionStatsRepository(
    private val dao: SessionStatsDao,
) {
    suspend fun insert(entity: SessionStats) {
        dao.insert(entity)
    }

    suspend fun read(id: Long): SessionStats {
        return dao.get(id)
    }

    fun readFlow(id: Long): Flow<SessionStats?> {
        return dao.getFlow(id)
    }

    suspend fun update(entity: SessionStats) {
        dao.update(entity)
    }

    suspend fun deleteForSession(id: Long) {
        dao.deleteForSession(id)
    }
}

class ThrowRepository(
    private val dao: ThrowDao,
) {
    suspend fun insert(entity: Throw): Long {
        return dao.insert(entity)
    }

    suspend fun delete(entity: Throw) {
        dao.delete(entity)
    }

    fun readForSession(id: Long): Flow<List<Throw>?> {
        return dao.readForSession(id)
    }

    suspend fun deleteForSession(id: Long) {
        dao.deleteForSession(id)
    }
}

class DartRepository(
    private val dao: DartDao,
) {
    suspend fun insert(entity: Dart) {
        dao.insert(entity)
    }

    suspend fun delete(entity: Dart) {
        dao.delete(entity)
    }

    suspend fun readForThrow(throwId: Long): List<Dart> {
        return dao.readForThrow(throwId)
    }

    suspend fun deleteForSession(id: Long) {
        dao.deleteForSession(id)
    }
}
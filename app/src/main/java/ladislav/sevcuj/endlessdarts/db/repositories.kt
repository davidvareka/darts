package ladislav.sevcuj.endlessdarts.db

import kotlinx.coroutines.flow.Flow

class UserRepository(
    private val dao: UserDao,
) {
    fun insert(entity: User): Long {
        return dao.insert(entity)
    }

    fun read(id: Long): User {
        return dao.get(id)
    }

    fun delete(entity: User) {
        dao.delete(entity)
    }
}

class SessionRepository(
    private val dao: SessionDao,
) {
    fun insert(entity: Session): Long {
        return dao.insert(entity)
    }

    fun read(id: Long): Session {
        return dao.get(id)
    }

    fun getLast(userId: Long): Session? {
        return dao.getLast(userId)
    }

    fun getForDay(userId: Long, date: String): Session? {
        val all = getLast(userId)
        return dao.getForDate(userId, date)
    }

    fun update(entity: Session) {
        dao.update(entity)
    }
}

class SessionStatsRepository(
    private val dao: SessionStatsDao,
) {
    fun insert(entity: SessionStats) {
        dao.insert(entity)
    }

    fun read(id: Long): SessionStats {
        return dao.get(id)
    }

    fun readFlow(id: Long): Flow<SessionStats> {
        return dao.getFlow(id)
    }

    fun update(entity: SessionStats) {
        dao.update(entity)
    }
}

class ThrowRepository(
    private val dao: ThrowDao,
) {
    fun insert(entity: Throw): Long {
        return dao.insert(entity)
    }

    fun read(id: Long): Throw {
        return dao.get(id)
    }

    fun readAll(): List<Throw> {
        return dao.readAll()
    }

    fun update(entity: Throw) {
        dao.update(entity)
    }

    fun delete(entity: Throw) {
        dao.delete(entity)
    }

    fun readForSession(id: Long): Flow<List<Throw>> {
        return dao.readForSession(id)
    }
}

class DartRepository(
    private val dao: DartDao,
) {
    fun insert(entity: Dart) {
        dao.insert(entity)
    }

    fun read(id: Long): Dart {
        return dao.get(id)
    }

    fun delete(entity: Dart) {
        dao.delete(entity)
    }

    fun update(entity: Dart) {
        dao.update(entity)
    }

    fun readForThrow(throwId: Long): List<Dart> {
        return dao.readForThrow(throwId)
    }
}
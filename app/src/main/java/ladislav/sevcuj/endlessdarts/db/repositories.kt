package ladislav.sevcuj.endlessdarts.db

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
    fun insert(entity: Session) {
        return dao.insert(entity)
    }

    fun read(id: Long): Session {
        return dao.get(id)
    }

    fun update(entity: Session) {
        dao.update(entity)
    }
}

class SessionStatsRepository(
    private val dao: SessionStatsDao,
) {
    fun insert(entity: SessionStats) {
        return dao.insert(entity)
    }

    fun read(id: Long): SessionStats {
        return dao.get(id)
    }

    fun update(entity: SessionStats) {
        dao.update(entity)
    }
}

class ThrowRepository(
    private val dao: ThrowDao,
) {
    fun insert(entity: Throw) {
        dao.insert(entity)
    }

    fun read(id: Long): Throw {
        return dao.get(id)
    }

    fun update(entity: Throw) {
        dao.update(entity)
    }

    fun delete(entity: Throw) {
        dao.delete(entity)
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
}
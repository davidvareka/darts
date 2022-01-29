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
    fun insert(entity: Session): Long {
        return dao.insert(entity)
    }

    fun read(id: Long): Session {
        return dao.get(id)
    }

    fun update(entity: Session) {
        dao.update(entity)
    }
}

class TargetRepository(
    private val dao: TargetDao,
) {
    fun insert(entity: Target): Long {
        return dao.insert(entity)
    }

    fun read(id: Long): Target {
        return dao.get(id)
    }
}

class ThrowRepository(
    private val dao: ThrowDao,
) {
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
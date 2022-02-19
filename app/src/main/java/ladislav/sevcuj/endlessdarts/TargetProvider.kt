package ladislav.sevcuj.endlessdarts

import ladislav.sevcuj.endlessdarts.db.GameTarget


object TargetProvider {
    const val randomId: Long = 3

    private val defaultPreferredFields = listOf(
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

    private val twenty = GameTarget(
        1,
        "20",
        20,
        defaultPreferredFields,
    )

    private val bull = GameTarget(
        2,
        "bull",
        25,
        defaultPreferredFields,
    )

    val all = listOf(
        twenty,
        bull,
        getRandom()
    )

    fun getDefault(): GameTarget {
        return twenty
    }

    fun get(number: Int): GameTarget {
        return when (number) {
            20 -> twenty
            25 -> bull
            else -> getRandom(number)
        }
    }

    fun random(): GameTarget {
        return getRandom()
    }

    private fun getRandom(number: Int? = null): GameTarget {
        val value = if (number == null) {
            val allFields = DartBoard.allFields
            val maxIndex = allFields.size - 1
            val randomIndex = (0..maxIndex).random()
            allFields[randomIndex].value
        } else {
            number
        }

        return GameTarget(
            randomId,
            "random",
            value,
            defaultPreferredFields,
        )
    }
}
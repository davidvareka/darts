package ladislav.sevcuj.endlessdarts

object DartBoard {

    private val regularFields = prepareRegularFields()

    private val specialFields = listOf(
        Field("25", "25", maxMultiplication = 2),
    )

    private fun prepareRegularFields(): List<Field> {
        val list = mutableListOf<Field>()

        for (i in 1..20) {
            list.add(Field(i.toString(), i.toString()))
        }

        return list.toList()
    }

    private fun allFields(): List<Field> {
        val fields = regularFields.toMutableList()
        fields.addAll(specialFields)

        return fields.toList()
    }

    val allFields = allFields()

    data class Field(
        val identifier: String,
        val label: String,
        val value: Int = identifier.toInt(),
        val maxMultiplication: Int = 3,
        val defaultMultiplication: Int = 1,
    )
}
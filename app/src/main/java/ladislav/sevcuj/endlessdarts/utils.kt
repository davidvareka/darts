package ladislav.sevcuj.endlessdarts

fun Double.toDecimalString(): String {
    return String.format("%.2f", this)
}

fun getRandomUserIdentifier() : String {
    val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
    return (1..10)
        .map { allowedChars.random() }
        .joinToString("")
}
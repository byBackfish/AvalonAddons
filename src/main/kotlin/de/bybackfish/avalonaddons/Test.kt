package de.bybackfish.avalonaddons

fun main() {

    val query = listOf(
        "lore:Test Lore",
        "name:Future Sarge",
        "Alphabet"
    )

    println(
        query.map {
            val filterName = if (it.split(":").size == 1) "title" else it.split(":")[0]
            val filterCondition = if (it.split(":").size == 1) it else it.split(":")[1]

            "Filter: $filterName, Condition: $filterCondition"
        }
    )
}

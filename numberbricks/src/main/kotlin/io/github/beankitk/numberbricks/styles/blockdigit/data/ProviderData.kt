package io.github.beankitk.numberbricks.blockdigit.data

interface ProviderData {
    val rows: Int
    val cols: Int
    val brickCount: Int
    val isAdaptive: Boolean
        get() = false
}

inline fun <reified T> ProviderData.createArray(
    crossinline factory: (index: Int) -> T
): Array<T> {
    @Suppress("UNCHECKED_CAST")
    return Array(brickCount) { factory(it) } as Array<T>
}

inline fun <reified T> ProviderData.createArray(
    vararg elements: T
): Array<T> {
    require(elements.size == brickCount) {
        "Array elements do not match provider size"
    }

    @Suppress("UNCHECKED_CAST")
    return elements as Array<T>
}

inline fun ProviderData.asString() = "ProviderData(rows=$rows, cols=$cols, brickCount=$brickCount, isAdaptive=$isAdaptive)"
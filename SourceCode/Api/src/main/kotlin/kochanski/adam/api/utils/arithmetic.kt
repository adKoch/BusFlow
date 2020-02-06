package kochanski.adam.api.utils

fun maxFloat(numbers: List<Float>): Float {
    var currentMax = Float.MIN_VALUE
    numbers.forEach { num ->
        if (num > currentMax) currentMax = num
    }
    return currentMax
}

fun minFloat(numbers: List<Float>): Float {
    var currentMax = Float.MAX_VALUE
    numbers.forEach { num ->
        if (num < currentMax) currentMax = num
    }
    return currentMax
}

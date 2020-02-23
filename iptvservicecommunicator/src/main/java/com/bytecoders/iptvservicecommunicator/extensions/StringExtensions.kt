package com.bytecoders.iptvservicecommunicator.extensions

fun Set<String>?.toIntList(): List<Int> {
    val intList: List<Int> = mutableListOf()
    this?.forEach {
        it.toInt()
    }
    return intList
}

fun List<Int>.toStringSet(): Set<String> {
    val stringSet: MutableSet<String> = mutableSetOf()
    forEach {
        stringSet.add(it.toString())
    }
    return stringSet
}
package org.example.project.data

import kotlin.math.pow

internal class ChunkManager {

    private fun countOfChunks(fileSize: Long, chunkSize: Long): Int {
        val result = fileSize / chunkSize
        return if (fileSize % chunkSize == 0L) {
            result.toInt()
        } else result.toInt() + 1
    }

    fun getPostfixes(fileSize: Long, chunkSize: Long): List<String> {
        //  у меня есть количество
        val countOfChunks = countOfChunks(fileSize, chunkSize)
        // мне нужно идти рекурсивно, пока не найду ближайший к этому числу квадрат числа 26
        val postfixLength = postfixLength(countOfChunks)
        // нашел, теперь степень - это длина
        return List(countOfChunks) { it }
            .map { sequenceElementAt(it, postfixLength) }
    }

    private fun sequenceElementAt(index: Int, length: Int): String {
        val result = CharArray(length)
        var currentIndex = index

        for (i in length - 1 downTo 0) {
            val divisor = 26.0.pow(i.toDouble()).toInt()
            val charIndex = currentIndex / divisor
            result[length - 1 - i] = 'a' + charIndex
            currentIndex %= divisor
        }

        return String(result)
    }

    private fun postfixLength(countOfChunks: Int): Int {
        val countOfVariantsForOneLength = 26L
        var length = 1
        var countOfChunksForThisLength = countOfVariantsForOneLength
        while (countOfChunksForThisLength < countOfChunks) {
            length += 1
            countOfChunksForThisLength *= countOfVariantsForOneLength
        }
        return length
    }

}
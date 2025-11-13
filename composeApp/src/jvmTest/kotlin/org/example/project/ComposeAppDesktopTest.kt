package org.example.project

import org.example.project.data.ChunkManager
import kotlin.test.Test

class ComposeAppDesktopTest {
    private val chunkManager = ChunkManager()

    @Test
    fun example() {
        val postfixes = chunkManager.getPostfixes(100000, 100)
        println(postfixes.joinToString())
    }
}
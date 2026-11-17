package com.mediaplayer.app

import com.mediaplayer.app.domain.GroupByFolderUseCase
import com.mediaplayer.app.domain.MediaFile
import com.mediaplayer.app.domain.PlaylistUseCases
import com.mediaplayer.app.player.boostToGainMb
import com.mediaplayer.app.ui.formatTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LogicTest {
    private fun f(id: Long, folder: String) = MediaFile(id, "content://m/$id", "t$id", "", 1000, folder, "/p/$folder", false, 1)

    @Test fun formatsTime() {
        assertEquals("0:00", formatTime(0))
        assertEquals("1:05", formatTime(65_000))
        assertEquals("1:01:01", formatTime(3_661_000))
    }

    @Test fun boostGain() {
        assertEquals(0, boostToGainMb(100))
        assertEquals(0, boostToGainMb(50))
        assertEquals(602, boostToGainMb(200))
        assertEquals(954, boostToGainMb(300))
    }

    @Test fun playlistOps() {
        val ops = PlaylistUseCases()
        val (l1, id) = ops.create(emptyList(), "  ")
        assertEquals("قائمة جديدة", l1.single().name)
        val l2 = ops.add(ops.add(l1, id, f(1, "a")), id, f(1, "a"))
        assertEquals(1, l2.single().items.size)
        assertTrue(ops.remove(l2, id, f(1, "a")).single().items.isEmpty())
        assertTrue(ops.delete(l2, id).isEmpty())
    }

    @Test fun groupsByFolder() {
        val g = GroupByFolderUseCase()(listOf(f(1, "b"), f(2, "a"), f(3, "b")))
        assertEquals(listOf("a", "b"), g.map { it.name })
        assertEquals(2, g[1].items.size)
    }
}

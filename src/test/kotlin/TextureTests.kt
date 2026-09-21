package sfr.tests

import nimby.readTextFile
import sfr.rendering.SignalTextures
import sfr.signalling.Aspect

internal fun testTextureAssets() {
    val names = (0..10).map { "tex${it.toString().padStart(2, '0')}.svg" } + "xx.svg"
    val expected = names.map { "imgs/ca/sem_bal/$it" }
    expected.forEach { expect(readTextFile(it).contains("<svg")) }
    val catalogue = readTextFile("mod.txt").lineSequence()
        .filter { it.startsWith("state=") }.map { it.substringAfter('=') }.toList()
    expect(catalogue == expected)
    Aspect.entries.forEach { aspect ->
        expect(SignalTextures.path(aspect, 0) in catalogue)
        expect(SignalTextures.path(aspect, 500) in catalogue)
    }
}

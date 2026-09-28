package sfr.tests

import sfr.signals.bal.*
import sfr.signals.carreavertissement.*

import nimby.readTextFile
import sfr.signals.bal.BalAspect as Aspect

internal fun testTextureAssets() {
    val names = (0..10).map { "tex${it.toString().padStart(2, '0')}.svg" } + "xx.svg"
    val expected = names.map { "imgs/ca/sem_bal/$it" } +
        listOf("tex00.svg", "tex01.svg", "tex03.svg", "xx.svg").map { "imgs/cc/cs_a/$it" }
    expected.forEach { expect(readTextFile(it).contains("<svg")) }
    val catalogue = readTextFile("mod.txt").lineSequence()
        .filter { it.startsWith("state=") }.map { it.substringAfter('=') }.toList()
    expect(catalogue == expected)
    CarreAspect.entries.forEach { aspect ->
        expect(CarreTextures.path(aspect, 0, 500) in catalogue)
        expect(CarreTextures.path(aspect, 500, 500) in catalogue)
    }
    Aspect.entries.forEach { aspect ->
        expect(BalTextures.path(aspect, 0) in catalogue)
        expect(BalTextures.path(aspect, 500) in catalogue)
    }
}

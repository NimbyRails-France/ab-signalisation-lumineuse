package sfr.tests

import sfr.signals.bal.*
import sfr.signals.carreavertissement.*
import sfr.signals.carrebal.*

import nimby.readTextFile
import sfr.signals.bal.BalAspect as Aspect

internal fun testTextureAssets() {
    val names = (0..10).map { "tex${it.toString().padStart(2, '0')}.svg" } + "xx.svg"
    val expected = names.map { "imgs/ca/sem_bal/$it" } +
        listOf("tex00.svg", "tex01.svg", "tex03.svg", "xx.svg").map { "imgs/cc/cs_a/$it" } +
        names.map { "imgs/cc/cc_sma/$it" }
    expected.forEach { expect(readTextFile(it).contains("<svg")) }
    val catalogue = readTextFile("mod.txt").lineSequence()
        .filter { it.startsWith("state=") }.map { it.substringAfter('=') }.toList()
    expect(catalogue == expected)
    CarreAspect.entries.forEach { aspect ->
        expect(CarreTextures.path(aspect) in catalogue)
    }
    Aspect.entries.forEach { aspect ->
        expect(BalTextures.forAspect(aspect).frameAt(0) in catalogue)
        expect(BalTextures.forAspect(aspect).frameAt(500) in catalogue)
    }
    CarreBalAspect.entries.forEach { aspect ->
        expect(CarreBalTextures.forAspect(aspect).frameAt(0) in catalogue)
        expect(CarreBalTextures.forAspect(aspect).frameAt(500) in catalogue)
    }
}

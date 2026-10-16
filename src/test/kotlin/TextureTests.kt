package sfr.tests

import sfr.signals.t_a.s.c.v.`011100000`.Textures as Textures011100000
import sfr.signals.t_c.c.b.v.`101000000`.Aspect as Aspect101000000
import sfr.signals.t_c.c.b.v.`101000000`.Textures as Textures101000000
import sfr.signals.t_c.c.b.v.`111000000`.Aspect as Aspect111000000
import sfr.signals.t_c.c.b.v.`111000000`.Textures as Textures111000000
import sfr.signals.t_c.c.b.v.`111001000`.Aspect as Aspect111001000
import sfr.signals.t_c.c.b.v.`111001000`.Textures as Textures111001000
import sfr.signals.uti.sidm.b.z.`200000000`.Textures as MarkerTextures

import nimby.readTextFile
import sfr.signals.t_a.s.c.v.`011100000`.Aspect as Aspect
import kotlin.test.Test

class TextureTests {
    @Test fun packagedCatalogueUsesBinaryReferenceImagesAndAnimationFrames() = testTextureAssets()
}

internal fun testTextureAssets() {
    val names = (0..10).map { "tex${it.toString().padStart(2, '0')}.svg" } + "xx.svg"
    val textures011100000 = names.filterNot { it == "tex01.svg" }.map { "imgs/t_a/s/c/v/011100000/$it" }
    val textures101000000 = listOf("tex00.svg", "tex01.svg", "tex03.svg", "xx.svg")
        .map { "imgs/t_c/c/b/v/101000000/$it" }
    val textures111000000 = names.map { "imgs/t_c/c/b/v/111000000/$it" }
    val textures111001000 = (names.dropLast(1) + listOf("tex12.svg", "tex13.svg", "tex14.svg", "xx.svg"))
        .map { "imgs/t_c/c/b/v/111001000/$it" }
    val protectingTextures = textures011100000 + textures101000000 + textures111000000 + textures111001000
    val markerTextures = listOf(MarkerTextures.MANOEUVRE, MarkerTextures.MANOEUVRE_CLIGNOTANTE)
    val expected = protectingTextures + markerTextures
    expect(Textures011100000.catalogue == textures011100000)
    expect(Textures101000000.catalogue == textures101000000)
    expect(Textures111000000.catalogue == textures111000000)
    expect(Textures111001000.catalogue == textures111001000)
    expect(protectingTextures.size == 42 && expected.size == 44)
    expect(expected[11] == textures101000000.first() && expected[15] == textures111000000.first())
    expect(expected[27] == textures111001000.first())
    protectingTextures.forEach { expect(readTextFile(it).contains("<svg")) }
    markerTextures.forEach { expect(readTextFile(it).isNotEmpty()) }
    val catalogue = readTextFile("mod.txt").lineSequence()
        .filter { it.startsWith("state=") }.map { it.substringAfter('=') }.toList()
    expect(catalogue == expected)
    Aspect101000000.entries.forEach { aspect ->
        expect(Textures101000000.path(aspect) in catalogue)
    }
    Aspect.entries.forEach { aspect ->
        expect(Textures011100000.forAspect(aspect).frameAt(0) in catalogue)
        expect(Textures011100000.forAspect(aspect).frameAt(500) in catalogue)
    }
    Aspect111000000.entries.forEach { aspect ->
        expect(Textures111000000.forAspect(aspect).frameAt(0) in catalogue)
        expect(Textures111000000.forAspect(aspect).frameAt(500) in catalogue)
    }
    Aspect111001000.entries.forEach { aspect ->
        expect(Textures111001000.forAspect(aspect).frameAt(0) in catalogue)
        expect(Textures111001000.forAspect(aspect).frameAt(500) in catalogue)
    }
}

package sfr.tests

import sfr.signals.t_a.s.c.v.`011100000`.Aspect as Aspect011100000
import sfr.signals.t_a.s.c.v.`011100000`.Rules as Rules011100000
import sfr.signals.t_a.s.c.v.`011100000`.Settings as Settings011100000
import sfr.signals.t_c.c.b.v.`101000000`.Signal as T_C_101000000

private var checks = 0
/** Les diagnostics isolés encodent le voisin en nombre. Cette conversion ne
 * sert qu'aux fixtures ; les règles en réseau lisent le voisin fourni par le SDK. */
internal fun evaluate011100000Fixture(settings: Settings011100000, observation: nimby.Observation) =
    Rules011100000.evaluate(settings, observation, Aspect011100000.entries[observation.next])
internal fun expect(value: Boolean) { checks++; check(value) { "Échec du contrôle $checks" } }
internal fun rejected(block: () -> Unit) {
    var failed = false
    try { block() } catch (_: IllegalArgumentException) { failed = true }
    expect(failed)
}
fun main() {
    test011100000Rules()
    testNetworkAndInstructions()
    testTextureAssets()
    println("PASS: $checks contrôles Kotlin : BAL, réseau, données absentes, consignes et textures")
}

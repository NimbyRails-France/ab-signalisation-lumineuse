package sfr.tests

import sfr.signals.bal.*
import sfr.signals.carreavertissement.CarreAvertissement

private var checks = 0
/** Les diagnostics isolés encodent le voisin en nombre. Cette conversion ne
 * sert qu'aux fixtures ; les règles en réseau lisent le voisin fourni par le SDK. */
internal fun evaluateBalFixture(settings: BalSettings, observation: nimby.Observation) =
    BalRules.evaluate(settings, observation, BalAspect.entries[observation.next])
internal fun expect(value: Boolean) { checks++; check(value) { "Échec du contrôle $checks" } }
internal fun rejected(block: () -> Unit) {
    var failed = false
    try { block() } catch (_: IllegalArgumentException) { failed = true }
    expect(failed)
}
fun main() {
    testBalRules()
    testNetworkAndInstructions()
    testTextureAssets()
    println("PASS: $checks contrôles Kotlin : BAL, réseau, données absentes, consignes et textures")
}

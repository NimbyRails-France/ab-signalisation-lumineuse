package sfr.tests

private var checks = 0
internal fun expect(value: Boolean) { checks++; check(value) { "Échec du contrôle $checks" } }
internal fun rejected(block: () -> Unit) {
    var failed = false
    try { block() } catch (_: IllegalArgumentException) { failed = true }
    expect(failed)
}
fun main() {
    testBalRules()
    testNetworkAndInstructions()
    testDrivingModelAndMemory()
    testCppEquivalence()
    testTextureAssets()
    println("PASS: $checks contrôles Kotlin : BAL, réseau, données absentes, consignes, freinage, mémoires et dégagement par la queue")
}

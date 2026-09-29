package sfr.signals.common.bal

/** Indications du BAL uniquement, indépendantes de la phase de clignotement. */
internal enum class BalAspect(val label: String) {
    Unknown("indéterminé"), VL("VL"), A("A"), S("S"),
    GreenFlash("VL cli"), YellowFlash("A cli"), RedFlash("S cli")
}

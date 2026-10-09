package sfr.signals.t_a.s.c.v.`011100000`

/** Indications du BAL uniquement, indépendantes de la phase de clignotement. */
internal enum class Aspect(val label: String) {
    Unknown("indéterminé"), VL("VL"), A("A"), S("S"),
    GreenFlash("VL cli"), YellowFlash("A cli"), RedFlash("S cli")
}

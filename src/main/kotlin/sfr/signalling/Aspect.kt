package sfr.signalling

/** Indication logique, independante de la phase du clignotement. */
internal enum class Aspect(val label: String) {
    Unknown("indetermine"),
    Inactive("inactif"),
    VL("VL"),
    A("A"),
    S("S"),
    GreenFlash("VL cli"),
    YellowFlash("A cli"),
    RedFlash("S cli")
}

package sfr.signals.t_c.c.b.v.`111001000`

import sfr.signals.common.bal.BalAspect

/** Indications du Carré BAL et de la manœuvre. Le régime fixe ne produit jamais S ou S cli. */
internal enum class Aspect(val label: String, val bal: BalAspect?) {
    Unknown("indéterminé", BalAspect.Unknown), VL("VL", BalAspect.VL), A("A", BalAspect.A),
    S("S", BalAspect.S), GreenFlash("VL cli", BalAspect.GreenFlash),
    YellowFlash("A cli", BalAspect.YellowFlash), RedFlash("S cli", BalAspect.RedFlash), Closed("C", null),
    White("M", null), WhiteFlash("M cli", null)
}

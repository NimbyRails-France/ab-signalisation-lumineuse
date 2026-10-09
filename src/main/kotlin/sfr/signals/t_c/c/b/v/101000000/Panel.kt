package sfr.signals.t_c.c.b.v.`101000000`

import nimby.tr

import nimby.Checkbox

/** Déclaration de l'interface uniquement ; les conditions d'ouverture sont
 * dans Rules et la lecture des valeurs dans Settings. */
internal object Panel {
    val automaticOpening = Checkbox(
        "active", tr("signal.t_c.c.b.v.101000000.approach"),
        tr("signal.t_c.c.b.v.101000000.approach.help"),
        defaultValue = true
    )
    val checkboxes = listOf(automaticOpening)
}

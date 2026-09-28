package sfr.signals.carreavertissement

import nimby.tr

import nimby.Checkbox

/** Déclaration de l'interface uniquement ; les conditions d'ouverture sont
 * dans CarreRules et la lecture des valeurs dans CarreSettings. */
internal object CarrePanel {
    val automaticOpening = Checkbox(
        "active", tr("carre.approach"),
        tr("carre.approach.help"),
        defaultValue = true
    )
    val checkboxes = listOf(automaticOpening)
}

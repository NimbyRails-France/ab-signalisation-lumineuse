package sfr.settings

import nimby.Checkbox

/** Cases affichees et sauvegardees par le SDK pour chaque signal. */
internal object SignalPanel {
    val checkboxes = listOf(
        Checkbox("active", "Activer le BAL automatique", "Calculer les indications de ce signal."),
        Checkbox("greenFlash", "Equipement : vert clignotant", "Autorise cette indication sans la forcer."),
        Checkbox("yellowFlash", "Equipement : jaune clignotant", "Autorise cette indication sans la forcer."),
        Checkbox("redFlash", "Equipement : rouge clignotant", "Autorise cette indication sans la forcer."),
        Checkbox("preannouncement", "Installation avec preannonce", "Employer la preannonce selon l'aval."),
        Checkbox("reducedAnnouncement", "Annonce a distance reduite", "Caracteristique declaree de l'installation."),
        Checkbox("work160", "Situation de travaux 160 active", "Situation du scenario ; requiert l'equipement vert clignotant."),
        Checkbox("redFlashUseDeclared", "Installation autorisant le rouge clignotant", "La condition d'emploi doit aussi etre etablie."),
        Checkbox("endOfBal", "Fin de zone BAL : aval suppose libre", "Limite temporaire du calcul. Le canton protege reste controle."),
        Checkbox("redFlashConditionActive", "Condition d'emploi du rouge clignotant active", "Scenario declare : exige aussi l'equipement et l'installation autorisant cette indication. Ne force pas le franchissement natif.")
    )
}

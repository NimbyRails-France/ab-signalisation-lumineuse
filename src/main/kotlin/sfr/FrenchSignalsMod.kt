package sfr

import nimby.*
import sfr.signals.bal.BalSignals
import sfr.signals.carreavertissement.CarreAvertissement

/** Composition du paquet : ajouter les modèles ici. Chaque modèle possède
 * ses types, replis et callbacks ; aucun catalogue global d'indications. */
internal fun FrenchSignalsMod(): SignallingMod = signalMod(
    id = "signalisationfrancaiserealiste",
    title = "Signalisation française réaliste"
) {
    maximumLineSpeed = true
    diagnosticFile = "sfr-faults.jsonl"
    signal(BalSignals.model)
    signal(CarreAvertissement.model)
}

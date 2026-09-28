package sfr

import nimby.*
import nimby.mod.modInfo
import sfr.signals.bal.BalSignals
import sfr.signals.bal.BalWorkZone
import sfr.signals.carreavertissement.CarreAvertissement

/** Composition du paquet : ajouter les modèles ici. Chaque modèle possède
 * ses types, replis et callbacks ; aucun catalogue global d'indications. */
internal fun FrenchSignalsMod(): SignallingMod = signalMod(modInfo) {
    metadata(author = "NimbyRails France", name = tr("mod.name"), description = tr("mod.description"))
    maximumLineSpeed = true
    diagnosticFile = "sfr-faults.jsonl"
    signal(BalSignals.model)
    signal(CarreAvertissement.model)
    prepareNetwork(BalWorkZone::apply)
}

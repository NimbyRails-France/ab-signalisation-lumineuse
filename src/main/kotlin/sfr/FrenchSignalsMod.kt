package sfr

import nimby.*
import nimby.mod.modInfo
import sfr.signals.bal.BalSignals
import sfr.signals.common.bal.BalWorkZone
import sfr.signals.carreavertissement.CarreAvertissement
import sfr.signals.carrebal.CarreBal

/** Composition du paquet : ajouter les modèles ici. Chaque modèle possède
 * ses types, replis et callbacks ; aucun catalogue global d'indications. */
internal fun FrenchSignalsMod(): SignallingMod = signalMod(modInfo) {
    metadata(author = "NimbyRails France", name = tr("mod.name"), description = tr("mod.description"))
    maximumLineSpeed = true
    diagnosticFile = "sfr-faults.jsonl"
    signal(BalSignals.model)
    signal(CarreAvertissement.model)
    signal(CarreBal.model)
    val workZone = BalWorkZone(listOf(BalSignals.model.type, CarreBal.model.type))
    prepareNetwork(workZone::apply)
}

package sfr

import nimby.*
import nimby.mod.modInfo
import sfr.signals.t_a.s.c.v.`011100000`.Signal as T_A_011100000
import sfr.signals.common.bal.BalWorkZone
import sfr.signals.t_c.c.b.v.`101000000`.Signal as T_C_101000000
import sfr.signals.t_c.c.b.v.`111000000`.Signal as T_C_111000000

/** Composition du paquet : ajouter les modèles ici. Chaque modèle possède
 * ses types, replis et callbacks ; aucun catalogue global d'indications. */
internal fun FrenchSignalsMod(): SignallingMod = signalMod(modInfo) {
    metadata(author = "NimbyRails France", name = tr("mod.name"), description = tr("mod.description"))
    maximumLineSpeed = true
    diagnosticFile = "sfr-faults.jsonl"
    signal(T_A_011100000.model)
    signal(T_C_101000000.model)
    signal(T_C_111000000.model)
    val workZone = BalWorkZone(listOf(T_A_011100000.model.type, T_C_111000000.model.type))
    prepareNetwork(workZone::apply)
}

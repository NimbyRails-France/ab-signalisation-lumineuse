package sfr.signals.t_a.s.c.v.`011100000`

import nimby.*
import sfr.integrations.offerSignalPlacement

/** Assemble les rôles du BAL. Ses enums, règles, images et consignes sont
 * indépendantes des autres modèles. Le SDK fournit les données du voisin. */
internal object Signal {
    const val TYPE = "sfr.t_a.s.c.v.011100000"
    const val TEXTURES = "sfr_t_a_s_c_v_011100000"

    val model = signalModel(
        SignalType(TYPE, tr("signal.t_a.s.c.v.011100000.name"), TEXTURES, Panel.checkboxes),
        fallback = Decision(Aspect.Unknown, Reason.MissingObservation),
        invalidNetwork = Decision(Aspect.Unknown, Reason.InvalidTopology)
    ) {
        number(Panel.workBlocks)
        construction(Textures.catalogue, name = tr("signal.t_a.s.c.v.011100000.name"),
            catalogueName = tr("signal.t_a.s.c.v.011100000.name"), size = 4, left = true)
        offerSignalPlacement()
        evaluate { settings, observation, next ->
            Rules.evaluate(Settings.from(settings), observation, next)
        }
        rules { Rules.decide(this) }
        appearance { Textures.forAspect(it.aspect) }
        driving(Driving::fromDecision)
        faults(Diagnostics::isFault)
        aspectNames { it.label }
        reasonNames { it.description }
    }
}

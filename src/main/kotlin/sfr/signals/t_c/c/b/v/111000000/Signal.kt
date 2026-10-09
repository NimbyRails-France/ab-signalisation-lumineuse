package sfr.signals.t_c.c.b.v.`111000000`

import nimby.*
import sfr.integrations.offerSignalPlacement


/** Même cantonnement que le BAL ; le carré ajoute une fermeture absolue. */
internal object Signal {
    const val TYPE = "sfr.t_c.c.b.v.111000000"
    const val TEXTURES = "sfr_t_c_c_b_v_111000000"

    val model = signalModel(
        SignalType(TYPE, tr("signal.t_c.c.b.v.111000000.name"), TEXTURES, Panel.checkboxes),
        fallback = Decision(Aspect.Unknown, Reason.MissingObservation),
        invalidNetwork = Decision(Aspect.Unknown, Reason.InvalidTopology)
    ) {
        number(Panel.workBlocks)
        construction(Textures.catalogue, name = tr("signal.t_c.c.b.v.111000000.name"), size = 4, left = true)
        offerSignalPlacement()
        evaluate { settings, observation, next ->
            Rules.evaluate(settings, observation, next)
        }
        rules { Rules.decide(this) }
        appearance { Textures.forAspect(it.aspect) }
        driving { Driving.driving(it) }
        faults(Diagnostics::isFault)
        aspectNames { it.label }
        reasonNames { it.description }
    }
}

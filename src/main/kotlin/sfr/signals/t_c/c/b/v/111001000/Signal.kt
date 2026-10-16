package sfr.signals.t_c.c.b.v.`111001000`

import nimby.*
import sfr.integrations.offerSignalPlacement

/** Carré BAL fixe ; les manœuvres exigent un parcours natif borné et qualifié. */
internal object Signal {
    const val TYPE = "sfr.t_c.c.b.v.111001000"
    const val TEXTURES = "sfr_t_c_c_b_v_111001000"

    val model = signalModel(
        SignalType(TYPE, tr("signal.t_c.c.b.v.111001000.name"), TEXTURES, Panel.checkboxes),
        fallback = Decision(Aspect.Closed, Reason.MissingObservation),
        invalidNetwork = Decision(Aspect.Closed, Reason.InvalidTopology)
    ) {
        number(Panel.workBlocks)
        construction(Textures.catalogue, name = tr("signal.t_c.c.b.v.111001000.name"), size = 4, left = true)
        observeApproach(blocks = 2)
        observeRoute()
        offerSignalPlacement()
        evaluate { settings, observation, next -> Rules.evaluate(settings, observation, next) }
        rules { Rules.decide(this) }
        appearance { Textures.forAspect(it.aspect) }
        driving { Driving.driving(it) }
        faults(Diagnostics::isFault)
        aspectNames { it.label }
        reasonNames { it.description }
    }
}

package sfr.signals.t_c.c.b.v.`101000000`

import nimby.*
import sfr.integrations.offerSignalPlacement

/** Assemble le Carré simple Avertissement. La fin de BAL est un usage possible,
 * pas son identité : aucune règle ni enum BAL n'est importée par ce modèle. */
internal object Signal {
    const val TYPE = "sfr.t_c.c.b.v.101000000"
    const val TEXTURES = "sfr_t_c_c_b_v_101000000"
    val declaration = SignalType(TYPE, tr("signal.t_c.c.b.v.101000000.name"), TEXTURES,
        Panel.checkboxes)

    val model = signalModel(
        declaration,
        fallback = Decision(Aspect.Closed, Reason.MissingObservation),
        invalidNetwork = Decision(Aspect.Closed, Reason.InvalidTopology)
    ) {
        construction(Textures.catalogue, name = tr("signal.t_c.c.b.v.101000000.name"), size = 4, left = true)
        observeApproach(blocks = 2)
        offerSignalPlacement()
        rules { Rules.decide(this) }
        appearance { Textures.forAspect(it.aspect) }
        driving(Driving::fromDecision)
        faults(Diagnostics::isFault)
        aspectNames { it.label }
        reasonNames { it.description }
    }
}

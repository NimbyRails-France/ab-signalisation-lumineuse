package sfr.signals.carreavertissement

import nimby.*
import sfr.integrations.offerSignalPlacement

/** Assemble le Carré simple Avertissement. La fin de BAL est un usage possible,
 * pas son identité : aucune règle ni enum BAL n'est importée par ce modèle. */
internal object CarreAvertissement {
    const val TYPE = "sfr.carre-simple-avertissement"
    const val TEXTURES = "sfr_cs_a_v1"
    val declaration = SignalType(TYPE, tr("carre.title"), TEXTURES,
        CarrePanel.checkboxes, observeApproach = true, approachBlocks = 2)

    val model = signalModel(
        declaration,
        fallback = CarreDecision(CarreAspect.Closed, CarreReason.MissingObservation),
        invalidNetwork = CarreDecision(CarreAspect.Closed, CarreReason.InvalidTopology)
    ) {
        offerSignalPlacement()
        rules { CarreRules.decide(this) }
        animatedImages { indication, time, half -> CarreTextures.path(indication.aspect, time, half) }
        driving(CarreDriving::fromDecision)
        faults(CarreDiagnostics::isFault)
        aspectNames { it.label }
        reasonNames { it.description }
    }
}

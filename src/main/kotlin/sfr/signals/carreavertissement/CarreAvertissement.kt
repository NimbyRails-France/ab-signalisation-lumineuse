package sfr.signals.carreavertissement

import nimby.*
import sfr.integrations.offerSignalPlacement

/** Assemble le Carré simple Avertissement. La fin de BAL est un usage possible,
 * pas son identité : aucune règle ni enum BAL n'est importée par ce modèle. */
internal object CarreAvertissement {
    const val TYPE = "sfr.carre-simple-avertissement"
    const val TEXTURES = "sfr_cs_a_v1"
    val declaration = SignalType(TYPE, tr("carre.title"), TEXTURES,
        CarrePanel.checkboxes)

    val model = signalModel(
        declaration,
        fallback = CarreDecision(CarreAspect.Closed, CarreReason.MissingObservation),
        invalidNetwork = CarreDecision(CarreAspect.Closed, CarreReason.InvalidTopology)
    ) {
        construction(CarreTextures.catalogue, name = tr("carre.construction"), size = 4, left = true)
        observeApproach(blocks = 2)
        offerSignalPlacement()
        rules { CarreRules.decide(this) }
        appearance { CarreTextures.forAspect(it.aspect) }
        driving(CarreDriving::fromDecision)
        faults(CarreDiagnostics::isFault)
        aspectNames { it.label }
        reasonNames { it.description }
    }
}

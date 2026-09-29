package sfr.signals.carrebal

import nimby.*
import sfr.integrations.offerSignalPlacement


/** Même cantonnement que le BAL ; le carré ajoute une fermeture absolue. */
internal object CarreBal {
    const val TYPE = "sfr.carre-bal-c"
    const val TEXTURES = "sfr_carre_bal_c_v1"

    val model = signalModel(
        SignalType(TYPE, tr("carreBal.title"), TEXTURES, CarreBalPanel.checkboxes),
        fallback = CarreBalDecision(CarreBalAspect.Unknown, CarreBalReason.MissingObservation),
        invalidNetwork = CarreBalDecision(CarreBalAspect.Unknown, CarreBalReason.InvalidTopology)
    ) {
        number(CarreBalPanel.workBlocks)
        construction(CarreBalTextures.catalogue, name = tr("carreBal.title"), size = 4, left = true)
        offerSignalPlacement()
        evaluate { settings, observation, next ->
            CarreBalRules.evaluate(settings, observation, next)
        }
        rules { CarreBalRules.decide(this) }
        appearance { CarreBalTextures.forAspect(it.aspect) }
        driving { CarreBalDriving.driving(it) }
        faults(CarreBalDiagnostics::isFault)
        aspectNames { it.label }
        reasonNames { it.description }
    }
}

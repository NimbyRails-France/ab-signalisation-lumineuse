package sfr.signals.bal

import nimby.*
import sfr.integrations.offerSignalPlacement

/** Assemble les rôles du BAL. Ses enums, règles, images et consignes sont
 * indépendantes des autres modèles. Le SDK fournit les données du voisin. */
internal object BalSignals {
    const val TYPE = "sfr.bal-a"
    const val TEXTURES = "sfr_bal_a_cpp_v1"

    val model = signalModel(
        SignalType(TYPE, tr("bal.title"), TEXTURES, BalPanel.checkboxes),
        fallback = BalDecision(BalAspect.Unknown, BalReason.MissingObservation),
        invalidNetwork = BalDecision(BalAspect.Unknown, BalReason.InvalidTopology)
    ) {
        offerSignalPlacement()
        evaluate { settings, observation, next ->
            BalRules.evaluate(BalSettings.from(settings), observation, next)
        }
        rules { BalRules.decide(this) }
        animatedImages { indication, time, half -> BalTextures.path(indication.aspect, time, half) }
        driving(BalDriving::fromDecision)
        faults(BalDiagnostics::isFault)
        aspectNames { it.label }
        reasonNames { it.description }
    }
}

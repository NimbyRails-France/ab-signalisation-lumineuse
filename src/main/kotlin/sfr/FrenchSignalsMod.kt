package sfr

import nimby.*
import sfr.settings.SignalSettings
import sfr.settings.SignalPanel
import sfr.signalling.*
import sfr.rendering.SignalTextures
import sfr.driving.DrivingInstructions
import sfr.driving.DrivingModel

/** Point de configuration du mod : aucun export natif ni code C++ à ajouter. */
internal class FrenchSignalsMod : SignallingMod() {
    override val id = "sfr.bal-a"
    override val title = "SFR - BAL semaphore A"
    // Identifiant conservé pour que les parties existantes retrouvent leurs signaux.
    override val textureSet = "sfr_bal_a_cpp_v1"
    override val maximumLineSpeed = true
    override val diagnosticFile = "sfr-bal-faults.jsonl"
    override val unknownDecision = decision(Aspect.Unknown, Reason.MissingObservation)
    override val invalidNetworkDecision = decision(Aspect.Unknown, Reason.InvalidTopology)
    override val checkboxes = SignalPanel.checkboxes

    override fun evaluate(settings: Map<String, Boolean>, observation: Observation) =
        BalRules.evaluate(SignalSettings.from(settings), observation.toBal()).toSdk()

    override fun decide(signal: Signal, next: Decision?): Decision? {
        var settings = SignalSettings.from(signal.settings)
        var observation = signal.observation.toBal()
        when (signal.settingsStatus) {
            SettingsStatus.Absent -> settings = SignalSettings()
            SettingsStatus.Unavailable -> {
                settings = settings.copy(active = true)
                observation = observation.copy(fresh = false)
            }
            SettingsStatus.Present -> Unit
        }
        return BalRules.decide(NetworkSignal(signal.id, signal.nextSignal, settings, observation), next?.toBal())?.toSdk()
    }

    override fun fromLive(signal: Signal) = signal.copy(observation = signal.observation.copy(
        redFlashCondition = signal.settings["redFlashConditionActive"] == true))

    override fun texture(decision: Decision, simulationMs: Long, halfPeriodMs: Long) =
        SignalTextures.path(decision.toBal().aspect, simulationMs, halfPeriodMs)

    // Codes du mod seulement. S force un arret absolu ; la permission
    // apres arret reste reservee a une occupation reellement observee.
    override fun forcedDecision(aspect: Int): Decision? {
        val value = Aspect.entries.getOrNull(aspect) ?: return null
        val reason = when (value) {
            Aspect.Unknown -> Reason.MissingObservation
            Aspect.Inactive -> Reason.Inactive
            Aspect.S -> Reason.ForcedStop
            Aspect.A -> Reason.StopAnnouncement
            Aspect.YellowFlash -> Reason.Preannouncement
            Aspect.GreenFlash -> Reason.Work160
            Aspect.RedFlash -> Reason.ReducedAnnouncement
            Aspect.VL -> Reason.Clear
        }
        return decision(value, reason)
    }

    override fun drivingRule(decision: Decision) = DrivingInstructions.fromDecision(decision.toBal())
    override fun isFault(decision: Decision) = decision.toBal().aspect == Aspect.Unknown
    override fun isActive(decision: Decision) = decision.toBal().aspect != Aspect.Inactive
    override fun aspectName(aspect: Int) = aspectOf(aspect).label
    override fun reasonName(reason: Int) = reasonOf(reason).description

    override fun plan(vehicle: Vehicle, settings: DrivingSettings, input: DrivingInput, constraints: List<Constraint>) =
        DrivingModel.plan(vehicle, settings, input, constraints)
}

// Conversion SDK : les regles du mod utilisent exclusivement des enums Kotlin.
private fun decision(aspect: Aspect, reason: Reason) = Decision(aspect.ordinal, reason.ordinal)
private fun aspectOf(code: Int) = requireNotNull(Aspect.entries.getOrNull(code)) { "Indication inconnue : $code" }
private fun reasonOf(code: Int) = requireNotNull(Reason.entries.getOrNull(code)) { "Motif inconnu : $code" }
private fun Decision.toBal() = SignalDecision(aspectOf(aspect), reasonOf(reason))
private fun SignalDecision.toSdk() = Decision(aspect.ordinal, reason.ordinal)
private fun Observation.toBal() = SignalObservation(block, fresh, routeKnown, forcedStop, lampFailed, redFlashCondition, aspectOf(next))

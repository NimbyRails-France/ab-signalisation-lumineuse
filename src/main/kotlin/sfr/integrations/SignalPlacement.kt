package sfr.integrations

import nimby.tr

import nimby.SignalModelBuilder

/** Coopération facultative du paquet SFR : seule la déclaration du bouton est commune.
 * Son fournisseur calcule la pose ; ce fichier ne contient aucune règle de signal.
 * Sans fournisseur présent, le SDK masque ce bouton et le signal reste autonome. */
internal fun <A : Enum<A>, R : Enum<R>> SignalModelBuilder<A, R>.offerSignalPlacement() {
    action("repeat", tr("placement.repeat"), whenMod = "signal-placement", service = "repeat.v1")
}

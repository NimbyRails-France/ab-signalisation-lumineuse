package sfr.signals.t_c.c.b.v.`111001000`

import sfr.signals.common.bal.CarreBalSettings

/** Carré fixe : aucune ancienne clé ni valeur injectée ne choisit un sémaphore
 * ou une fermeture forcée invisible. Les arrêts observés restent prioritaires. */
internal object Settings {
    fun from(values: Map<String, Boolean>) =
        CarreBalSettings.fixedSquare(values)
}

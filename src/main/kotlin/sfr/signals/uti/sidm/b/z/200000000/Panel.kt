package sfr.signals.uti.sidm.b.z.`200000000`

import nimby.tr

/** Libellés préparés pour le futur sélecteur de marqueurs.
 * Les noms affichés sont AB UTIL T31, AB UTIL T32, AB UTIL T33…, indépendamment de leur
 * signification métier. Les références de traduction sont créées une seule fois
 * et suivent exactement l'ordre du catalogue, hors du calcul des signaux.
 */
internal object Panel {
    val names = Textures.catalogue.map { path ->
        val file = path.substringAfterLast('/').substringBeforeLast('.').lowercase()
        val suffix = file.removePrefix("tex")
        val code = if (suffix.all { it in '0'..'9' }) "t$suffix" else file
        tr("signal.uti.sidm.b.z.200000000.$code.name")
    }
}

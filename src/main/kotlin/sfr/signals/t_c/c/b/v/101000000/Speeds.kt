package sfr.signals.t_c.c.b.v.`101000000`

/** Politique de conduite du Carré, en m/s. Ce n'est pas une vitesse imposée
 * pendant toute l'approche sous avertissement : le SDK calcule le freinage
 * nécessaire pour atteindre la cible annoncée au signal suivant. */
internal object Speeds {
    /** Passage du signal cible s'il rouvre et autorise explicitement le passage,
     * alors qu'une annonce d'arrêt a déjà été reçue et mémorisée par le train. */
    const val REOPENED_PASSING_SPEED = 30.0 / 3.6
}

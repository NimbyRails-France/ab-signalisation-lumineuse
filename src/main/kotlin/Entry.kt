package nimby.mod

import nimby.SignallingMod
import sfr.FrenchSignalsMod

/** Point d'entrée du paquet. Les types et leurs rôles sont assemblés dans sfr;
 * aucune règle, texture ni recherche de processus n'appartient à ce fichier. */
internal fun createMod(): SignallingMod = FrenchSignalsMod()

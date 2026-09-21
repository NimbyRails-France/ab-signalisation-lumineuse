# Réglages du sémaphore BAL A

Le plugin déclare les cases du panneau `sfr.bal-a` dans
`src/main/kotlin/sfr/settings/SignalPanel.kt`. Le SDK assure leur affichage
et leur sauvegarde pour chaque signal du catalogue `sfr_bal_a_cpp_v1`.

| Case | Rôle |
| --- | --- |
| Activer le BAL automatique | Active le calcul des indications du signal. |
| Équipement : vert clignotant | Autorise l'emploi du vert clignotant. |
| Équipement : jaune clignotant | Autorise l'emploi du jaune clignotant. |
| Équipement : rouge clignotant | Autorise l'emploi du rouge clignotant. |
| Installation avec préannonce | Déclare la préannonce selon l'aval. |
| Annonce à distance réduite | Déclare cette caractéristique de l'installation. |
| Situation de travaux 160 active | Déclare la situation de travaux ; requiert l'équipement vert clignotant. |
| Installation autorisant le rouge clignotant | Déclare l'emploi possible de cette indication. |
| Fin de zone BAL : aval supposé libre | Fixe la limite du calcul ; le canton protégé reste contrôlé. |
| Condition d'emploi du rouge clignotant active | Déclare la condition du scénario ; requiert aussi l'équipement et l'autorisation de l'installation. |

Les cases d'équipement autorisent une indication sans la forcer. L'occupation
et l'itinéraire proviennent des observations du SDK. La case de condition du
rouge clignotant ne force pas le franchissement natif.

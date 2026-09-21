# Réinitialisation des releases GitHub — inventaire

Relevé du 21 septembre 2026. Aucune release ni aucun tag n'a été supprimé.
Le périmètre de suppression reste à confirmer : nouvelle série avec historique
conservé, ou suppression des anciennes releases et de leurs tags.

| Dépôt NimbyRails-France | Tags associés aux releases | Fichiers attachés |
| --- | --- | ---: |
| signalisationfrancaiserealiste | v0.1.0 | 3 |
| sdk | v0.4.0, v0.5.0, v0.6.0, v0.6.6, v0.7.0, v0.7.1, v0.7.2 | 33 |
| hub | v0.1.0, v0.2.0, v0.2.1, v0.2.2, v0.2.3, v0.3.0, v0.3.1 | 21 |
| tco | v0.1.0, v0.2.0, v0.4.0, v0.5.0, v0.5.1, v0.5.2 | 24 |

Total : **21 releases, 21 tags distants et 81 fichiers attachés**.
Les métadonnées GitHub et les références distantes sont conservées localement
dans `../sdk/build/github-release-inventory/*.json`. Les fichiers binaires
attachés ne sont pas sauvegardés par cet inventaire.

La suppression des releases retire leurs téléchargements ; la suppression des
tags retire les références de version. Elle ne doit pas être confondue avec
une réécriture des branches ou des commits, qui n'est pas demandée.

Les nouvelles releases utiliseront le [format multiplateforme](releases.md).
Une version déjà distribuée ne devrait pas désigner un nouveau contenu : les
installations existantes comparent les versions et peuvent conserver leurs
anciens paquets en cache. Supprimer les anciennes releases ne remet pas ces
installations à zéro.

La première version de la nouvelle série et sa compatibilité SDK doivent être
cohérentes dans les quatre projets avant publication. Le numéro de départ
n'est pas encore changé. La migration Linux restant incomplète, les paquets
locaux actuels ne constituent pas une nouvelle distribution complète.

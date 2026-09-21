# Migration Kotlin et Linux — état du 21 septembre 2026

La migration complète n'est pas terminée. Ce relevé distingue les constructions
et tests locaux de l'intégration au jeu.

## Vérifications réalisées

| Composant | Windows | Ubuntu WSL x64 | Portée |
| --- | --- | --- | --- |
| Mod Kotlin/Native + API Kotlin | 5 groupes de régression, DLL construite | 5 groupes de régression, `.so` construite | Projet de vérification contre les sources SDK, pas un kit Linux distribuable |
| SDK natif | 40 tests passent | 24 tests passent | Tests portables, chargement de modules, interface de réglages, lecture de processus et serveur de lecture Linux ; hooks Linux non validés |
| Client Kotlin du SDK | 6 tests passent | 6 tests passent | Bibliothèque native de test, dont fermeture, coexistence de sessions et disponibilité des horaires |
| Plugin Gradle des mods | 9 tests passent | 9 tests passent | Configuration Windows/Linux, manifestes de release, dépendances de vérification, rejet de kit incohérent |
| Hub Compose | 44 tests passent | 44 tests passent | Installation Linux, fichiers utilisés par un autre processus, gestion et releases par canal/plateforme ; les tests Unix ne s'exécutent pas sous Windows |
| TCO Compose | 10 tests passent | 10 tests passent | Géométrie/filtres, index spatial, transitions de chargement et fermeture de connexion |

Les liaisons Kotlin/Native produisent des avertissements dans le C++ généré
par Kotlin 2.2.20 (187 sur la construction Windows). Les tâches terminent avec
succès ; ce code généré n'est pas du code C++ à maintenir dans le mod.

Les DEB du Hub et du TCO ont été installés dans WSL. Le TCO a également subi une
mise à niveau du DEB reconstruit ; son `--check-sdk` fonctionne avec le Java
inclus et la bibliothèque native de test (PID 42). Les RPM sont construits,
mais leur installation n'a pas été testée sur une distribution RPM. Un MSI
TCO a été construit lors de la migration ; son installation n'est pas validée.
Les paquets macOS et les workflows distants ne sont pas validés.

Le Hub produit désormais ses installateurs et son manifeste de release Linux
avec `prepareRelease`. La taille et le SHA-256 du DEB référencé ont été vérifiés.
Le kit développeur Kotlin Windows a également été reconstruit localement.
Le mod a passé `packageMod` contre ce kit : cinq groupes de régression,
construction de la DLL, vérification des exports, démarrage, arrêt, redémarrage
et rechargement, puis génération du ZIP et des deux manifestes.
Ces résultats ne constituent pas une publication ni une validation des mises
à jour depuis les anciennes versions distribuées.

## Partie Linux réelle

Le lancement Steam en conteneur reste noir sur cette configuration WSL : les
bibliothèques du pilote D3D12 hôte ne sont pas exposées correctement. Le raccourci
**NIMBY Rails Ubuntu RTX** lance le jeu natif dans Ubuntu, avec Steam connecté.

Un second problème, `std::bad_alloc`, a été reproduit et diagnostiqué sous GDB :
le chargeur de textures traite `/` comme une image. La bibliothèque de
compatibilité locale refuse la lecture de dossiers et permet à la même copie
de sauvegarde d'atteindre la carte avec les lignes et les trains visibles.
Voir [le diagnostic WSL](wsl-vulkan.md). La cause amont du chemin de texture reste
à déterminer ; toutes les textures et la synchronisation Cloud ne sont pas
qualifiées par ce test.

Le résolveur SDK retrouve alors UIState, la base, la copie et la simulation,
avec des valeurs concordantes avec GDB. Il exige la taille et le SHA-256 exacts
du binaire Linux et deux captures de racines identiques. Le relevé technique
est dans le dépôt SDK : `docs/research/linux-runtime.md`.

La lecture réelle retrouve 3 342 stations, 248 409 voies, 25 968 signaux et
947 trains dans la copie de diagnostic. Les catalogues de textures, les
sélecteurs, la version de la base et les occupations ont également été lus.
Les réservations ont été confirmées sur une capture brièvement arrêtée sous
GDB ; pendant la simulation, leur mutation peut faire rejeter la capture.
Ces observations ne valident pas les hooks ni les écritures dans le jeu.

Un serveur local de lecture permet au SDK Linux d'exposer sa mémoire au même
utilisateur sans modifier la politique ptrace. Ses tests couvrent l'identité
du processus, les limites, les adresses invalides et une pause d'observation.
Son chargement dans le jeu réel et la connexion du TCO sans privilèges root
ont été vérifiés. Le transfert d'un descripteur de mémoire en lecture seule
évite les échanges par socket pour chaque petite lecture. Avec le SDK Release,
le contrôle TCO en ligne de commande a retrouvé les 947 trains, 248 409 voies
et 25 968 signaux en 2,09 secondes, démarrage Java compris. Cette mesure WSL
n'est ni un benchmark Linux natif ni une recette de stabilité prolongée.

Le paquet portable TCO et le SDK Linux ont été installés puis retirés par le
gestionnaire Kotlin du Hub dans une copie du dossier de jeu. Le TCO installé
a lu le processus réel. Les empreintes du jeu, les fichiers gérés et la
suppression du raccourci ont été contrôlés. Le lancement du jeu depuis un
profil Hub reste à tester ; ce scénario n'a pas lancé la copie d'exécutable.

## Travail restant pour une distribution Linux complète

1. Achever la qualification des structures Linux, notamment la cohérence des
   réservations en mouvement et l'horloge. Les offsets Windows ne sont pas interchangeables.
2. Terminer le backend SDK, les hooks et le raccordement du chargeur Linux
   au démarrage du jeu. L'accès en lecture du client ordinaire fonctionne.
3. Construire le kit SDK Linux réel et vérifier le chargement du mod, ses
   réglages, ses textures et ses commandes dans le jeu, puis une recette longue.
4. Valider le lancement des profils Linux dans le Hub avec le SDK installé.
   L'installation Kotlin et le raccordement au lanceur graphique WSL existent ;
   le jeu doit encore être fermé manuellement avant un changement de profil.
5. Achever la parité du TCO Kotlin avec les panneaux et interactions de l'ancien
   TCO, puis tester son observation du jeu réel sur chaque plateforme.
6. Tester les cycles d'installation/désinstallation et la publication des
   paquets par plateforme. Aucune release n'a été publiée pendant ces essais.

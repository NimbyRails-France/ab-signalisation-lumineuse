# Reprise sur Ubuntu natif — 21 septembre 2026

Point de passage de la migration, **pas une version prête à publier**.
Ubuntu WSL a été supprimé à la demande de l'utilisateur. Les quatre dépôts
Windows ont été conservés puis préparés pour reprise sur Linux natif.
Aucune release ni aucun tag n'est demandé pour ce point de passage.

## Récupération

Cloner les quatre dépôts côte à côte, sur leur branche `main` :

```sh
mkdir -p ~/projects/nimby-france
cd ~/projects/nimby-france
git clone https://github.com/NimbyRails-France/sdk.git
git clone https://github.com/NimbyRails-France/hub.git
git clone https://github.com/NimbyRails-France/tco.git
git clone https://github.com/NimbyRails-France/signalisationfrancaiserealiste.git
```

Le TCO utilise par défaut `../sdk/kotlin-client`. Le mod destiné aux développeurs
attend un kit via `nrfSdkDir` ou `NRF_KOTLIN_SDK` ; le kit Linux complet reste à
terminer. Pour vérifier ses sources en attendant, utiliser le projet de
vérification du SDK ci-dessous.

## Vérifications à relancer

Prérequis : JDK 21, CMake, Ninja, compilateurs C/C++, en-têtes OpenSSL,
Python 3 et bibliothèques graphiques de Compose. Les installateurs Linux
demandent également `fakeroot` et `rpm`. Exécuter depuis le dossier parent :

```sh
cmake -S sdk -B sdk/build/linux-native -G Ninja -DCMAKE_BUILD_TYPE=Release -DBUILD_TESTING=ON -DNIMBY_BUILD_LINUX_COMPAT=ON
cmake --build sdk/build/linux-native -j 4
ctest --test-dir sdk/build/linux-native --output-on-failure
bash sdk/gradle-plugin/gradlew -p sdk/gradle-plugin test
bash hub/gradlew -p sdk/kotlin-client test
bash hub/gradlew -p hub desktopTest
bash tco/gradlew -p tco desktopTest
bash hub/gradlew -p sdk/verification/kotlin-mod -PmodProject="$PWD/signalisationfrancaiserealiste" hostTest linkDebugSharedHost
```

`prepareRelease` dans Hub/TCO prépare des fichiers locaux ; ce n'est pas une
publication. Les commandes ci-dessus sont les commandes de reprise, pas des
tests déjà exécutés sur la nouvelle installation Linux.

## Derniers résultats avant suppression de WSL

- SDK natif : 40 tests Windows et 24 tests Linux passent.
- Hub : 44 tests sur chaque OS ; les scénarios Unix ne s'exécutent que sous Linux.
- TCO : 10 tests sur chaque OS ; client SDK Kotlin : 6 ; plugin Gradle : 9.
- Mod Kotlin/Native : 5 groupes de régression sur chaque OS.
- Le TCO lit le jeu Linux réel : 947 trains, 248 409 voies, 25 968 signaux.
- Installation puis retrait des paquets Linux via le Hub validés sur une copie
  du dossier de jeu ; le TCO ainsi installé lit le processus réel.
- Paquets TCO Windows et Linux préparés. L'application Windows embarquée a
  passé `--check-sdk` avec la bibliothèque de test, PID 42. L'installation
  Windows et la mise à niveau depuis une ancienne version restent à valider.
- Après le dernier correctif du Hub sur les fichiers chargés par d'autres
  processus, les 44 tests et `prepareRuntime` ont réussi sous Linux. Le cycle
  complet des paquets n'a pas été rejoué après ce dernier correctif.

## Priorités restantes

1. Qualifier les hooks Linux : interface de réglages, textures, conduite et
   écritures d'horloge. Les adresses Windows ne sont pas réutilisables telles quelles.
2. Raccorder le chargeur de mods au démarrage Linux et produire un vrai kit SDK.
   Un objet compilé ou une lecture réussie par le TCO ne prouve pas que le mod agit.
3. Tester le mod chargé dans le jeu, ses réglages, sa persistance et sa stabilité.
4. Tester le lancement des profils depuis le Hub et compléter la parité du TCO,
   notamment son mécanisme de mise à jour autonome.
5. Valider installation, mise à niveau, retrait et récupération sur chaque OS,
   puis remettre les documentations en cohérence avant toute publication.

Les politiques de versionnement présentes sur GitHub ont été fusionnées avec
ce point de reprise. Les anciens pipelines Woodpecker du mod et du TCO restent
à adapter à Gradle ; leurs commandes CMake historiques ne valident pas la
migration Kotlin. Les commits de fusion portent `[skip ci]` et ne demandent
aucune publication. Les nouveaux numéros de version restent en préparation.

Sous WSL, des lanceurs graphiques et un contournement de lecture de dossiers
étaient nécessaires. Ne pas les installer automatiquement sur Ubuntu natif :
reproduire d'abord le comportement avec Steam et les pilotes Linux normaux.
Le SDK exige l'empreinte du jeu connue ; une mise à jour du jeu nécessite une
nouvelle qualification. Les détails sont dans `sdk/docs/research/linux-runtime.md`.

Le reset des releases/tags GitHub n'a pas été effectué. Le choix alpha/beta
est conservé quand une stable plus récente devient la meilleure mise à jour.

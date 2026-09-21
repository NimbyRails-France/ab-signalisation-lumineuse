# Signalisation francaise realiste

Mod **Kotlin/Native** pour NIMBY Rails, charge par NRF Loader. Version 0.2.0.
Le SDK gere toute l'integration native : aucun fichier C++ n'est requis dans le mod.

Le code est organise par domaine dans `src/main/kotlin/sfr` :

- `settings` : equipement des signaux et cases dans le jeu ;
- `signalling` : indications, observations et regles BAL ;
- `rendering` : textures et clignotements ;
- `driving` : consignes, courbes de freinage et memoires ;
- `FrenchSignalsMod.kt` : declaration du mod et liaison avec l'API Kotlin du SDK.

Pour compiler et lancer les tests :

```text
.\gradlew.bat build
```

Ouvrir `build.gradle.kts` comme projet dans IntelliJ IDEA. Le chemin du SDK
Kotlin se configure avec `NRF_KOTLIN_SDK` ou `nrfSdkDir` dans le fichier
`gradle.properties` utilisateur. Sélectionner un JDK 21 pour Gradle.

La sortie Release est dans `build/gradle/mod/release`, et le ZIP dans
`build/gradle/distributions`. Aucun CMake ou compilateur C++ a installer cote mod.

[Guide IntelliJ et Gradle](docs/gradle-intellij.md) : ouverture, dependance SDK,
compilation et tests. [Organisation du Kotlin](docs/kotlin-development.md).

Les identifiants de catalogue et de reglages sont conserves pour les parties
existantes. Voir la [documentation du plugin](docs/README.md) pour son architecture
et ses reglages.

## Versions, changelog et notifications

- La version de référence est dans `VERSION`. Elle doit correspondre à `CMakeLists.txt` ou à `package.json` et son lockfile, selon le projet.
- Documenter les changements dans `CHANGELOG.md`, sous `[Unreleased]` pendant le développement, puis dans une section `## [X.Y.Z] - AAAA-MM-JJ` au moment de publier.
- Après une CI réussie, créer le tag `vX.Y.Z` sur le commit vérifié et publier sa release GitHub avec les notes de cette section (`python .woodpecker/check-release.py --notes`). Joindre les artefacts construits avec l'outillage habituel lorsqu'ils sont nécessaires.
- Les builds Woodpecker sont annoncés dans le salon Discord `1550478726557470791`. Seules les releases GitHub publiées, versionnées et avec des notes sont annoncées dans `1549088597594873907`. Un push ou un tag seul ne publie aucune annonce de mise à jour.
- La CI refuse les incohérences de versions et les tags sans changelog daté. Les releases en brouillon ne sont pas annoncées. Une correction des notes modifie l'annonce existante.

Woodpecker compile Windows x64 avec MinGW et exécute les tests CTest autonomes sous Wine. Cela ne remplace pas les essais dans le jeu ni la validation native Windows des installateurs et scripts PowerShell.

## Canaux de publication

**Stable** : `vX.Y.Z` (release normale). **Bêta** : `vX.Y.Z-beta.N`. **Alpha** : `vX.Y.Z-alpha.N` (ces deux dernières sont des prereleases GitHub). `N` commence à 1. Le Hub mémorise un canal par projet, stable par défaut, sans basculer vers un autre canal si aucune release n’existe. Un retour vers une version plus ancienne nécessite une installation manuelle.

`VERSION` et le manifeste portent la version complète ; la version CMake garde seulement `X.Y.Z`. Publier le ZIP et son `project.json` dans la **même release**, avec son changelog. Pour le Hub lui-même, publier l’installateur et `hub-latest.json`. Le manifeste donne la taille, le SHA-256, le dossier racine et les règles de compatibilité. Aucun catalogue central ne doit être modifié.

La politique est dans `release-channels.json`. Le contrôle `.woodpecker/check-release.py` refuse les autres canaux. Une release de test n’est jamais marquée comme dernière version stable.

## Publier une mise à jour

- **main** : canal stable.
- **alpha** : canal alpha.
- **beta** : canal beta.

Un commit ordinaire lance les vérifications sans publier. Pour publier, préparez la même version dans `VERSION` et les fichiers de version du projet, puis ajoutez une entrée datée dans `CHANGELOG.md`. Décrivez les nouveautés, améliorations et corrections du point de vue des utilisateurs.

Le titre exact du commit de publication est `release X.Y.Z` (exemple : alpha : `release 0.4.0-alpha.1` ; beta : `release 0.4.0-beta.1`). Poussez ce commit sur la branche du canal choisi. La compilation, les tests et la préparation des téléchargements doivent réussir avant la publication GitHub et son annonce Discord. Une version déjà publiée ne peut pas être remplacée : choisissez un nouveau numéro.

Ne créez pas le tag à la main. Les préversions restent dans leur canal et ne remplacent pas la version stable.

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

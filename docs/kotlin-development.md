# Développer le mod en Kotlin

Le code du mod et ses tests sont en Kotlin/Native. Le SDK fournit le plugin
Gradle, l'adaptateur natif, les exports du loader, les observations du jeu,
le panneau des réglages et la publication des consignes. Aucun fichier C++
n'est nécessaire dans un projet de mod Kotlin.

## Où modifier quoi ?

```text
src/main/kotlin/
  Entry.kt                         création du mod
  sfr/
    FrenchSignalsMod.kt            raccordement à l'API Kotlin du SDK
    settings/
      SignalSettings.kt           équipement et conditions d'un signal
      SignalPanel.kt              cases proposées dans le jeu
    signalling/
      Aspect.kt                   indications et libellés
      DecisionReason.kt           motifs des décisions
      Signal.kt                   observations, décisions et liens typés
      BalRules.kt                 priorités et règles du BAL
    rendering/
      SignalTextures.kt           images et phases de clignotement
    driving/
      DrivingLimits.kt            plafonds français en m/s
      DrivingInstructions.kt      consignes envoyées au SDK
      SignalConstraints.kt        cibles métriques de scénario
      DrivingModel.kt             enveloppe de freinage simplifiée
      DrivingMemory.kt            annonces et restrictions mémorisées
src/test/kotlin/
  BalRulesTests.kt
  NetworkTests.kt
  DrivingTests.kt
  TextureTests.kt
  ReferenceEquivalenceTests.kt
  reference/CppReference.kt        résultats figés de l'ancienne version
  RegressionTests.kt              tests exposés à Gradle
  TestSuite.kt                    lancement des tests
```

Par exemple, une règle manipule `Aspect.S` et `Reason.BlockOccupied`, jamais
un index numérique de texture. `FrenchSignalsMod` convertit les valeurs à la
frontière de l'API. Les réglages sont des `data class` immuables :

```kotlin
val settings = SignalSettings(active = true)
val occupied = SignalObservation(
    block = Occupancy.Occupied,
    fresh = true,
    routeKnown = true
)
val decision = BalRules.evaluate(settings, occupied)
check(decision.aspect == Aspect.S)
```

Imports : `nimby.Occupancy`, `sfr.settings.SignalSettings` et
`sfr.signalling.*`. Pour faire varier une observation, utiliser `copy(...)`.
Les nombres et pointeurs du transport natif appartiennent au SDK.

## Compiler dans IntelliJ ou en terminal

Le projet applique `fr.nimbyrails.mod` version `0.7.3`. Ouvrir `build.gradle.kts`
dans IntelliJ avec un JDK 21, puis lancer `build`. Configurer le chemin du kit
via `NRF_KOTLIN_SDK`, `-PnrfSdkDir` ou le fichier `gradle.properties` utilisateur.
Le chemin personnel ne doit pas être versionné dans le projet.

```text
.\gradlew.bat build
.\gradlew.bat windowsTest
.\gradlew.bat assembleDebugMod
```

[Guide IntelliJ et Gradle](gradle-intellij.md) : premiere ouverture, dependance
SDK, roles des DLL et de la bibliotheque Kotlin, taches et sorties.

Les versions sont epinglees : Kotlin 2.2.20, Gradle 8.14.3. Un JDK 21 est
necessaire pour developper. Il n'est pas necessaire d'installer CMake, MinGW
ou CLion : le SDK contient le pont natif deja compile. Les joueurs n'ont pas
besoin de Java.

Les dossiers produits sont `build/gradle/mod/debug` et
`build/gradle/mod/release`. `packageMod` cree le ZIP dans
`build/gradle/distributions` après les tests, avec le manifeste Hub `project.json`.
Le SDK génère aussi le manifeste du loader depuis `mod.json`. Le mod ne contient
aucun script PowerShell de compilation, de paquet ou d'installation.

Deux DLL du mod sont livrees ensemble : `SignalisationFrancaiseRealisteMod.dll`
(adaptateur precompile du SDK) et `SignalisationFrancaiseRealisteModKotlin.dll`
(code Kotlin compile). Les deux sont necessaires. Le runtime Kotlin reste
charge jusqu'a la fermeture du processus ; apres recompilation, redemarrer
le jeu pour essayer le nouveau binaire.

Les identifiants `sfr.bal-a`, `sfr_bal_a_cpp_v1` et les dix noms des cases sont
conserves pour les sauvegardes existantes.

## Développement et installation

Le Hub ajoute le dossier du projet à son profil développeur et appelle la même
tâche `packageMod` que l'IDE. Il prépare le paquet local et permet de choisir
entre projet local et version publiée. L'installation appartient au Hub.

Les outils ponctuels de relevés et d'analyse peuvent exister localement dans
`tools/`, entièrement ignoré par Git. Le build et le fonctionnement du mod
n'en dépendent pas. Les guides communs de l'API et de l'outillage sont livrés
dans la documentation du SDK ; ce guide décrit l'organisation propre à SFR.

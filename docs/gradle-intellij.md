# Ouvrir et compiler dans IntelliJ IDEA

Le mod utilise le plugin Gradle `fr.nimbyrails.mod` fourni par le SDK Kotlin.
La compilation, les vérifications natives et le paquet sont définis côté SDK.

## Première ouverture

1. Installer un JDK 21 et le sélectionner comme **Gradle JVM**.
2. Extraire le kit SDK Kotlin 0.7.3 pour Windows x64.
3. Configurer `NRF_KOTLIN_SDK`, ou renseigner `nrfSdkDir=C:/SDK/NimbyKotlin`
   dans `%USERPROFILE%/.gradle/gradle.properties`.
4. Ouvrir `build.gradle.kts` comme projet, avec le Wrapper Gradle.
5. Synchroniser, puis lancer `build`.

Le chemin du SDK reste une préférence locale. Le kit doit contenir le plugin
dans `gradle-repository/`, l'API dans `klib/`, le pont et `sdk.json`.
La première compilation télécharge les dépendances Gradle et Kotlin.
Aucun CMake, compilateur C++ ou script PowerShell n'est requis dans le mod.

## Commandes et sorties

```text
gradlew.bat build
gradlew.bat windowsTest
gradlew.bat assembleDebugMod
```

Pour choisir un kit par commande :

```text
gradlew.bat build -PnrfSdkDir=C:/SDK/NimbyKotlin
```

| Tâche | Résultat |
| --- | --- |
| `windowsTest` | Tests Kotlin et rapports dans IntelliJ |
| `assembleDebugMod` | `build/gradle/mod/debug/` |
| `assembleReleaseMod` | `build/gradle/mod/release/` |
| `verifyNativeMod` | Vérification des exports et du cycle de vie sans jeu |
| `packageMod` | ZIP et `project.json` dans `build/gradle/distributions/` |
| `build` | Compilation, tests, vérification et distribution |

Le SDK génère `nrf-mod.ini` à partir de `mod.json`. Le paquet contient les
deux DLL du mod, les ressources et les licences. Le runtime SDK et son testeur
restent hors du paquet du mod.

## Essayer depuis le Hub

Activer le profil développeur, choisir le kit Kotlin et ajouter ce dossier
comme projet local. Le Hub appelle `packageMod` puis prépare l'installation
de développement. Aucun script d'installation n'est à fournir.

Revenir au profil Jouer pour utiliser les versions publiées.
Un nouveau binaire Kotlin exige de redémarrer le jeu. Les sauvegardes et certains
réglages peuvent rester partagés : utiliser une partie d'essai.

Les règles du plugin restent décrites dans le [guide Kotlin](kotlin-development.md).
La documentation générale de l'outillage est fournie avec le SDK.
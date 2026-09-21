# Architecture du plugin

Signalisation française réaliste est un mod Kotlin/Native pour NIMBY Rails,
chargé par NRF Loader et raccordé au jeu par l'API Kotlin du SDK NRF.

## Organisation

- `src/main/kotlin/Entry.kt` crée le mod.
- `sfr/FrenchSignalsMod.kt` déclare le mod et convertit les données entre
  l'API du SDK et les types métier du plugin.
- `sfr/settings` définit les équipements et les cases de réglage des signaux.
- `sfr/signalling` calcule les indications BAL et les motifs des décisions.
- `sfr/rendering` associe les indications aux textures et au clignotement.
- `sfr/driving` définit les consignes françaises, les contraintes, le modèle
  de freinage et les mémoires de conduite.

Ces chemins `sfr/` sont relatifs à `src/main/kotlin/`.

## Responsabilités du SDK et du plugin

Le SDK fournit l'intégration native, les observations du jeu, le panneau des
réglages et la publication des consignes. Le plugin définit les règles BAL,
les indications, les équipements et les limites de conduite françaises.
Les observations absentes, périmées ou invalides sont représentées explicitement
dans les décisions ; une texture seule ne constitue pas une autorisation.

Les identifiants `sfr.bal-a` et `sfr_bal_a_cpp_v1` restent stables pour les
sauvegardes existantes, même si le code du plugin est désormais en Kotlin.

## Compilation et ressources

Gradle compile le code Kotlin, exécute les tests de `src/test/kotlin` et prépare
les paquets. Le plugin est livré avec son adaptateur natif précompilé et sa DLL
Kotlin. `assets/mod.txt`, `imgs/` et `config/` fournissent les ressources.

Le plugin Gradle du SDK fournit toute la logique de compilation et de paquet.
Le Hub assure l'installation et le choix entre projet local et version publiée.
Les [releases multiplateformes](releases.md) séparent version, canal alpha/bêta/
stable et système/architecture ; chaque plateforme possède son manifeste.
Le dossier local `tools/`, ignoré par Git, est réservé aux relevés et analyses
ponctuels ; il ne fait pas partie du projet distribué.

Consulter le [guide Gradle](gradle-intellij.md) et le
[guide Kotlin](kotlin-development.md) pour les commandes et les sorties.

Les essais graphiques Linux sous WSL sont décrits dans le
[relevé Vulkan WSL](wsl-vulkan.md). Le rendu Vulkan sur RTX et l'affichage du menu
du jeu par lancement natif direct y sont vérifiés. Une copie de partie atteint
également la carte après contournement d'un défaut de lecture des textures.
Cela ne valide pas encore le portage Linux complet du SDK ni la stabilité longue.
Le [relevé de migration multiplateforme](migration-multiplateforme.md) précise
les tests effectués et le travail restant pour une distribution complète.

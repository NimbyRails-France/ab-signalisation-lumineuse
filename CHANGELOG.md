# Changelog

## [Unreleased]

## [0.2.0-alpha.7] - 2026-10-09

### Français

- Adopte les noms de référence **AB T_A 011100000**, **AB T_C 101000000** et **AB T_C 111000000** dans le catalogue de construction et les panneaux des signaux.
- Précise les modèles concernés dans les aides des réglages, notamment pour les zones de travaux.
- **Nouvelle partie requise :** les sauvegardes utilisant les anciens modèles de signaux ne sont pas compatibles avec ce nouveau catalogue.

Prérequis : **SDK 0.9.0-alpha.2** (alpha) ou plus récent, inférieur à **0.10.0**.

### English

- Uses the reference names **AB T_A 011100000**, **AB T_C 101000000** and **AB T_C 111000000** in the construction catalogue and signal panels.
- Clarifies which models each setting applies to, including work-zone coverage.
- **A new game is required:** saves using the previous signal models are incompatible with this new catalogue.

Requires **SDK 0.9.0-alpha.2** (alpha) or later, below **0.10.0**.

## [0.2.0-alpha.6] - 2026-10-08

- Nouveau nom : **AB Signalisation lumineuse**, avec le préfixe **AB** dans les noms des signaux. Le mod reste en développement.
- Améliore la fluidité des zones de travaux sur les grands réseaux, notamment lorsque plusieurs zones se chevauchent.
- Optimise l'affichage du Carré simple Avertissement.

Prérequis : **SDK 0.9.0-alpha.1** (alpha) ou plus récent, inférieur à **0.10.0**.

English:

- New name: **AB Signalisation lumineuse**, with the **AB** prefix in signal names. The mod remains in development.
- Improves work-zone performance on large networks, especially when several zones overlap.
- Optimizes the Carré simple Avertissement display.

Requires **SDK 0.9.0-alpha.1** (alpha) or later, below **0.10.0**.

## [0.2.0-alpha.5] - 2026-09-29

- Nécessite le SDK 0.8.0-alpha.8 : le compteur travaux apparaît juste sous sa case d’activation.

- Extrait le cantonnement, les réglages, les vitesses et les zones de travaux dans `signals/common/bal/`. Chaque modèle conserve ses fichiers d'indications, de règles, de panneau, de textures et de conduite.
- Ajoute le Carré BAL type C avec les textures CC SMA, les mêmes réglages que le Sémaphore BAL et une case « Forcer au carré » pour maintenir un arrêt absolu.
- Les zones de travaux traversent les BAL et Carrés BAL ; leur portée et les réglages sauvegardés restent indépendants du forçage au carré.
- Les noms des signaux restent en français, avec leur type, dans toutes les langues. Les nouveaux signaux restent à gauche et en taille 4.
- Tests des règles BAL, du carré forcé, des réseaux mixtes et des textures. Confirmation visuelle en jeu à effectuer.

English:

- Requires SDK 0.8.0-alpha.8 so the works counter appears directly below its enabling checkbox.
- Moves shared BAL rules, settings, speeds and work-zone propagation into `signals/common/bal/`, keeping each model's declarations, indications, panel, textures and driving files separate.
- Adds Carré BAL type C using CC SMA textures, the existing BAL settings and a force-closed option imposing an absolute stop.
- Work zones extend across BAL and Carré BAL signals without overwriting saved settings or bypassing a forced stop.
- Signal names retain their French names and type in every language. New signals default to the left side and size 4.
- Tests cover BAL rules, forced stops, mixed networks and texture mappings. Visual confirmation in the game remains required.

## [0.2.0-alpha.4] - 2026-09-29

- Ajoute « Nombre de cantons travaux suivants » (0 à 64) sous Vert CLI Travaux : le signal source et les N BAL suivants sont couverts, sous réserve des indications plus restrictives.
- Recalcule la couverture sans modifier les réglages enregistrés des signaux suivants. Les zones peuvent se chevaucher ; diminuer le nombre ou décocher Travaux retire la couverture correspondante.
- Les nouveaux signaux BAL et Carré utilisent la taille 4 et le placement à gauche du sens de circulation. Les signaux déjà posés restent inchangés.
- Nécessite le SDK 0.8.0-alpha.7. Tests automatisés des règles et de l'intégration ; confirmation visuelle en jeu encore nécessaire.

English:

- Adds a following work-block count (0-64): the source and the next N BAL signals receive work-zone coverage, while more restrictive indications retain priority.
- Recalculates overlapping work zones without changing saved downstream settings; reducing the count or disabling Works removes the corresponding coverage.
- New BAL and Carre signals default to size 4 and the left side of travel. Existing signals remain unchanged.
- Requires SDK 0.8.0-alpha.7. Automated rules and integration tests pass; visual confirmation in the game remains required.

## [0.2.0-alpha.3] - 2026-09-28

- Améliore l'ouverture du Carré simple Avertissement à l'approche d'un train dans les deux cantons précédents, y compris en simulation accélérée.
- Conserve la fermeture au passage de la tête du train et prépare l'ouverture pour le train suivant.
- Améliore le fonctionnement de la marche à vue après un arrêt au BAL avec le nouveau SDK.
- Traduit les noms et descriptions du mod et de ses signaux en français et en anglais.
- Nécessite le SDK 0.8.0-alpha.3. Signal Placement reste facultatif.

Improves approach detection and restricted movement with SDK 0.8.0-alpha.3. Mod and signal names are available in French and English; Signal Placement remains optional.

## [0.2.0-alpha.2] - 2026-09-28

- Carré simple Avertissement : ouverture pour une tête de train dans les deux cantons en amont, fermeture après passage et nouvelle ouverture pour le train suivant.
- Pour ce modèle, un aval inconnu est supposé libre ; une occupation détectée, une observation périmée ou un arrêt forcé maintiennent la fermeture. Les règles BAL restent distinctes.
- Réglages et bouton Répéter traduits en français et anglais selon le jeu, avec repli français.
- Intégration facultative de Signal Placement ; SFR fonctionne aussi seul.
- Nécessite le SDK 0.8.0-alpha.2. Nouvelle partie requise pour la nouvelle organisation des modèles.

- Identifiant du mod distinct des types : `signalisationfrancaiserealiste`. BAL et Carré regroupent leurs règles, réglages, images et consignes dans leurs dossiers respectifs.
- Composition identique par modèle : enums et décision typée, panneau, réglages, règles, textures, consignes, vitesses et diagnostics. Le SDK prépare le contexte et fournit le voisin ; aucune couche d'adaptation ni enum globale dans SFR.
- Nouvelle partie requise : suppression des migrations, anciennes clés, forçages et du simulateur de conduite de diagnostic. Le moteur SDK continue d'exécuter les consignes des modèles ; le calcul `plan` spécifique à SFR devient indisponible.
- BAL limité à quatre réglages ; intégration optionnelle dans `sfr/integrations/SignalPlacement.kt`. Anciens tests de simulation parallèle et dossiers vides retirés.
- Action de répétition facultative, fournie par Signal Placement. Le mod reste autonome ; vérification réelle du nouveau panneau en jeu à effectuer.
- Déclaration du mod simplifiée avec l'API Kotlin `signalMod` ; conversions numériques prises en charge par le SDK.
- Carré simple Avertissement distinct du BAL, fermé au repos et ouvert sur approche confirmée avec canton libre.
- Quatre cases BAL : Vert CLI Cantonnement, Vert CLI Travaux, Jaune CLI et Rouge CLI. Calcul automatique ; aucun import des anciens profils. Un aval absent reste indéterminé.
- Wiki réservé à l'apprentissage générique du SDK, sans guide interne SFR ni projet SFR présenté comme exemple à télécharger. Organisation du produit documentée dans son README.
- Tests locaux Windows des règles, textures et DLL. La recette complète en jeu reste à effectuer.

## [0.2.0-alpha.1] - 2026-09-27

### Windows alpha
- Mod SFR écrit en Kotlin/Native avec le kit SDK 0.8.0-alpha.1.
- Règles BAL et vitesses définies dans le mod ; commande et observation génériques fournies par le SDK.
- Corrections des annonces, réouvertures et restrictions conservées jusqu'au franchissement approprié.
- Journaux de chargement, réglages, anomalies et erreurs Kotlin avec diagnostic détaillé.
- Nécessite le SDK 0.8.0-alpha.1 et un jeu reconnu par le SDK. Recette complète en jeu à effectuer.
- Distribution Windows uniquement ; Linux suspendu.

## [0.1.0] - 2026-09-18

### Première version expérimentale
- Mise à disposition des premières ressources de signalisation française.
- Installation et chargement du mod depuis le Hub.

### À savoir
- Utilisez au minimum le Hub 0.2.3 et le SDK 0.7.2, puis activez les ressources dans le jeu.
- La signalisation française automatique n’est pas encore disponible : cette version ne calcule pas les indications et ne change pas seule l’apparence des signaux.
- La validation de l’affichage en jeu reste à réaliser.
Ce point de reprise n'est pas une publication.

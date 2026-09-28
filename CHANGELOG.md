# Changelog

## [Unreleased]

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

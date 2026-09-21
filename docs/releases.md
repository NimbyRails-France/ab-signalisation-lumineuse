# Releases Kotlin multiplateformes

Une release est identifiée par le dépôt, la version, le canal et la plateforme.
Le canal est porté par la version : `1.0.0-alpha.1`, `1.0.0-beta.1`, puis `1.0.0`
pour stable. Le tag GitHub est `v` suivi de cette version. Les préversions doivent
également être marquées comme préreleases dans GitHub.

Une même release peut contenir plusieurs plateformes : `windows-x64`,
`linux-x64`, `linux-arm64`, `macos-x64`, `macos-arm64`. Cette liste décrit le
format des manifestes, pas la disponibilité du SDK natif pour chacune d’elles.
Chaque paquet est construit et testé sur son système cible.

## Paquets et manifestes

Exemple de nom : `SignalisationFrancaiseRealiste-1.0.0-beta.2-linux-x64.zip`.
Le suffixe de plateforme empêche les collisions entre les fichiers d’une release.

Chaque plateforme possède son manifeste `project-<plateforme>.json` ; pour
l’auto-mise à jour du Hub, il s’agit de `hub-latest-<plateforme>.json`. Le contenu
déclare version, canal, plateforme, URL exacte, taille et SHA-256. Les projets
déclarent aussi leurs versions compatibles du SDK et les empreintes des jeux.

Le Hub choisit la version la plus récente **compatible avec la plateforme et
le canal sélectionnés**. Stable reçoit uniquement les stables ; alpha et bêta
reçoivent leur propre canal ainsi que les stables. Ainsi `2.0.0` remplace
`2.0.0-alpha.9` ou `2.0.0-beta.3`, mais ne remplace pas `2.1.0-beta.1`.
La préférence alpha/bêta est conservée pour les prochaines préversions.
Une release sans paquet compatible est ignorée. Les anciens manifestes non
suffixés sont acceptés uniquement pour Windows x64.

## Préparer localement

Pour le mod, `packageMod` produit le ZIP vérifié, `project.json` pour l’import
local et `project-<plateforme>.json`. La propriété Gradle `releaseBaseUrl` permet
de remplacer l’URL locale par l’adresse exacte de la release du dépôt et de la
version du mod. La génération des fichiers ne publie rien sur GitHub.

Pour le Hub, `prepareRelease` exécute les tests et crée les installateurs,
leurs empreintes et le manifeste dans `build/.../release/<version>/<plateforme>`.
Les installateurs natifs ont leurs propres contraintes de numérotation : une
prérelease exige une propriété `nativePackageVersion=X.Y.Z` explicite, unique
et croissante. La version complète alpha/bêta reste l’identité de la release
et celle utilisée pour les comparaisons du Hub.

La migration des fonctions natives Linux et la préparation des releases du SDK
et du TCO doivent être terminées avant de distribuer un ensemble complet.
Le [relevé de migration](migration-multiplateforme.md) distingue les validations
déjà réalisées des fonctions encore en cours de portage.

Le mode développeur conserve la protection des projets locaux, quel que soit
le canal de publication sélectionné.

L'[inventaire de réinitialisation GitHub](github-release-reset.md) précise les
anciennes releases concernées et distingue leur suppression du démarrage
d'une nouvelle série de versions.

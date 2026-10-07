# AB Signalisation lumineuse

Mod Kotlin/Native pour NIMBY Rails sous Windows. Le SDK gère l'intégration
native ; le mod garde ses règles, vitesses, réglages et textures.
Le [wiki officiel du SDK](https://wiki.nimbyrails-france.fr) enseigne la création
de votre propre projet avec des exemples indépendants. AB Signalisation lumineuse est un produit,
pas un projet exemple à télécharger pour apprendre le SDK.

## Mod, modèles et signaux posés

Le **mod** est le paquet `signalisationfrancaiserealiste`. Il contient les
**modèles** `sfr.bal-a`, `sfr.carre-simple-avertissement` et `sfr.carre-bal-c`. Un **signal posé**
est une instance de l'un de ces modèles, avec son ID et ses propres réglages.
AB Signalisation lumineuse fonctionne sans BA Signal Placement : le bouton de répétition apparaît
seulement si son fournisseur est chargé et observe la même partie.
L'intégration du nouveau panneau reste à qualifier en jeu.

## Organisation et responsabilités

### Zone de travaux et construction

Dans un BAL ou Carré BAL, cocher **Vert CLI Travaux** fait apparaître **Nombre de cantons travaux suivants** (0 à 64).
Avec 2, le BAL sélectionné et les deux BAL ou Carrés BAL suivants sont couverts. Avec 0, seul le BAL sélectionné l'est.
Un arrêt, une occupation ou une annonce plus restrictive restent prioritaires. La propagation suit les liens aval connus et s'arrête hors des modèles BAL et Carré BAL, sur une liaison manquante ou sur une observation indisponible.
Les réglages des BAL suivants ne sont pas réécrits : décocher la source ou réduire le nombre recalcule immédiatement sa couverture. Les zones qui se chevauchent se combinent.

Les nouveaux BAL et Carré sont déclarés en taille **4**, à **gauche dans le sens de circulation**, avec le SDK 0.8.0-alpha.8.
Les signaux existants conservent leurs propriétés ; Répéter conserve celles du signal copié.

### Carré BAL type C

Le Carré BAL reprend les quatre réglages du Sémaphore BAL et le compteur de cantons travaux suivants.
**Forcer au carré** maintient un arrêt absolu jusqu’à décocher la case ; les travaux et le rouge clignotant ne peuvent pas le libérer.
Sans forçage, le cantonnement et les consignes sont ceux du BAL, y compris le sémaphore pour un canton occupé.
Le modèle utilise `imgs/cc/cc_sma`, avec son propre catalogue, ajouté après les modèles existants.
Les noms des signaux sont toujours en français et comportent leur type ; aides et réglages restent traduits.

### Sources

L'identité vient de `mod.json` via `nimby.mod.modInfo`. `FrenchSignalsMod`
déclare l'auteur et la description ; chaque modèle déclare `construction` avec
le catalogue ordonné de son fichier `Textures`. Le plugin génère `mod.txt` au
build. Ne pas réordonner les images d'un catalogue déjà utilisé dans une partie.
Les descriptions des cases s'affichent sous leur libellé dans le panneau du jeu.

```text
src/main/kotlin/
  Entry.kt                       Point d'entrée attendu par le SDK
  sfr/
    FrenchSignalsMod.kt          Identité du paquet et liste des modèles
    integrations/
      SignalPlacement.kt         Action facultative commune aux modèles
    signals/
      BalNeighbours.kt          Interprétation des voisins entre modèles
      common/
        bal/
          BalAspect.kt          Indications du calcul partagé
          BalReason.kt          Motifs du calcul partagé
          BalDecision.kt        Résultat typé du calcul partagé
          BalRules.kt           Calcul du cantonnement commun
          BalSettings.kt        Format des quatre réglages BAL
          BalPanel.kt           Options et compteur travaux communs
          BalDriving.kt         Consignes communes de conduite
          BalSpeeds.kt          Vitesses BAL en m/s
          BalWorkZone.kt        Propagation des zones de travaux
      bal/
        BalSignals.kt            Déclaration et assemblage des callbacks BAL
        BalAspect.kt             Indications BAL uniquement
        BalReason.kt             Motifs BAL uniquement
        BalDecision.kt           Association typée des deux enums
        BalRules.kt              Utilisation des règles communes par le Sémaphore
        BalPanel.kt              Les quatre cases visibles
        BalSettings.kt           Lecture des réglages
        BalTextures.kt           SVG et phases de clignotement
        BalDriving.kt            Consignes transmises au SDK
        BalDiagnostics.kt        Classement des défauts
      carrebal/
        CarreBal.kt              Déclaration et assemblage du Carré BAL
        CarreBalAspect.kt        Indications propres, dont le carré fermé
        CarreBalReason.kt        Motifs propres au modèle
        CarreBalDecision.kt      Association typée des deux enums
        CarreBalRules.kt         Fermeture au carré puis règles BAL communes
        CarreBalSettings.kt      Réglages BAL et forçage au carré
        CarreBalPanel.kt         Options communes et case Forcer au carré
        CarreBalTextures.kt      SVG CC SMA et clignotements
        CarreBalDriving.kt       Arrêt absolu ou consignes BAL communes
        CarreBalDiagnostics.kt   Classement des défauts
      carreavertissement/
        CarreAvertissement.kt     Déclaration et assemblage des callbacks Carré
        CarreAspect.kt           Indications Carré uniquement
        CarreReason.kt           Motifs Carré uniquement
        CarreDecision.kt         Association typée des deux enums
        CarreRules.kt            Conditions de fermeture et d'ouverture
        CarrePanel.kt            Sa case et son libellé
        CarreSettings.kt         Lecture de ses réglages
        CarreTextures.kt         Ses SVG
        CarreDriving.kt          Ses consignes
        CarreSpeeds.kt           Sa vitesse de passage après réouverture
        CarreDiagnostics.kt      Ses défauts à journaliser
src/test/kotlin/                  Tests des règles actuelles et de leur intégration SDK
```

Chaque modèle possède ses enums : les fonctions du BAL ne peuvent pas recevoir
un `CarreAspect`, et inversement. Les noms et ordinaux peuvent se répéter d'un
modèle à l'autre sans collision.

Le SDK fournit le même contexte à tous les modèles : observations, réglages,
statut de disponibilité et voisin aval résolu. Un profil absent utilise les
valeurs déclarées ; un profil indisponible rend l’observation non fraîche.
Il n’y a aucun adaptateur d’observation ni fichier Neighbours à écrire par modèle.

`next` fournit l’ID, le type, la décision et la consigne de conduite du voisin.
`next.of(monModele)` donne aussi accès à ses enums typées si nécessaire.
Le BAL et le Carré BAL partagent le calcul du cantonnement dans `common/bal/BalRules.kt`.
Ce dossier commun ne dépend d'aucun modèle concret. `BalNeighbours` interprète les
indications des deux modèles et les consignes de conduite des autres modèles.
`FrenchSignalsMod` fournit à la propagation des travaux les modèles qui y participent.
Le SDK ne déduit ni un feu ni une vitesse de cette lecture. Une consigne non
prise en charge reste inconnue. Le voisin est celui du lien aval dans le réseau
résolu du mod, pas une recherche géométrique de tous les signaux alentour.

Le Carré ouvert à l'avertissement transmet la même obligation de conduite que
l'avertissement BAL : annoncer un arrêt au signal suivant, suivre sa consigne
visible et conserver une approche déjà reçue jusqu'au passage. Les 30 km/h dans
`CarreSpeeds` concernent le passage après une réouverture autorisée ; ils ne
limitent pas toute l'approche à une vitesse fixe. Fermé, ce Carré impose l'arrêt
absolu. La fin de BAL est un usage possible, pas la définition de ce modèle.

Ce Carré recherche une tête de train dans les **deux cantons en amont**
(`observeApproach = true`, `approachBlocks = 2`). Il s’ouvre à l’avertissement,
puis se referme dès que cette tête l’a franchi ; une queue encore en amont ne
prouve pas une nouvelle approche. Un autre train en approche peut le rouvrir.
Pour ce modèle seulement, l’absence de signal aval ou une occupation aval
inconnue est supposée libre. Une occupation réellement détectée reste bloquante,
comme une observation périmée, une panne ou un arrêt forcé. Cette règle appartient
au mod ; le SDK conserve ses observations brutes et le BAL ne reprend pas cette
tolérance.

Le parcours est : contexte SDK → règle du modèle → décision → image et consigne.
Le SDK exécute la conduite en jeu. Le mod ne contient plus de simulateur de freinage
parallèle, de migration ou de forçage destiné au banc. Les appels de calcul
`plan` renvoient désormais « indisponible » ; les consignes de `…Driving.kt`
restent actives.

| Modification | Fichier du modèle |
| --- | --- |
| Ajouter un état ou un motif | `…Aspect.kt`, `…Reason.kt` |
| Changer les conditions d'un feu | `…Rules.kt` |
| Interpréter la consigne du voisin | `…Rules.kt`, via le contexte SDK |
| Ajouter une case | `…Panel.kt`, puis `…Settings.kt` |
| Changer l'image ou le clignotement | `…Textures.kt` |
| Changer une consigne ou une vitesse | `…Driving.kt`, `…Speeds.kt` |
| Classer une anomalie | `…Diagnostics.kt` |

Pour ajouter un modèle : créer son dossier et ses enums, déclarer son
`signalModel` avec ses replis et callbacks, puis ajouter `signal(Nouveau.model)`
dans `FrenchSignalsMod`. Tester ses règles et ses interactions avec les voisins.
Ses cases et ses images restent locales au modèle. Les ressources sont séparées
dans `imgs/ca/sem_bal` et `imgs/cc/cs_a` ; le jeu conserve un seul catalogue
`assets/mod.txt` par paquet.

## Nouvelle base et développement

Cette version vise une nouvelle partie et est incompatible avec les anciens
profils SFR. Aucune migration ni lecture des anciennes clés de réglage n’est
conservée. Seules les quatre cases BAL actuelles sont reconnues.


L’application de banc a été retirée. Aucun état forcé n’est autorisé par ce mod.
Les règles se testent automatiquement avec des observations simulées, puis
se valident en conditions réelles dans une nouvelle partie.

Ouvrir `settings.gradle.kts` dans IntelliJ avec un JDK 21. Configurer `nrfSdkDir`
vers un kit Kotlin construit depuis cette branche du SDK, puis recharger Gradle.

```powershell
.\gradlew.bat windowsTest verifyNativeMod
.\gradlew.bat assembleReleaseMod
```

Les fichiers produits sont dans `build/gradle/mod/release`. Ces commandes ne
lancent pas le jeu, n'installent pas le mod et ne publient rien.
Les anciens prototypes restent dans l'historique Git. Le VPS est réservé à la
production ; les tests automatisés sont locaux et les essais en jeu restent
une validation distincte.

Publication GitHub pendant une indisponibilité du VPS : commit de release avec
le trailer `Release-Runner: github`. Les binaires Windows sont construits et
testés par GitHub Actions, puis le catalogue public du Hub est actualisé.
Le SDK épinglé doit être publié avant de lancer cette release.

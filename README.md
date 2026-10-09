# AB Signalisation lumineuse

Mod Kotlin/Native pour NIMBY Rails sous Windows. Le SDK gère l'intégration
native ; le mod garde ses règles, vitesses, réglages et textures.
La version **0.2.0-alpha.7** nécessite le SDK **0.9.0-alpha.2** ou plus récent,
avec une version inférieure à **0.10.0**.
Le [wiki officiel du SDK](https://wiki-dev.nimbyrails-france.fr) enseigne la création
de votre propre projet avec des exemples indépendants. AB Signalisation lumineuse est un produit,
pas un projet exemple à télécharger pour apprendre le SDK.

## Mod, modèles et signaux posés

Le **mod** est le paquet `signalisationfrancaiserealiste`. Il contient les
**modèles** `sfr.t_a.s.c.v.011100000`, `sfr.t_c.c.b.v.101000000` et
`sfr.t_c.c.b.v.111000000`. Un **signal posé**
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

Les BAL et Carré sont déclarés en taille **4**, à **gauche dans le sens de circulation**.
Répéter conserve les propriétés du signal copié.

### Carré BAL type C

Le Carré BAL reprend les quatre réglages du Sémaphore BAL et le compteur de cantons travaux suivants.
**Forcer au carré** maintient un arrêt absolu jusqu’à décocher la case ; les travaux et le rouge clignotant ne peuvent pas le libérer.
Sans forçage, le cantonnement et les consignes sont ceux du BAL, y compris le sémaphore pour un canton occupé.
Le modèle utilise `imgs/t_c/c/b/v/111000000`, avec son propre catalogue.
Les noms affichés sont `AB T_A 011100000`, `AB T_C 101000000` et
`AB T_C 111000000`. Les aides et réglages restent traduits.

### Sources

L'identité vient de `mod.json` via `nimby.mod.modInfo`. `FrenchSignalsMod`
déclare l'auteur et la description ; chaque modèle déclare `construction` avec
le catalogue ordonné de son fichier `Textures.kt`. Le plugin génère `mod.txt` au
build. Ne pas réordonner les images d'un catalogue déjà utilisé dans une partie.
Les descriptions des cases s'affichent sous leur libellé dans le panneau du jeu.

L'arborescence sous `sfr/signals` reprend exactement le classement des textures
sous `imgs` : type, signal, protection, cantonnement, puis code sur neuf positions.
Chaque déclaration s'appelle `Signal` dans son propre package. Les fichiers
portent seulement leur rôle : `Signal.kt`, `Aspect.kt`, `Rules.kt`, `Textures.kt`,
etc. Le code est déjà présent dans le dossier. Les règles réutilisées restent
séparées dans `common/bal`.
Le terme BAL y nomme la règle partagée. Les noms visibles dans le jeu sont ceux
des déclarations de construction et des traductions ; ils ne sont pas déduits
des noms de fichiers Kotlin.

```text
src/main/kotlin/
  Entry.kt                       Point d'entrée attendu par le SDK
  sfr/
    FrenchSignalsMod.kt          Identité du paquet et liste des modèles
    integrations/
      SignalPlacement.kt         Action facultative commune aux modèles
    signals/
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
          BalNeighbours.kt      Interprétation des voisins entre modèles
      t_a/s/c/v/011100000/
        Signal.kt               Déclaration du Sémaphore BAL type A
        Aspect.kt               Indications propres au modèle
        Reason.kt               Motifs propres au modèle
        Decision.kt             Association typée des deux enums
        Rules.kt                Utilisation des règles BAL communes
        Panel.kt                Les quatre cases visibles
        Settings.kt             Lecture des réglages
        Textures.kt             SVG et phases de clignotement
        Driving.kt              Consignes transmises au SDK
        Diagnostics.kt          Classement des défauts
      t_c/c/b/v/101000000/
        Signal.kt               Déclaration du Carré simple Avertissement
        Aspect.kt               Indications propres au modèle
        Reason.kt               Motifs propres au modèle
        Decision.kt             Association typée des deux enums
        Rules.kt                Conditions de fermeture et d'ouverture
        Panel.kt                Sa case et son libellé
        Settings.kt             Lecture de ses réglages
        Textures.kt             Ses SVG
        Driving.kt              Ses consignes
        Speeds.kt               Vitesse de passage après réouverture
        Diagnostics.kt          Défauts à journaliser
      t_c/c/b/v/111000000/
        Signal.kt               Déclaration du Carré BAL type C
        Aspect.kt               Indications propres, dont le carré fermé
        Reason.kt               Motifs propres au modèle
        Decision.kt             Association typée des deux enums
        Rules.kt                Fermeture au carré puis règles BAL communes
        Settings.kt             Réglages BAL et forçage au carré
        Panel.kt                Options communes et case Forcer au carré
        Textures.kt             SVG CC SMA et clignotements
        Driving.kt              Arrêt absolu ou consignes BAL communes
        Diagnostics.kt          Classement des défauts
src/test/kotlin/                  Tests des règles actuelles et de leur intégration SDK
```

Le dossier réel est nommé `011100000`, sans caractère supplémentaire. Kotlin
exige des accents graves autour du segment numérique dans les packages et imports :

```kotlin
package sfr.signals.t_a.s.c.v.`011100000`
```

Un fichier qui utilise plusieurs modèles les distingue par des alias d'import,
par exemple dans `FrenchSignalsMod` :

```kotlin
import sfr.signals.t_a.s.c.v.`011100000`.Signal as T_A_011100000
import sfr.signals.t_c.c.b.v.`101000000`.Signal as T_C_101000000
import sfr.signals.t_c.c.b.v.`111000000`.Signal as T_C_111000000
```

Ces alias n'ajoutent aucun suffixe aux fichiers ou aux classes déclarées. Chaque
modèle possède ses propres enums `Aspect` et `Reason` : une fonction qui attend
l'`Aspect` du package `011100000` ne peut pas recevoir celui du package `111000000`.
Les noms et ordinaux peuvent se répéter d'un modèle à l'autre sans collision.

Le SDK fournit le même contexte à tous les modèles : observations, réglages,
statut de disponibilité et voisin aval résolu. Un profil absent utilise les
valeurs déclarées ; un profil indisponible rend l’observation non fraîche.
Il n’y a aucun adaptateur d’observation ni fichier Neighbours à écrire par modèle.

`next` fournit l’ID, le type, la décision et la consigne de conduite du voisin.
`next.of(monModele)` donne aussi accès à ses enums typées si nécessaire.
Le BAL et le Carré BAL partagent le calcul du cantonnement dans `common/bal/BalRules.kt`.
Ce calcul utilise les types communs `BalAspect`, `BalReason` et `BalDecision`.
Dans le même dossier, `BalNeighbours` raccorde les modèles concrets : il interprète
leurs indications et les consignes de conduite des autres modèles.
`FrenchSignalsMod` fournit à la propagation des travaux les modèles qui y participent.
Le SDK ne déduit ni un feu ni une vitesse de cette lecture. Une consigne non
prise en charge reste inconnue. Le voisin est celui du lien aval dans le réseau
résolu du mod, pas une recherche géométrique de tous les signaux alentour.

Le Carré ouvert à l'avertissement transmet la même obligation de conduite que
l'avertissement BAL : annoncer un arrêt au signal suivant, suivre sa consigne
visible et conserver une approche déjà reçue jusqu'au passage. Les 30 km/h dans
`t_c/c/b/v/101000000/Speeds.kt` concernent le passage après une réouverture autorisée ; ils ne
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
`plan` renvoient désormais « indisponible » ; les consignes de `Driving.kt`
restent actives.

| Modification | Fichier du modèle |
| --- | --- |
| Ajouter un état ou un motif | `Aspect.kt`, `Reason.kt` |
| Changer les conditions d'un feu | `Rules.kt` |
| Interpréter la consigne du voisin | `Rules.kt`, via le contexte SDK |
| Ajouter une case | `Panel.kt`, puis `Settings.kt` |
| Changer l'image ou le clignotement | `Textures.kt` |
| Changer une consigne ou une vitesse | `Driving.kt`, `Speeds.kt` |
| Classer une anomalie | `Diagnostics.kt` |

Pour ajouter un modèle : reprendre le chemin de ses textures sous `sfr/signals`,
créer `Signal.kt` et ses types par rôle, déclarer son `signalModel` avec ses replis
et callbacks, puis enregistrer sa propriété `model` avec `signal(...)` dans
`FrenchSignalsMod`. Tester ses règles et ses interactions avec les voisins.
Ses cases et ses images restent locales au modèle. Les ressources sont séparées
selon la nomenclature ci-dessous ; le plugin génère un seul `mod.txt` par paquet,
contenant les trois catalogues de textures.

### Nomenclature des images

Les dossiers suivent le tableau des signaux AB et son aperçu HTML :

```text
imgs/<type>/<signal>/<protection>/<cantonnement>/<code sur 9 positions>/<image>
```

Le type désigne la famille graphique (`t_a`, `t_c`, `t_f`, etc.). Le signal vaut
`c`, `s`, `a`, `cv`, `d`, `gal` ou `tlc` selon la ligne du tableau.
La protection utilise `a` pour la protection carré violet, `b` pour la protection
carré, `c` pour le cantonnement et `d` pour un signal permanent. Le cantonnement
utilise `v` pour tous, `w` pour BM, `x` pour BAPR, `y` pour BAL et `z` pour non concerné.

Le code reste du **texte sur neuf positions**, sans préfixe `0b` : ses zéros
initiaux font partie du chemin. De gauche à droite, les positions du tableau sont :

| Position | Désignation du tableau |
| --- | --- |
| 1 | Carré ou disque |
| 2 | Voie libre |
| 3 | Avertissement |
| 4 | Sémaphore |
| 5 | Carré violet |
| 6 | Blanc |
| 7 | Ralentissement |
| 8 | Rappel de ralentissement |
| 9 | Croix de Saint-André |

Le **chemin complet** identifie une ressource : le même code peut exister dans
plusieurs familles ou protections. Ce code décrit le classement du tableau,
pas les règles de conduite. Par exemple, le Carré BAL `111000000` possède aussi
des images de sémaphore : ses indications restent définies explicitement dans
`t_c/c/b/v/111000000/Textures.kt` et ses règles. Le tableau comporte également deux lignes
`t_c/c/b/v/111000000` ; elles ne créent pas deux dossiers ni deux modèles.

| Nom affiché | Règle du modèle | Dossier des images |
| --- | --- | --- |
| AB T_A 011100000 | Sémaphore BAL type A | `imgs/t_a/s/c/v/011100000/` |
| AB T_C 101000000 | Carré simple Avertissement | `imgs/t_c/c/b/v/101000000/` |
| AB T_C 111000000 | Carré BAL type C | `imgs/t_c/c/b/v/111000000/` |

Les autres dossiers restent des ressources disponibles ; leur présence n'ajoute
pas de modèle ni de règle de signalisation. Les noms de fichiers existants sont
conservés (`texNN.svg`, `xx.svg`, ou `TNN.png` pour les familles concernées).

Les identifiants de modèle (`TYPE`) et de catalogue (`TEXTURES`) reprennent
le chemin complet de la référence, avec des points ou des traits de soulignement :

| Modèle (`TYPE`) | Catalogue (`TEXTURES`) | Nombre d'images |
| --- | --- | --- |
| `sfr.t_a.s.c.v.011100000` | `sfr_t_a_s_c_v_011100000` | 11 |
| `sfr.t_c.c.b.v.101000000` | `sfr_t_c_c_b_v_101000000` | 4 |
| `sfr.t_c.c.b.v.111000000` | `sfr_t_c_c_b_v_111000000` | 12 |

Les trois catalogues déclarent **27 images au total**. Celui de `011100000`
contient `tex00.svg`, `tex02.svg` à `tex10.svg`, puis `xx.svg` : `tex01.svg`
n'existe pas dans cette référence et aucun emplacement ne lui est réservé.
Celui de `101000000` déclare `tex00.svg`, `tex01.svg`, `tex03.svg`, puis `xx.svg`.
Celui de `111000000` déclare `tex00.svg` à `tex10.svg`, puis `xx.svg`.
Chaque liste est explicite dans son `Textures.kt` ; les dossiers ne sont pas
parcourus ou triés pour reconstruire automatiquement les catalogues.

## Nouvelle base et développement

Cette version constitue une **nouvelle base pour une nouvelle partie**. Ses
identifiants binaires de modèle et de catalogue remplacent les anciennes
identités SFR ; les anciens catalogues et leurs indices ne sont pas réutilisés.
Les anciennes parties et les anciens profils SFR ne sont pas compatibles.
Aucune migration ni lecture des anciennes clés de réglage n’est conservée.
Seules les quatre cases BAL actuelles sont reconnues.


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

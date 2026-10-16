# AB Signalisation lumineuse

Mod Kotlin/Native pour NIMBY Rails sous Windows. Le SDK gère l'intégration
native ; le mod garde ses règles, vitesses, réglages et textures.
La version locale **0.2.0-alpha.8**, non publiée, nécessite le SDK **0.9.0-alpha.4** ou plus récent,
avec une version inférieure à **0.10.0**.
Le [wiki officiel du SDK](https://wiki-dev.nimbyrails-france.fr) enseigne la création
de votre propre projet avec des exemples indépendants. AB Signalisation lumineuse est un produit,
pas un projet exemple à télécharger pour apprendre le SDK.

Pour tester la branche `master`, suivre [le protocole de test](TESTING.md) :
le kit Kotlin et le SDK d'exécution doivent provenir du même commit SDK épinglé.

## Mod, modèles et signaux posés

Le **mod** est le paquet `signalisationfrancaiserealiste`. Il contient les
**modèles** `sfr.t_a.s.c.v.011100000`, `sfr.t_c.c.b.v.101000000`,
`sfr.t_c.c.b.v.111000000` et `sfr.t_c.c.b.v.111001000`, ainsi que les deux
marqueurs passifs `sfr.uti.sidm.b.z.200000000.t32` et `.t33`. Un **signal posé**
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

### Carrés BAL type C

Les modèles `111000000` et `111001000` partagent le calcul BAL, les quatre
réglages BAL et le compteur de cantons travaux suivants. En fonctionnement
**Carré**, les annonces A, A clignotant, VL et VL clignotant restent celles du
BAL, mais C remplace S et S clignotant. Le signal reste au carré au repos et
peut s’ouvrir dès qu’une tête est confirmée dans l’un des **deux cantons en
amont**, si le canton protégé est délimité et libre. Le passage de la tête et
la disparition de l’approche referment le carré ; une queue encore présente
interdit la réouverture.

Sur **AB T_C 111000000**, le fonctionnement par défaut est **Carré**. La case
**Retour au sémaphore** est décochée par défaut :

- **Décochée : Carré**, avec le comportement décrit ci-dessus.
- **Cochée : Sémaphore**, avec le BAL habituel, y compris S ou S clignotant
  lorsque le canton connu est occupé.

**Forcer au carré** apparaît uniquement quand **Retour au sémaphore** est coché.
Cette option impose alors l’arrêt absolu, même avec une approche, les travaux
ou le rouge clignotant. En revenant au fonctionnement Carré, elle est masquée
et sa valeur mémorisée est ignorée : elle ne bloque pas une ouverture à
l’approche. Revenir au Sémaphore retrouve cette préférence tant qu’elle n’a
pas été décochée. Le choix utilise la nouvelle clé `returnToSemaphore` ;
l’ancienne clé `automaticOpening` est ignorée sans inversion ni migration.

**AB T_C 111001000** fonctionne **toujours en Carré**. Il ne propose ni
**Retour au sémaphore** ni **Forcer au carré**. Ces anciennes valeurs, même
présentes dans des réglages fournis au modèle, ne changent pas son régime.
Un arrêt externe observé reste prioritaire dans tous les cas.

Pour les deux modèles, un conflit croisé détecté, des données périmées, une
limite de canton inconnue ou une occupation inconnue imposent C avec leur
motif de diagnostic. Un voisin non résolu impose aussi ce repli lorsqu’une
annonce aval est nécessaire. Une panne de lampe garde l’indication
indéterminée et l’arrêt. Les réservations du jeu restent actives.

Chaque modèle possède son catalogue : `imgs/t_c/c/b/v/111000000` ou
`imgs/t_c/c/b/v/111001000`. Les noms affichés sont `AB T_A 011100000`,
`AB T_C 101000000`, `AB T_C 111000000` et `AB T_C 111001000`.
Les aides et réglages restent traduits.

### Manœuvres du modèle 111001000

`111001000` demande aussi le parcours que le jeu analyse pour le train en
approche. Ce parcours observé n'est ni une preuve de propriété de réservation,
ni une autorisation de mouvement : les réservations et refus du jeu restent actifs.

- Si le parcours atteint un autre **signal de protection**, le panneau conserve
  son fonctionnement BAL normal, même si T32 ou T33 apparaît sur le parcours.
- S'il se termine sur une **borne native qualifiée**, est entièrement observé
  et libre, **AB UTIL T32** demande **M, blanc fixe** et **AB UTIL T33** demande
  **M cli, blanc clignotant**.
- Une occupation positive, un conflit croisé, un arrêt externe, une panne,
  une approche disparue ou des données périmées empêchent cette ouverture.
  T32 et T33 présents ensemble imposent C avec un diagnostic de marqueurs incompatibles.

La borne positive utilisée par le SDK est l'événement natif inconditionnel
**BALISE3**. Un simple marqueur, l'absence de signal aval dans la liste du mod,
une distance atteinte ou une capture interrompue ne prouvent jamais cette fin.
Le modèle reconnaît uniquement les catalogues propres de T32 et T33, état 0,
copiés sur ce parcours pour le même train. Il ne cherche pas des images ou des
balises proches sur d'autres voies. L'occupation de cette étendue doit être
connue ; un préfixe occupé suffit à refuser, jamais à déclarer le reste libre.

Si cette nouvelle observation est inconnue, le calcul BAL existant reste
applicable uniquement avec ses propres données qualifiées. Sans borne ni
canton libre connu, le panneau reste au C ; aucune absence ne devient un blanc.
L'absence de signal de protection peut donc être traitée par l'étendue native
qualifiée, sans inventer un canton BAL aval.

Les images sont explicites : `tex12.svg` pour M, puis `tex13.svg` et
`tex14.svg` pour les phases allumée et éteinte de M cli, chacune durant 500 ms.

**M et M cli autorisent tous deux le franchissement à 30 km/h maximum**, sous
réserve de l'autorisation native du jeu. Le plafond s'applique dès ce panneau et
reste retenu jusqu'au franchissement d'un signal donnant voie libre, puis au
dégagement de ce signal par la queue du train. L'absence de signal suivant ne
libère pas la limite. Cette consigne ne remplace ni une réservation manquante ni
un refus du jeu par une permission de marche à vue. Les tests locaux vérifient
les règles sur des observations synthétiques ; l'ensemble reste à qualifier en
jeu et n'est pas publié.

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
          BalDiagnostics.kt     Défauts BAL selon leur motif, même avec un feu S
          BalSpeeds.kt          Vitesses BAL en m/s
          BalWorkZone.kt        Propagation des zones de travaux
          BalNeighbours.kt      Interprétation des voisins entre modèles
          CarreBalSettings.kt   Réglages et mode partagés des Carrés BAL
          CarreBalDecision.kt   Résultat commun avant conversion vers le modèle
          CarreBalRules.kt      Protection, repos et calcul BAL des Carrés
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
        Rules.kt                Conversion typée des règles Carré BAL communes
        Settings.kt             Réglages BAL et forçage au carré
        Panel.kt                Retour au sémaphore et forçage conditionnel
        Textures.kt             SVG CC SMA et clignotements
        Driving.kt              Arrêt absolu ou consignes BAL communes
        Diagnostics.kt          Classement des défauts
      t_c/c/b/v/111001000/
        Signal.kt               Modèle indépendant, base BAL et approche identiques
        Aspect.kt               Indications BAL actives propres au modèle
        Reason.kt               Motifs propres au modèle
        Decision.kt             Association typée des deux enums
        Rules.kt                BAL normal ou manœuvre selon le parcours qualifié
        Settings.kt             Lecture des valeurs depuis son panneau
        Panel.kt                Réglages BAL seuls, régime Carré fixe
        Textures.kt             Catalogue propre, BAL, blanc fixe et clignotant
        Driving.kt              BAL ; manœuvres limitées à 30 km/h
        Diagnostics.kt          Classement des défauts
      uti/sidm/b/z/200000000/
        Markers.kt              Deux balises passives T32 et T33, sans consigne
        Textures.kt             Catalogue préparé des 19 images présentes
        Panel.kt                Libellés des ressources UTIL
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
import sfr.signals.t_c.c.b.v.`111001000`.Signal as T_C_111001000
```

Ces alias n'ajoutent aucun suffixe aux fichiers ou aux classes déclarées. Chaque
modèle de protection possède ses propres enums `Aspect` et `Reason` : une fonction qui attend
l'`Aspect` du package `011100000` ne peut pas recevoir celui du package `111000000`.
Les noms et ordinaux peuvent se répéter d'un modèle à l'autre sans collision.

Le SDK fournit le même contexte à tous les modèles : observations, réglages,
statut de disponibilité et voisin aval résolu. Un profil absent utilise les
valeurs déclarées ; un profil indisponible rend l’observation non fraîche.
Il n’y a aucun adaptateur d’observation ni fichier Neighbours à écrire par modèle.

`next` fournit l’ID, le type, la décision et la consigne de conduite du voisin.
`next.of(monModele)` donne aussi accès à ses enums typées si nécessaire.
Le BAL et les deux Carrés BAL partagent le calcul du cantonnement dans `common/bal/BalRules.kt`.
Ce calcul utilise les types communs `BalAspect`, `BalReason` et `BalDecision`.
Dans le même dossier, `BalNeighbours` raccorde les modèles concrets : il interprète
leurs indications et les consignes de conduite des autres modèles.
`CarreBalRules` partage leur protection ; `111000000` choisit son régime et `111001000` impose le Carré.
Chaque `Rules.kt` local convertit son résultat vers les enums du modèle.
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
prouve pas une nouvelle approche. Un autre train en approche peut le rouvrir
uniquement si le canton protégé est délimité et observé libre. **La tolérance
ancienne d’aval inconnu est retirée.** Une occupation, un conflit croisé détecté,
une limite inconnue, une occupation inconnue ou une observation périmée gardent
le carré fermé, comme une panne ou un arrêt forcé. Sans frontière aval reconnue,
y compris en terminus, ce modèle reste fermé : aucune libération n’est déduite
de la distance, d’un délai ou de la seule disparition du conflit.

Les motifs distinguent les conflits croisés, le canton occupé, la limite inconnue,
l’occupation inconnue et les données indisponibles. Pour les deux Carrés BAL, une limite
inconnue impose désormais C, même si une occupation partielle est détectée.
Le BAL simple conserve son comportement propre ; un BAL affichant S avec un
itinéraire inconnu reste un défaut à journaliser.
Une occupation connue, une fermeture forcée ou l'attente normale d'une approche
ne deviennent pas des anomalies. Les règles n'écrivent aucun fichier : la cadence,
la limite et les données du journal sont gérées par le SDK.

L'approche derrière les aiguilles provient du SDK, sur le parcours natif observé
pour la tête du train. AB déclare deux cantons pour `101000000`, `111000000` et `111001000`,
mais ne reconstruit pas toutes les branches possibles. Une observation d'approche
ne réserve aucune voie et ne certifie pas la libération par la queue.

Le choix retenu le 16 octobre est de **conserver les réservations du jeu**.
Le SDK garde le suivi des passages et la libération par la queue des consignes
de conduite. Sa version de développement transmet aussi les occupants déjà
observés du canton à Kotlin : une queue encore présente reste détectable après
disparition de la tête en approche. Une liste incomplète ou périmée ne prouve
jamais un dégagement. Ces données préparent l'enclenchement complémentaire ;
elles n'activent pas une nouvelle autorité d'itinéraire.

Les journaux du SDK distinguent désormais un conflit observé d'une observation
perdue, d'un autre train sélectionné ou d'un parcours remplacé. Les Carrés
conservent leur fermeture sur les conflits croisés qualifiés. Le durcissement
des replis des Carrés est décrit ci-dessus ; les textures et les règles
du BAL simple restent inchangées. `routeKnown` décrit une frontière de canton
connue, et `routeConflict == null` ne certifie jamais l’absence de conflit.
Ces règles ne constituent pas un enclenchement complet : elles ne verrouillent
pas les aiguilles et ne prouvent pas le dégagement de toutes les branches par
la queue. Ces ajouts nécessitent un kit et un mod reconstruits ; ils ne sont
pas annoncés comme déjà installés ou validés dans une partie.

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
contenant les six catalogues de textures, dans l'ordre des quatre modèles de
protection puis des deux marqueurs passifs T32 et T33.

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
| AB T_C 111001000 | Carré BAL type C et affichage M/M cli sur parcours qualifié | `imgs/t_c/c/b/v/111001000/` |
| AB UTIL T32 | Balise passive de demande de manœuvre | `imgs/uti/sidm/b/z/200000000/tex32.png` |
| AB UTIL T33 | Balise passive de demande de manœuvre clignotante | `imgs/uti/sidm/b/z/200000000/tex33.png` |

Les autres dossiers restent des ressources disponibles ; leur présence n'ajoute
pas de modèle ni de règle de signalisation. Les noms de fichiers existants sont
conservés (`texNN.svg`, `xx.svg` ou `texNN.png` pour les familles concernées).

La référence `uti/sidm/b/z/200000000` possède un catalogue préparé dans
`sfr/signals/uti/sidm/b/z/200000000/Textures.kt`, avec les 19 PNG présents conservés.
Les correspondances confirmées sont :

| Image | Désignation |
| --- | --- |
| `tex31.png` | Butoir (T31) |
| `tex32.png` | Manœuvre (T32) |
| `tex33.png` | Manœuvre clignotante (T33) |

Les noms affichés sont **AB UTIL T31**, **AB UTIL T32**, **AB UTIL T33**, etc.,
sans remplacer les codes par leur désignation métier. `Panel.kt` prépare leurs libellés depuis
`assets/translations.json`, identiques en français et en anglais.

Seuls **T32 et T33** sont déclarés comme marqueurs constructibles : type natif
**MARKER**, une image fixe par catalogue, taille 4 et placement à gauche.
Leur définition est passive : aucun script de réservation, aucune consigne de
conduite, aucune observation d'approche et aucune fonction de protection.
Le SDK n'évalue pas ces balises comme les signaux PATH. T33 reste une balise
fixe ; c'est le panneau `111001000` qui présente le blanc clignotant.

Les 19 images présentes sont conservées dans le catalogue préparé, qui n'est
pas enregistré globalement. T31 et les autres images ne deviennent pas
constructibles du seul fait de leur présence. Aucune vitesse n'est déduite
d'un numéro d'image ou de son nom.

Les identifiants de modèle (`TYPE`) et de catalogue (`TEXTURES`) reprennent
le chemin complet de la référence, avec des points ou des traits de soulignement :

| Modèle (`TYPE`) | Catalogue (`TEXTURES`) | Nombre d'images |
| --- | --- | --- |
| `sfr.t_a.s.c.v.011100000` | `sfr_t_a_s_c_v_011100000` | 11 |
| `sfr.t_c.c.b.v.101000000` | `sfr_t_c_c_b_v_101000000` | 4 |
| `sfr.t_c.c.b.v.111000000` | `sfr_t_c_c_b_v_111000000` | 12 |
| `sfr.t_c.c.b.v.111001000` | `sfr_t_c_c_b_v_111001000` | 15 |
| `sfr.uti.sidm.b.z.200000000.t32` | `sfr_uti_sidm_b_z_200000000_t32` | 1 |
| `sfr.uti.sidm.b.z.200000000.t33` | `sfr_uti_sidm_b_z_200000000_t33` | 1 |

Les six catalogues déclarent **44 images au total**, dont les 42 images des
quatre modèles de protection, conservées dans leur ordre. Celui de `011100000`
contient `tex00.svg`, `tex02.svg` à `tex10.svg`, puis `xx.svg` : `tex01.svg`
n'existe pas dans cette référence et aucun emplacement ne lui est réservé.
Celui de `101000000` déclare `tex00.svg`, `tex01.svg`, `tex03.svg`, puis `xx.svg`.
Celui de `111000000` déclare `tex00.svg` à `tex10.svg`, puis `xx.svg`.
Celui de `111001000` déclare `tex00.svg` à `tex10.svg`, puis `tex12.svg`,
`tex13.svg`, `tex14.svg` et `xx.svg`. Il ne contient pas de `tex11.svg`.
Chaque liste de protection est explicite dans son `Textures.kt` ; `Markers.kt`
déclare les deux catalogues passifs à une image. Les dossiers ne sont pas
parcourus ou triés pour reconstruire automatiquement les catalogues.

## Nouvelle base et développement

Cette version constitue une **nouvelle base pour une nouvelle partie**. Ses
identifiants binaires de modèle et de catalogue remplacent les anciennes
identités SFR ; les anciens catalogues et leurs indices ne sont pas réutilisés.
Les anciennes parties et les anciens profils SFR ne sont pas compatibles.
Aucune migration ni lecture des anciennes clés de réglage n’est conservée.
Les réglages propres à chaque modèle restent ceux décrits ci-dessus ; les
marqueurs passifs ne proposent aucun réglage de conduite.


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
Les anciens prototypes restent dans l'historique Git. Sur `master`, GitHub Actions
reconstruit le SDK épinglé et conserve des artefacts de test ; il ne publie aucune
release. Les essais en jeu restent une validation distincte.

Publication GitHub pendant une indisponibilité du VPS : commit de release avec
le trailer `Release-Runner: github`. Les binaires Windows sont construits et
testés par GitHub Actions, puis le catalogue public du Hub est actualisé.
Le SDK épinglé doit être publié avant de lancer cette release.

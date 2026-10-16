# Tester la branche main

Cette branche prépare AB **0.2.0-alpha.8** avec le SDK **0.9.0-alpha.4**.
Ces numéros ne suffisent pas à identifier les binaires de développement : utiliser
le commit SDK exact inscrit dans [.woodpecker/sdk-revision.txt](.woodpecker/sdk-revision.txt).
Le kit publié portant le même numéro peut ne pas encore fournir la nouvelle API.
Aucune release ni mise à jour du catalogue public n'est déclenchée par ces essais.

## Récupérer et compiler

```powershell
git clone --branch main https://github.com/NimbyRails-France/ab-signalisation-lumineuse.git
cd ab-signalisation-lumineuse
$sdkRevision = (Get-Content .woodpecker/sdk-revision.txt).Trim()
git clone https://github.com/NimbyRails-France/sdk.git ../sdk-test
git -C ../sdk-test checkout --detach $sdkRevision
```

Dans GitHub Actions, ouvrir **Main Windows validation** pour le commit AB à tester.
Le workflow compile le SDK depuis ce pin, puis exécute les tests Windows sous Wine
et la vérification de chargement d'AB. Un résultat réussi produit :

- `ab-main-windows-test` : dossier assemblé du mod et identifiants des deux commits ;
- `sdk-main-windows-test` : kit Kotlin et fichiers du runtime issus du même build ;
- `main-verification-reports` : comptes rendus disponibles des vérifications.

Ces artefacts sont des fichiers de développement, pas des installateurs ou des
releases. Les tests sous Wine ne remplacent pas un essai dans NIMBY Rails.

Pour recompiler sur Windows, utiliser un JDK 21 et extraire le kit contenant
`sdk.json`. Depuis le dépôt AB :

```powershell
.\gradlew.bat windowsTest verifyNativeMod -PnrfSdkDir=C:/dev/kits/sdk-test
```

Le résultat se trouve dans `build/gradle/mod/release`. `verifyNativeMod` assemble
aussi cette version Release. La commande ne lance pas le jeu et n'installe rien.

## Préparer l'essai dans le Hub

Dans le profil **Développer**, ajouter ce dépôt comme projet local et sélectionner
le kit Kotlin du build épinglé. Utiliser également le SDK d'exécution de ce même
build, préparé pour le profil développeur selon sa documentation. Le dossier `bin`
brut de l'artefact CI n'est pas un paquet d'installation du Hub. Ne pas mélanger
un kit récent avec le runtime publié uniquement parce que leur numéro est identique.
Le SDK et le mod doivent annoncer les commits attendus avant de comparer les résultats.

Compiler le projet dans le Hub, puis lancer une partie d'essai avec cette version
locale d'AB active. Employer une nouvelle partie ; cette base ne migre pas les
anciens identifiants SFR. Les réservations et refus du jeu restent actifs.

## Cas à vérifier pour 111001000

`AB T_C 111001000` fonctionne en Carré fixe. Il ne propose ni retour au sémaphore
ni forçage conditionnel au carré.

| Parcours effectivement analysé pour le train en approche | Résultat attendu |
| --- | --- |
| T32, puis un signal de protection avant la fin qualifiée | BAL normal, aucun blanc |
| T33, puis un signal de protection avant la fin qualifiée | BAL normal, aucun blanc |
| Fin native qualifiée, entièrement observée et libre, avec T32 | M, blanc fixe, 30 km/h maximum |
| Même cas avec T33 | M cli, blanc clignotant, 30 km/h maximum |
| Fin native qualifiée et libre, avec T32 et T33 ensemble | Carré, marqueurs incompatibles |
| Marqueur seulement sur une autre branche | Aucune demande de blanc sur le parcours choisi |
| Lecture incomplète, périmée ou pour un autre train | Aucune ouverture en blanc |
| Occupation positive, conflit ou arrêt imposé | Pas d'ouverture en manœuvre |

La fin est confirmée aujourd'hui par une borne native **BALISE3 inconditionnelle**
reconnue par le SDK. T32/T33 sont des marqueurs passifs : ils ne prouvent pas cette
fin et n'accordent aucune réservation. L'absence de signal suivant, un terminus
ou une distance de lecture atteinte ne suffisent pas. Un état inconnu ne devient
jamais une autorisation de blanc ; le BAL antérieur reste soumis à ses propres
observations qualifiées.

Les deux blancs retiennent 30 km/h jusqu'à un signal donnant voie libre, puis
jusqu'au dégagement de ce signal par la queue. Sans signal suivant, cette limite
n'est pas libérée. Le clignotement alterne toutes les 500 ms. Cette conduite reste
à qualifier en jeu ; les tests automatiques utilisent des observations synthétiques.

Pour un problème, conserver les deux commits, les diagnostics exportés par le Hub,
le modèle, les réglages et un schéma du parcours choisi incluant aiguilles,
marqueurs, borne et signal suivant. Indiquer l'aspect attendu, l'aspect observé et
la présence éventuelle d'un refus natif de réservation.

# Vulkan dans WSL : essai local sur RTX 5070

## Résultat du 21 septembre 2026

Sous Ubuntu 26.04, WSL 2.7.11.0 et WSLg 1.0.73.2, le pilote Mesa
Dozen 26.0.8 compilé localement expose la NVIDIA GeForce RTX 5070 Laptop GPU
à Vulkan via Direct3D 12. `vulkaninfo --summary` annonce Vulkan 1.2.335,
`DRIVER_ID_MESA_DOZEN` et un GPU dédié. Le pilote Vulkan Ubuntu par défaut
ne proposait que le rendu CPU `llvmpipe`.

Deux exécutions de `vkcube --c 300 --wsi xcb` ont terminé avec le code 0 sur
la RTX : une avec sélection explicite du GPU, l'autre via le lanceur ci-dessous.
Il s'agit d'un test de rendu court, pas d'une validation de stabilité en jeu.
Dozen affiche lui-même « not a conformant Vulkan implementation, testing use only ».
Ces premiers tests ne validaient ni NIMBY Rails ni le SDK Linux. Le diagnostic
du démarrage du jeu ci-dessous complète ce relevé.

## Steam dans Ubuntu

Steam a ensuite été installé avec `steam-installer` depuis les dépôts Ubuntu,
après activation de l'architecture i386. Sa première mise à jour est terminée
et la fenêtre « Sign in to Steam » a été détectée sous WSLg.

Le raccourci Windows **Steam Ubuntu RTX** lance
`/home/sami/.local/bin/steam-wsl-rtx`. Dans Ubuntu :

```sh
~/.local/bin/steam-wsl-rtx
```

Ce lanceur conserve la sélection Vulkan Dozen, mais retire `GALLIUM_DRIVER`
pour l'interface OpenGL 32 bits de Steam : forcer D3D12 dans cette interface
provoquait un crash. `GSK_RENDERER=cairo` et `GDK_BACKEND=x11` évitent également
le blocage de la fenêtre GTK de décompression du runtime.

NIMBY Rails natif Linux est installé dans cette bibliothèque, sans Proton.
Utiliser le lanceur dédié ci-dessous pour démarrer le jeu sous WSL.

## Écran noir du jeu : diagnostic et lancement corrigé

Le 21 septembre 2026, le lancement depuis Steam Runtime 4 créait une fenêtre
noire. Le processus tournait sans bibliothèque Vulkan chargée. Un test
`vulkaninfo` dans son conteneur échouait avec `ERROR_INCOMPATIBLE_DRIVER` :
`/usr/lib/wsl/lib/libd3d12.so` et `libdxcore.so` n'y étaient pas accessibles.
Ajouter uniquement leur répertoire hôte à la recherche des bibliothèques
déplaçait l'erreur vers `ID3D12DeviceFactory::CreateDevice failed`. L'ajout de
`/usr/lib/wsl` aux montages demandés était refusé car `/usr` est réservé par
Steam Runtime. Ces essais n'ont pas modifié la configuration permanente de Steam.

Le contournement local consiste à lancer le binaire natif directement dans
Ubuntu, avec Steam connecté, SDL 3 fourni par Ubuntu (`libsdl3-0`),
`SteamAppId=1134710`, `SteamGameId=1134710`, `SDL_VIDEO_DRIVER=x11` et le
lanceur Vulkan RTX existant. Aucun binaire du jeu n'a été modifié.

Le raccourci Windows **NIMBY Rails Ubuntu RTX** et l'entrée Linux du même nom
utilisent `/home/sami/.local/bin/nimby-wsl-rtx`. Depuis Ubuntu :

```sh
~/.local/bin/nimby-wsl-rtx
```

Le menu principal, la carte de fond et ses boutons ont été observés sur une
capture réelle de la fenêtre Linux. Steam reste nécessaire pour l'authentification,
l'installation et les mises à jour. Le bouton Jouer habituel de Steam utilise
encore le conteneur problématique ; employer le raccourci dédié sur cette machine.
Le journal du lancement direct est `~/.cache/nimby-wsl/game.log`.

Le plantage `std::bad_alloc` a été reproduit au chargement d'une copie de partie.
GDB montre que `load_image_file` tente de lire `/` comme une image, puis demande
`0x7fffffffffffffff` octets. Un contournement local refuse les ouvertures en
lecture de dossiers via `fopen`/`fopen64`, sans modifier le binaire du jeu.
Son source et son test sont dans le dépôt SDK, `tools/linux/directory-read-guard.c`
et `directory-read-guard-test.c`. Le lanceur charge cette bibliothèque uniquement
pour le processus du jeu via `LD_PRELOAD`.

Avec ce contournement, la copie atteint la carte, avec lignes et trains visibles.
Pour le désactiver ponctuellement :
`NRF_DIRECTORY_READ_GUARD=0 ~/.local/bin/nimby-wsl-rtx`.
L'origine du chemin de texture incorrect reste à déterminer. Ce résultat ne
valide pas toutes les textures, une partie longue, la synchronisation Steam
Cloud en lancement direct, ni le SDK Linux complet.

## Utilisation dans Ubuntu

Le lanceur local est installé dans `/home/sami/.local/bin/wsl-vulkan-rtx` :

```sh
~/.local/bin/wsl-vulkan-rtx vulkaninfo --summary
~/.local/bin/wsl-vulkan-rtx vkcube --c 300 --wsi xcb
~/.local/bin/wsl-vulkan-rtx commande arguments
```

Il définit `VK_DRIVER_FILES` et `VK_ICD_FILENAMES` vers le manifeste Dozen,
`MESA_VK_DEVICE_SELECT=10de:2d58!` pour exposer uniquement la RTX via la couche
Mesa de sélection, ainsi que `GALLIUM_DRIVER=d3d12` et
`MESA_D3D12_DEFAULT_ADAPTER_NAME=NVIDIA` pour OpenGL.
La sélection Vulkan a été vérifiée : un seul GPU est exposé avec ces paramètres.
Ces identifiants sont propres à cette machine.

La configuration ne s'applique qu'aux commandes lancées avec ce script.
Les pilotes système et les fichiers de démarrage du shell n'ont pas été remplacés.
Pour revenir au comportement Ubuntu d'origine, lancer la commande sans le script.

## Construction locale

Sources officielles : <https://archive.mesa3d.org/mesa-26.0.8.tar.xz>.
Sources extraites : `~/.local/src/mesa-wsl/mesa-26.0.8`.
Installation : `~/.local/opt/mesa-dzn-26.0.8`.

Les outils de compilation et bibliothèques de développement proviennent des
dépôts Ubuntu : Meson, Ninja, GCC, Mako, YAML, PLY, pkg-config, DirectX-Headers,
glslang, DRM, X11/XCB, Wayland, xshmfence, display-info, zstd, zlib, expat,
Bison et Flex. DirectX-Headers utilisé : 1.619.1.

```sh
meson setup ~/.local/src/mesa-wsl/build ~/.local/src/mesa-wsl/mesa-26.0.8 \
  --prefix="$HOME/.local/opt/mesa-dzn-26.0.8" --libdir=lib --buildtype=release \
  -Dvulkan-drivers=microsoft-experimental -Dgallium-drivers= \
  -Dplatforms=x11,wayland -Dglx=disabled -Degl=disabled -Dgbm=disabled \
  -Dllvm=disabled -Dvideo-codecs= -Dbuild-tests=false
ninja -C ~/.local/src/mesa-wsl/build -j 8
meson install -C ~/.local/src/mesa-wsl/build --no-rebuild
```

Le manifeste installé est `share/vulkan/icd.d/dzn_icd.x86_64.json`.
La compilation est uniquement x86-64, liée aux bibliothèques d'Ubuntu 26.04.
Le problème de conteneur Steam Runtime observé sur cette machine est contourné
par le lancement natif direct décrit ci-dessus. Les applications Vulkan 32 bits
ne sont pas couvertes par cette installation.

Référence : [installation isolée de Mesa](https://docs.mesa3d.org/install.html).

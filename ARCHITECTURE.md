## Architecture

L'architecture suit une approche **hexagonale** simplifiée :

- **Domaine** (`com.goli.marsrover.model`)  
  Contient la logique métier pure :
    - `Plateau` : représente le plateau (bornes et validation des coordonnées)
    - `Direction`, `Instruction` : enums métier
    - `Position` : coordonnée et orientation d'un rover
    - `Rover` : entité qui sait exécuter des instructions (`L`, `R`, `M`) via un contrôleur
    - `MissionController` : orchestre les rovers, gère les collisions et la progression séquentielle
    - `RoverCommand` : couple un `Rover` et sa liste d'instructions

- **Entrée / Adapters** (`com.goli.marsrover.input`)  
  Adapte des formats d'entrée concrets vers les objets métier :
    - `InputSource` : interface qui fournit une liste de `RoverCommand`
    - `FileInputSource` : implémentation qui lit un fichier texte et le parse

- **Exceptions** (`com.goli.marsrover.exception`)
    - `InvalidPositionException` : erreurs métier de déplacement (sortie de plateau, collision)
    - `InvalidCommandException` : erreurs de parsing / format d'entrée

- **Main** (`com.goli.marsrover.MarsRoverApp`)  
  Point d'entrée qui :
    - récupère le chemin d'un fichier d'entrée,
    - utilise `FileInputSource` pour charger les commandes,
    - délègue l'exécution à `MissionController`.

L'idée clé : le **domaine** ne dépend pas du format d'entrée.  
Si l'on souhaite, plus tard, utiliser une autre source (JSON, REST, base de données, stdin…), il suffit d'ajouter un
nouvel adapter `InputSource`.

**Points importants** :

- `Rover` ne connaît pas les autres rovers : il délègue à `MissionController` la vérification des collisions et des
  bornes.
- `move` :
    - applique `L` / `R` en modifiant la direction,
    - calcule la destination pour `M`,
    - demande à `MissionController` s'il peut se déplacer,
    - en cas de refus, lève `InvalidPositionException`,
    - sinon, met à jour la position via `controller.updatePosition`.

Cette séparation permet au rover de rester focalisé sur sa logique de mouvement, et au contrôleur de gérer la vision
globale du plateau.

---

## Gestion des collisions et des erreurs

### Collisions

- Chaque position occupée par un rover est enregistrée dans `MissionController`.
- Avant chaque mouvement (`MOVE`), `Rover` demande `canMoveTo` :
    - si la case est hors plateau → mouvement refusé,
    - si un autre rover occupe déjà la case → mouvement refusé.
- En cas de refus, `Rover.move` lève `InvalidPositionException`.

### Politique d'erreur

- La boucle globale (`executeAll`) est protégée par un `try/catch`.
- Dès qu'un rover rencontre une `InvalidPositionException` :
    - la mission est interrompue (*fail-fast*),
    - un log d'erreur est produit,
    - les positions finales (au moment de l'arrêt) sont affichées.

Cette politique est volontairement stricte.
En cas d'erreur, on arrête tout pour une mission aussi critique.
On pourrait la rendre configurable (arrêter seulement le rover courant, continuer avec le suivant, etc.) en introduisant
une couche de stratégie d'exécution.

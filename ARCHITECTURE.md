# Présentation
Mon implémentation du Mars Rover avec une architecture inspirée de l'hexagonale, pour séparer clairement logique métier et I/O.

* Plateau rectangulaire sur Mars.
* Plusieurs rovers, chacun avec une position initiale et une suite d'instructions (L, R, M).
* Les rovers sont exécutés séquentiellement.

Objectifs du projet :

* Implémenter les règles métier (déplacements, rotations, collisions, limites du plateau).
* Garder le domaine testable et indépendant de la source de données.
INPUT
5 5
1 2 N
LMLMLMLMM
3 3 E
MMRMMRMRRM

OUTPUT
1 3 N
5 1 E

## Architecture

Une classe Main qui câble tout, un adapter fichier, un contrôleur de mission, et un domaine qui gère les mouvements

           +---------------------------+
           |       MarsRoverApp        |
           |       (main / CLI)        |
           +-------------+-------------+
                         |
                         v
            +------------+------------+
            |      FileInputSource    |
            |   (adapter fichier)     |
            +------------+------------+
                         |
                         v
            +------------+------------+
            |    MissionController    |
            | (orchestrateur métier)  |
            +------------+------------+
                         |
           +-------------+------------------+
           |   Rovers + Plateau + Règles    |
           | (Rover, Plateau, Position,     |
           |  Direction, Instruction)       |
           +--------------------------------+


com.goli.marsrover
├── MarsRoverApp          // main, CLI
│
├── input
│   ├── InputSource       // interface source de données (port de lecture)
│   └── FileInputSource   // adapter fichier texte
│
├── model                 // domaine coeur métier + orchestration
│   ├── Pateau
│   ├── Position
│   ├── Direction
│   ├── Instruction
│   ├── Rover
│   ├── MissionController
│   └── RoverCommand
│
└── exception
    ├── InvalidPositionException
    └── InvalidCommandException


+------------------+        +------------------+
|    Plateau       |        |     Position     |
+------------------+        +------------------+
| - maxX : int     |        | - x : int        |
| - maxY : int     |        | - y : int        |
+------------------+        | - direction:     |
| +isValidPosition |        |   Direction      |
|   (x:int, y:int) |        +------------------+
+------------------+        | +getX/Y/Dir()    |
                            | +setX/Y/Dir(...) |
                            +------------------+

+------------------+        +------------------+
|    Direction     |        |   Instruction    |
+------------------+        +------------------+
| NORTH, EAST, ... |        | LEFT, RIGHT,MOVE |
+------------------+        +------------------+
| +rotateLeft()    |        | +fromChar(c)     |
| +rotateRight()   |        +------------------+
+------------------+

+------------------+
|      Rover       |
+------------------+
| - controller:    |
|   MissionCtrl    |
| - position:      |
|   Position       |
+------------------+
| +move(instr)     |
| +rotateLeft()    |
| +rotateRight()   |
| +toString()      |
+------------------+

+---------------------------+
|     MissionController     |
+---------------------------+
| - grid : Grid             |
| - positions: Map<R,P>     |
+---------------------------+
| +register(rover,pos)      |
| +canMoveTo(rover,x,y)     |
| +updatePosition(rover,x,y)|
| +executeAll(List<RoverCmd>) *
+---------------------------+

+------------------------------+
|        RoverCommand          |
+------------------------------+
| - rover: Rover               |
| - instructions: List<Instr>  |
+------------------------------+

L'architecture suit une approche **hexagonale** simplifiée :

- **Domaine** (`com.goli.marsrover.model`)  
  Contient la logique métier pure :
    - `Plateau` : représente le plateau (bornes et validation des coordonnées)
    - `Direction`, `Instruction` : enums métier
    - `Position` : coordonnée et orientation d'un rover
    - `Rover` : entité qui sait exécuter des instructions (`L`, `R`, `M`) via un contrôleur
    - `MissionController` : Tour de contrôle : orchestre les rovers, gère les collisions et la progression séquentielle
    - `RoverCommand` : couple un `Rover` et sa liste d'instructions

- **Entrée / Adapters** (`com.goli.marsrover.input`)  
  Adapte des formats d'entrée concrets vers les objets métier :
    - `InputSource` : interface qui lit une source de données et fournit une liste de `RoverCommand`
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

- `Rover` ne connaît pas les autres rovers ni le plateau : il délègue à `MissionController` la vérification des collisions et des bornes.
- `move` :
    - applique `L` / `R` en modifiant la direction,
    - calcule la destination pour `M`,
    - demande à `MissionController` s'il peut se déplacer,
    - en cas de refus, lève `InvalidPositionException`,
    - sinon, met à jour la position via `controller.updatePosition`.

Cette séparation permet au rover de rester focalisé sur sa logique de mouvement, et au contrôleur de gérer la vision globale du plateau.

---

## Gestion des collisions et des erreurs

### Collisions

- Chaque position occupée par un rover est enregistrée dans `MissionController`.
- Avant chaque mouvement, `Rover` demande `canMoveTo` :
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
On pourrait la rendre configurable (arrêter seulement le rover courant, continuer avec le suivant, etc.) en introduisant une couche de stratégie d'exécution.

### Tests et qualité
L'archi facilite les tests unitaires.

* Unitaires
Rover (mouvements, rotations, collisions),

MissionController (scénarios complets),

FileInputSource (parsing nominal + cas invalides).

Utilisation de @TempDir pour créer des fichiers de test temporaires côté parser.

* Tests de parsing :

file standard,
lignes vides,
plateau invalide,
position invalide,
commandes manquantes,
commandes/directions invalides


### Conclusion
L'architecture actuelle permet de remplacer facilement 
* la source de données (fichier → API, JSON, DB) en ajoutant un nouvel InputSource. 
* Le plateau en ajoutant une interface avec une méthode **boolean isValidPosition(int x, int y)**


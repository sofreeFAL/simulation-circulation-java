# 🚦 Simulation de circulation en Java Concurrente

![Java Version](https://img.shields.io/badge/Java-17-blue.svg)
![License](https://img.shields.io/badge/License-MIT-green.svg)
![Platform](https://img.shields.io/badge/Platform-Windows%20%7C%20Linux%20%7C%20Mac-lightgrey.svg)

##  Description

Ce projet est une simulation visuelle de trois scénarios de circulation utilisant la programmation concurrente en Java. Il illustre la gestion des ressources partagées et la synchronisation entre threads à travers des situations réalistes.

###  Les trois simulations

| Simulation | Mécanisme | Description |
|------------|-----------|-------------|
| 🚦 **Carrefour** | `ReentrantLock` + `Condition` | Priorité à droite avec détection automatique des blocages circulaires |
| 🚇 **Tunnel** | `ReentrantLock` + `Condition` | Sens unique alterné avec file d'attente équitable |
| 🅿️ **Parking** | `Semaphore` + `AtomicInteger` | Capacité limitée avec gestion de portes multiples |

##  Technologies utilisées

| Technologie | Utilisation |
|-------------|-------------|
| **Java 17** | Langage principal |
| **Threads** | Simulation des véhicules |
| **ReentrantLock** | Synchronisation du carrefour et tunnel |
| **Condition** | Gestion des files d'attente |
| **Semaphore** | Contrôle de capacité du parking |
| **Swing** | Interface graphique |

##  Structure du projet
SimulationCirculation/

├── src/

│ ├── Main.java

│ ├── carrefour/

│ │ ├── Carrefour.java

│ │ └── VoitureCarrefour.java

│ ├── tunnel/

│ │ ├── Tunnel.java

│ │ └── VoitureTunnel.java

│ ├── parking/

│ │ ├── Parking.java

│ │ └── VoitureParking.java

│ ├── common/

│ │ ├── Direction.java

│ │ └── Utils.java

│ └── ui/

│ ├── SimulationFrame.java

│ ├── AppColors.java

│ ├── RoundedButton.java

│ └── RoundedPanel.java

├── .gitignore

├── README.md

├── README C.md

└── SimulationCirculation.iml


##  Installation et exécution

### Prérequis

- Java JDK 17 ou supérieur
- Git (optionnel)

### Étapes d'installation

```bash
# 1. Cloner le repository
git clone https://github.com/sofreeFAL/simulation-circulation-java.git

# 2. Se déplacer dans le dossier
cd SimulationCirculation

# 3. Compiler le projet
javac -d out src/Main.java src/carrefour/*.java src/common/*.java src/parking/*.java src/tunnel/*.java src/ui/*.java

# 4. Exécuter l'application
java -cp out Main

Exécution avec IntelliJ IDEA
Ouvrir le projet dans IntelliJ IDEA

Localiser le fichier src/Main.java

Cliquer droit → Run 'Main.main()'

## Utilisation de l'application
Interface utilisateur
https://screenshots/main_interface.png


Élément	                Fonction

Type de simulation	Choix : Carrefour / Tunnel / Parking
Nombre de voitures	Nombre de threads à lancer
Capacité du parking	Nombre de places disponibles (Parking uniquement)
Nombre de portes	Portes d'entrée/sortie (Parking uniquement)
Lancer	                Démarre la simulation

Effacer	                Vide le journal des événements
Quitter	                Ferme l'application

## Journal des événements (codage couleur)
Couleur	       Catégorie	Signification
🔵 Bleu	[INFO]	        Informations générales
🟠 Orange	[ATTENTE]	Voiture en file d'attente
🟢 Vert	        [PASSAGE]	Voiture accède à la ressource
🔴 Rouge	[SORTIE]	Voiture libère la ressource
🟣 Violet	[DÉBLOCAGE]	Détection et résolution d'interblocage
⚫ Gris	[FIN]	        Fin de simulation
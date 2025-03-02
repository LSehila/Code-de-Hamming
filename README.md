# Calcul et Vérification des Codes de Hamming en Java

Ce projet a pour objectif de développer un programme Java permettant de calculer et de vérifier des codes de Hamming. Il s'adresse à toute personne souhaitant approfondir ses connaissances en programmation Java et en traitement des données binaires dans un contexte de détection d'erreurs.

## Description du Projet

Le programme comporte deux fonctionnalités principales :

1. **Calcul du Code de Hamming**  
   À partir d'un mot binaire saisi par l'utilisateur, le programme calcule le code de contrôle à adjoindre et génère le mot de Hamming complet.

2. **Vérification du Mot de Hamming**  
   En saisissant un mot de Hamming, l'application vérifie la présence d'éventuelles erreurs et détaille les étapes du processus de vérification.

## Analyse et Approche

Le projet repose sur une analyse précise de la structure d'un mot de Hamming, défini par la forme `x-y`, où :
- **x** représente la longueur totale du mot de Hamming.
- **y** représente la longueur du message initial (sans le code de contrôle).

Pour un nombre de bits de contrôle `i` (i = 1, 2, 3, ...), on a :
- **Longueur totale du mot** : `x = 2^i - 1`
- **Longueur du message** : `y = (2^i - 1) - i`
- **Longueur du code de contrôle** : `i`

Cette approche garantit la cohérence dans le calcul et la vérification du code de Hamming.

## Prérequis

- **Java JDK** : Assurez-vous d'avoir installé le Java Development Kit pour compiler et exécuter le programme.
- **Environnement de Développement (IDE)** : Eclipse, IntelliJ IDEA ou tout autre IDE de votre choix sont recommandés pour faciliter le développement.

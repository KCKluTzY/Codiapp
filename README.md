# CodiApp 
**Système collaboratif d'aide à la mobilité pour les personnes avec déficience cognitive**

CodiApp est une application mobile développée avec React Native (Expo) permettant de mettre en relation :

- Des aidés (personnes ayant besoin d’aide)

- Des aidants (helpers)

- Des administrateurs

Elle permet la gestion des demandes d’aide, la communication, la localisation et le suivi des interventions.

# Intallation du projet
## Prerequis: 
Avant de lancer l’application, installez :

Node.js ≥ 18

npm ou yarn

Android Studio (émulateur)

Expo CLI

Pour installer Expo CLI il suffit de taper cette commande dans le terminal:
npm install -g expo-cli

## Clonage du projet
Pour pouvoir lancer notre projet il vous suffira de le cloner via la commande suivante dans le terminal:
git clone https://github.com/KCKluTzY/Codiapp.git 
puis placez vous dans le dossier CODIAPP via : 
cd Codiapp

## Installation des dépendances
Après le clonage il vous faudra installer les dépendances pour cela vous devrez lancer la commande: 
npm install

## Lancement du projet 
Une fois le projet clonné et les dépendance installer on peux maintenant passer au lancement du projet pour cela lancez votre émulateur android studio puis cliquer sur more action selectionnez Virtual Devices Manager et créer un nouvel appareil grace au petit + en haut a gauche , choisissez le model Pixel 7 et nommé le comme vous le souhaitez. Ensuite lancez l'appareil un ecran de téléphone apparaitra. 
Une fois cela fait dirigez vous dans un Powershell et placez vous dans le chemin du projet jusqu'a arriver sur quelque chose comme \Codiapp puis selon l'application que vous souhaitez lancer faites: 
cd CodiAppUser (pour l'application Aidé) 
cd CodiAppHelper (pour l'application Aidant)
cd CodiAppAdmin (pour l'application Admin)

Puis lancez Expo avec la commande: 
npx expo start

un QR code apparaitra vous pouvez le scanner avec votre Smartphone via l'application Expo go ou alors appuyer sur a qui lancera l'application sur votre émulateur Android Studio

Vous pourrez alors acceder aux différentes fonctionnalité de l'application et l'utiliser comme bon vous semble.

## Authentification 
L’application est connectée à une API d’authentification.

Base URL
http://localhost:8080/api/v1/auth

Attention sur émulateur Android :

http://10.0.2.2:8080/api/v1/auth

Pour le register il faudra passer par Postman on se place dans la balise Post à l'URL suivant : http://localhost:8080/api/v1/auth/register
Puis dans Body nous entrons les données utilisateur en respectant une structure par exemple : 
{
  "username": "Bob_Dubois",
  "email": "bob_dubois@example.com",
  "password": "SecurePassword123!",
  "role": "ROLE_PERSON_DI"
}

Les roles disponible sont : 
ROLE_PERSON_DI (pour les Aidé)
ROLE_HELPER (pour les Aidant)
ROLE_ADMINISTRATOR (pour les Admin)

Le reste des informations pourront quant à elle être remplie comme vous le souhaitez. 

Ensuite dans la page authentification de l'application vous devrez taper l'email et le password un pop up de connexion reussi apparaitra si cette dernière aboutie. 

## Dépanage d'Erreur Probable : 
 Slider / Picker non trouvé

Installer :

npx expo install @react-native-community/slider
npx expo install @react-native-picker/picker

 API ne répond pas

Vérifier :

Backend lancé sur port 8080

URL = 10.0.2.2

Pas localhost

 Erreur 500 Expo

Nettoyer cache :

npx expo start -c

## Fonctionnalités principales
Aidé

-Authentification

-Créer une demande

-Envoyer messages

-Appeler un aidant

-Se localiser

Aidant

-Authentification

-Voir demandes

-Accepter / Refuser

-Gérer disponibilités

-Définir rayon (km)

-Interagir avec aidé

-Suivre aides en cours

-Localiser aidé

-Statistiques

-Paramètres accessibilité

Admin

-Gérer les Aidés

-Gérer les Aidants

-Suspendre comptes

-Accéder aux statistiques

-Ajouter fonctionnalités

## Auteur 
Projet réalisé dans le cadre d'un projet d'étude CodiApp par Molin Quentin  et Carrillo Théo


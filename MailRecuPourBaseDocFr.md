Salut,

tu pourrais m'expliquer comment on utilise ton application "pas à pas" ? Est-ce que ça fonctionne quelque soit l'OS du bureau ?



Salut !

(attention ça va être long, mais très détaillé)

Oui mon application devrait fonctionner sous tout les OS,
mais la fonction de capture d'écran marche mal sous MAC OS (ça coupe la connexion je pense), et n'est pas assez testée sous Linux.
Le serveur est fait en Java, donc c'est multi plateforme.

Voilà comment utiliser mon application :

Mon application sert à remplacer un contrôleur sans fil, comme une souris sans fil par exemple

On la récupère sur le Market.
La première fois qu'on la lance, elle affiche l'aide (qui est accessible via le bouton "menu" quand on est dans l'appli)
Désolé, c'est en anglais pour le moment :P (comment la version actuelle est orienté "développement", je n'ai pas de temps à consacre à plusieurs langues en même temps)

Pour que mon application fonctionne, il faut que tu ai le wifi chez toi, pour que ton téléphone puisse accéder à ton pc.
Il faut que ton téléphone et ton pc soient sur le même "réseau local"
Donc tu désactive la 3G temporairement, et/ou tu actives le wifi sur ton téléphone
Ton pc n'a pas besoin d'être en wifi, juste branché à ton routeur/box

Mon application fonctionne sur un mode client/serveur : il faut donc un logiciel client (sur le téléphone), et un serveur, un programme, sur ton pc
Pour récupérer le serveur, vas dans "get server"
Ca va t'afficher en gros une URL du type http://192.168.0.xxx:64788
Tu la rentre dans ton navigateur web (Internet explorer (bouh), Firefox, Opera, Safari, Chrome)
Ca va te télécharger un fichier zip
Tu le décompresses dans un dossier

Maintenant il faut lancer le serveur
Le serveur est fait en Java, donc tu vas avoir besoin de java sur ton PC
Normalement tu l'as déjà, mais tu peux le télécharger ici : http://java.com/fr/download/
Si tu es sous Windows, lances PRemoteDroid-Server.exe
Si tu es sous Linux/Mac, tu double clic sur PRemoteDroid-Server.jar (ça devrait lancer le programme)
Si ça ne marche pas (???), tu peux ouvrir une console, te placer dans le dossier de l'application, et taper : "java -jar PRemoteDroid-Server.jar"

Quand le serveur se lance, il doit afficher une petite icone dans ta zone de notification.
Il doit aussi afficher un petit message, avec l'adresse IP/port du serveur (192.168.xxx:64788)
Essaye de la retenir, ça sera utile après
Tu peux paramétrer le mot de passe et le port du serveur en faisant un clic droit (gauche sur mac) sur l'icone de l'application
N'oublies pas de relancer le serveur si tu changes le port

Retournes sur ton téléphone
Sur PRemoteDroid, vas dans "Settings"
Là tu peux paramétrer ta connexion avec le serveur, le contrôle de la souris, la capture d'écran
Dans server, tu rentres l'adresse IP indiqué dans le message au lancement du serveur : "192.168.0.xxx", sans le port derrière (64788)
Normalement c'est déjà bon, mais...
Dans port, tu mets 64788 (déjà fait normalement)
Dans password, tu mets le mot de passe choisi sur le serveur ("azerty" par défaut)
Dans "Control", tu peux régler tout ce qui touche au déplacement de la souris, et au clic.
Dans "Screen capture", tu peux regler .......
C'est une fonction qui sert à afficher "en différé", ce qu'il y a autour du curseur de ta souris, mais ce n'est pas du VNC

Retourne dans l'application, bouton retour
ça devrait se connecter tout seul (ou relance la)

Pour bouger la souris, tu touches la zone centrale, et tu bouges le doigt.
La "sensitivity" contrôle la vitesse de déplacement de la souris
L' "Acceleration" est un systeme permettant de faire bouger encore plus vite la souris si tu te déplace plus vite sur le téléphone

Pour cliquer, tu as les boutons sur l'écran (gauche, milieu, et droit)
Un long clic sur un de ces boutons le mettra en mode "maintenu"
Tu peux aussi cliquer directement sur la surface de déplacement (il ne faut pas bouger le doigt en touchant la surface).
Un clic très court fera comme un simple clic gauche de souris.
Un clic long maintiendra la bouton gauche, faire un simple clic pour le relacher.

Voilàààààà
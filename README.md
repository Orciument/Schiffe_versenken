# Schiffe_versenken
Dies ist das Projekt für meine Facharbeit in der 10.Klasse über den TCP/IP Stack (bzw. auch OSI), dieses Projekt hat dabei die Facharbeit begleitet und diente als Quelle für verschiedenen Besipielcode in der Facharbeit.

Das Projekt besteht aus 2 Komponenten, dem Client und dem Server. Der Server hostet dabei das Spiel, und die Clients sind jeweisl die beiden Spieler.

Die Application nutzt Java 17 (openJDK-17).

# Verwendung
Gestartet werden der Client und Server mit den artifacts und:
``java -jar .\Server.jar``
``java -jar .\Client.jar``
Darauf wird dich der Server bzw. Client nach den benötigten Daten fragen.
Zu beachten ist das die Clients IP und Port in zwei Separaten Prompts haben wollen, für die Server Adresse muss daher nur die Domain/IP eingeben werden.
Falls Client und Server in verschiedenen Netzwerken sind manuell der Port freigeben werden. 

Sobald das Spiel gestartet hat stehen einem diese Befehle zur Verfügung, diese können dazu auch immer mit ``help`` eingesehen werden:
Befehle:
(Alles in und inklusive der eckigen Klammern muss ersetzt werden, und sind nur platzhalter)
| Command | Beschreibung |
|---------|--------------|
| shot [x-Koordinate] [y-Koordinate] | Schieße auf das Feld deines Gegners |
| placeship [Ship Size] [x-Koordinate] [y-Koordinate] [Blickrichtung] | Platziere ein Schiff auf deinem Feld, die Blickrichtung als ("up","down","left", "right") |
| debug [true/false] | Schalte den debug Mode an, für weiter Informationen |
| help | |

## Beispiel
![image](https://github.com/Orciument/Schiffe_versenken/assets/67759477/437e6a9c-d7fa-4b31-975a-91f902b3a5d9)

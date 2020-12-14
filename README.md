# PRTG Alarm Service mit Philipps Hue
Diese Dokumentation beschreibt das Vorgehen für die Installation und Weiterentwicklung des PRTG Alarm Service. Die PRTG Alarm Service Applikation wurde in Java geschrieben.

## Installation
### JDK Installation
Da die Applikation in Java programmiert wurde, ist die Installation von Java essentiell. Die Applikation wurde mit der Java LTS 11 getestet und entwicklet. 

Java kann unter folgenden URL heruntergeladen werden:
https://www.oracle.com/java/technologies/javase-jdk11-downloads.html

Nach dem Downlad kann die Software auf Windows ausgeführt werden. Die Installation kann im Normalfall ohne Änderung durchgeklickt werden.

### Java Applikation hinterlegen
Da Java nun auf dem Computer / Server installiert wurde kann das Java Programm ausgeführt werden. Bei machen Distributionen kann es sein, das Windows das *.jar File nicht erkennt. In diesem Fall muss die Applikation mit einem Doppelklick gestartet werden und für die Ausführung Java LTS 11 ausgewählt werden. Am besten wählt man zusätzlich die Einstellungen «immer dieses Programm für diese Datei verwenden».

Nun muss die *.jar Applikation in den Autostart Ordner kopiert werden. Dieser befindet sich bei Windows 10 unter folgendem Pfad:
«C:\ProgramData\Microsoft\Windows\Start Menu\Programs\StartUp»

Nach einem Neustart des Computers/Servers sollte die Applikation automatisch gestartet werden. Dies testet man am besten, indem man einen Fehler auf dem PRTG Server simmuliert.

## Weiterentwicklung
Für folgende Anpassungen muss das Programm neu Kompiliert werden:
- Farbeinstellungen
- Anpassungen an der Bridge (User, IP Adresse, API Token)
- Anpassungen an PRTG (User, Passwort, URL, IP Adresse)

Für die Weiterentwicklung oder Anpassung der Konfiguration wird die Software IntelliJ benötigt. Die aktuelle Version von IntelliJ kann unter folgender Webseite heruntergeladen werden:
https://www.jetbrains.com/de-de/idea/download/#section=windows

Den Source Code findet man unter den folgeden GitHub Repositorys:
https://github.com/linusniederer/prtg-alarm-service

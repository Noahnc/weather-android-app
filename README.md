# Android Wetter Benachrichtigungs App

Die Android-App in dieser Repository kann den Anwender in regelmässigen Abständen über eine Temperatur Unter- / Überschreitung informieren.
Der Nutzer kann dazu in der App eine Temperatur eintragen, die im Local Storage des Geräts gespeichert wird. Anschliessen kann ein Background-Service gestartet werden.
Der Service prüft in einem Intervall von einer Minute die Wetterstation "Tiefenbrunnen". Ist die Temperatur an dieser höher oder tiefer als der festgelegte Wert, wird eine Push-Meldung ausgegeben.

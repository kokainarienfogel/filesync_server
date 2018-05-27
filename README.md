# CE KT SS 2018 Filesync Java Backend Server

Wir benutzen [Undertow](http://undertow.io/) als Webserver. Undertow ist schön unkompliziert, läuft sowohl *blocking* als auch *non-blocking* und einzelne Routen können als Lambda-Funktionen geschrieben werden. Darüber hinaus wars der erste Server zu dem ein schönes Tutorial zu finden war (welches über API Doku hinaus geht und auch fortgeschrittene Sachen schön erklärt).

Wesentliche Funktionen sind funktional geschrieben, hilft ungemein um Sachen später einfach parallel ausführbar zu bekommen, ausserdem ist es meistens kompakter. Das ist übrigens auch der Grund warum [Fugue](https://bitbucket.org/atlassian/fugue) als Abhängigkeit dabei ist, obwohl eigentlich kaum etwas davon benötigt wird - vielleicht fliegts für die endgültige Abgabe wieder raus.

[Jackson-Jr](https://github.com/FasterXML/jackson-jr) ganz einfach deshalb weil Jackson soweit mir das bekannt ist in Sachen JSON der Standard für Java ist.

*AtomicInteger* und *AtomicReference<>* sind augenscheinlich schöne Klassen um performant thread-sicher Daten und Objekte auszutauschen. Eventuell ist noch ein wenig mehr Recherche erforderlich, da explizit erwähnt dass hiermit primär nicht-blockierende Datenstrukturen gebaut werden können - geht irgendwie gegen den Einsatzzweck.

=====================================================================
 CineMax - Sistema di gestione di un piccolo cinema monosala (200 posti)
 Laboratorio Interdisciplinare A - a.a. 2025/2026
=====================================================================

1. CONTENUTO DEL REPOSITORY
---------------------------
  autori.txt   dati degli autori del progetto
  README.txt   questo file (installazione e compilazione)
  doc/         Manuale Utente e Manuale Tecnico in formato .pdf,
               piu' la cartella javadoc/ con la documentazione Javadoc
  src/         codice sorgente Java (package cinemax)
  bin/         codice eseguibile CineMax.jar
  data/        file dei dati: proiezioni.csv, utenti.csv, prenotazioni.csv
  lib/         eventuali librerie esterne (il progetto usa solo la
               libreria standard di Java: la cartella e' vuota)

2. REQUISITI
------------
  - Java Development Kit (JDK) versione 17 o successiva
    (per la sola esecuzione del .jar e' sufficiente il JRE 17+).
  - Sistema operativo qualsiasi (Windows, macOS, Linux):
    il progetto e' multipiattaforma.

3. COMPILAZIONE
---------------
  Dalla cartella radice del progetto eseguire:

    javac -d out src/cinemax/*.java

  Le classi compilate vengono prodotte nella cartella out/.

4. CREAZIONE DELL'ESEGUIBILE (.jar)
-----------------------------------
  Sempre dalla cartella radice, dopo la compilazione:

    jar --create --file bin/CineMax.jar --main-class cinemax.CineMax -C out .

5. ESECUZIONE
-------------
  Dalla cartella radice del progetto (importante: l'applicazione
  cerca i file dei dati nella sottocartella data/):

    java -jar bin/CineMax.jar

  In alternativa e' possibile indicare una cartella dati diversa:

    java -jar bin/CineMax.jar percorso/della/cartella/dati

6. GENERAZIONE DELLA JAVADOC
----------------------------
  Dalla cartella radice del progetto:

    javadoc -d doc/javadoc -private -encoding UTF-8 -charset UTF-8 src/cinemax/*.java

7. UTENTI PREDEFINITI (file data/utenti.csv)
--------------------------------------------
  Le password sono memorizzate cifrate (hash SHA-256). Le credenziali
  in chiaro degli utenti predefiniti sono:

    Proiezionisti:  lbianchi / proiezione1
                    gverdi   / proiezione2
    Bigliettai:     mferrari / biglietto1
                    scolombo / biglietto2
                    pmoretti / biglietto3
                    eriva    / biglietto4
                    agalli   / biglietto5
    Cliente demo:   crossi   / cliente1

  I nuovi clienti possono registrarsi dal menu' iniziale
  dell'applicazione (opzione 2).

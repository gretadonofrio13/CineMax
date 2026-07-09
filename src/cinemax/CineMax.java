/*
 * Autori del progetto:
 * D'Onofrio Greta - Matricola 766016 - Sede CO
 * Tricarico Nicolo' - Matricola 767095 - Sede CO
 */

package cinemax;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * La classe <code>CineMax</code> e' la classe principale
 * dell'applicazione: contiene il metodo {@link #main(String[])} e
 * realizza l'interfaccia a terminale (TUI) della piattaforma.
 * <p>
 * All'avvio l'applicazione mostra un menu' iniziale in cui e'
 * possibile effettuare il login, registrarsi come cliente oppure
 * proseguire come utente <em>guest</em> indicando solo il nome
 * (anche parziale) di un film. In base al ruolo dell'utente
 * autenticato viene poi mostrato il menu' del cliente, del
 * proiezionista o del bigliettaio.
 * <p>
 * I dati sono letti e scritti nei file CSV contenuti nella cartella
 * <code>data</code>: <code>proiezioni.csv</code>,
 * <code>utenti.csv</code> e <code>prenotazioni.csv</code>. E'
 * possibile indicare una cartella dati diversa come primo argomento
 * da riga di comando.
 *
 * @author D'Onofrio Greta
 * @author Tricarico Nicolo'
 * @version 1.0
 */
public class CineMax {

    /** Formato con cui l'utente inserisce le date (es. 20/05/2027). */
    private static final DateTimeFormatter FORMATO_DATA_INPUT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /** Formato con cui l'utente inserisce gli orari (es. 21:30). */
    private static final DateTimeFormatter FORMATO_ORA_INPUT =
            DateTimeFormatter.ofPattern("HH:mm");

    /** Lo scanner condiviso per la lettura da tastiera. */
    private final Scanner tastiera = new Scanner(System.in);

    /** Il gestore del palinsesto delle proiezioni. */
    private final GestoreProiezioni gestoreProiezioni;

    /** Il gestore degli utenti registrati. */
    private final GestoreUtenti gestoreUtenti;

    /** Il gestore delle prenotazioni dei clienti. */
    private final GestorePrenotazioni gestorePrenotazioni;

    /**
     * Costruisce l'applicazione caricando i dati dai file CSV
     * contenuti nella cartella indicata.
     *
     * @param cartellaDati la cartella contenente i file dei dati
     * @throws IOException se uno dei file non e' leggibile
     */
    public CineMax(Path cartellaDati) throws IOException {
        gestoreProiezioni =
                new GestoreProiezioni(cartellaDati.resolve("proiezioni.csv"));
        gestoreUtenti =
                new GestoreUtenti(cartellaDati.resolve("utenti.csv"));
        gestorePrenotazioni =
                new GestorePrenotazioni(cartellaDati.resolve("prenotazioni.csv"));
    }

    /**
     * Punto di ingresso dell'applicazione. Carica i dati dalla
     * cartella <code>data</code> (o dalla cartella indicata come
     * primo argomento) e avvia il menu' iniziale.
     *
     * @param args argomenti da riga di comando: l'eventuale primo
     *             argomento indica la cartella dei dati
     */
    public static void main(String[] args) {
        Path cartellaDati = Path.of(args.length > 0 ? args[0] : "data");
        try {
            new CineMax(cartellaDati).menuIniziale();
        } catch (IOException e) {
            System.out.println("Errore nell'accesso ai file dei dati nella cartella '"
                    + cartellaDati + "': " + e.getMessage());
            System.out.println("Verificare di eseguire l'applicazione dalla cartella "
                    + "radice del progetto (si veda il UtentiPredefiniti.txt).");
        }
    }

    // ------------------------------------------------------------------
    // Menu' iniziale e accesso
    // ------------------------------------------------------------------

    /**
     * Mostra il menu' iniziale dell'applicazione, da cui e' possibile
     * effettuare il login, registrarsi come cliente, proseguire come
     * utente guest oppure uscire.
     */
    public void menuIniziale() {
        System.out.println("=====================================================");
        System.out.println("   CineMax - gestione di un piccolo cinema monosala");
        System.out.println("=====================================================");
        boolean esci = false;
        while (!esci) {
            System.out.println();
            System.out.println("--- Menu' iniziale ---");
            System.out.println("1) Login");
            System.out.println("2) Registrati come cliente");
            System.out.println("3) Prosegui come guest (ricerca film per nome)");
            System.out.println("0) Esci");
            int scelta = leggiIntero("Scelta: ", 0, 3);
            switch (scelta) {
                case 1 -> login();
                case 2 -> registraCliente();
                case 3 -> menuGuest();
                default -> esci = true;
            }
        }
        System.out.println("Arrivederci da CineMax!");
    }

    /**
     * Gestisce la procedura di login: chiede username e password e,
     * in caso di successo, apre il menu' corrispondente al ruolo
     * dell'utente autenticato.
     */
    private void login() {
        System.out.println();
        System.out.println("--- Login ---");
        String username = leggiTesto("Username: ");
        String password = leggiTesto("Password: ");
        Utente utente = gestoreUtenti.login(username, password);
        if (utente == null) {
            System.out.println("Credenziali non valide. Riprovare.");
            return;
        }
        System.out.println("Benvenuto/a, " + utente.getNome() + " "
                + utente.getCognome() + "!");
        switch (utente.getRuolo()) {
            case CLIENTE -> menuCliente(utente);
            case PROIEZIONISTA -> menuProiezionista(utente);
            case BIGLIETTAIO -> menuBigliettaio(utente);
        }
    }

    /**
     * Gestisce la registrazione di un nuovo cliente
     * (funzionalita' <code>registraCliente()</code>): chiede tutti i
     * dati del cliente e li registra tramite {@link GestoreUtenti}.
     */
    private void registraCliente() {
        System.out.println();
        System.out.println("--- Registrazione nuovo cliente ---");
        String nome = leggiTesto("Nome: ");
        String cognome = leggiTesto("Cognome: ");
        String username = leggiTesto("Username: ");
        if (gestoreUtenti.esisteUsername(username)) {
            System.out.println("Username gia' in uso: scegliere un altro username.");
            return;
        }
        String password = leggiTesto("Password: ");
        LocalDate dataNascita =
                leggiDataOpzionale("Data di nascita (gg/mm/aaaa, INVIO per saltare): ");
        String domicilio = leggiTesto("Luogo del domicilio: ");
        try {
            Utente nuovo = gestoreUtenti.registraCliente(nome, cognome,
                    username, password, dataNascita, domicilio);
            if (nuovo != null) {
                System.out.println("Registrazione completata! Ora puoi effettuare il login.");
            } else {
                System.out.println("Registrazione non riuscita: username gia' in uso.");
            }
        } catch (IOException e) {
            System.out.println("Errore nel salvataggio degli utenti: " + e.getMessage());
        }
    }

    // ------------------------------------------------------------------
    // Menu' guest (login non necessario)
    // ------------------------------------------------------------------

    /**
     * Gestisce l'accesso come utente guest: come da specifica, viene
     * chiesto solo il nome (anche parziale) di un film e viene
     * mostrato l'elenco delle relative proiezioni (se esistenti);
     * l'utente puo' poi cercare altre proiezioni e visualizzarne i
     * dettagli, senza autenticarsi.
     */
    private void menuGuest() {
        System.out.println();
        System.out.println("--- Accesso come guest ---");
        String nomeFilm = leggiTesto("Nome (anche parziale) del film: ");
        List<Proiezione> trovate = gestoreProiezioni
                .cercaProiezione(nomeFilm, null, null, null, null, null);
        if (trovate.isEmpty()) {
            System.out.println("Nessuna proiezione trovata per \"" + nomeFilm + "\".");
        } else {
            stampaElencoProiezioni(trovate);
        }
        boolean indietro = false;
        while (!indietro) {
            System.out.println();
            System.out.println("--- Menu' guest ---");
            System.out.println("1) Cerca proiezioni");
            System.out.println("2) Visualizza i dettagli di una proiezione");
            System.out.println("0) Torna al menu' iniziale");
            int scelta = leggiIntero("Scelta: ", 0, 2);
            switch (scelta) {
                case 1 -> cercaProiezioni();
                case 2 -> visualizzaProiezione();
                default -> indietro = true;
            }
        }
    }

    // ------------------------------------------------------------------
    // Funzionalita' di consultazione delle proiezioni
    // ------------------------------------------------------------------

    /**
     * Realizza la funzionalita' di ricerca
     * <code>cercaProiezione()</code>: chiede all'utente i criteri di
     * ricerca desiderati (titolo anche parziale, tipologia, intervallo
     * di date, costo del biglietto, oppure una loro combinazione: ogni
     * criterio puo' essere saltato premendo INVIO) e stampa l'elenco
     * delle proiezioni trovate.
     *
     * @return la lista delle proiezioni trovate
     */
    private List<Proiezione> cercaProiezioni() {
        System.out.println();
        System.out.println("--- Ricerca proiezioni ---");
        System.out.println("(premere INVIO per saltare un criterio)");
        String titolo = leggiTestoOpzionale("Titolo (anche parziale): ");
        String genere = leggiTestoOpzionale("Tipologia di film (genere): ");
        LocalDate dataDa = leggiDataOpzionale("A partire dal giorno (gg/mm/aaaa): ");
        LocalDate dataA = leggiDataOpzionale("Fino al giorno (gg/mm/aaaa): ");
        Double prezzoMin = leggiDoubleOpzionale("Costo minimo del biglietto (EUR): ");
        Double prezzoMax = leggiDoubleOpzionale("Costo massimo del biglietto (EUR): ");
        List<Proiezione> trovate = gestoreProiezioni.cercaProiezione(
                titolo, genere, dataDa, dataA, prezzoMin, prezzoMax);
        if (trovate.isEmpty()) {
            System.out.println("Nessuna proiezione soddisfa i criteri indicati.");
        } else {
            stampaElencoProiezioni(trovate);
        }
        return trovate;
    }

    /**
     * Realizza la funzionalita' <code>visualizzaProiezione()</code>:
     * dopo la ricerca, l'utente seleziona una proiezione tramite la
     * sua data e ora e ne vengono mostrati tutti i dettagli, incluso
     * il numero di posti liberi ricavato dal numero di prenotazioni
     * e dalla capienza della sala.
     */
    private void visualizzaProiezione() {
        Proiezione p = selezionaProiezione();
        if (p == null) {
            return;
        }
        Film f = p.getFilm();
        System.out.println();
        System.out.println("--- Dettagli della proiezione ---");
        System.out.println("Titolo:          " + f.getTitolo());
        System.out.println("Genere:          " + f.getGenere());
        System.out.println("Regista:         " + f.getRegista());
        System.out.println("Anno:            " + f.getAnno());
        System.out.println("Durata:          " + f.getDurataMinuti() + " minuti");
        System.out.println("Eta' minima:     "
                + (f.getEtaMinima() == 0 ? "per tutti" : f.getEtaMinima() + "+"));
        System.out.println("Data e ora:      "
                + p.getDataOra().format(Proiezione.FORMATO_VIDEO));
        System.out.println("Costo biglietto: "
                + String.format("%.2f", p.getPrezzoBiglietto()) + " EUR");
        System.out.println("Posti liberi:    "
                + gestorePrenotazioni.postiLiberi(p.getDataOra())
                + " su " + GestorePrenotazioni.CAPIENZA_SALA);
    }

    /**
     * Chiede all'utente la data e l'ora di una proiezione e la cerca
     * nel palinsesto, ripetendo la richiesta finche' non viene
     * indicata una proiezione esistente o l'utente non rinuncia.
     *
     * @return la proiezione selezionata, oppure <code>null</code> se
     *         l'utente rinuncia
     */
    private Proiezione selezionaProiezione() {
        while (true) {
            System.out.println();
            System.out.println("Selezione della proiezione (INVIO sulla data per annullare)");
            LocalDate data = leggiDataOpzionale("Data della proiezione (gg/mm/aaaa): ");
            if (data == null) {
                return null;
            }
            LocalTime ora = leggiOra("Ora della proiezione (hh:mm): ");
            Proiezione p = gestoreProiezioni
                    .trovaPerDataOra(LocalDateTime.of(data, ora));
            if (p != null) {
                return p;
            }
            System.out.println("Nessuna proiezione inizia in quella data e ora. Riprovare.");
        }
    }

    /**
     * Stampa a terminale un elenco di proiezioni, una per riga.
     *
     * @param elenco la lista delle proiezioni da stampare
     */
    private void stampaElencoProiezioni(List<Proiezione> elenco) {
        System.out.println();
        System.out.println("Proiezioni trovate: " + elenco.size());
        for (Proiezione p : elenco) {
            System.out.println("  " + p);
        }
    }

    // ------------------------------------------------------------------
    // Menu' del cliente
    // ------------------------------------------------------------------

    /**
     * Mostra il menu' del cliente autenticato, con le funzionalita'
     * richieste dalla specifica: ricerca e visualizzazione delle
     * proiezioni, inserimento, visualizzazione, modifica e
     * cancellazione delle proprie prenotazioni, logout.
     *
     * @param cliente il cliente autenticato
     */
    private void menuCliente(Utente cliente) {
        boolean logout = false;
        while (!logout) {
            System.out.println();
            System.out.println("--- Menu' cliente (" + cliente.getUsername() + ") ---");
            System.out.println("1) Cerca proiezioni");
            System.out.println("2) Visualizza i dettagli di una proiezione");
            System.out.println("3) Inserisci una prenotazione");
            System.out.println("4) Visualizza le tue prenotazioni");
            System.out.println("5) Modifica una prenotazione (cambio data)");
            System.out.println("6) Cancella una prenotazione");
            System.out.println("0) Logout");
            int scelta = leggiIntero("Scelta: ", 0, 6);
            switch (scelta) {
                case 1 -> cercaProiezioni();
                case 2 -> visualizzaProiezione();
                case 3 -> creaPrenotazione(cliente);
                case 4 -> visualizzaPrenotazioniCliente(cliente);
                case 5 -> modificaPrenotazione(cliente);
                case 6 -> eliminaPrenotazione(cliente);
                default -> logout = true;
            }
        }
        System.out.println("Logout effettuato.");
    }

    /**
     * Realizza la funzionalita' <code>creaPrenotazione()</code>: dopo
     * la ricerca, il cliente seleziona una proiezione e indica il
     * numero di posti; la prenotazione viene creata solo se i posti
     * richiesti non superano quelli disponibili e riceve un codice
     * univoco, che viene comunicato al cliente.
     *
     * @param cliente il cliente autenticato
     */
    private void creaPrenotazione(Utente cliente) {
        System.out.println();
        System.out.println("--- Nuova prenotazione ---");
        System.out.println("Cerca prima la proiezione da prenotare.");
        List<Proiezione> trovate = cercaProiezioni();
        if (trovate.isEmpty()) {
            return;
        }
        Proiezione p = selezionaProiezione();
        if (p == null) {
            return;
        }
        int liberi = gestorePrenotazioni.postiLiberi(p.getDataOra());
        System.out.println("Posti liberi per questa proiezione: " + liberi);
        if (liberi <= 0) {
            System.out.println("Spiacenti, la proiezione e' al completo.");
            return;
        }
        int posti = leggiIntero("Numero di posti da prenotare: ", 1,
                GestorePrenotazioni.CAPIENZA_SALA);
        try {
            Prenotazione nuova =
                    gestorePrenotazioni.creaPrenotazione(cliente.getUsername(), p, posti);
            if (nuova == null) {
                System.out.println("Prenotazione non effettuata: i posti richiesti ("
                        + posti + ") superano quelli disponibili (" + liberi + ").");
            } else {
                System.out.println("Prenotazione effettuata con successo!");
                System.out.println("Codice prenotazione: " + nuova.getCodice());
                System.out.println("Costo totale: "
                        + String.format("%.2f", nuova.getCostoTotale()) + " EUR");
            }
        } catch (IOException e) {
            System.out.println("Errore nel salvataggio delle prenotazioni: "
                    + e.getMessage());
        }
    }

    /**
     * Mostra al cliente l'elenco delle proprie prenotazioni
     * (funzionalita' <code>visualizzaPrenotazione()</code> lato
     * cliente).
     *
     * @param cliente il cliente autenticato
     * @return la lista delle prenotazioni del cliente
     */
    private List<Prenotazione> visualizzaPrenotazioniCliente(Utente cliente) {
        List<Prenotazione> mie =
                gestorePrenotazioni.prenotazioniDiCliente(cliente.getUsername());
        System.out.println();
        if (mie.isEmpty()) {
            System.out.println("Non hai prenotazioni.");
        } else {
            System.out.println("Le tue prenotazioni:");
            for (Prenotazione p : mie) {
                System.out.println("  " + p);
            }
        }
        return mie;
    }

    /**
     * Realizza la funzionalita' <code>modificaPrenotazione()</code>:
     * il cliente sceglie una propria prenotazione tramite il codice e
     * la sposta su una proiezione futura dello stesso film; come da
     * specifica sia la vecchia sia la nuova data devono essere
     * successive alla data odierna.
     *
     * @param cliente il cliente autenticato
     */
    private void modificaPrenotazione(Utente cliente) {
        List<Prenotazione> mie = visualizzaPrenotazioniCliente(cliente);
        if (mie.isEmpty()) {
            return;
        }
        Prenotazione scelta = selezionaPrenotazioneDelCliente(mie);
        if (scelta == null) {
            return;
        }
        Proiezione attuale =
                gestoreProiezioni.trovaPerDataOra(scelta.getDataOraProiezione());
        if (attuale == null) {
            System.out.println("La proiezione prenotata non e' piu' in palinsesto.");
            return;
        }
        System.out.println("Proiezioni alternative di \""
                + attuale.getFilm().getTitolo() + "\":");
        List<Proiezione> alternative = gestoreProiezioni.cercaProiezione(
                attuale.getFilm().getTitolo(), null,
                LocalDate.now().plusDays(1), null, null, null);
        alternative.removeIf(p -> p.getDataOra().equals(attuale.getDataOra()));
        if (alternative.isEmpty()) {
            System.out.println("Non esistono altre proiezioni future di questo film.");
            return;
        }
        stampaElencoProiezioni(alternative);
        Proiezione nuova = selezionaProiezione();
        if (nuova == null) {
            return;
        }
        if (!nuova.getFilm().getTitolo()
                .equalsIgnoreCase(attuale.getFilm().getTitolo())) {
            System.out.println("La nuova proiezione deve riguardare lo stesso film.");
            return;
        }
        try {
            if (gestorePrenotazioni.modificaPrenotazione(scelta, nuova)) {
                System.out.println("Prenotazione aggiornata alla proiezione del "
                        + nuova.getDataOra().format(Proiezione.FORMATO_VIDEO) + ".");
            } else {
                System.out.println("Modifica non consentita: entrambe le date devono "
                        + "essere successive a oggi e la nuova proiezione deve avere "
                        + "posti sufficienti.");
            }
        } catch (IOException e) {
            System.out.println("Errore nel salvataggio delle prenotazioni: "
                    + e.getMessage());
        }
    }

    /**
     * Realizza la funzionalita' <code>eliminaPrenotazione()</code>:
     * il cliente sceglie una propria prenotazione tramite il codice e
     * la cancella, purche' la proiezione non sia gia' avvenuta.
     *
     * @param cliente il cliente autenticato
     */
    private void eliminaPrenotazione(Utente cliente) {
        List<Prenotazione> mie = visualizzaPrenotazioniCliente(cliente);
        if (mie.isEmpty()) {
            return;
        }
        Prenotazione scelta = selezionaPrenotazioneDelCliente(mie);
        if (scelta == null) {
            return;
        }
        String conferma =
                leggiTesto("Confermi la cancellazione di " + scelta.getCodice()
                        + "? (s/n): ");
        if (!conferma.equalsIgnoreCase("s")) {
            System.out.println("Cancellazione annullata.");
            return;
        }
        try {
            if (gestorePrenotazioni.eliminaPrenotazione(scelta)) {
                System.out.println("Prenotazione cancellata.");
            } else {
                System.out.println("Cancellazione non consentita: la proiezione "
                        + "e' gia' avvenuta.");
            }
        } catch (IOException e) {
            System.out.println("Errore nel salvataggio delle prenotazioni: "
                    + e.getMessage());
        }
    }

    /**
     * Chiede al cliente il codice di una delle proprie prenotazioni e
     * la restituisce, verificando che appartenga effettivamente al
     * cliente.
     *
     * @param mie l'elenco delle prenotazioni del cliente
     * @return la prenotazione selezionata, oppure <code>null</code>
     *         se l'utente rinuncia
     */
    private Prenotazione selezionaPrenotazioneDelCliente(List<Prenotazione> mie) {
        while (true) {
            String codice = leggiTestoOpzionale(
                    "Codice della prenotazione (INVIO per annullare): ");
            if (codice == null) {
                return null;
            }
            for (Prenotazione p : mie) {
                if (p.getCodice().equalsIgnoreCase(codice)) {
                    return p;
                }
            }
            System.out.println("Codice non trovato tra le tue prenotazioni. Riprovare.");
        }
    }

    // ------------------------------------------------------------------
    // Menu' del proiezionista
    // ------------------------------------------------------------------

    /**
     * Mostra il menu' del proiezionista autenticato, con le
     * funzionalita' di gestione del palinsesto richieste dalla
     * specifica: inserimento di un film con data e costo della
     * proiezione, modifica della data di una proiezione,
     * cancellazione di una proiezione, logout.
     *
     * @param proiezionista il proiezionista autenticato
     */
    private void menuProiezionista(Utente proiezionista) {
        boolean logout = false;
        while (!logout) {
            System.out.println();
            System.out.println("--- Menu' proiezionista ("
                    + proiezionista.getUsername() + ") ---");
            System.out.println("1) Cerca proiezioni");
            System.out.println("2) Visualizza i dettagli di una proiezione");
            System.out.println("3) Aggiungi una proiezione (film, data e costo)");
            System.out.println("4) Modifica la data di una proiezione");
            System.out.println("5) Elimina una proiezione");
            System.out.println("0) Logout");
            int scelta = leggiIntero("Scelta: ", 0, 5);
            switch (scelta) {
                case 1 -> cercaProiezioni();
                case 2 -> visualizzaProiezione();
                case 3 -> aggiungiProiezione();
                case 4 -> modificaProiezione();
                case 5 -> eliminaProiezione();
                default -> logout = true;
            }
        }
        System.out.println("Logout effettuato.");
    }

    /**
     * Realizza la funzionalita' <code>aggiungiProiezione()</code>: il
     * proiezionista inserisce i dati del film e, successivamente, la
     * data e il costo del biglietto della proiezione; l'aggiunta e'
     * consentita solo se la proiezione non si sovrappone ad una
     * proiezione esistente.
     */
    private void aggiungiProiezione() {
        System.out.println();
        System.out.println("--- Aggiunta di una proiezione ---");
        System.out.println("Dati del film:");
        String titolo = leggiTesto("  Titolo: ");
        String genere = leggiTesto("  Genere: ");
        String regista = leggiTesto("  Regista: ");
        int anno = leggiIntero("  Anno: ", 1888, 2100);
        int durata = leggiIntero("  Durata (minuti): ", 1, 1000);
        int etaMinima = leggiIntero("  Eta' minima (0 = per tutti): ", 0, 18);
        Film film = new Film(titolo, genere, regista, anno, durata, etaMinima);
        System.out.println("Dati della proiezione:");
        LocalDate data = leggiData("  Data (gg/mm/aaaa): ");
        LocalTime ora = leggiOra("  Ora di inizio (hh:mm): ");
        double prezzo = leggiDouble("  Costo del biglietto (EUR): ");
        Proiezione nuova =
                new Proiezione(LocalDateTime.of(data, ora), film, prezzo);
        try {
            if (gestoreProiezioni.aggiungiProiezione(nuova)) {
                System.out.println("Proiezione aggiunta al palinsesto.");
            } else {
                System.out.println("Aggiunta non consentita: la proiezione si "
                        + "sovrappone ad una proiezione esistente.");
            }
        } catch (IOException e) {
            System.out.println("Errore nel salvataggio delle proiezioni: "
                    + e.getMessage());
        }
    }

    /**
     * Realizza la funzionalita' <code>modificaProiezione()</code>: il
     * proiezionista seleziona una proiezione e ne cambia la data e
     * ora; come da specifica la modifica e' consentita solo se non ci
     * sono prenotazioni per quella proiezione, e la nuova collocazione
     * non deve sovrapporsi ad altre proiezioni.
     */
    private void modificaProiezione() {
        System.out.println();
        System.out.println("--- Modifica della data di una proiezione ---");
        Proiezione p = selezionaProiezione();
        if (p == null) {
            return;
        }
        if (gestorePrenotazioni.esistonoPrenotazioniPer(p.getDataOra())) {
            System.out.println("Modifica non consentita: esistono prenotazioni "
                    + "per questa proiezione.");
            return;
        }
        LocalDate data = leggiData("Nuova data (gg/mm/aaaa): ");
        LocalTime ora = leggiOra("Nuova ora di inizio (hh:mm): ");
        try {
            if (gestoreProiezioni.modificaProiezione(p, LocalDateTime.of(data, ora))) {
                System.out.println("Proiezione aggiornata.");
            } else {
                System.out.println("Modifica non consentita: la nuova collocazione "
                        + "si sovrappone ad una proiezione esistente.");
            }
        } catch (IOException e) {
            System.out.println("Errore nel salvataggio delle proiezioni: "
                    + e.getMessage());
        }
    }

    /**
     * Realizza la funzionalita' <code>eliminaProiezione()</code>: il
     * proiezionista seleziona una proiezione e la elimina dal
     * palinsesto; come da specifica la cancellazione e' consentita
     * solo se non ci sono prenotazioni per quella proiezione.
     */
    private void eliminaProiezione() {
        System.out.println();
        System.out.println("--- Eliminazione di una proiezione ---");
        Proiezione p = selezionaProiezione();
        if (p == null) {
            return;
        }
        if (gestorePrenotazioni.esistonoPrenotazioniPer(p.getDataOra())) {
            System.out.println("Eliminazione non consentita: esistono prenotazioni "
                    + "per questa proiezione.");
            return;
        }
        String conferma = leggiTesto("Confermi l'eliminazione della proiezione \""
                + p.getFilm().getTitolo() + "\" del "
                + p.getDataOra().format(Proiezione.FORMATO_VIDEO) + "? (s/n): ");
        if (!conferma.equalsIgnoreCase("s")) {
            System.out.println("Eliminazione annullata.");
            return;
        }
        try {
            gestoreProiezioni.eliminaProiezione(p);
            System.out.println("Proiezione eliminata dal palinsesto.");
        } catch (IOException e) {
            System.out.println("Errore nel salvataggio delle proiezioni: "
                    + e.getMessage());
        }
    }

    // ------------------------------------------------------------------
    // Menu' del bigliettaio
    // ------------------------------------------------------------------

    /**
     * Mostra il menu' del bigliettaio autenticato, con le
     * funzionalita' richieste dalla specifica: visualizzazione delle
     * prenotazioni nella data odierna, ricerca di una prenotazione,
     * visualizzazione dei dettagli di una prenotazione, logout.
     *
     * @param bigliettaio il bigliettaio autenticato
     */
    private void menuBigliettaio(Utente bigliettaio) {
        boolean logout = false;
        while (!logout) {
            System.out.println();
            System.out.println("--- Menu' bigliettaio ("
                    + bigliettaio.getUsername() + ") ---");
            System.out.println("1) Visualizza le prenotazioni di oggi");
            System.out.println("2) Cerca una prenotazione");
            System.out.println("3) Visualizza i dettagli di una prenotazione (per codice)");
            System.out.println("0) Logout");
            int scelta = leggiIntero("Scelta: ", 0, 3);
            switch (scelta) {
                case 1 -> visualizzaPrenotazioniOdierne();
                case 2 -> cercaPrenotazioni();
                case 3 -> visualizzaPrenotazionePerCodice();
                default -> logout = true;
            }
        }
        System.out.println("Logout effettuato.");
    }

    /**
     * Mostra al bigliettaio l'elenco delle prenotazioni relative a
     * proiezioni che si svolgono nella data odierna.
     */
    private void visualizzaPrenotazioniOdierne() {
        List<Prenotazione> odierne = gestorePrenotazioni.prenotazioniOdierne();
        System.out.println();
        if (odierne.isEmpty()) {
            System.out.println("Nessuna prenotazione per le proiezioni di oggi.");
        } else {
            System.out.println("Prenotazioni per le proiezioni di oggi:");
            for (Prenotazione p : odierne) {
                System.out.println("  " + p + "  (cliente: "
                        + p.getUsernameCliente() + ")");
            }
        }
    }

    /**
     * Realizza la funzionalita' <code>cercaPrenotazione()</code> del
     * bigliettaio: e' possibile cercare per codice, per nome e
     * cognome del cliente, per titolo (anche parziale) del film e per
     * intervallo di date; ogni criterio puo' essere saltato premendo
     * INVIO. Al termine viene stampato l'elenco delle prenotazioni
     * trovate.
     */
    private void cercaPrenotazioni() {
        System.out.println();
        System.out.println("--- Ricerca prenotazioni ---");
        System.out.println("(premere INVIO per saltare un criterio)");
        String codice = leggiTestoOpzionale("Codice prenotazione: ");
        if (codice != null) {
            Prenotazione p = gestorePrenotazioni.cercaPerCodice(codice);
            if (p == null) {
                System.out.println("Nessuna prenotazione con codice " + codice + ".");
            } else {
                stampaDettagliPrenotazione(p);
            }
            return;
        }
        String nome = leggiTestoOpzionale("Nome del cliente: ");
        String cognome = null;
        List<String> username = null;
        if (nome != null) {
            cognome = leggiTesto("Cognome del cliente: ");
            username = gestoreUtenti.usernamePerNomeCognome(nome, cognome);
            if (username.isEmpty()) {
                System.out.println("Nessun cliente di nome "
                        + nome + " " + cognome + ".");
                return;
            }
        }
        String titolo = leggiTestoOpzionale("Titolo (anche parziale) del film: ");
        LocalDate dataDa = leggiDataOpzionale("A partire dal giorno (gg/mm/aaaa): ");
        LocalDate dataA = leggiDataOpzionale("Fino al giorno (gg/mm/aaaa): ");
        List<Prenotazione> trovate = gestorePrenotazioni.cercaPrenotazione(
                username, titolo, dataDa, dataA, gestoreProiezioni);
        System.out.println();
        if (trovate.isEmpty()) {
            System.out.println("Nessuna prenotazione soddisfa i criteri indicati.");
        } else {
            System.out.println("Prenotazioni trovate: " + trovate.size());
            for (Prenotazione p : trovate) {
                System.out.println("  " + p + "  (cliente: "
                        + p.getUsernameCliente() + ")");
            }
        }
    }

    /**
     * Realizza la funzionalita' <code>visualizzaPrenotazione()</code>
     * del bigliettaio: dopo la ricerca, il bigliettaio seleziona una
     * prenotazione tramite il codice e ne visualizza tutti i dettagli.
     */
    private void visualizzaPrenotazionePerCodice() {
        String codice = leggiTesto("Codice della prenotazione: ");
        Prenotazione p = gestorePrenotazioni.cercaPerCodice(codice);
        if (p == null) {
            System.out.println("Nessuna prenotazione con codice " + codice + ".");
        } else {
            stampaDettagliPrenotazione(p);
        }
    }

    /**
     * Stampa a terminale i dettagli completi di una prenotazione:
     * codice, nome e cognome del cliente, data e ora della proiezione,
     * numero di biglietti, costo unitario e costo totale.
     *
     * @param p la prenotazione da visualizzare
     */
    private void stampaDettagliPrenotazione(Prenotazione p) {
        Utente cliente = gestoreUtenti.trovaPerUsername(p.getUsernameCliente());
        String nominativo = (cliente == null)
                ? p.getUsernameCliente()
                : cliente.getNome() + " " + cliente.getCognome();
        Proiezione proiezione =
                gestoreProiezioni.trovaPerDataOra(p.getDataOraProiezione());
        System.out.println();
        System.out.println("--- Dettagli della prenotazione ---");
        System.out.println("Codice:             " + p.getCodice());
        System.out.println("Cliente:            " + nominativo);
        System.out.println("Data e ora:         "
                + p.getDataOraProiezione().format(Proiezione.FORMATO_VIDEO));
        if (proiezione != null) {
            System.out.println("Film:               "
                    + proiezione.getFilm().getTitolo());
        }
        System.out.println("Numero biglietti:   " + p.getNumeroPosti());
        System.out.println("Costo unitario:     "
                + String.format("%.2f", p.getCostoUnitario()) + " EUR");
        System.out.println("Costo totale:       "
                + String.format("%.2f", p.getCostoTotale()) + " EUR");
    }

    // ------------------------------------------------------------------
    // Funzioni di lettura e validazione dell'input da tastiera
    // ------------------------------------------------------------------

    /**
     * Legge da tastiera una riga di testo non vuota, ripetendo la
     * richiesta finche' l'utente non inserisce almeno un carattere.
     *
     * @param messaggio il messaggio da mostrare all'utente
     * @return il testo inserito, privato degli spazi iniziali e finali
     */
    private String leggiTesto(String messaggio) {
        while (true) {
            System.out.print(messaggio);
            String riga = tastiera.nextLine().trim();
            if (!riga.isEmpty()) {
                return riga;
            }
            System.out.println("Il valore non puo' essere vuoto.");
        }
    }

    /**
     * Legge da tastiera una riga di testo facoltativa.
     *
     * @param messaggio il messaggio da mostrare all'utente
     * @return il testo inserito, oppure <code>null</code> se l'utente
     *         ha premuto INVIO senza scrivere nulla
     */
    private String leggiTestoOpzionale(String messaggio) {
        System.out.print(messaggio);
        String riga = tastiera.nextLine().trim();
        return riga.isEmpty() ? null : riga;
    }

    /**
     * Legge da tastiera un numero intero compreso in un intervallo,
     * ripetendo la richiesta in caso di input non valido.
     *
     * @param messaggio il messaggio da mostrare all'utente
     * @param minimo    il valore minimo ammesso (incluso)
     * @param massimo   il valore massimo ammesso (incluso)
     * @return il numero intero inserito
     */
    private int leggiIntero(String messaggio, int minimo, int massimo) {
        while (true) {
            System.out.print(messaggio);
            String riga = tastiera.nextLine().trim();
            try {
                int valore = Integer.parseInt(riga);
                if (valore >= minimo && valore <= massimo) {
                    return valore;
                }
                System.out.println("Inserire un numero tra "
                        + minimo + " e " + massimo + ".");
            } catch (NumberFormatException e) {
                System.out.println("Inserire un numero intero valido.");
            }
        }
    }

    /**
     * Legge da tastiera un numero decimale non negativo, ripetendo la
     * richiesta in caso di input non valido. E' accettata sia la
     * virgola sia il punto come separatore decimale.
     *
     * @param messaggio il messaggio da mostrare all'utente
     * @return il numero decimale inserito
     */
    private double leggiDouble(String messaggio) {
        while (true) {
            System.out.print(messaggio);
            String riga = tastiera.nextLine().trim().replace(',', '.');
            try {
                double valore = Double.parseDouble(riga);
                if (valore >= 0) {
                    return valore;
                }
                System.out.println("Inserire un numero non negativo.");
            } catch (NumberFormatException e) {
                System.out.println("Inserire un numero valido.");
            }
        }
    }

    /**
     * Legge da tastiera un numero decimale facoltativo.
     *
     * @param messaggio il messaggio da mostrare all'utente
     * @return il numero inserito, oppure <code>null</code> se l'utente
     *         ha premuto INVIO senza scrivere nulla
     */
    private Double leggiDoubleOpzionale(String messaggio) {
        while (true) {
            System.out.print(messaggio);
            String riga = tastiera.nextLine().trim().replace(',', '.');
            if (riga.isEmpty()) {
                return null;
            }
            try {
                double valore = Double.parseDouble(riga);
                if (valore >= 0) {
                    return valore;
                }
                System.out.println("Inserire un numero non negativo.");
            } catch (NumberFormatException e) {
                System.out.println("Inserire un numero valido (o INVIO per saltare).");
            }
        }
    }

    /**
     * Legge da tastiera una data obbligatoria nel formato
     * <code>gg/mm/aaaa</code>, ripetendo la richiesta in caso di
     * input non valido.
     *
     * @param messaggio il messaggio da mostrare all'utente
     * @return la data inserita
     */
    private LocalDate leggiData(String messaggio) {
        while (true) {
            System.out.print(messaggio);
            String riga = tastiera.nextLine().trim();
            try {
                return LocalDate.parse(riga, FORMATO_DATA_INPUT);
            } catch (DateTimeParseException e) {
                System.out.println("Data non valida: usare il formato gg/mm/aaaa.");
            }
        }
    }

    /**
     * Legge da tastiera una data facoltativa nel formato
     * <code>gg/mm/aaaa</code>.
     *
     * @param messaggio il messaggio da mostrare all'utente
     * @return la data inserita, oppure <code>null</code> se l'utente
     *         ha premuto INVIO senza scrivere nulla
     */
    private LocalDate leggiDataOpzionale(String messaggio) {
        while (true) {
            System.out.print(messaggio);
            String riga = tastiera.nextLine().trim();
            if (riga.isEmpty()) {
                return null;
            }
            try {
                return LocalDate.parse(riga, FORMATO_DATA_INPUT);
            } catch (DateTimeParseException e) {
                System.out.println("Data non valida: usare il formato gg/mm/aaaa "
                        + "(o INVIO per saltare).");
            }
        }
    }

    /**
     * Legge da tastiera un orario nel formato <code>hh:mm</code>,
     * ripetendo la richiesta in caso di input non valido.
     *
     * @param messaggio il messaggio da mostrare all'utente
     * @return l'orario inserito
     */
    private LocalTime leggiOra(String messaggio) {
        while (true) {
            System.out.print(messaggio);
            String riga = tastiera.nextLine().trim();
            try {
                return LocalTime.parse(riga, FORMATO_ORA_INPUT);
            } catch (DateTimeParseException e) {
                System.out.println("Ora non valida: usare il formato hh:mm.");
            }
        }
    }
}

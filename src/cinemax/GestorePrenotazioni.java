/*
 * Autori del progetto:
 * D'Onofrio Greta - Matricola 766016 - Sede CO
 * Tricarico Nicolo' - Matricola 767095 - Sede CO
 */
package cinemax;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * La classe <code>GestorePrenotazioni</code> gestisce le prenotazioni
 * dei clienti: mantiene in memoria l'elenco delle {@link Prenotazione}
 * e ne cura la persistenza sul file <code>prenotazioni.csv</code>,
 * come richiesto dalla specifica ("e' necessario che l'applicazione
 * memorizzi su file i dati delle prenotazioni").
 * <p>
 * Il file ha una riga di intestazione e i seguenti campi:
 * <code>codice, username_cliente, data_ora_proiezione, numero_posti,
 * costo_unitario</code>. Il gestore genera inoltre i codici univoci
 * delle prenotazioni e calcola il numero di posti liberi di una
 * proiezione a partire dalla capienza della sala (200 posti).
 *
 * @author D'Onofrio Greta
 * @author Tricarico Nicolo'
 * @version 1.0
 */
public class GestorePrenotazioni {

    /** La capienza della sala del cinema monosala CineMax. */
    public static final int CAPIENZA_SALA = 200;

    /** Intestazione del file CSV delle prenotazioni. */
    private static final String INTESTAZIONE =
            "codice,username_cliente,data_ora_proiezione,numero_posti,costo_unitario";

    /** Caratteri ammessi nei codici di prenotazione generati. */
    private static final String CARATTERI_CODICE = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

    /** Il percorso del file CSV delle prenotazioni. */
    private final Path filePrenotazioni;

    /** L'elenco delle prenotazioni caricate in memoria. */
    private final List<Prenotazione> prenotazioni = new ArrayList<>();

    /** Generatore casuale usato per i codici di prenotazione. */
    private final Random casuale = new Random();

    /**
     * Costruisce il gestore e carica in memoria le prenotazioni dal
     * file indicato. Se il file non esiste viene creato vuoto (con la
     * sola intestazione), cosi' che l'applicazione possa essere
     * eseguita anche alla prima installazione.
     *
     * @param filePrenotazioni il percorso del file <code>prenotazioni.csv</code>
     * @throws IOException se il file non e' leggibile o creabile
     */
    public GestorePrenotazioni(Path filePrenotazioni) throws IOException {
        this.filePrenotazioni = filePrenotazioni;
        if (!Files.exists(filePrenotazioni)) {
            salva();
        }
        carica();
    }

    /**
     * Legge il file CSV delle prenotazioni e popola l'elenco in
     * memoria. Le righe non valide vengono ignorate segnalando un
     * avviso.
     *
     * @throws IOException se il file non e' leggibile
     */
    private void carica() throws IOException {
        prenotazioni.clear();
        try (BufferedReader lettore =
                     Files.newBufferedReader(filePrenotazioni, StandardCharsets.UTF_8)) {
            String riga = lettore.readLine(); // intestazione, ignorata
            while ((riga = lettore.readLine()) != null) {
                if (riga.isBlank()) {
                    continue;
                }
                try {
                    List<String> campi = CsvUtil.dividiRiga(riga);
                    prenotazioni.add(new Prenotazione(
                            campi.get(0).trim(),
                            campi.get(1).trim(),
                            LocalDateTime.parse(campi.get(2).trim(), Proiezione.FORMATO_CSV),
                            Integer.parseInt(campi.get(3).trim()),
                            Double.parseDouble(campi.get(4).trim())));
                } catch (RuntimeException e) {
                    System.out.println("[Avviso] Riga di prenotazioni.csv ignorata: " + riga);
                }
            }
        }
    }

    /**
     * Salva l'intero elenco delle prenotazioni sul file CSV,
     * sovrascrivendone il contenuto precedente.
     *
     * @throws IOException se il file non e' scrivibile
     */
    public void salva() throws IOException {
        try (PrintWriter scrittore = new PrintWriter(
                Files.newBufferedWriter(filePrenotazioni, StandardCharsets.UTF_8))) {
            scrittore.println(INTESTAZIONE);
            for (Prenotazione p : prenotazioni) {
                scrittore.println(String.join(",",
                        p.getCodice(),
                        CsvUtil.proteggi(p.getUsernameCliente()),
                        p.getDataOraProiezione().format(Proiezione.FORMATO_CSV),
                        String.valueOf(p.getNumeroPosti()),
                        String.format(Locale.US, "%.2f", p.getCostoUnitario())));
            }
        }
    }

    /**
     * Genera un nuovo codice di prenotazione univoco, nel formato
     * <code>PRN-XXXXXXXX</code>, dove le otto <code>X</code> sono
     * caratteri alfanumerici estratti casualmente. Il metodo verifica
     * che il codice non sia gia' in uso da un'altra prenotazione.
     *
     * @return il nuovo codice univoco
     */
    public String generaCodice() {
        String codice;
        do {
            StringBuilder sb = new StringBuilder("PRN-");
            for (int i = 0; i < 8; i++) {
                sb.append(CARATTERI_CODICE.charAt(
                        casuale.nextInt(CARATTERI_CODICE.length())));
            }
            codice = sb.toString();
        } while (cercaPerCodice(codice) != null);
        return codice;
    }

    /**
     * Calcola il numero di posti gia' prenotati per la proiezione che
     * inizia nella data e ora indicata.
     *
     * @param dataOraProiezione la data e ora della proiezione
     * @return il totale dei posti prenotati per quella proiezione
     */
    public int postiPrenotati(LocalDateTime dataOraProiezione) {
        int totale = 0;
        for (Prenotazione p : prenotazioni) {
            if (p.getDataOraProiezione().equals(dataOraProiezione)) {
                totale += p.getNumeroPosti();
            }
        }
        return totale;
    }

    /**
     * Calcola il numero di posti ancora liberi per una proiezione,
     * ricavato dalla capienza della sala (200 posti) e dal numero di
     * posti gia' prenotati, come richiesto dalla specifica.
     *
     * @param dataOraProiezione la data e ora della proiezione
     * @return il numero di posti liberi
     */
    public int postiLiberi(LocalDateTime dataOraProiezione) {
        return CAPIENZA_SALA - postiPrenotati(dataOraProiezione);
    }

    /**
     * Verifica se esistono prenotazioni per la proiezione indicata.
     * E' usato dal proiezionista, che puo' modificare o eliminare una
     * proiezione solo se questa non ha prenotazioni.
     *
     * @param dataOraProiezione la data e ora della proiezione
     * @return <code>true</code> se esiste almeno una prenotazione
     */
    public boolean esistonoPrenotazioniPer(LocalDateTime dataOraProiezione) {
        return postiPrenotati(dataOraProiezione) > 0;
    }

    /**
     * Crea una nuova prenotazione per la proiezione indicata
     * (funzionalita' <code>creaPrenotazione()</code> della specifica),
     * a patto che il numero di posti richiesti non superi il numero di
     * posti disponibili. Al momento della creazione viene generato un
     * codice univoco, memorizzato insieme ai dati della prenotazione.
     *
     * @param usernameCliente lo username del cliente
     * @param proiezione      la proiezione da prenotare
     * @param numeroPosti     il numero di posti richiesti (almeno 1)
     * @return la prenotazione creata, oppure <code>null</code> se i
     *         posti richiesti superano quelli disponibili o non sono validi
     * @throws IOException se il salvataggio su file fallisce
     */
    public Prenotazione creaPrenotazione(String usernameCliente,
                                         Proiezione proiezione,
                                         int numeroPosti) throws IOException {
        if (numeroPosti < 1
                || numeroPosti > postiLiberi(proiezione.getDataOra())) {
            return null;
        }
        Prenotazione nuova = new Prenotazione(generaCodice(), usernameCliente,
                proiezione.getDataOra(), numeroPosti,
                proiezione.getPrezzoBiglietto());
        prenotazioni.add(nuova);
        salva();
        return nuova;
    }

    /**
     * Modifica una prenotazione spostandola su una proiezione con una
     * diversa data (funzionalita' <code>modificaPrenotazione()</code>).
     * Come da specifica la modifica e' ammessa solo se sia la vecchia
     * sia la nuova data sono successive alla data odierna; inoltre la
     * nuova proiezione deve avere posti liberi sufficienti.
     *
     * @param prenotazione    la prenotazione da modificare
     * @param nuovaProiezione la proiezione di destinazione
     * @return <code>true</code> se la modifica e' andata a buon fine,
     *         <code>false</code> se i vincoli sulle date o sui posti
     *         non sono rispettati
     * @throws IOException se il salvataggio su file fallisce
     */
    public boolean modificaPrenotazione(Prenotazione prenotazione,
                                        Proiezione nuovaProiezione) throws IOException {
        LocalDate oggi = LocalDate.now();
        if (!prenotazione.getDataOraProiezione().toLocalDate().isAfter(oggi)
                || !nuovaProiezione.getDataOra().toLocalDate().isAfter(oggi)) {
            return false;
        }
        if (prenotazione.getNumeroPosti()
                > postiLiberi(nuovaProiezione.getDataOra())) {
            return false;
        }
        prenotazione.setDataOraProiezione(nuovaProiezione.getDataOra());
        salva();
        return true;
    }

    /**
     * Cancella una prenotazione inserita in precedenza
     * (funzionalita' <code>eliminaPrenotazione()</code>). La
     * cancellazione e' ammessa solo se la data della proiezione e'
     * successiva alla data odierna: non e' possibile annullare una
     * prenotazione relativa ad una proiezione gia' avvenuta.
     *
     * @param prenotazione la prenotazione da cancellare
     * @return <code>true</code> se la cancellazione e' andata a buon
     *         fine, <code>false</code> se la proiezione e' gia' passata
     * @throws IOException se il salvataggio su file fallisce
     */
    public boolean eliminaPrenotazione(Prenotazione prenotazione) throws IOException {
        if (!prenotazione.getDataOraProiezione().toLocalDate()
                .isAfter(LocalDate.now())) {
            return false;
        }
        prenotazioni.remove(prenotazione);
        salva();
        return true;
    }

    /**
     * Restituisce tutte le prenotazioni effettuate dal cliente
     * indicato (funzionalita' <code>visualizzaPrenotazione()</code>
     * lato cliente).
     *
     * @param usernameCliente lo username del cliente
     * @return la lista delle prenotazioni del cliente
     */
    public List<Prenotazione> prenotazioniDiCliente(String usernameCliente) {
        List<Prenotazione> risultato = new ArrayList<>();
        for (Prenotazione p : prenotazioni) {
            if (p.getUsernameCliente().equals(usernameCliente)) {
                risultato.add(p);
            }
        }
        return risultato;
    }

    /**
     * Restituisce tutte le prenotazioni relative a proiezioni che si
     * svolgono nella data odierna (funzionalita' del bigliettaio
     * "visualizzare le prenotazioni nella data odierna").
     *
     * @return la lista delle prenotazioni odierne
     */
    public List<Prenotazione> prenotazioniOdierne() {
        LocalDate oggi = LocalDate.now();
        List<Prenotazione> risultato = new ArrayList<>();
        for (Prenotazione p : prenotazioni) {
            if (p.getDataOraProiezione().toLocalDate().equals(oggi)) {
                risultato.add(p);
            }
        }
        return risultato;
    }

    /**
     * Cerca una prenotazione per codice.
     *
     * @param codice il codice della prenotazione cercata
     * @return la prenotazione trovata, oppure <code>null</code> se non esiste
     */
    public Prenotazione cercaPerCodice(String codice) {
        for (Prenotazione p : prenotazioni) {
            if (p.getCodice().equalsIgnoreCase(codice)) {
                return p;
            }
        }
        return null;
    }

    /**
     * Cerca le prenotazioni che soddisfano tutti i criteri indicati
     * (funzionalita' <code>cercaPrenotazione()</code> del bigliettaio).
     * Ogni criterio e' facoltativo: passando <code>null</code> il
     * criterio viene ignorato.
     *
     * @param usernameClienti la lista degli username ammessi (ricavata
     *                        dal nome e cognome del cliente), oppure
     *                        <code>null</code> per non filtrare
     * @param titoloParziale  titolo (anche parziale) del film, oppure
     *                        <code>null</code>; richiede il gestore
     *                        delle proiezioni per risalire al film
     * @param dataDa          data minima (inclusa) della proiezione,
     *                        oppure <code>null</code>
     * @param dataA           data massima (inclusa) della proiezione,
     *                        oppure <code>null</code>
     * @param proiezioni      il gestore delle proiezioni, usato per
     *                        risalire al titolo del film prenotato
     * @return la lista delle prenotazioni che soddisfano tutti i criteri
     */
    public List<Prenotazione> cercaPrenotazione(List<String> usernameClienti,
                                                String titoloParziale,
                                                LocalDate dataDa, LocalDate dataA,
                                                GestoreProiezioni proiezioni) {
        List<Prenotazione> risultato = new ArrayList<>();
        for (Prenotazione p : prenotazioni) {
            if (usernameClienti != null
                    && !usernameClienti.contains(p.getUsernameCliente())) {
                continue;
            }
            LocalDate data = p.getDataOraProiezione().toLocalDate();
            if (dataDa != null && data.isBefore(dataDa)) {
                continue;
            }
            if (dataA != null && data.isAfter(dataA)) {
                continue;
            }
            if (titoloParziale != null) {
                Proiezione proiezione =
                        proiezioni.trovaPerDataOra(p.getDataOraProiezione());
                if (proiezione == null || !proiezione.getFilm().getTitolo()
                        .toLowerCase().contains(titoloParziale.toLowerCase())) {
                    continue;
                }
            }
            risultato.add(p);
        }
        return risultato;
    }
}

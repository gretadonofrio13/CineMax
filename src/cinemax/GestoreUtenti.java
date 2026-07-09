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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * La classe <code>GestoreUtenti</code> gestisce gli utenti registrati
 * della piattaforma: mantiene in memoria l'elenco degli {@link Utente}
 * e ne cura la persistenza sul file <code>utenti.csv</code>.
 * <p>
 * Il file ha una riga di intestazione e i seguenti campi:
 * <code>nome, cognome, username, password_hash, data_nascita,
 * domicilio, ruolo</code>. Il campo <code>data_nascita</code> e'
 * facoltativo e puo' essere vuoto; la password e' memorizzata come
 * hash SHA-256 (si veda {@link Sicurezza}). Come da specifica, il
 * file iniziale contiene gia' 2 proiezionisti e 5 bigliettai.
 *
 * @author D'Onofrio Greta
 * @author Tricarico Nicolo'
 * @version 1.0
 */
public class GestoreUtenti {

    /** Intestazione del file CSV degli utenti. */
    private static final String INTESTAZIONE =
            "nome,cognome,username,password_hash,data_nascita,domicilio,ruolo";

    /** Formato ISO della data di nascita nel file (es. 2001-05-20). */
    private static final DateTimeFormatter FORMATO_DATA =
            DateTimeFormatter.ISO_LOCAL_DATE;

    /** Il percorso del file CSV degli utenti. */
    private final Path fileUtenti;

    /** Gli utenti caricati in memoria, indicizzati per username. */
    private final Map<String, Utente> utenti = new LinkedHashMap<>();

    /**
     * Costruisce il gestore e carica in memoria gli utenti dal file
     * indicato.
     *
     * @param fileUtenti il percorso del file <code>utenti.csv</code>
     * @throws IOException se il file non esiste o non e' leggibile
     */
    public GestoreUtenti(Path fileUtenti) throws IOException {
        this.fileUtenti = fileUtenti;
        carica();
    }

    /**
     * Legge il file CSV degli utenti e popola la mappa in memoria.
     * Le righe non valide vengono ignorate segnalando un avviso.
     *
     * @throws IOException se il file non e' leggibile
     */
    private void carica() throws IOException {
        utenti.clear();
        try (BufferedReader lettore =
                     Files.newBufferedReader(fileUtenti, StandardCharsets.UTF_8)) {
            String riga = lettore.readLine(); // intestazione, ignorata
            while ((riga = lettore.readLine()) != null) {
                if (riga.isBlank()) {
                    continue;
                }
                try {
                    List<String> campi = CsvUtil.dividiRiga(riga);
                    LocalDate nascita = campi.get(4).trim().isEmpty()
                            ? null
                            : LocalDate.parse(campi.get(4).trim(), FORMATO_DATA);
                    Utente u = new Utente(
                            campi.get(0).trim(),
                            campi.get(1).trim(),
                            campi.get(2).trim(),
                            campi.get(3).trim(),
                            nascita,
                            campi.get(5).trim(),
                            Ruolo.daStringa(campi.get(6)));
                    utenti.put(u.getUsername(), u);
                } catch (RuntimeException e) {
                    System.out.println("[Avviso] Riga di utenti.csv ignorata: " + riga);
                }
            }
        }
    }

    /**
     * Salva l'intero elenco degli utenti sul file CSV,
     * sovrascrivendone il contenuto precedente.
     *
     * @throws IOException se il file non e' scrivibile
     */
    public void salva() throws IOException {
        try (PrintWriter scrittore = new PrintWriter(
                Files.newBufferedWriter(fileUtenti, StandardCharsets.UTF_8))) {
            scrittore.println(INTESTAZIONE);
            for (Utente u : utenti.values()) {
                scrittore.println(String.join(",",
                        CsvUtil.proteggi(u.getNome()),
                        CsvUtil.proteggi(u.getCognome()),
                        CsvUtil.proteggi(u.getUsername()),
                        u.getPasswordHash(),
                        u.getDataNascita() == null
                                ? "" : u.getDataNascita().format(FORMATO_DATA),
                        CsvUtil.proteggi(u.getDomicilio()),
                        u.getRuolo().name().toLowerCase()));
            }
        }
    }

    /**
     * Verifica se uno username risulta gia' registrato.
     *
     * @param username lo username da verificare
     * @return <code>true</code> se lo username e' gia' in uso
     */
    public boolean esisteUsername(String username) {
        return utenti.containsKey(username);
    }

    /**
     * Effettua il login di un utente: verifica che lo username esista
     * e che la password fornita sia corretta.
     *
     * @param username lo username inserito
     * @param password la password in chiaro inserita
     * @return l'utente autenticato, oppure <code>null</code> se le
     *         credenziali non sono valide
     */
    public Utente login(String username, String password) {
        Utente u = utenti.get(username);
        if (u != null && u.verificaPassword(password)) {
            return u;
        }
        return null;
    }

    /**
     * Registra un nuovo cliente sulla piattaforma
     * (funzionalita' <code>registraCliente()</code> della specifica)
     * e aggiorna il file degli utenti. La password viene cifrata con
     * SHA-256 prima della memorizzazione.
     *
     * @param nome        il nome del cliente
     * @param cognome     il cognome del cliente
     * @param username    lo username scelto (deve essere libero)
     * @param password    la password in chiaro scelta
     * @param dataNascita la data di nascita, oppure <code>null</code> (facoltativa)
     * @param domicilio   il luogo del domicilio
     * @return il nuovo utente registrato, oppure <code>null</code>
     *         se lo username risulta gia' in uso
     * @throws IOException se il salvataggio su file fallisce
     */
    public Utente registraCliente(String nome, String cognome, String username,
                                  String password, LocalDate dataNascita,
                                  String domicilio) throws IOException {
        if (esisteUsername(username)) {
            return null;
        }
        Utente nuovo = new Utente(nome, cognome, username,
                Sicurezza.sha256(password), dataNascita, domicilio, Ruolo.CLIENTE);
        utenti.put(username, nuovo);
        salva();
        return nuovo;
    }

    /**
     * Restituisce gli username dei clienti il cui nome e cognome
     * coincidono (senza distinzione tra maiuscole e minuscole) con
     * quelli indicati. E' usato dal bigliettaio per la ricerca delle
     * prenotazioni per nome e cognome del cliente.
     *
     * @param nome    il nome del cliente cercato
     * @param cognome il cognome del cliente cercato
     * @return la lista (eventualmente vuota) degli username corrispondenti
     */
    public List<String> usernamePerNomeCognome(String nome, String cognome) {
        List<String> risultato = new ArrayList<>();
        for (Utente u : utenti.values()) {
            if (u.getNome().equalsIgnoreCase(nome)
                    && u.getCognome().equalsIgnoreCase(cognome)) {
                risultato.add(u.getUsername());
            }
        }
        return risultato;
    }

    /**
     * Restituisce l'utente con lo username indicato.
     *
     * @param username lo username cercato
     * @return l'utente corrispondente, oppure <code>null</code> se non esiste
     */
    public Utente trovaPerUsername(String username) {
        return utenti.get(username);
    }
}

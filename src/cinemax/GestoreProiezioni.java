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
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * La classe <code>GestoreProiezioni</code> gestisce il palinsesto del
 * cinema: mantiene in memoria l'elenco delle {@link Proiezione} e ne
 * cura la persistenza sul file <code>proiezioni.csv</code>.
 * <p>
 * Il file ha una riga di intestazione e i seguenti campi:
 * <code>data_ora_proiezione, titolo_film, genere, regista, anno,
 * durata_minuti, eta_minima, prezzo_biglietto</code>.
 * Le proiezioni sono mantenute ordinate per data e ora crescente.
 *
 * @author D'Onofrio Greta
 * @author Tricarico Nicolo'
 * @version 1.0
 */
public class GestoreProiezioni {

    /** Intestazione del file CSV delle proiezioni. */
    private static final String INTESTAZIONE =
            "data_ora_proiezione,titolo_film,genere,regista,anno,"
                    + "durata_minuti,eta_minima,prezzo_biglietto";

    /** Il percorso del file CSV delle proiezioni. */
    private final Path fileProiezioni;

    /** L'elenco delle proiezioni caricate in memoria. */
    private final List<Proiezione> proiezioni = new ArrayList<>();

    /**
     * Costruisce il gestore e carica in memoria le proiezioni dal
     * file indicato.
     *
     * @param fileProiezioni il percorso del file <code>proiezioni.csv</code>
     * @throws IOException se il file non esiste o non e' leggibile
     */
    public GestoreProiezioni(Path fileProiezioni) throws IOException {
        this.fileProiezioni = fileProiezioni;
        carica();
    }

    /**
     * Legge il file CSV delle proiezioni e popola l'elenco in
     * memoria. Le righe non valide vengono ignorate segnalando un
     * avviso a terminale, in modo che un singolo record corrotto non
     * impedisca l'avvio dell'applicazione.
     *
     * @throws IOException se il file non e' leggibile
     */
    private void carica() throws IOException {
        proiezioni.clear();
        try (BufferedReader lettore =
                     Files.newBufferedReader(fileProiezioni, StandardCharsets.UTF_8)) {
            String riga = lettore.readLine(); // intestazione, ignorata
            while ((riga = lettore.readLine()) != null) {
                if (riga.isBlank()) {
                    continue;
                }
                try {
                    List<String> campi = CsvUtil.dividiRiga(riga);
                    LocalDateTime dataOra =
                            LocalDateTime.parse(campi.get(0).trim(), Proiezione.FORMATO_CSV);
                    Film film = new Film(
                            campi.get(1).trim(),
                            campi.get(2).trim(),
                            campi.get(3).trim(),
                            Integer.parseInt(campi.get(4).trim()),
                            Integer.parseInt(campi.get(5).trim()),
                            Integer.parseInt(campi.get(6).trim()));
                    double prezzo = Double.parseDouble(campi.get(7).trim());
                    proiezioni.add(new Proiezione(dataOra, film, prezzo));
                } catch (RuntimeException e) {
                    System.out.println("[Avviso] Riga di proiezioni.csv ignorata: " + riga);
                }
            }
        }
        proiezioni.sort(Comparator.comparing(Proiezione::getDataOra));
    }

    /**
     * Salva l'intero elenco delle proiezioni sul file CSV,
     * sovrascrivendone il contenuto precedente.
     *
     * @throws IOException se il file non e' scrivibile
     */
    public void salva() throws IOException {
        try (PrintWriter scrittore = new PrintWriter(
                Files.newBufferedWriter(fileProiezioni, StandardCharsets.UTF_8))) {
            scrittore.println(INTESTAZIONE);
            for (Proiezione p : proiezioni) {
                Film f = p.getFilm();
                scrittore.println(String.join(",",
                        CsvUtil.proteggi(p.getDataOra().format(Proiezione.FORMATO_CSV)),
                        CsvUtil.proteggi(f.getTitolo()),
                        CsvUtil.proteggi(f.getGenere()),
                        CsvUtil.proteggi(f.getRegista()),
                        String.valueOf(f.getAnno()),
                        String.valueOf(f.getDurataMinuti()),
                        String.valueOf(f.getEtaMinima()),
                        String.format(Locale.US, "%.2f", p.getPrezzoBiglietto())));
            }
        }
    }

    /**
     * Restituisce una copia dell'elenco di tutte le proiezioni,
     * ordinate per data e ora crescente.
     *
     * @return la lista di tutte le proiezioni
     */
    public List<Proiezione> tutte() {
        return new ArrayList<>(proiezioni);
    }

    /**
     * Cerca le proiezioni che soddisfano tutti i criteri indicati.
     * Ogni criterio e' facoltativo: passando <code>null</code> il
     * criterio viene ignorato, per cui e' possibile usare un solo
     * criterio oppure una qualsiasi combinazione dei precedenti.
     *
     * @param titoloParziale titolo (anche parziale) del film, senza
     *                       distinzione tra maiuscole e minuscole,
     *                       oppure <code>null</code>
     * @param genere         tipologia (genere) del film, oppure <code>null</code>
     * @param dataDa         data minima (inclusa) della proiezione, oppure <code>null</code>
     * @param dataA          data massima (inclusa) della proiezione, oppure <code>null</code>
     * @param prezzoMin      costo minimo del biglietto, oppure <code>null</code>
     * @param prezzoMax      costo massimo del biglietto, oppure <code>null</code>
     * @return la lista delle proiezioni che soddisfano tutti i criteri
     */
    public List<Proiezione> cercaProiezione(String titoloParziale, String genere,
                                            LocalDate dataDa, LocalDate dataA,
                                            Double prezzoMin, Double prezzoMax) {
        List<Proiezione> risultato = new ArrayList<>();
        for (Proiezione p : proiezioni) {
            if (titoloParziale != null && !p.getFilm().getTitolo()
                    .toLowerCase().contains(titoloParziale.toLowerCase())) {
                continue;
            }
            if (genere != null && !p.getFilm().getGenere().equalsIgnoreCase(genere)) {
                continue;
            }
            LocalDate data = p.getDataOra().toLocalDate();
            if (dataDa != null && data.isBefore(dataDa)) {
                continue;
            }
            if (dataA != null && data.isAfter(dataA)) {
                continue;
            }
            if (prezzoMin != null && p.getPrezzoBiglietto() < prezzoMin) {
                continue;
            }
            if (prezzoMax != null && p.getPrezzoBiglietto() > prezzoMax) {
                continue;
            }
            risultato.add(p);
        }
        return risultato;
    }

    /**
     * Restituisce la proiezione che inizia esattamente nella data e
     * ora indicata. Essendo il cinema monosala, la data e ora di
     * inizio identifica univocamente la proiezione.
     *
     * @param dataOra la data e ora di inizio cercata
     * @return la proiezione trovata, oppure <code>null</code> se non esiste
     */
    public Proiezione trovaPerDataOra(LocalDateTime dataOra) {
        for (Proiezione p : proiezioni) {
            if (p.getDataOra().equals(dataOra)) {
                return p;
            }
        }
        return null;
    }

    /**
     * Aggiunge una proiezione al palinsesto, a patto che non si
     * sovrapponga temporalmente ad alcuna proiezione esistente
     * (vincolo del cinema monosala). In caso di successo il file
     * viene aggiornato.
     *
     * @param nuova la proiezione da aggiungere
     * @return <code>true</code> se la proiezione e' stata aggiunta,
     *         <code>false</code> se si sovrappone ad una esistente
     * @throws IOException se il salvataggio su file fallisce
     */
    public boolean aggiungiProiezione(Proiezione nuova) throws IOException {
        for (Proiezione p : proiezioni) {
            if (p.siSovrappone(nuova)) {
                return false;
            }
        }
        proiezioni.add(nuova);
        proiezioni.sort(Comparator.comparing(Proiezione::getDataOra));
        salva();
        return true;
    }

    /**
     * Modifica la data e ora di una proiezione esistente, a patto che
     * la nuova collocazione non si sovrapponga ad altre proiezioni.
     * Il controllo sull'assenza di prenotazioni per la proiezione e'
     * a carico del chiamante (si veda {@link GestorePrenotazioni}).
     *
     * @param proiezione   la proiezione da modificare
     * @param nuovaDataOra la nuova data e ora di inizio
     * @return <code>true</code> se la modifica e' andata a buon fine,
     *         <code>false</code> se la nuova collocazione si sovrappone
     *         ad un'altra proiezione
     * @throws IOException se il salvataggio su file fallisce
     */
    public boolean modificaProiezione(Proiezione proiezione,
                                      LocalDateTime nuovaDataOra) throws IOException {
        Proiezione tentativo =
                new Proiezione(nuovaDataOra, proiezione.getFilm(),
                        proiezione.getPrezzoBiglietto());
        for (Proiezione p : proiezioni) {
            if (p != proiezione && p.siSovrappone(tentativo)) {
                return false;
            }
        }
        proiezione.setDataOra(nuovaDataOra);
        proiezioni.sort(Comparator.comparing(Proiezione::getDataOra));
        salva();
        return true;
    }

    /**
     * Elimina una proiezione dal palinsesto e aggiorna il file. Il
     * controllo sull'assenza di prenotazioni per la proiezione e' a
     * carico del chiamante (si veda {@link GestorePrenotazioni}).
     *
     * @param proiezione la proiezione da eliminare
     * @throws IOException se il salvataggio su file fallisce
     */
    public void eliminaProiezione(Proiezione proiezione) throws IOException {
        proiezioni.remove(proiezione);
        salva();
    }
}

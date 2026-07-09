/*
 * Autori del progetto:
 * D'Onofrio Greta - Matricola 766016 - Sede CO
 * Tricarico Nicolo' - Matricola 767095 - Sede CO
 */
package cinemax;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Un oggetto della classe <code>Proiezione</code> rappresenta la
 * proiezione di un {@link Film} in una certa data e ora, con un
 * certo costo del biglietto.
 * <p>
 * Poiche' il cinema CineMax e' monosala, la data e ora di inizio
 * identifica univocamente una proiezione: due proiezioni distinte
 * non possono iniziare nello stesso istante ne' sovrapporsi.
 *
 * @author D'Onofrio Greta
 * @author Tricarico Nicolo'
 * @version 1.0
 */
public class Proiezione {

    /** Formato di data e ora usato nei file CSV (es. 2026-05-20 21:30:00). */
    public static final DateTimeFormatter FORMATO_CSV =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /** Formato di data e ora usato per la visualizzazione a terminale. */
    public static final DateTimeFormatter FORMATO_VIDEO =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /** La data e ora di inizio della proiezione. */
    private LocalDateTime dataOra;

    /** Il film proiettato. */
    private final Film film;

    /** Il costo del biglietto in euro. */
    private double prezzoBiglietto;

    /**
     * Costruisce una nuova proiezione.
     *
     * @param dataOra         la data e ora di inizio della proiezione
     * @param film            il film proiettato
     * @param prezzoBiglietto il costo del biglietto in euro
     */
    public Proiezione(LocalDateTime dataOra, Film film, double prezzoBiglietto) {
        this.dataOra = dataOra;
        this.film = film;
        this.prezzoBiglietto = prezzoBiglietto;
    }

    /**
     * Restituisce la data e ora di inizio della proiezione.
     *
     * @return la data e ora di inizio
     */
    public LocalDateTime getDataOra() {
        return dataOra;
    }

    /**
     * Imposta una nuova data e ora di inizio per la proiezione.
     *
     * @param dataOra la nuova data e ora di inizio
     */
    public void setDataOra(LocalDateTime dataOra) {
        this.dataOra = dataOra;
    }

    /**
     * Restituisce il film proiettato.
     *
     * @return il film della proiezione
     */
    public Film getFilm() {
        return film;
    }

    /**
     * Restituisce il costo del biglietto in euro.
     *
     * @return il costo del biglietto
     */
    public double getPrezzoBiglietto() {
        return prezzoBiglietto;
    }

    /**
     * Imposta il costo del biglietto in euro.
     *
     * @param prezzoBiglietto il nuovo costo del biglietto
     */
    public void setPrezzoBiglietto(double prezzoBiglietto) {
        this.prezzoBiglietto = prezzoBiglietto;
    }

    /**
     * Restituisce la data e ora di fine della proiezione, calcolata
     * sommando la durata del film alla data e ora di inizio.
     *
     * @return la data e ora di fine della proiezione
     */
    public LocalDateTime getDataOraFine() {
        return dataOra.plusMinutes(film.getDurataMinuti());
    }

    /**
     * Verifica se questa proiezione si sovrappone temporalmente ad
     * un'altra proiezione. Essendo il cinema monosala, due proiezioni
     * che si sovrappongono non possono coesistere nel palinsesto.
     *
     * @param altra la proiezione con cui verificare la sovrapposizione
     * @return <code>true</code> se le due proiezioni si sovrappongono
     */
    public boolean siSovrappone(Proiezione altra) {
        return dataOra.isBefore(altra.getDataOraFine())
                && altra.getDataOra().isBefore(getDataOraFine());
    }

    /**
     * Restituisce una rappresentazione compatta della proiezione,
     * su una sola riga, adatta agli elenchi mostrati a terminale.
     *
     * @return la stringa che descrive sinteticamente la proiezione
     */
    @Override
    public String toString() {
        return dataOra.format(FORMATO_VIDEO) + "  -  " + film.getTitolo()
                + "  [" + film.getGenere() + "]  -  "
                + String.format("%.2f", prezzoBiglietto) + " EUR";
    }
}

/*
 * Autori del progetto:
 * D'Onofrio Greta - Matricola 766016 - Sede CO
 * Tricarico Nicolo' - Matricola 767095 - Sede CO
 */
package cinemax;

import java.time.LocalDateTime;

/**
 * Un oggetto della classe <code>Prenotazione</code> rappresenta la
 * prenotazione di uno o piu' posti, effettuata da un cliente, per
 * una {@link Proiezione} del cinema CineMax.
 * <p>
 * Ogni prenotazione e' identificata da un codice univoco generato
 * al momento della creazione (si veda
 * {@link GestorePrenotazioni#creaPrenotazione}). Poiche' il cinema
 * e' monosala, la proiezione di riferimento e' identificata dalla
 * sua data e ora di inizio.
 *
 * @author D'Onofrio Greta
 * @author Tricarico Nicolo'
 * @version 1.0
 */
public class Prenotazione {

    /** Il codice univoco della prenotazione (es. <code>PRN-1A2B3C4D</code>). */
    private final String codice;

    /** Lo username del cliente che ha effettuato la prenotazione. */
    private final String usernameCliente;

    /** La data e ora di inizio della proiezione prenotata. */
    private LocalDateTime dataOraProiezione;

    /** Il numero di posti prenotati. */
    private final int numeroPosti;

    /** Il costo unitario del biglietto al momento della prenotazione, in euro. */
    private final double costoUnitario;

    /**
     * Costruisce una nuova prenotazione.
     *
     * @param codice            il codice univoco della prenotazione
     * @param usernameCliente   lo username del cliente
     * @param dataOraProiezione la data e ora della proiezione prenotata
     * @param numeroPosti       il numero di posti prenotati
     * @param costoUnitario     il costo unitario del biglietto in euro
     */
    public Prenotazione(String codice, String usernameCliente,
                        LocalDateTime dataOraProiezione,
                        int numeroPosti, double costoUnitario) {
        this.codice = codice;
        this.usernameCliente = usernameCliente;
        this.dataOraProiezione = dataOraProiezione;
        this.numeroPosti = numeroPosti;
        this.costoUnitario = costoUnitario;
    }

    /**
     * Restituisce il codice univoco della prenotazione.
     *
     * @return il codice della prenotazione
     */
    public String getCodice() {
        return codice;
    }

    /**
     * Restituisce lo username del cliente che ha effettuato la
     * prenotazione.
     *
     * @return lo username del cliente
     */
    public String getUsernameCliente() {
        return usernameCliente;
    }

    /**
     * Restituisce la data e ora della proiezione prenotata.
     *
     * @return la data e ora della proiezione
     */
    public LocalDateTime getDataOraProiezione() {
        return dataOraProiezione;
    }

    /**
     * Sposta la prenotazione su una proiezione con una diversa data
     * e ora (funzionalita' di modifica della prenotazione).
     *
     * @param dataOraProiezione la nuova data e ora della proiezione
     */
    public void setDataOraProiezione(LocalDateTime dataOraProiezione) {
        this.dataOraProiezione = dataOraProiezione;
    }

    /**
     * Restituisce il numero di posti prenotati.
     *
     * @return il numero di posti
     */
    public int getNumeroPosti() {
        return numeroPosti;
    }

    /**
     * Restituisce il costo unitario del biglietto in euro.
     *
     * @return il costo unitario del biglietto
     */
    public double getCostoUnitario() {
        return costoUnitario;
    }

    /**
     * Restituisce il costo totale della prenotazione, ottenuto
     * moltiplicando il costo unitario per il numero di posti.
     *
     * @return il costo totale in euro
     */
    public double getCostoTotale() {
        return costoUnitario * numeroPosti;
    }

    /**
     * Restituisce una rappresentazione compatta della prenotazione,
     * su una sola riga, adatta agli elenchi mostrati a terminale.
     *
     * @return la stringa che descrive sinteticamente la prenotazione
     */
    @Override
    public String toString() {
        return codice + "  -  proiezione del "
                + dataOraProiezione.format(Proiezione.FORMATO_VIDEO)
                + "  -  " + numeroPosti + " posti  -  totale "
                + String.format("%.2f", getCostoTotale()) + " EUR";
    }
}

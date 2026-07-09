/*
 * Autori del progetto:
 * D'Onofrio Greta - Matricola 766016 - Sede CO
 * Tricarico Nicolo' - Matricola 767095 - Sede CO
 */
package cinemax;

/**
 * L'enumerazione <code>Ruolo</code> rappresenta i possibili ruoli
 * di un {@link Utente} registrato sulla piattaforma CineMax.
 *
 * @author D'Onofrio Greta
 * @author Tricarico Nicolo'
 * @version 1.0
 */
public enum Ruolo {

    /** Cliente del cinema: puo' cercare proiezioni e gestire le proprie prenotazioni. */
    CLIENTE,

    /** Proiezionista: gestisce il palinsesto (inserimento, modifica e cancellazione di proiezioni). */
    PROIEZIONISTA,

    /** Bigliettaio: cerca e visualizza le prenotazioni dei clienti. */
    BIGLIETTAIO;

    /**
     * Converte una stringa (senza distinzione tra maiuscole e
     * minuscole) nel ruolo corrispondente.
     *
     * @param s la stringa da convertire (es. "cliente")
     * @return il ruolo corrispondente
     * @throws IllegalArgumentException se la stringa non corrisponde ad alcun ruolo
     */
    public static Ruolo daStringa(String s) {
        return Ruolo.valueOf(s.trim().toUpperCase());
    }
}

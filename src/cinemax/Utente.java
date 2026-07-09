/*
 * Autori del progetto:
 * D'Onofrio Greta - Matricola 766016 - Sede CO
 * Tricarico Nicolo' - Matricola 767095 - Sede CO
 */
package cinemax;

import java.time.LocalDate;

/**
 * Un oggetto della classe <code>Utente</code> rappresenta un utente
 * registrato sulla piattaforma CineMax: un cliente, un proiezionista
 * o un bigliettaio (si veda {@link Ruolo}).
 * <p>
 * La password non e' mai memorizzata in chiaro: l'oggetto conserva
 * esclusivamente l'impronta (hash) SHA-256 della password, calcolata
 * tramite la classe {@link Sicurezza}.
 *
 * @author D'Onofrio Greta
 * @author Tricarico Nicolo'
 * @version 1.0
 */
public class Utente {

    /** Il nome dell'utente. */
    private final String nome;

    /** Il cognome dell'utente. */
    private final String cognome;

    /** Lo username, univoco all'interno della piattaforma. */
    private final String username;

    /** L'hash SHA-256 (in esadecimale) della password dell'utente. */
    private final String passwordHash;

    /** La data di nascita dell'utente; e' facoltativa e puo' essere <code>null</code>. */
    private final LocalDate dataNascita;

    /** Il luogo del domicilio dell'utente. */
    private final String domicilio;

    /** Il ruolo dell'utente sulla piattaforma. */
    private final Ruolo ruolo;

    /**
     * Costruisce un nuovo utente.
     *
     * @param nome         il nome
     * @param cognome      il cognome
     * @param username     lo username (univoco)
     * @param passwordHash l'hash SHA-256 della password
     * @param dataNascita  la data di nascita, oppure <code>null</code> se non fornita
     * @param domicilio    il luogo del domicilio
     * @param ruolo        il ruolo dell'utente
     */
    public Utente(String nome, String cognome, String username,
                  String passwordHash, LocalDate dataNascita,
                  String domicilio, Ruolo ruolo) {
        this.nome = nome;
        this.cognome = cognome;
        this.username = username;
        this.passwordHash = passwordHash;
        this.dataNascita = dataNascita;
        this.domicilio = domicilio;
        this.ruolo = ruolo;
    }

    /**
     * Restituisce il nome dell'utente.
     *
     * @return il nome
     */
    public String getNome() {
        return nome;
    }

    /**
     * Restituisce il cognome dell'utente.
     *
     * @return il cognome
     */
    public String getCognome() {
        return cognome;
    }

    /**
     * Restituisce lo username dell'utente.
     *
     * @return lo username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Restituisce l'hash SHA-256 della password dell'utente.
     *
     * @return l'hash della password, in esadecimale
     */
    public String getPasswordHash() {
        return passwordHash;
    }

    /**
     * Restituisce la data di nascita dell'utente.
     *
     * @return la data di nascita, oppure <code>null</code> se non fornita
     */
    public LocalDate getDataNascita() {
        return dataNascita;
    }

    /**
     * Restituisce il luogo del domicilio dell'utente.
     *
     * @return il domicilio
     */
    public String getDomicilio() {
        return domicilio;
    }

    /**
     * Restituisce il ruolo dell'utente.
     *
     * @return il ruolo
     */
    public Ruolo getRuolo() {
        return ruolo;
    }

    /**
     * Verifica se la password fornita in chiaro corrisponde a quella
     * dell'utente, confrontandone l'hash SHA-256 con quello memorizzato.
     *
     * @param password la password in chiaro da verificare
     * @return <code>true</code> se la password e' corretta
     */
    public boolean verificaPassword(String password) {
        return passwordHash.equalsIgnoreCase(Sicurezza.sha256(password));
    }

    /**
     * Restituisce una rappresentazione testuale dell'utente.
     *
     * @return la stringa che descrive l'utente
     */
    @Override
    public String toString() {
        return nome + " " + cognome + " (" + username + ", "
                + ruolo.name().toLowerCase() + ")";
    }
}

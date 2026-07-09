/*
 * Autori del progetto:
 * D'Onofrio Greta - Matricola 766016 - Sede CO
 * Tricarico Nicolo' - Matricola 767095 - Sede CO
 */
package cinemax;

/**
 * Un oggetto della classe <code>Film</code> rappresenta un film
 * proiettabile presso il cinema CineMax, con le sue informazioni
 * anagrafiche: titolo, genere, regista, anno di uscita, durata in
 * minuti ed eta' minima del pubblico ammesso.
 *
 * @author D'Onofrio Greta
 * @author Tricarico Nicolo'
 * @version 1.0
 */
public class Film {

    /** Il titolo del film. */
    private final String titolo;

    /** Il genere del film (es. <code>Drama</code>, <code>Comedy</code>). */
    private final String genere;

    /** Il regista del film. */
    private final String regista;

    /** L'anno di uscita del film. */
    private final int anno;

    /** La durata del film espressa in minuti. */
    private final int durataMinuti;

    /**
     * L'eta' minima del pubblico ammesso alla visione
     * (0 se il film e' adatto a tutti).
     */
    private final int etaMinima;

    /**
     * Costruisce un nuovo film con le informazioni fornite.
     *
     * @param titolo       il titolo del film
     * @param genere       il genere del film
     * @param regista      il regista del film
     * @param anno         l'anno di uscita
     * @param durataMinuti la durata in minuti
     * @param etaMinima    l'eta' minima del pubblico (0 = per tutti)
     */
    public Film(String titolo, String genere, String regista,
                int anno, int durataMinuti, int etaMinima) {
        this.titolo = titolo;
        this.genere = genere;
        this.regista = regista;
        this.anno = anno;
        this.durataMinuti = durataMinuti;
        this.etaMinima = etaMinima;
    }

    /**
     * Restituisce il titolo del film.
     *
     * @return il titolo del film
     */
    public String getTitolo() {
        return titolo;
    }

    /**
     * Restituisce il genere del film.
     *
     * @return il genere del film
     */
    public String getGenere() {
        return genere;
    }

    /**
     * Restituisce il regista del film.
     *
     * @return il regista del film
     */
    public String getRegista() {
        return regista;
    }

    /**
     * Restituisce l'anno di uscita del film.
     *
     * @return l'anno di uscita
     */
    public int getAnno() {
        return anno;
    }

    /**
     * Restituisce la durata del film in minuti.
     *
     * @return la durata in minuti
     */
    public int getDurataMinuti() {
        return durataMinuti;
    }

    /**
     * Restituisce l'eta' minima del pubblico ammesso.
     *
     * @return l'eta' minima (0 se il film e' per tutti)
     */
    public int getEtaMinima() {
        return etaMinima;
    }

    /**
     * Restituisce una rappresentazione testuale del film, adatta
     * ad essere mostrata all'utente dell'interfaccia a terminale.
     *
     * @return la stringa che descrive il film
     */
    @Override
    public String toString() {
        String eta = (etaMinima == 0) ? "per tutti" : (etaMinima + "+");
        return titolo + " (" + anno + ", " + genere + ", regia di "
                + regista + ", " + durataMinuti + " min, " + eta + ")";
    }
}

/*
 * Autori del progetto:
 * D'Onofrio Greta - Matricola 766016 - Sede CO
 * Tricarico Nicolo' - Matricola 767095 - Sede CO
 */
package cinemax;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * La classe <code>Sicurezza</code> raccoglie le funzioni di utilita'
 * relative alla cifratura delle password. Le password degli utenti
 * non vengono mai salvate in chiaro sul file <code>utenti.csv</code>:
 * si memorizza soltanto l'impronta SHA-256, non invertibile.
 *
 * @author D'Onofrio Greta
 * @author Tricarico Nicolo'
 * @version 1.0
 */
public final class Sicurezza {

    /** Costruttore privato: la classe espone solo metodi statici. */
    private Sicurezza() {
    }

    /**
     * Calcola l'hash SHA-256 della stringa fornita e lo restituisce
     * come stringa esadecimale di 64 caratteri.
     *
     * @param testo il testo di cui calcolare l'hash
     * @return l'hash SHA-256 in esadecimale (lettere minuscole)
     * @throws IllegalStateException se l'algoritmo SHA-256 non e'
     *         disponibile nella JVM (evento che non dovrebbe mai
     *         verificarsi, essendo SHA-256 obbligatorio per specifica)
     */
    public static String sha256(String testo) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(testo.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Algoritmo SHA-256 non disponibile", e);
        }
    }
}

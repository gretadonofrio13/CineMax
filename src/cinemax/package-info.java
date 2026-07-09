/*
 * Autori del progetto:
 * D'Onofrio Greta - Matricola 766016 - Sede CO
 * Tricarico Nicolo' - Matricola 767095 - Sede CO
 */
/**
 * Il package <code>cinemax</code> contiene l'intera applicazione
 * CineMax, il sistema di gestione di un piccolo cinema monosala da
 * 200 posti sviluppato per il Laboratorio Interdisciplinare A
 * (2025/2026).
 * <p>
 * Le classi si dividono in tre gruppi:
 * <ul>
 *   <li><strong>Modello dei dati</strong>: {@link cinemax.Film},
 *       {@link cinemax.Proiezione}, {@link cinemax.Utente},
 *       {@link cinemax.Ruolo} e {@link cinemax.Prenotazione};</li>
 *   <li><strong>Logica e persistenza</strong>:
 *       {@link cinemax.GestoreProiezioni},
 *       {@link cinemax.GestoreUtenti} e
 *       {@link cinemax.GestorePrenotazioni}, che mantengono i dati in
 *       memoria e li salvano sui file CSV della cartella
 *       <code>data</code>;</li>
 *   <li><strong>Interfaccia e utilita'</strong>:
 *       {@link cinemax.CineMax} (interfaccia a terminale e metodo
 *       <code>main</code>), {@link cinemax.CsvUtil} (lettura e
 *       scrittura CSV) e {@link cinemax.Sicurezza} (cifratura SHA-256
 *       delle password).</li>
 * </ul>
 *
 * @see cinemax.CineMax
 * @see cinemax.GestoreProiezioni
 * @see cinemax.GestoreUtenti
 * @see cinemax.GestorePrenotazioni
 */
package cinemax;

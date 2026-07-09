/*
 * Autori del progetto:
 * D'Onofrio Greta - Matricola 766016 - Sede CO
 * Tricarico Nicolo' - Matricola 767095 - Sede CO
 */
package cinemax;

import java.util.ArrayList;
import java.util.List;

/**
 * La classe <code>CsvUtil</code> raccoglie le funzioni di utilita'
 * per la lettura e la scrittura di righe in formato CSV (valori
 * separati da virgola), con supporto ai campi racchiusi tra doppi
 * apici che possono contenere virgole (es. il titolo
 * <code>"Monsters, Inc."</code> nel file <code>proiezioni.csv</code>).
 *
 * @author D'Onofrio Greta
 * @author Tricarico Nicolo'
 * @version 1.0
 */
public final class CsvUtil {

    /** Costruttore privato: la classe espone solo metodi statici. */
    private CsvUtil() {
    }

    /**
     * Suddivide una riga CSV nei suoi campi, rispettando i doppi
     * apici: le virgole racchiuse tra apici non separano i campi e
     * la sequenza <code>""</code> all'interno di un campo tra apici
     * rappresenta un singolo doppio apice.
     *
     * @param riga la riga CSV da analizzare
     * @return la lista dei campi della riga, nell'ordine originale
     */
    public static List<String> dividiRiga(String riga) {
        List<String> campi = new ArrayList<>();
        StringBuilder corrente = new StringBuilder();
        boolean traApici = false;
        for (int i = 0; i < riga.length(); i++) {
            char c = riga.charAt(i);
            if (traApici) {
                if (c == '"') {
                    if (i + 1 < riga.length() && riga.charAt(i + 1) == '"') {
                        corrente.append('"');
                        i++;
                    } else {
                        traApici = false;
                    }
                } else {
                    corrente.append(c);
                }
            } else {
                if (c == '"') {
                    traApici = true;
                } else if (c == ',') {
                    campi.add(corrente.toString());
                    corrente.setLength(0);
                } else {
                    corrente.append(c);
                }
            }
        }
        campi.add(corrente.toString());
        return campi;
    }

    /**
     * Prepara un campo per la scrittura su file CSV: se il campo
     * contiene virgole o doppi apici viene racchiuso tra doppi apici
     * e gli eventuali apici interni vengono raddoppiati.
     *
     * @param campo il valore del campo da scrivere
     * @return il campo pronto per essere concatenato nella riga CSV
     */
    public static String proteggi(String campo) {
        if (campo == null) {
            return "";
        }
        if (campo.contains(",") || campo.contains("\"")) {
            return "\"" + campo.replace("\"", "\"\"") + "\"";
        }
        return campo;
    }
}

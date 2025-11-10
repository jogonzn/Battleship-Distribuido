package battleship.model;

/**
 * Representa una coordenada en el tablero de Battleship.
 * Utiliza un sistema de coordenadas de fila y columna (0-9).
 * 
 * @author Jorge González Navas
 */
public class Coordenada {
    // Fila de la coordenada (0-9)
    private final int fila;
    // Columna de la coordenada (0-9)
    private final int columna;

    /**
     * Constructor de Coordenada.
     * 
     * @param fila Fila de la coordenada (0-9)
     * @param columna Columna de la coordenada (0-9)
     */
    public Coordenada(int fila, int columna) {
        this.fila = fila;
        this.columna = columna;
    }

    /**
     * Obtiene la fila de la coordenada.
     * 
     * @return Fila (0-9)
     */
    public int getFila() {
        return fila;
    }

    /**
     * Obtiene la columna de la coordenada.
     * 
     * @return Columna (0-9)
     */
    public int getColumna() {
        return columna;
    }

    /**
     * Valida si la coordenada está dentro del tablero (0-9).
     * 
     * @return true si es válida, false en caso contrario
     */
    public boolean esValida() {
        return fila >= 0 && fila < 10 && columna >= 0 && columna < 10;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Coordenada that = (Coordenada) obj;
        return fila == that.fila && columna == that.columna;
    }

    @Override
    public int hashCode() {
        return 31 * fila + columna;
    }

    @Override
    public String toString() {
        return "(" + fila + "," + columna + ")";
    }
}

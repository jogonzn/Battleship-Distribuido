package battleship.model;

import java.util.ArrayList;

/**
 * Representa un barco en el juego Battleship.
 * Cada barco tiene un tipo, tamaño y ocupa varias posiciones en el tablero.
 * 
 * @author Jorge González Navas
 */
public class Barco {
    
    /**
     * Enumeración de tipos de barcos disponibles
     */
    public enum TipoBarco {
        PORTAAVIONES(5),
        ACORAZADO(4),
        CRUCERO(3),
        SUBMARINO(3),
        DESTRUCTOR(2);
        
        private final int tamanio;
        
        TipoBarco(int tamanio) {
            this.tamanio = tamanio;
        }
        
        public int getTamanio() {
            return tamanio;
        }
    }
    
    /**
     * Enumeración de orientaciones posibles
     */
    public enum Orientacion {
        HORIZONTAL,  // De izquierda a derecha
        VERTICAL     // De arriba hacia abajo
    }
    
    // Tipo de barco
    private final TipoBarco tipo;
    // Lista de coordenadas que ocupa el barco
    private final ArrayList<Coordenada> posiciones;
    // Lista de coordenadas que han sido impactadas
    private final ArrayList<Coordenada> impactos;
    
    /**
     * Constructor de Barco.
     * 
     * @param tipo Tipo de barco
     */
    public Barco(TipoBarco tipo) {
        this.tipo = tipo;
        this.posiciones = new ArrayList<Coordenada>();
        this.impactos = new ArrayList<Coordenada>();
    }
    
    /**
     * Obtiene el tipo de barco.
     * 
     * @return Tipo de barco
     */
    public TipoBarco getTipo() {
        return tipo;
    }
    
    /**
     * Obtiene el tamaño del barco.
     * 
     * @return Tamaño del barco
     */
    public int getTamanio() {
        return tipo.getTamanio();
    }
    
    /**
     * Obtiene las posiciones que ocupa el barco.
     * 
     * @return Lista de coordenadas
     */
    public ArrayList<Coordenada> getPosiciones() {
        return posiciones;
    }
    
    /**
     * Coloca el barco en el tablero a partir de una coordenada inicial.
     * 
     * @param inicio Coordenada inicial
     * @param orientacion Orientación del barco
     * @return true si se pudo colocar, false en caso contrario
     */
    public boolean colocar(Coordenada inicio, Orientacion orientacion) {
        posiciones.clear();
        
        // Calcular todas las posiciones del barco
        for (int i = 0; i < tipo.getTamanio(); i++) {
            Coordenada pos;
            if (orientacion == Orientacion.HORIZONTAL) {
                pos = new Coordenada(inicio.getFila(), inicio.getColumna() + i);
            } else {
                pos = new Coordenada(inicio.getFila() + i, inicio.getColumna());
            }
            
            // Validar que la posición esté dentro del tablero
            if (!pos.esValida()) {
                posiciones.clear();
                return false;
            }
            
            posiciones.add(pos);
        }
        
        return true;
    }
    
    /**
     * Verifica si el barco ocupa una coordenada específica.
     * 
     * @param coord Coordenada a verificar
     * @return true si el barco ocupa esa posición
     */
    public boolean ocupaPosicion(Coordenada coord) {
        for (Coordenada pos : posiciones) {
            if (pos.equals(coord)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Registra un impacto en el barco.
     * 
     * @param coord Coordenada del impacto
     * @return true si el impacto fue válido, false si ya había sido impactado
     */
    public boolean registrarImpacto(Coordenada coord) {
        if (!ocupaPosicion(coord)) {
            return false;
        }
        
        // Verificar si ya fue impactado
        for (Coordenada imp : impactos) {
            if (imp.equals(coord)) {
                return false;
            }
        }
        
        impactos.add(coord);
        return true;
    }
    
    /**
     * Verifica si el barco está completamente hundido.
     * 
     * @return true si está hundido
     */
    public boolean estaHundido() {
        return impactos.size() == tipo.getTamanio();
    }
    
    /**
     * Obtiene el número de impactos recibidos.
     * 
     * @return Número de impactos
     */
    public int getNumeroImpactos() {
        return impactos.size();
    }
    
    @Override
    public String toString() {
        return tipo.name() + " (" + impactos.size() + "/" + tipo.getTamanio() + ")";
    }
}
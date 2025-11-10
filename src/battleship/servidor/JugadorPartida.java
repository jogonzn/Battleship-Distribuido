package battleship.servidor;

import battleship.model.Tablero;
import java.net.Socket;

/**
 * Representa un jugador dentro de una partida.
 * Contiene el socket de conexión, tablero y estado del jugador.
 * 
 * @author Jorge González Navas
 */
public class JugadorPartida {
    
    // Nombre del jugador
    private final String nombre;
    
    // Socket de conexión del jugador
    private final Socket socket;
    
    // Tablero del jugador
    private final Tablero tablero;
    
    // Indica si el jugador está listo (ha colocado todos sus barcos)
    private boolean listo;
    
    /**
     * Constructor de JugadorPartida.
     * 
     * @param nombre Nombre del jugador
     * @param socket Socket de conexión
     */
    public JugadorPartida(String nombre, Socket socket) {
        this.nombre = nombre;
        this.socket = socket;
        this.tablero = new Tablero();
        this.listo = false;
    }
    
    /**
     * Obtiene el nombre del jugador.
     * 
     * @return Nombre del jugador
     */
    public String getNombre() {
        return nombre;
    }
    
    /**
     * Obtiene el socket del jugador.
     * 
     * @return Socket de conexión
     */
    public Socket getSocket() {
        return socket;
    }
    
    /**
     * Obtiene el tablero del jugador.
     * 
     * @return Tablero
     */
    public Tablero getTablero() {
        return tablero;
    }
    
    /**
     * Verifica si el jugador está listo.
     * 
     * @return true si está listo
     */
    public boolean isListo() {
        return listo;
    }
    
    /**
     * Establece el estado de listo del jugador.
     * 
     * @param listo Estado de listo
     */
    public void setListo(boolean listo) {
        this.listo = listo;
    }
    
    @Override
    public String toString() {
        return nombre + " (listo: " + listo + ")";
    }
}

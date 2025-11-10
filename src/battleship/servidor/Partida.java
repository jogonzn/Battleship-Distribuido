package battleship.servidor;

import battleship.model.*;
import battleship.protocol.Mensaje;
import java.net.Socket;
import java.io.*;
import java.util.ArrayList;

/**
 * Representa una partida de Battleship entre dos jugadores.
 * Gestiona el estado de la partida y la lógica del juego.
 * 
 * @author Jorge González Navas
 */
public class Partida {
    
    /**
     * Estados posibles de una partida
     */
    public enum EstadoPartida {
        ESPERANDO_JUGADOR,      // Esperando segundo jugador
        COLOCANDO_BARCOS,       // Jugadores colocando barcos
        EN_CURSO,               // Partida en juego
        FINALIZADA              // Partida terminada
    }
    
    // ID único de la partida
    private final int id;
    
    // Jugadores de la partida
    private JugadorPartida jugador1;
    private JugadorPartida jugador2;
    
    // Estado actual de la partida
    private EstadoPartida estado;
    
    // Turno actual (1 o 2)
    private int turnoActual;
    
    // Lock para sincronización
    private final Object lock = new Object();
    
    /**
     * Constructor de Partida.
     * 
     * @param id ID de la partida
     */
    public Partida(int id) {
        this.id = id;
        this.estado = EstadoPartida.ESPERANDO_JUGADOR;
        this.turnoActual = 1;
    }
    
    /**
     * Obtiene el ID de la partida.
     * 
     * @return ID de la partida
     */
    public int getId() {
        return id;
    }
    
    /**
     * Obtiene el estado de la partida.
     * 
     * @return Estado de la partida
     */
    public EstadoPartida getEstado() {
        synchronized (lock) {
            return estado;
        }
    }
    
    /**
     * Agrega un jugador a la partida.
     * 
     * @param nombre Nombre del jugador
     * @param socket Socket del jugador
     * @return true si se agregó exitosamente
     */
    public boolean agregarJugador(String nombre, Socket socket) {
        synchronized (lock) {
            if (jugador1 == null) {
                jugador1 = new JugadorPartida(nombre, socket);
                return true;
            } else if (jugador2 == null) {
                jugador2 = new JugadorPartida(nombre, socket);
                estado = EstadoPartida.COLOCANDO_BARCOS;
                return true;
            }
            return false;
        }
    }
    
    /**
     * Verifica si la partida está completa (2 jugadores).
     * 
     * @return true si hay 2 jugadores
     */
    public boolean estaCompleta() {
        synchronized (lock) {
            return jugador1 != null && jugador2 != null;
        }
    }
    
    /**
     * Obtiene el jugador 1.
     * 
     * @return Jugador 1
     */
    public JugadorPartida getJugador1() {
        return jugador1;
    }
    
    /**
     * Obtiene el jugador 2.
     * 
     * @return Jugador 2
     */
    public JugadorPartida getJugador2() {
        return jugador2;
    }
    
    /**
     * Verifica si un socket pertenece a un jugador de esta partida.
     * 
     * @param socket Socket a verificar
     * @return true si pertenece a la partida
     */
    public boolean contieneJugador(Socket socket) {
        synchronized (lock) {
            return (jugador1 != null && jugador1.getSocket().equals(socket)) ||
                   (jugador2 != null && jugador2.getSocket().equals(socket));
        }
    }
    
    /**
     * Obtiene el rival de un jugador.
     * 
     * @param socket Socket del jugador
     * @return Rival o null
     */
    public JugadorPartida obtenerRival(Socket socket) {
        synchronized (lock) {
            if (jugador1 != null && jugador1.getSocket().equals(socket)) {
                return jugador2;
            } else if (jugador2 != null && jugador2.getSocket().equals(socket)) {
                return jugador1;
            }
            return null;
        }
    }
    
    /**
     * Obtiene el jugador asociado a un socket.
     * 
     * @param socket Socket del jugador
     * @return Jugador o null
     */
    public JugadorPartida obtenerJugador(Socket socket) {
        synchronized (lock) {
            if (jugador1 != null && jugador1.getSocket().equals(socket)) {
                return jugador1;
            } else if (jugador2 != null && jugador2.getSocket().equals(socket)) {
                return jugador2;
            }
            return null;
        }
    }
    
    /**
     * Marca que un jugador está listo (terminó de colocar barcos).
     * 
     * @param socket Socket del jugador
     */
    public void marcarJugadorListo(Socket socket) {
        synchronized (lock) {
            JugadorPartida jugador = obtenerJugador(socket);
            if (jugador != null) {
                jugador.setListo(true);
                
                // Si ambos están listos, iniciar partida
                if (jugador1.isListo() && jugador2.isListo()) {
                    estado = EstadoPartida.EN_CURSO;
                }
            }
        }
    }
    
    /**
     * Verifica si ambos jugadores están listos.
     * 
     * @return true si ambos están listos
     */
    public boolean ambosJugadoresListos() {
        synchronized (lock) {
            return jugador1 != null && jugador2 != null &&
                   jugador1.isListo() && jugador2.isListo();
        }
    }
    
    /**
     * Verifica si es el turno de un jugador.
     * 
     * @param socket Socket del jugador
     * @return true si es su turno
     */
    public boolean esTurnoDeJugador(Socket socket) {
        synchronized (lock) {
            if (jugador1 != null && jugador1.getSocket().equals(socket)) {
                return turnoActual == 1;
            } else if (jugador2 != null && jugador2.getSocket().equals(socket)) {
                return turnoActual == 2;
            }
            return false;
        }
    }
    
    /**
     * Cambia el turno al siguiente jugador.
     */
    public void cambiarTurno() {
        synchronized (lock) {
            turnoActual = (turnoActual == 1) ? 2 : 1;
        }
    }
    
    /**
     * Procesa un disparo en la partida.
     * 
     * @param socket Socket del jugador que dispara
     * @param fila Fila del disparo
     * @param columna Columna del disparo
     * @return Resultado del disparo
     */
    public ResultadoDisparo procesarDisparo(Socket socket, int fila, int columna) {
        synchronized (lock) {
            JugadorPartida rival = obtenerRival(socket);
            if (rival == null) {
                return null;
            }
            
            Coordenada coord = new Coordenada(fila, columna);
            ResultadoDisparo resultado = rival.getTablero().recibirDisparo(coord);
            
            // Verificar si el rival perdió
            if (rival.getTablero().todosBarcosHundidos()) {
                estado = EstadoPartida.FINALIZADA;
            }
            
            return resultado;
        }
    }
    
    /**
     * Finaliza la partida.
     */
    public void finalizar() {
        synchronized (lock) {
            estado = EstadoPartida.FINALIZADA;
        }
    }
}

package battleship.servidor;

import battleship.protocol.Mensaje;
import battleship.model.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Servidor principal del juego Battleship.
 * Gestiona conexiones de clientes y partidas activas.
 * 
 * @author Jorge González Navas
 */
public class ServidorBattleship {
    
    // Puerto del servidor
    private static final int PUERTO = 5001;
    
    // Lista de partidas activas
    private static ArrayList<Partida> partidas = new ArrayList<Partida>();
    
    // Contador para IDs de partidas
    private static int contadorPartidas = 1;
    
    // Pool de hilos para manejar conexiones
    private static ExecutorService pool = Executors.newCachedThreadPool();
    
    public static void main(String[] args) {
        System.out.println("====================================");
        System.out.println("  Servidor Battleship");
        System.out.println("====================================");
        
        try (ServerSocket ss = new ServerSocket(PUERTO)) {
            System.out.println("Servidor iniciado en puerto " + PUERTO);
            System.out.println("Esperando conexiones...\n");
            
            while (true) {
                Socket cliente = ss.accept();
                System.out.println("Nueva conexión desde: " + cliente.getInetAddress());
                
                // Crear hilo para manejar el cliente
                pool.execute(new ManejadorCliente(cliente));
            }
            
        } catch (IOException e) {
            System.err.println("Error en el servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Crea una nueva partida.
     * 
     * @param nombre Nombre del jugador creador
     * @param socket Socket del jugador
     * @return ID de la partida creada
     */
    public static synchronized int crearPartida(String nombre, Socket socket) {
        int id = contadorPartidas++;
        Partida partida = new Partida(id);
        partida.agregarJugador(nombre, socket);
        partidas.add(partida);
        
        System.out.println("Partida " + id + " creada por " + nombre);
        return id;
    }
    
    /**
     * Une un jugador a una partida existente.
     * 
     * @param idPartida ID de la partida
     * @param nombre Nombre del jugador
     * @param socket Socket del jugador
     * @return true si se unió exitosamente
     */
    public static synchronized boolean unirseAPartida(int idPartida, String nombre, Socket socket) {
        for (Partida partida : partidas) {
            if (partida.getId() == idPartida && !partida.estaCompleta()) {
                boolean exito = partida.agregarJugador(nombre, socket);
                if (exito) {
                    System.out.println(nombre + " se unió a la partida " + idPartida);
                }
                return exito;
            }
        }
        return false;
    }
    
    /**
     * Obtiene la partida de un jugador.
     * 
     * @param socket Socket del jugador
     * @return Partida del jugador o null
     */
    public static synchronized Partida obtenerPartida(Socket socket) {
        for (Partida partida : partidas) {
            if (partida.contieneJugador(socket)) {
                return partida;
            }
        }
        return null;
    }
    
    /**
     * Elimina una partida de la lista.
     * 
     * @param partida Partida a eliminar
     */
    public static synchronized void eliminarPartida(Partida partida) {
        partidas.remove(partida);
        System.out.println("Partida " + partida.getId() + " eliminada");
    }
}

/**
 * Hilo que maneja la comunicación con un cliente.
 */
class ManejadorCliente implements Runnable {
    
    private Socket socket;
    private BufferedReader br;
    private DataOutputStream dos;
    private String nombreJugador;
    
    public ManejadorCliente(Socket socket) {
        this.socket = socket;
    }
    
    @Override
    public void run() {
        try {
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            dos = new DataOutputStream(socket.getOutputStream());
            
            // Enviar mensaje de bienvenida
            enviarMensaje(new Mensaje(Mensaje.BIENVENIDA, "Conectado al servidor Battleship"));
            
            // Bucle de procesamiento de mensajes
            String linea;
            while ((linea = br.readLine()) != null) {
                Mensaje mensaje = Mensaje.deserializar(linea);
                if (mensaje == null) {
                    continue;
                }
                // Log diferido: primero procesamos para asegurar nombreJugador asignado
                String nombreAntes = nombreJugador; // puede ser null antes de CONECTAR
                procesarMensaje(mensaje);
                String nombreDespues = (nombreJugador != null) ? nombreJugador : nombreAntes;
                System.out.println("Recibido de " + (nombreDespues != null ? nombreDespues : "[sin_nombre]") + ": " + mensaje);
            }
            
        } catch (IOException e) {
            System.err.println("Error con cliente: " + e.getMessage());
        } finally {
            desconectar();
        }
    }
    
    /**
     * Procesa un mensaje recibido del cliente.
     */
    private void procesarMensaje(Mensaje mensaje) {
        try {
            switch (mensaje.getComando()) {
                case Mensaje.CONECTAR:
                    procesarConectar(mensaje);
                    break;
                    
                case Mensaje.CREAR_PARTIDA:
                    procesarCrearPartida(mensaje);
                    break;
                    
                case Mensaje.UNIR_PARTIDA:
                    procesarUnirPartida(mensaje);
                    break;
                    
                case Mensaje.COLOCAR_BARCO:
                    procesarColocarBarco(mensaje);
                    break;
                    
                case Mensaje.LISTO:
                    procesarListo(mensaje);
                    break;
                    
                case Mensaje.DISPARAR:
                    procesarDisparar(mensaje);
                    break;
                    
                case Mensaje.DESCONECTAR:
                    desconectar();
                    break;
                    
                default:
                    enviarMensaje(new Mensaje(Mensaje.ERROR, "Comando desconocido"));
            }
        } catch (Exception e) {
            System.err.println("Error procesando mensaje: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Procesa comando CONECTAR.
     */
    private void procesarConectar(Mensaje mensaje) {
        if (mensaje.getNumParametros() > 0) {
            nombreJugador = mensaje.getParametro(0);
            System.out.println("Jugador " + nombreJugador + " conectado");
        }
    }
    
    /**
     * Procesa comando CREAR_PARTIDA.
     */
    private void procesarCrearPartida(Mensaje mensaje) {
        int idPartida = ServidorBattleship.crearPartida(nombreJugador, socket);
        enviarMensaje(new Mensaje(Mensaje.PARTIDA_CREADA, String.valueOf(idPartida)));
        enviarMensaje(new Mensaje(Mensaje.ESPERANDO_RIVAL));
    }
    
    /**
     * Procesa comando UNIR_PARTIDA.
     */
    private void procesarUnirPartida(Mensaje mensaje) {
        if (mensaje.getNumParametros() > 0) {
            try {
                int idPartida = Integer.parseInt(mensaje.getParametro(0));
                boolean exito = ServidorBattleship.unirseAPartida(idPartida, nombreJugador, socket);
                
                if (exito) {
                    Partida partida = ServidorBattleship.obtenerPartida(socket);
                    if (partida != null && partida.estaCompleta()) {
                        // Notificar a ambos jugadores
                        JugadorPartida rival = partida.obtenerRival(socket);
                        
                        enviarMensaje(new Mensaje(Mensaje.RIVAL_CONECTADO, rival.getNombre()));
                        enviarMensajeA(rival.getSocket(), new Mensaje(Mensaje.RIVAL_CONECTADO, nombreJugador));
                        
                        // Solicitar colocación de barcos
                        enviarMensaje(new Mensaje(Mensaje.COLOCAR_BARCOS));
                        enviarMensajeA(rival.getSocket(), new Mensaje(Mensaje.COLOCAR_BARCOS));
                    }
                } else {
                    enviarMensaje(new Mensaje(Mensaje.ERROR, "No se pudo unir a la partida"));
                }
            } catch (NumberFormatException e) {
                enviarMensaje(new Mensaje(Mensaje.ERROR, "ID de partida inválido"));
            }
        }
    }
    
    /**
     * Procesa comando COLOCAR_BARCO.
     */
    private void procesarColocarBarco(Mensaje mensaje) {
        if (mensaje.getNumParametros() >= 4) {
            try {
                String tipoStr = mensaje.getParametro(0);
                int fila = Integer.parseInt(mensaje.getParametro(1));
                int columna = Integer.parseInt(mensaje.getParametro(2));
                String orientacionStr = mensaje.getParametro(3);
                
                // Parsear tipo de barco
                Barco.TipoBarco tipo = Barco.TipoBarco.valueOf(tipoStr);
                
                // Parsear orientación validando entrada
                Barco.Orientacion orientacion;
                if ("H".equalsIgnoreCase(orientacionStr)) {
                    orientacion = Barco.Orientacion.HORIZONTAL;
                } else if ("V".equalsIgnoreCase(orientacionStr)) {
                    orientacion = Barco.Orientacion.VERTICAL;
                } else {
                    enviarMensaje(new Mensaje(Mensaje.ERROR, "Orientación inválida (usa H o V)"));
                    return;
                }
                
                // Obtener partida y tablero del jugador
                Partida partida = ServidorBattleship.obtenerPartida(socket);
                if (partida != null) {
                    JugadorPartida jugador = partida.obtenerJugador(socket);
                    
                    Barco barco = new Barco(tipo);
                    Coordenada inicio = new Coordenada(fila, columna);
                    
                    boolean exito = jugador.getTablero().colocarBarco(barco, inicio, orientacion);
                    
                    if (exito) {
                        enviarMensaje(new Mensaje(Mensaje.BARCO_COLOCADO, tipoStr));
                    } else {
                        enviarMensaje(new Mensaje(Mensaje.ERROR, "No se pudo colocar el barco"));
                    }
                }
                
            } catch (Exception e) {
                enviarMensaje(new Mensaje(Mensaje.ERROR, "Parámetros inválidos: " + e.getMessage()));
            }
        }
    }
    
    /**
     * Procesa comando LISTO.
     */
    private void procesarListo(Mensaje mensaje) {
        Partida partida = ServidorBattleship.obtenerPartida(socket);
        if (partida != null) {
            JugadorPartida jugador = partida.obtenerJugador(socket);
            
            // Verificar que haya colocado todos los barcos
            if (!jugador.getTablero().todosBarcoColocados()) {
                enviarMensaje(new Mensaje(Mensaje.ERROR, "Debes colocar todos los barcos primero"));
                return;
            }
            
            partida.marcarJugadorListo(socket);
            
            if (partida.ambosJugadoresListos()) {
                // Iniciar partida
                JugadorPartida j1 = partida.getJugador1();
                JugadorPartida j2 = partida.getJugador2();
                
                enviarMensajeA(j1.getSocket(), new Mensaje(Mensaje.TU_TURNO));
                enviarMensajeA(j2.getSocket(), new Mensaje(Mensaje.ESPERA_TURNO));
                
                System.out.println("Partida " + partida.getId() + " iniciada");
            }
        }
    }
    
    /**
     * Procesa comando DISPARAR.
     */
    private void procesarDisparar(Mensaje mensaje) {
        if (mensaje.getNumParametros() >= 2) {
            try {
                int fila = Integer.parseInt(mensaje.getParametro(0));
                int columna = Integer.parseInt(mensaje.getParametro(1));
                
                Partida partida = ServidorBattleship.obtenerPartida(socket);
                if (partida == null) {
                    enviarMensaje(new Mensaje(Mensaje.ERROR, "No estás en una partida"));
                    return;
                }
                
                // Verificar turno
                if (!partida.esTurnoDeJugador(socket)) {
                    enviarMensaje(new Mensaje(Mensaje.ERROR, "No es tu turno"));
                    return;
                }
                
                // Procesar disparo
                ResultadoDisparo resultado = partida.procesarDisparo(socket, fila, columna);
                
                if (resultado == ResultadoDisparo.YA_DISPARADO) {
                    enviarMensaje(new Mensaje(Mensaje.ERROR, "Ya disparaste en esa posición"));
                    return;
                }
                
                // Enviar resultado al jugador que disparó
                enviarMensaje(new Mensaje(Mensaje.RESULTADO_DISPARO, 
                    resultado.name(), String.valueOf(fila), String.valueOf(columna)));
                
                // Enviar disparo al rival
                JugadorPartida rival = partida.obtenerRival(socket);
                enviarMensajeA(rival.getSocket(), new Mensaje(Mensaje.DISPARO_RIVAL, 
                    String.valueOf(fila), String.valueOf(columna), resultado.name()));
                
                // Si hundió un barco, notificar
                if (resultado == ResultadoDisparo.HUNDIDO) {
                    Coordenada coord = new Coordenada(fila, columna);
                    Barco barcoHundido = rival.getTablero().obtenerBarcoHundido(coord);
                    if (barcoHundido != null) {
                        String tipoBarco = barcoHundido.getTipo().name();
                        enviarMensaje(new Mensaje(Mensaje.BARCO_HUNDIDO, tipoBarco));
                        enviarMensajeA(rival.getSocket(), new Mensaje(Mensaje.BARCO_HUNDIDO, tipoBarco));
                    }
                }
                
                // Verificar victoria
                if (rival.getTablero().todosBarcosHundidos()) {
                    enviarMensaje(new Mensaje(Mensaje.VICTORIA));
                    enviarMensajeA(rival.getSocket(), new Mensaje(Mensaje.DERROTA, nombreJugador));
                    
                    System.out.println("Partida " + partida.getId() + " finalizada. Ganador: " + nombreJugador);
                    ServidorBattleship.eliminarPartida(partida);
                } else {
                    // Cambiar turno
                    partida.cambiarTurno();
                    enviarMensaje(new Mensaje(Mensaje.ESPERA_TURNO));
                    enviarMensajeA(rival.getSocket(), new Mensaje(Mensaje.TU_TURNO));
                }
                
            } catch (NumberFormatException e) {
                enviarMensaje(new Mensaje(Mensaje.ERROR, "Coordenadas inválidas"));
            }
        }
    }
    
    /**
     * Envía un mensaje al cliente.
     */
    private void enviarMensaje(Mensaje mensaje) {
        try {
            dos.writeBytes(mensaje.serializar());
            dos.flush();
        } catch (IOException e) {
            System.err.println("Error enviando mensaje: " + e.getMessage());
        }
    }
    
    /**
     * Envía un mensaje a un socket específico.
     */
    private void enviarMensajeA(Socket destino, Mensaje mensaje) {
        try {
            DataOutputStream dosDestino = new DataOutputStream(destino.getOutputStream());
            dosDestino.writeBytes(mensaje.serializar());
            dosDestino.flush();
        } catch (IOException e) {
            System.err.println("Error enviando mensaje: " + e.getMessage());
        }
    }
    
    /**
     * Desconecta al cliente.
     */
    private void desconectar() {
        try {
            // Notificar a rival si está en partida
            Partida partida = ServidorBattleship.obtenerPartida(socket);
            if (partida != null) {
                JugadorPartida rival = partida.obtenerRival(socket);
                if (rival != null) {
                    enviarMensajeA(rival.getSocket(), 
                        new Mensaje(Mensaje.ERROR, "El rival se desconectó"));
                }
                ServidorBattleship.eliminarPartida(partida);
            }
            
            socket.close();
            System.out.println("Cliente " + nombreJugador + " desconectado");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

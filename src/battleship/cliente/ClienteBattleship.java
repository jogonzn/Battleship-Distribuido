package battleship.cliente;

import battleship.protocol.Mensaje;
import battleship.model.*;
import battleship.util.Colores;
import java.io.*;
import java.net.*;
import java.util.concurrent.CountDownLatch;

/**
 * Cliente del juego Battleship.
 * Permite conectarse al servidor y jugar partidas.
 * 
 * @author Jorge González Navas
 */
public class ClienteBattleship {
    
    private static final String HOST = "localhost";
    private static final int PUERTO = 5001;
    
    private Socket socket;
    private BufferedReader br;
    private DataOutputStream dos;
    private BufferedReader inputReader; // Lector de entrada del usuario
    
    private String nombreJugador;
    private Tablero miTablero;
    private Tablero tableroRival; // Para registrar disparos
    
    // Variable para controlar el menú
    private volatile boolean enJuego = false;
    private CountDownLatch iniciarColocacion = new CountDownLatch(1);
    
    public ClienteBattleship() {
        this.inputReader = new BufferedReader(new InputStreamReader(System.in));
        this.miTablero = new Tablero();
        this.tableroRival = new Tablero();
    }
    
    public static void main(String[] args) {
        ClienteBattleship cliente = new ClienteBattleship();
        cliente.iniciar();
    }
    
    /**
     * Inicia el cliente y se conecta al servidor.
     */
    public void iniciar() {
        try {
            System.out.println("====================================");
            System.out.println("  Cliente Battleship");
            System.out.println("====================================");
            System.out.println("Conectando al servidor...\n");
            
            socket = new Socket(HOST, PUERTO);
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            dos = new DataOutputStream(socket.getOutputStream());
            
            System.out.println("Conectado al servidor\n");
            
            // Solicitar nombre del jugador
            System.out.print("Ingresa tu nombre: ");
            nombreJugador = inputReader.readLine();
            
            // Enviar mensaje de conexión
            enviarMensaje(new Mensaje(Mensaje.CONECTAR, nombreJugador));
            
            // Crear hilo para recibir mensajes del servidor
            Thread hiloRecepcion = new Thread(new ReceptorMensajes());
            hiloRecepcion.start();
            
            // Menú principal en el hilo principal
            mostrarMenu();
            
        } catch (UnknownHostException e) {
            System.err.println("No se pudo conectar al servidor: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error de conexión: " + e.getMessage());
        }
    }
    
    /**
     * Muestra el menú principal del cliente.
     */
    private void mostrarMenu() {
        boolean salir = false;
        
        try {
            while (!salir && !enJuego) {
                System.out.println("\n====== MENÚ PRINCIPAL ======");
                System.out.println("1. Crear nueva partida");
                System.out.println("2. Unirse a partida existente");
                System.out.println("3. Salir");
                System.out.print("Opción: ");
                System.out.flush();
                String opcion = leerLineaMenuAbortable(); // retorna null si enJuego se activa
                if (enJuego || opcion == null) break;
                switch (opcion.trim()) {
                    case "1":
                        enviarMensaje(new Mensaje(Mensaje.CREAR_PARTIDA));
                        break;
                    case "2":
                        System.out.print("ID de la partida: ");
                        System.out.flush();
                        String idStr = leerLineaMenuAbortable();
                        if (idStr != null && !enJuego) {
                            enviarMensaje(new Mensaje(Mensaje.UNIR_PARTIDA, idStr.trim()));
                        }
                        break;
                    case "3":
                        enviarMensaje(new Mensaje(Mensaje.DESCONECTAR));
                        salir = true;
                        cerrar();
                        break;
                    default:
                        if (!enJuego) System.out.println("Opción inválida");
                }
            }
            
            // Si el juego comenzó, esperar señal y colocar barcos
            if (!salir && enJuego) {
                try {
                    iniciarColocacion.await(); // Espera señal de COLOCAR_BARCOS
                } catch (InterruptedException ignored) {}
                // Pasamos directamente a colocar barcos sin manipular buffers
                colocarBarcos();
            }
            
        } catch (IOException e) {
            if (!enJuego) {
                System.err.println("Error leyendo entrada: " + e.getMessage());
            }
        }
        
        if (!enJuego) {
            cerrar();
        }
    }
    
    /**
     * Solicita al usuario colocar todos sus barcos.
     */
    private synchronized void colocarBarcos() {
        System.out.println("\n" + Colores.Battleship.TITULO + "=".repeat(50) + Colores.RESET);
        System.out.println(Colores.Battleship.TITULO + "====== COLOCACIÓN DE BARCOS ======" + Colores.RESET);
        System.out.println(Colores.Battleship.TITULO + "=".repeat(50) + Colores.RESET);
        System.out.println("\n🚢 " + Colores.AMARILLO_BRILLANTE + "Debes colocar 5 barcos en tu tablero:" + Colores.RESET);
        System.out.println("  " + Colores.Battleship.PORTAAVIONES + "P" + Colores.RESET + " = Portaaviones (5)");
        System.out.println("  " + Colores.Battleship.ACORAZADO + "A" + Colores.RESET + " = Acorazado (4)");
        System.out.println("  " + Colores.Battleship.CRUCERO + "C" + Colores.RESET + " = Crucero (3)");
        System.out.println("  " + Colores.Battleship.SUBMARINO + "S" + Colores.RESET + " = Submarino (3)");
        System.out.println("  " + Colores.Battleship.DESTRUCTOR + "D" + Colores.RESET + " = Destructor (2)\n");
        
        Barco.TipoBarco[] tipos = {
            Barco.TipoBarco.PORTAAVIONES,
            Barco.TipoBarco.ACORAZADO,
            Barco.TipoBarco.CRUCERO,
            Barco.TipoBarco.SUBMARINO,
            Barco.TipoBarco.DESTRUCTOR
        };
        
        for (Barco.TipoBarco tipo : tipos) {
            boolean colocado = false;
            while (!colocado) {
                try {
                    System.out.println("\n" + Colores.CYAN + "📋 Tablero actual:" + Colores.RESET);
                    System.out.println(miTablero.obtenerVisualizacion(true));
                    char simbolo;
                    String colorBarco;
                    switch (tipo) {
                        case PORTAAVIONES: simbolo = 'P'; colorBarco = Colores.Battleship.PORTAAVIONES; break;
                        case ACORAZADO: simbolo = 'A'; colorBarco = Colores.Battleship.ACORAZADO; break;
                        case CRUCERO: simbolo = 'C'; colorBarco = Colores.Battleship.CRUCERO; break;
                        case SUBMARINO: simbolo = 'S'; colorBarco = Colores.Battleship.SUBMARINO; break;
                        case DESTRUCTOR: simbolo = 'D'; colorBarco = Colores.Battleship.DESTRUCTOR; break;
                        default: simbolo = '?'; colorBarco = Colores.ROJO_BRILLANTE; break;
                    }
                    System.out.println("\n🚢 Colocando " + Colores.AMARILLO_BRILLANTE + tipo.name() + Colores.RESET +
                            " [" + colorBarco + Colores.NEGRITA + simbolo + Colores.RESET + "] (tamaño: " + tipo.getTamanio() + ")");
                    int fila = leerEntero(0, 9, "Fila inicial (0-9): ");
                    int columna = leerEntero(0, 9, "Columna inicial (0-9): ");
                    Barco.Orientacion orientacion = leerOrientacion("Orientación (H=Horizontal, V=Vertical): ");
                    String orientacionStr = (orientacion == Barco.Orientacion.HORIZONTAL) ? "H" : "V";
                    Barco barco = new Barco(tipo);
                    Coordenada inicio = new Coordenada(fila, columna);
                    Tablero.ColocacionResultado resultado = miTablero.colocarBarcoDetallado(barco, inicio, orientacion);
                    switch (resultado) {
                        case EXITO:
                            enviarMensaje(new Mensaje(Mensaje.COLOCAR_BARCO, tipo.name(), String.valueOf(fila), String.valueOf(columna), orientacionStr));
                            colocado = true;
                            System.out.println(Colores.Battleship.EXITO + "✓ Barco colocado exitosamente" + Colores.RESET);
                            break;
                        case FUERA_DE_RANGO:
                            System.out.println(Colores.Battleship.ERROR + "✗ Fuera de rango: el barco excede el tablero" + Colores.RESET);
                            break;
                        case COLISION:
                            System.out.println(Colores.Battleship.ERROR + "✗ Colisión: ya hay un barco en esa trayectoria" + Colores.RESET);
                            break;
                    }
                } catch (IOException e) {
                    System.err.println("Error leyendo entrada: " + e.getMessage());
                    return; // abortar colocación por error IO
                }
            }
        }
        
        System.out.println("\n" + Colores.VERDE_BRILLANTE + "🎉 ¡Todos los barcos colocados!" + Colores.RESET);
        System.out.println(miTablero.obtenerVisualizacion(true));
        
        // Notificar al servidor que está listo
        enviarMensaje(new Mensaje(Mensaje.LISTO));
        System.out.println("\n" + Colores.AMARILLO + "⏳ Esperando al rival..." + Colores.RESET);
    }
    
    /**
     * Solicita al jugador realizar un disparo.
     */
    private synchronized void realizarDisparo() {
        try {
            System.out.println("\n" + Colores.Battleship.TITULO + "=".repeat(50) + Colores.RESET);
            System.out.println(Colores.Battleship.TITULO + "====== 🎯 TU TURNO 🎯 ======" + Colores.RESET);
            System.out.println(Colores.Battleship.TITULO + "=".repeat(50) + Colores.RESET);
            
            System.out.println("\n" + Colores.ROJO_BRILLANTE + "📍 TABLERO RIVAL" + Colores.RESET + " (tus disparos):");
            System.out.println("  " + Colores.Battleship.TOCADO + "X" + Colores.RESET + " = Tocado  |  " + 
                             Colores.Battleship.AGUA + "O" + Colores.RESET + " = Agua");
            System.out.println(tableroRival.obtenerVisualizacion(false));
            
            System.out.println("\n" + Colores.VERDE_BRILLANTE + "🚢 TU TABLERO:" + Colores.RESET);
            System.out.println("  " + Colores.Battleship.PORTAAVIONES + "P" + Colores.RESET + " = Portaaviones | " + 
                             Colores.Battleship.ACORAZADO + "A" + Colores.RESET + " = Acorazado | " +
                             Colores.Battleship.CRUCERO + "C" + Colores.RESET + " = Crucero");
            System.out.println("  " + Colores.Battleship.SUBMARINO + "S" + Colores.RESET + " = Submarino    | " +
                             Colores.Battleship.DESTRUCTOR + "D" + Colores.RESET + " = Destructor");
            System.out.println("  " + Colores.Battleship.TOCADO + "X" + Colores.RESET + " = Impacto recibido | " +
                             Colores.Battleship.AGUA + "O" + Colores.RESET + " = Agua (rival falló)");
            System.out.println(miTablero.obtenerVisualizacion(true));
            
            int fila = leerEntero(0, 9, "🎯 Fila del disparo (0-9): ");
            int columna = leerEntero(0, 9, "🎯 Columna del disparo (0-9): ");
            
            enviarMensaje(new Mensaje(Mensaje.DISPARAR, String.valueOf(fila), String.valueOf(columna)));
        } catch (IOException e) {
            System.err.println("Error leyendo entrada: " + e.getMessage());
        }
    }

    /**
     * Lee un entero dentro de un rango, reintentando hasta que sea válido.
     */
    private int leerEntero(int min, int max, String prompt) throws IOException {
        while (true) {
            System.out.print(Colores.Battleship.PROMPT + prompt + Colores.RESET);
            System.out.flush();
            String linea = inputReader.readLine();
            if (linea == null) throw new IOException("Entrada cerrada");
            linea = linea.trim();
            try {
                int valor = Integer.parseInt(linea);
                if (valor < min || valor > max) {
                    System.out.println(Colores.Battleship.ERROR + "✗ Número fuera de rango. Debe estar entre " + min + " y " + max + Colores.RESET);
                    continue;
                }
                return valor;
            } catch (NumberFormatException ex) {
                System.out.println(Colores.Battleship.ERROR + "✗ Ingresa un número válido" + Colores.RESET);
            }
        }
    }

    /**
     * Lee la orientación (H/V) asegurando validez.
     */
    private Barco.Orientacion leerOrientacion(String prompt) throws IOException {
        while (true) {
            System.out.print(Colores.Battleship.PROMPT + prompt + Colores.RESET);
            System.out.flush();
            String linea = inputReader.readLine();
            if (linea == null) throw new IOException("Entrada cerrada");
            linea = linea.trim().toUpperCase();
            if (linea.equals("H")) return Barco.Orientacion.HORIZONTAL;
            if (linea.equals("V")) return Barco.Orientacion.VERTICAL;
            System.out.println(Colores.Battleship.ERROR + "✗ Orientación inválida. Usa H o V" + Colores.RESET);
        }
    }
    
    /**
     * Envía un mensaje al servidor.
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
     * Cierra la conexión con el servidor.
     */
    private void cerrar() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            System.out.println("Conexión cerrada");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Hilo que recibe mensajes del servidor.
     */
    class ReceptorMensajes implements Runnable {
        
        @Override
        public void run() {
            try {
                String linea;
                while ((linea = br.readLine()) != null) {
                    Mensaje mensaje = Mensaje.deserializar(linea);
                    
                    if (mensaje != null) {
                        procesarMensaje(mensaje);
                    }
                }
            } catch (IOException e) {
                System.err.println("Conexión con el servidor perdida");
            }
        }
        
        /**
         * Procesa un mensaje recibido del servidor.
         */
        private void procesarMensaje(Mensaje mensaje) {
            switch (mensaje.getComando()) {
                case Mensaje.BIENVENIDA:
                    // Ya mostrado en iniciar()
                    break;
                    
                case Mensaje.PARTIDA_CREADA:
                    System.out.println("\n✓ Partida creada con ID: " + mensaje.getParametro(0));
                    break;
                    
                case Mensaje.ESPERANDO_RIVAL:
                    System.out.println("Esperando a que otro jugador se una...");
                    break;
                    
                case Mensaje.RIVAL_CONECTADO:
                    System.out.println("\n✓ Rival conectado: " + mensaje.getParametro(0));
                    break;
                    
                case Mensaje.COLOCAR_BARCOS:
                    enJuego = true; // Activar modo juego
                    // Señalar al hilo principal que comience la colocación
                    iniciarColocacion.countDown();
                    break;
                    
                case Mensaje.BARCO_COLOCADO:
                    System.out.println("\n" + Colores.Battleship.EXITO + "✔ Servidor confirmó barco: " + mensaje.getParametro(0) + Colores.RESET);
                    break;
                    
                case Mensaje.TU_TURNO:
                    realizarDisparo();
                    break;
                    
                case Mensaje.ESPERA_TURNO:
                    System.out.println("\nEsperando turno del rival...");
                    break;
                    
                case Mensaje.RESULTADO_DISPARO:
                    procesarResultadoDisparo(mensaje);
                    break;
                    
                case Mensaje.DISPARO_RIVAL:
                    procesarDisparoRival(mensaje);
                    break;
                    
                case Mensaje.BARCO_HUNDIDO:
                    System.out.println("\n¡Barco " + mensaje.getParametro(0) + " HUNDIDO!");
                    break;
                    
                case Mensaje.VICTORIA:
                    System.out.println("\n" + "=".repeat(40));
                    System.out.println("¡VICTORIA! Has ganado la partida");
                    System.out.println("=".repeat(40));
                    break;
                    
                case Mensaje.DERROTA:
                    System.out.println("\n" + "=".repeat(40));
                    System.out.println("DERROTA. " + mensaje.getParametro(0) + " ha ganado");
                    System.out.println("=".repeat(40));
                    break;
                    
                case Mensaje.ERROR:
                    System.out.println("\n✗ Error: " + mensaje.getParametro(0));
                    break;
                    
                default:
                    System.out.println("Mensaje desconocido: " + mensaje);
            }
        }
        
        /**
         * Procesa el resultado de un disparo propio.
         */
        private void procesarResultadoDisparo(Mensaje mensaje) {
            String resultado = mensaje.getParametro(0);
            int fila = Integer.parseInt(mensaje.getParametro(1));
            int columna = Integer.parseInt(mensaje.getParametro(2));
            
            System.out.println("\nDisparo en (" + fila + "," + columna + "): " + resultado);
            
            // Actualizar tablero rival con el resultado del disparo
            Coordenada coord = new Coordenada(fila, columna);
            boolean tocado = !resultado.equals("AGUA");
            tableroRival.registrarDisparoRealizado(coord, tocado);
            // Mostrar tablero rival actualizado inmediatamente
            System.out.println(Colores.ROJO_BRILLANTE + "\nTABLERO RIVAL ACTUALIZADO:" + Colores.RESET);
            System.out.println(tableroRival.obtenerVisualizacion(false));
        }
        
        /**
         * Procesa un disparo recibido del rival.
         */
        private void procesarDisparoRival(Mensaje mensaje) {
            int fila = Integer.parseInt(mensaje.getParametro(0));
            int columna = Integer.parseInt(mensaje.getParametro(1));
            String resultado = mensaje.getParametro(2);
            
            System.out.println("\nEl rival disparó en (" + fila + "," + columna + "): " + resultado);
            
            // Actualizar mi tablero con el impacto recibido
            Coordenada coord = new Coordenada(fila, columna);
            miTablero.recibirDisparo(coord);
            System.out.println(Colores.VERDE_BRILLANTE + "\nTU TABLERO ACTUALIZADO:" + Colores.RESET);
            System.out.println(miTablero.obtenerVisualizacion(true));
        }
    }

    /**
     * Lee una línea para el menú sin quedar bloqueado si comienza el juego.
     * Devuelve null si `enJuego` cambia antes de recibir una línea.
     */
    private String leerLineaMenuAbortable() throws IOException {
        while (!enJuego) {
            // Si hay una línea completa disponible usamos readLine normal
            if (inputReader.ready()) {
                return inputReader.readLine();
            }
            try { Thread.sleep(40); } catch (InterruptedException ignored) {}
        }
        return null; // juego empezó
    }
}

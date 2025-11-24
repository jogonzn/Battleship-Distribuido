package battleship.model;

import battleship.util.Colores;
import java.util.ArrayList;

/**
 * Representa el tablero de juego de Battleship.
 * Es una matriz de 10x10 donde se colocan los barcos y se registran los disparos.
 * 
 * @author Jorge González Navas
 */
public class Tablero {
    
    /**
     * Estados posibles de cada casilla del tablero
     */
    private enum EstadoCasilla {
        VACIA,          // Casilla sin barco y sin disparar
        BARCO,          // Casilla con barco, sin disparar
        AGUA,           // Casilla disparada sin barco
        TOCADO          // Casilla disparada con barco
    }
    
    // Dimensión del tablero
    private static final int DIMENSION = 10;
    
    // Matriz que representa el tablero
    private EstadoCasilla[][] casillas;
    
    // Lista de barcos en el tablero
    private ArrayList<Barco> barcos;
    
    // Lista de coordenadas disparadas
    private ArrayList<Coordenada> disparos;
    
    /**
     * Constructor del tablero.
     * Inicializa un tablero vacío de 10x10.
     */
    public Tablero() {
        this.casillas = new EstadoCasilla[DIMENSION][DIMENSION];
        this.barcos = new ArrayList<Barco>();
        this.disparos = new ArrayList<Coordenada>();
        
        // Inicializar todas las casillas como vacías
        for (int i = 0; i < DIMENSION; i++) {
            for (int j = 0; j < DIMENSION; j++) {
                casillas[i][j] = EstadoCasilla.VACIA;
            }
        }
    }
    
    /**
     * Intenta colocar un barco en el tablero.
     * 
     * @param barco Barco a colocar
     * @param inicio Coordenada inicial
     * @param orientacion Orientación del barco
     * @return true si se colocó exitosamente, false en caso contrario
     */
        public enum ColocacionResultado { EXITO, FUERA_DE_RANGO, COLISION }

        /**
         * Variante detallada que devuelve causa.
         */
        public ColocacionResultado colocarBarcoDetallado(Barco barco, Coordenada inicio, Barco.Orientacion orientacion) {
            if (!barco.colocar(inicio, orientacion)) {
                return ColocacionResultado.FUERA_DE_RANGO;
            }
            for (Coordenada pos : barco.getPosiciones()) {
                if (casillas[pos.getFila()][pos.getColumna()] == EstadoCasilla.BARCO) {
                    return ColocacionResultado.COLISION;
                }
            }
            for (Coordenada pos : barco.getPosiciones()) {
                casillas[pos.getFila()][pos.getColumna()] = EstadoCasilla.BARCO;
            }
            barcos.add(barco);
            return ColocacionResultado.EXITO;
        }

        // Método legacy para compatibilidad
        public boolean colocarBarco(Barco barco, Coordenada inicio, Barco.Orientacion orientacion) {
            return colocarBarcoDetallado(barco, inicio, orientacion) == ColocacionResultado.EXITO;
        }
    
    /**
     * Procesa un disparo en una coordenada.
     * 
     * @param coord Coordenada del disparo
     * @return Resultado del disparo
     */
    public ResultadoDisparo recibirDisparo(Coordenada coord) {
        // Validar coordenada
        if (!coord.esValida()) {
            return ResultadoDisparo.AGUA;
        }
        
        // Verificar si ya se disparó en esta posición
        for (Coordenada disp : disparos) {
            if (disp.equals(coord)) {
                return ResultadoDisparo.YA_DISPARADO;
            }
        }
        
        // Registrar el disparo
        disparos.add(coord);
        
        EstadoCasilla casilla = casillas[coord.getFila()][coord.getColumna()];
        
        if (casilla == EstadoCasilla.VACIA || casilla == EstadoCasilla.AGUA) {
            // Disparo al agua
            casillas[coord.getFila()][coord.getColumna()] = EstadoCasilla.AGUA;
            return ResultadoDisparo.AGUA;
        } else if (casilla == EstadoCasilla.BARCO) {
            // Disparo tocó un barco
            casillas[coord.getFila()][coord.getColumna()] = EstadoCasilla.TOCADO;
            
            // Buscar qué barco fue impactado
            for (Barco barco : barcos) {
                if (barco.ocupaPosicion(coord)) {
                    barco.registrarImpacto(coord);
                    
                    // Verificar si el barco está hundido
                    if (barco.estaHundido()) {
                        return ResultadoDisparo.HUNDIDO;
                    } else {
                        return ResultadoDisparo.TOCADO;
                    }
                }
            }
            
            return ResultadoDisparo.TOCADO;
        }
        
        return ResultadoDisparo.AGUA;
    }
    
    /**
     * Verifica si todos los barcos han sido hundidos.
     * 
     * @return true si todos los barcos están hundidos
     */
    public boolean todosBarcosHundidos() {
        for (Barco barco : barcos) {
            if (!barco.estaHundido()) {
                return false;
            }
        }
        return barcos.size() > 0; // Debe haber al menos un barco
    }
    
    /**
     * Obtiene el barco que fue hundido en la última jugada.
     * 
     * @param coord Coordenada del último disparo
     * @return Barco hundido o null si no hay ninguno
     */
    public Barco obtenerBarcoHundido(Coordenada coord) {
        for (Barco barco : barcos) {
            if (barco.ocupaPosicion(coord) && barco.estaHundido()) {
                return barco;
            }
        }
        return null;
    }
    
    /**
     * Obtiene la lista de barcos en el tablero.
     * 
     * @return Lista de barcos
     */
    public ArrayList<Barco> getBarcos() {
        return barcos;
    }
    
    /**
     * Verifica si se han colocado todos los barcos requeridos.
     * 
     * @return true si hay 5 barcos colocados
     */
    public boolean todosBarcoColocados() {
        return barcos.size() == 5;
    }
    
    /**
     * Registra un disparo realizado (para tablero rival).
     * 
     * @param coord Coordenada del disparo
     * @param tocado true si fue tocado, false si fue agua
     */
    public void registrarDisparoRealizado(Coordenada coord, boolean tocado) {
        if (!coord.esValida()) return;
        
        if (tocado) {
            casillas[coord.getFila()][coord.getColumna()] = EstadoCasilla.TOCADO;
        } else {
            casillas[coord.getFila()][coord.getColumna()] = EstadoCasilla.AGUA;
        }
        
        disparos.add(coord);
    }
    
    /**
     * Obtiene el símbolo del barco en una posición específica.
     * 
     * @param coord Coordenada a consultar
     * @return Símbolo del tipo de barco o ' ' si no hay barco
     */
    private char obtenerSimboloBarco(Coordenada coord) {
        for (Barco barco : barcos) {
            if (barco.ocupaPosicion(coord)) {
                switch (barco.getTipo()) {
                    case PORTAAVIONES:
                        return 'P';
                    case ACORAZADO:
                        return 'A';
                    case CRUCERO:
                        return 'C';
                    case SUBMARINO:
                        return 'S';
                    case DESTRUCTOR:
                        return 'D';
                }
            }
        }
        return ' ';
    }
    
    /**
     * Obtiene el color para un tipo de barco.
     */
    private String obtenerColorBarco(Coordenada coord) {
        for (Barco barco : barcos) {
            if (barco.ocupaPosicion(coord)) {
                switch (barco.getTipo()) {
                    case PORTAAVIONES:
                        return Colores.Battleship.PORTAAVIONES;
                    case ACORAZADO:
                        return Colores.Battleship.ACORAZADO;
                    case CRUCERO:
                        return Colores.Battleship.CRUCERO;
                    case SUBMARINO:
                        return Colores.Battleship.SUBMARINO;
                    case DESTRUCTOR:
                        return Colores.Battleship.DESTRUCTOR;
                }
            }
        }
        return Colores.RESET;
    }
    
    /**
     * Obtiene una representación visual del tablero con colores.
     * 
     * @param mostrarBarcos true para mostrar los barcos, false para ocultarlos
     * @return String con la representación del tablero
     */
    public String obtenerVisualizacion(boolean mostrarBarcos) {
        StringBuilder sb = new StringBuilder();
        
        // Encabezado de columnas con color
        sb.append(Colores.CYAN_BRILLANTE).append("   ");
        for (int i = 0; i < DIMENSION; i++) {
            sb.append(" ").append(Colores.NEGRITA).append(i).append(Colores.RESET).append(Colores.CYAN_BRILLANTE);
        }
        sb.append(Colores.RESET).append("\n");
        
        // Filas del tablero
        for (int i = 0; i < DIMENSION; i++) {
            // Número de fila con color
            sb.append(Colores.CYAN_BRILLANTE).append(String.format("%2d ", i)).append(Colores.RESET);
            
            for (int j = 0; j < DIMENSION; j++) {
                EstadoCasilla casilla = casillas[i][j];
                Coordenada coord = new Coordenada(i, j);
                char simbolo;
                String color = "";
                
                switch (casilla) {
                    case VACIA:
                        simbolo = '·';
                        color = Colores.Battleship.VACIO;
                        break;
                    case BARCO:
                        if (mostrarBarcos) {
                            simbolo = obtenerSimboloBarco(coord);
                            color = obtenerColorBarco(coord);
                        } else {
                            simbolo = '·';
                            color = Colores.Battleship.VACIO;
                        }
                        break;
                    case AGUA:
                        simbolo = 'O';
                        color = Colores.Battleship.AGUA;
                        break;
                    case TOCADO:
                        simbolo = 'X';
                        color = Colores.Battleship.TOCADO;
                        break;
                    default:
                        simbolo = '?';
                        color = Colores.ROJO;
                }
                
                sb.append(" ").append(color).append(simbolo).append(Colores.RESET);
            }
            sb.append("\n");
        }
        
        return sb.toString();
    }
    
    @Override
    public String toString() {
        return obtenerVisualizacion(true);
    }
}
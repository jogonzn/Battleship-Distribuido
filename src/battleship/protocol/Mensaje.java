package battleship.protocol;

/**
 * Clase que representa un mensaje en el protocolo de Battleship.
 * Los mensajes siguen el formato: COMANDO|param1|param2|...\r\n
 * 
 * @author Jorge González Navas
 */
public class Mensaje {
    
    // Delimitador de campos en el mensaje
    public static final String DELIMITADOR = "|";
    // Terminador de línea
    public static final String TERMINADOR = "\r\n";
    
    // Comando del mensaje
    private String comando;
    // Parámetros del mensaje
    private String[] parametros;
    
    /**
     * Constructor para crear un mensaje sin parámetros.
     * 
     * @param comando Comando del mensaje
     */
    public Mensaje(String comando) {
        this.comando = comando;
        this.parametros = new String[0];
    }
    
    /**
     * Constructor para crear un mensaje con parámetros.
     * 
     * @param comando Comando del mensaje
     * @param parametros Parámetros del mensaje
     */
    public Mensaje(String comando, String... parametros) {
        this.comando = comando;
        this.parametros = parametros;
    }
    
    /**
     * Obtiene el comando del mensaje.
     * 
     * @return Comando
     */
    public String getComando() {
        return comando;
    }
    
    /**
     * Obtiene los parámetros del mensaje.
     * 
     * @return Array de parámetros
     */
    public String[] getParametros() {
        return parametros;
    }
    
    /**
     * Obtiene un parámetro específico por índice.
     * 
     * @param indice Índice del parámetro
     * @return Parámetro o null si no existe
     */
    public String getParametro(int indice) {
        if (indice >= 0 && indice < parametros.length) {
            return parametros[indice];
        }
        return null;
    }
    
    /**
     * Obtiene el número de parámetros.
     * 
     * @return Número de parámetros
     */
    public int getNumParametros() {
        return parametros.length;
    }
    
    /**
     * Serializa el mensaje al formato de protocolo.
     * 
     * @return String con el mensaje serializado
     */
    public String serializar() {
        StringBuilder sb = new StringBuilder();
        sb.append(comando);
        
        for (String param : parametros) {
            sb.append(DELIMITADOR);
            sb.append(param);
        }
        
        sb.append(TERMINADOR);
        return sb.toString();
    }
    
    /**
     * Deserializa un mensaje desde una línea de texto.
     * 
     * @param linea Línea de texto con el mensaje
     * @return Mensaje deserializado o null si hay error
     */
    public static Mensaje deserializar(String linea) {
        if (linea == null || linea.isEmpty()) {
            return null;
        }
        
        // Eliminar terminador si existe
        linea = linea.replace("\r\n", "").replace("\n", "");
        
        // Dividir por delimitador
        String[] partes = linea.split("\\" + DELIMITADOR);
        
        if (partes.length == 0) {
            return null;
        }
        
        String comando = partes[0];
        
        // Extraer parámetros si existen
        if (partes.length > 1) {
            String[] parametros = new String[partes.length - 1];
            System.arraycopy(partes, 1, parametros, 0, parametros.length);
            return new Mensaje(comando, parametros);
        } else {
            return new Mensaje(comando);
        }
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(comando);
        
        if (parametros.length > 0) {
            sb.append("(");
            for (int i = 0; i < parametros.length; i++) {
                sb.append(parametros[i]);
                if (i < parametros.length - 1) {
                    sb.append(", ");
                }
            }
            sb.append(")");
        }
        
        return sb.toString();
    }
    
    // Comandos del protocolo como constantes
    
    // Cliente -> Servidor
    public static final String CONECTAR = "CONECTAR";
    public static final String CREAR_PARTIDA = "CREAR_PARTIDA";
    public static final String UNIR_PARTIDA = "UNIR_PARTIDA";
    public static final String COLOCAR_BARCO = "COLOCAR_BARCO";
    public static final String LISTO = "LISTO";
    public static final String DISPARAR = "DISPARAR";
    public static final String DESCONECTAR = "DESCONECTAR";
    
    // Servidor -> Cliente
    public static final String BIENVENIDA = "BIENVENIDA";
    public static final String PARTIDA_CREADA = "PARTIDA_CREADA";
    public static final String ESPERANDO_RIVAL = "ESPERANDO_RIVAL";
    public static final String RIVAL_CONECTADO = "RIVAL_CONECTADO";
    public static final String COLOCAR_BARCOS = "COLOCAR_BARCOS";
    public static final String BARCO_COLOCADO = "BARCO_COLOCADO";
    public static final String ERROR = "ERROR";
    public static final String TU_TURNO = "TU_TURNO";
    public static final String ESPERA_TURNO = "ESPERA_TURNO";
    public static final String RESULTADO_DISPARO = "RESULTADO_DISPARO";
    public static final String DISPARO_RIVAL = "DISPARO_RIVAL";
    public static final String BARCO_HUNDIDO = "BARCO_HUNDIDO";
    public static final String VICTORIA = "VICTORIA";
    public static final String DERROTA = "DERROTA";
}

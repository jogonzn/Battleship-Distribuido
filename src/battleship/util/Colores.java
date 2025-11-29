package battleship.util;

/**
 * * @author Jorge González Navas
 */
public class Colores {
    
    // Reset
    public static final String RESET = "\033[0m";
    
    public static final String NEGRO = "\033[0;30m";
    public static final String ROJO = "\033[0;31m";
    public static final String VERDE = "\033[0;32m";
    public static final String AMARILLO = "\033[0;33m";
    public static final String AZUL = "\033[0;34m";
    public static final String MAGENTA = "\033[0;35m";
    public static final String CYAN = "\033[0;36m";
    public static final String BLANCO = "\033[0;37m";
    public static final String ROJO_BRILLANTE = "\033[1;31m";
    public static final String VERDE_BRILLANTE = "\033[1;32m";
    public static final String AMARILLO_BRILLANTE = "\033[1;33m";
    public static final String AZUL_BRILLANTE = "\033[1;34m";
    public static final String MAGENTA_BRILLANTE = "\033[1;35m";
    public static final String CYAN_BRILLANTE = "\033[1;36m";
    public static final String BLANCO_BRILLANTE = "\033[1;37m";
    
    public static final String FONDO_ROJO = "\033[41m";
    public static final String FONDO_VERDE = "\033[42m";
    public static final String FONDO_AZUL = "\033[44m";
    
    public static final String NEGRITA = "\033[1m";
    public static final String SUBRAYADO = "\033[4m";
    
    /**
     * Colorea un texto con el color especificado.
     */
    public static String colorear(String texto, String color) {
        return color + texto + RESET;
    }
    
    /**
     * Aplica negrita a un texto.
     */
    public static String negrita(String texto) {
        return NEGRITA + texto + RESET;
    }
    
    /**
     * Colores específicos y semánticos para el juego Battleship.
     * Diseñados para no confundir barcos con estados de disparo.
     */
    public static class Battleship {
        // --- BARCOS --- 
        
        // P (5): Magenta Brillante (Púrpura)
        public static final String PORTAAVIONES = MAGENTA_BRILLANTE;
        
        // A (4): Verde Brillante
        public static final String ACORAZADO = VERDE_BRILLANTE;
        
        // C (3): Amarillo Brillante (Ojo: en fondo blanco puede verse dorado, pero es legible)
        public static final String CRUCERO = AMARILLO_BRILLANTE;
        
        // S (3): Cyan Brillante (Celeste) - Diferente del Azul del agua
        public static final String SUBMARINO = CYAN_BRILLANTE;
        
        // D (2): Blanco Brillante (Destaca en negro, se ve como gris oscuro en fondo blanco)
        public static final String DESTRUCTOR = BLANCO_BRILLANTE;
        
        // --- ESTADOS DEL TABLERO ---
        
        // Agua (O): Azul Estándar (Más oscuro que el Cyan del Submarino)
        public static final String AGUA = AZUL;
        
        // Impacto (X): Rojo Brillante (Alerta visual)
        public static final String TOCADO = ROJO_BRILLANTE;
        
        // Hundido: Fondo Rojo con Texto Blanco (Máxima prioridad visual)
        public static final String HUNDIDO = FONDO_ROJO + BLANCO_BRILLANTE;
        
        // Esto garantiza que se vea tanto en fondo negro (será blanco/gris) como en blanco (será negro).
        public static final String VACIO = RESET;
        
        public static final String TITULO = CYAN_BRILLANTE + NEGRITA;
        public static final String EXITO = VERDE_BRILLANTE;
        public static final String ERROR = ROJO_BRILLANTE;
        public static final String INFO = AMARILLO; 
        public static final String PROMPT = MAGENTA;
    }
}
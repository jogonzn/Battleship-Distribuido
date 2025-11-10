package battleship.util;

/**
 * Utilidad para aplicar colores ANSI a la consola.
 * Mejora significativamente la experiencia visual del juego.
 * 
 * @author Jorge González Navas
 */
public class Colores {
    
    // Reset
    public static final String RESET = "\033[0m";
    
    // Colores de texto
    public static final String NEGRO = "\033[0;30m";
    public static final String ROJO = "\033[0;31m";
    public static final String VERDE = "\033[0;32m";
    public static final String AMARILLO = "\033[0;33m";
    public static final String AZUL = "\033[0;34m";
    public static final String MORADO = "\033[0;35m";
    public static final String CYAN = "\033[0;36m";
    public static final String BLANCO = "\033[0;37m";
    
    // Colores brillantes
    public static final String ROJO_BRILLANTE = "\033[1;31m";
    public static final String VERDE_BRILLANTE = "\033[1;32m";
    public static final String AMARILLO_BRILLANTE = "\033[1;33m";
    public static final String AZUL_BRILLANTE = "\033[1;34m";
    public static final String MORADO_BRILLANTE = "\033[1;35m";
    public static final String CYAN_BRILLANTE = "\033[1;36m";
    public static final String BLANCO_BRILLANTE = "\033[1;37m";
    
    // Fondos
    public static final String FONDO_ROJO = "\033[41m";
    public static final String FONDO_VERDE = "\033[42m";
    public static final String FONDO_AMARILLO = "\033[43m";
    public static final String FONDO_AZUL = "\033[44m";
    public static final String FONDO_MORADO = "\033[45m";
    public static final String FONDO_CYAN = "\033[46m";
    
    // Estilos
    public static final String NEGRITA = "\033[1m";
    public static final String SUBRAYADO = "\033[4m";
    public static final String INVERTIDO = "\033[7m";
    
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
     * Colores específicos para el juego Battleship.
     */
    public static class Battleship {
        // Colores para tipos de barcos
        public static final String PORTAAVIONES = MORADO_BRILLANTE;
        public static final String ACORAZADO = AZUL_BRILLANTE;
        public static final String CRUCERO = CYAN_BRILLANTE;
        public static final String SUBMARINO = VERDE_BRILLANTE;
        public static final String DESTRUCTOR = AMARILLO_BRILLANTE;
        
        // Colores para estados
        public static final String AGUA = AZUL;
        public static final String TOCADO = ROJO_BRILLANTE;
        public static final String HUNDIDO = ROJO + NEGRITA;
        public static final String VACIO = "\033[0;37m";
        
        // UI
        public static final String TITULO = CYAN_BRILLANTE + NEGRITA;
        public static final String EXITO = VERDE_BRILLANTE;
        public static final String ERROR = ROJO_BRILLANTE;
        public static final String INFO = AMARILLO;
        public static final String PROMPT = CYAN;
    }
}

package battleship.model;

/**
 * Enumeración que representa los posibles resultados de un disparo.
 * 
 * @author Jorge González Navas
 */
public enum ResultadoDisparo {
    /**
     * El disparo cayó en agua (no hay barco)
     */
    AGUA,
    
    /**
     * El disparo impactó en un barco pero no lo hundió
     */
    TOCADO,
    
    /**
     * El disparo hundió completamente un barco
     */
    HUNDIDO,
    
    /**
     * El disparo se realizó en una posición ya disparada
     */
    YA_DISPARADO
}

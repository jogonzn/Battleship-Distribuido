#!/bin/bash

# Script de compilación para Battleship
# Uso: ./build.sh [clean|compile|run-server|run-client|all]

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo "======================================"
echo "  Battleship - Script de Compilación"
echo "======================================"

# Directorios
SRC_DIR="src"
BIN_DIR="bin"
SERVER_CLASS="battleship.servidor.ServidorBattleship"
CLIENT_CLASS="battleship.cliente.ClienteBattleship"

# Función para limpiar
clean() {
    echo -e "${YELLOW}Limpiando directorio bin...${NC}"
    rm -rf $BIN_DIR
    mkdir -p $BIN_DIR
    echo -e "${GREEN}✓ Limpieza completada${NC}"
}

# Función para compilar
compile() {
    echo -e "${YELLOW}Compilando código fuente...${NC}"
    
    if [ ! -d "$BIN_DIR" ]; then
        mkdir -p $BIN_DIR
    fi
    
    # Compilar todos los archivos Java
    javac -d $BIN_DIR -sourcepath $SRC_DIR $(find $SRC_DIR -name "*.java")
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✓ Compilación exitosa${NC}"
        return 0
    else
        echo -e "${RED}✗ Error en la compilación${NC}"
        return 1
    fi
}

# Función para ejecutar el servidor
run_server() {
    echo -e "${YELLOW}Iniciando servidor Battleship...${NC}"
    java -cp $BIN_DIR $SERVER_CLASS
}

# Función para ejecutar el cliente
run_client() {
    echo -e "${YELLOW}Iniciando cliente Battleship...${NC}"
    java -cp $BIN_DIR $CLIENT_CLASS
}

# Procesar argumentos
case "$1" in
    clean)
        clean
        ;;
    compile)
        compile
        ;;
    run-server)
        # Siempre recompilar para asegurar que los cambios en src/ estén reflejados
        compile
        run_server
        ;;
    run-client)
        compile
        run_client
        ;;
    all)
        clean
        compile
        if [ $? -eq 0 ]; then
            echo ""
            echo -e "${GREEN}Para ejecutar:${NC}"
            echo "  Servidor: ./build.sh run-server"
            echo "  Cliente:  ./build.sh run-client"
        fi
        ;;
    *)
        echo "Uso: $0 {clean|compile|run-server|run-client|all}"
        echo ""
        echo "Comandos:"
        echo "  clean       - Limpiar archivos compilados"
        echo "  compile     - Compilar código fuente"
        echo "  run-server  - Ejecutar servidor"
        echo "  run-client  - Ejecutar cliente"
        echo "  all         - Limpiar y compilar todo"
        exit 1
        ;;
esac

exit 0

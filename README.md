# Battleship

Juego Battleship cliente-servidor en Java.

## Cambios recientes (refactor y correcciones)

### 1. Logging servidor `Recibido de null`
El mensaje aparecía porque originalmente el servidor registraba la línea antes de procesar `CONECTAR`; por tanto `nombreJugador` aún era `null`. Se modificó el bucle de lectura en `ServidorBattleship.java` para:
- Deserializar mensaje.
- Procesarlo (asignando `nombreJugador` en CONECTAR).
- Luego registrar usando el nombre ya establecido.
Si sigue viendo `null`, es casi seguro que se está ejecutando una versión compilada previa (clases obsoletas). Ahora el script `build.sh` fuerza recompilación antes de ejecutar.

### 2. "Opción inválida" al comenzar colocación de barcos
Este evento ocurría una sola vez porque el menú imprimía y esperaba la entrada justo antes de que llegara el comando `COLOCAR_BARCOS` del servidor. El usuario tecleaba el primer número de fila (ej. `0`) mientras el menú todavía estaba activo; ese `0` era leído como opción del menú y caía en el caso `default` => "Opción inválida". Después el flujo entraba a colocación y el mismo `0` se volvía a pedir correctamente.
Correcciones:
- En el menú se verifica `enJuego` inmediatamente antes de leer la opción; si ya cambió se rompe el bucle sin intentar leer.
- Se eliminó la limpieza agresiva de buffers y sleeps que podían provocar condiciones de carrera y pérdida de la primera entrada.
Resultado: ya no debe aparecer el mensaje espurio.

### 3. Lectura robusta de entradas
Se añadieron métodos `leerEntero(min,max,prompt)` y `leerOrientacion(prompt)` en `ClienteBattleship.java` que validan rango y formato, evitando excepciones y reintentos desordenados.

### 4. Validación de orientación
El servidor ahora verifica que la orientación recibida sea exactamente `H` o `V` y responde con error si no lo es.

### 5. Mensajes detallados de colocación
`Tablero` ahora expone `ColocacionResultado` (EXITO, FUERA_DE_RANGO, COLISION) para informar la causa específica del fallo al colocar un barco. El cliente muestra mensajes diferenciados.

### 6. Confirmación de colocación desde el servidor
Al recibir `BARCO_COLOCADO` el cliente muestra un mensaje de confirmación, aumentando transparencia entre cliente y servidor.

### 7. Simplificación de flujo de inicio de colocación
Se retiró toda la lógica de espera con limpieza manual del buffer (loops sobre `ready()` y `System.in.available()`) para reducir riesgos de descartar entradas legítimas.

### 8. Recompilación garantizada
`build.sh` recompila siempre antes de `run-server` y `run-client` para evitar ejecutar clases antiguas y confusión al depurar.

## Posibles causas si persisten los síntomas
| Síntoma | Causa probable | Verificación |
|--------|----------------|--------------|
| `Recibido de null` | No se ha recompilado (clase antigua) | Verificar fecha de `bin/battleship/servidor/ServidorBattleship.class` |
| `Opción inválida` inicial | Aún ejecutando versión previa sin refactor menú | Asegurar recompilación limpia (`./build.sh clean && ./build.sh run-client`) |
| Falla al colocar barco | Colisión o fuera de rango ahora diferenciadas | Revisar mensaje específico |

## Próximas mejoras sugeridas
- Implementar estado global (enum) para reemplazar booleanos sueltos: MENU, ESPERANDO_RIVAL, COLOCACION, JUEGO, FIN.
- Añadir sincronización de turnos y visualización del tablero rival tras cada disparo.
- Serializar información de tablero para validación cruzada en servidor (actualmente el servidor confía en la colocación del cliente al enviar `COLOCAR_BARCO`).

## Ejecución
```bash
./build.sh run-server
./build.sh run-client
```

## Autor
Jorge González Navas

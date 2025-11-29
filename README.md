# Battleship Distribuido
Implementación de Battleship (Hundir la Flota) con una arquitectura Cliente-Servidor clásica sobre TCP, diseñada para ser sólida y gestionar la concurrencia de forma eficaz. Me he ceñido estrictamente a las herramientas de alto nivel de Java vistas en la asignatura. 

## Funcionamiento
La aplicación permite enfrentamientos en tiempo real entre múltiples parejas.

* **El Servidor:** Centraliza el estado del juego (es autoritativo) y gestiona el emparejamiento.
* **El Cliente:** Funciona como interfaz de consola y gestiona la comunicación con un protocolo de texto propio (ej. `DISPARAR|3|4`).
* **Flujo:** `Conexión -> Emparejamiento -> Colocación de barcos -> Turnos de disparo -> Fin`.

## Autor
Jorge González Navas
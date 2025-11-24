# Battleship Distribuido

Implementación sencilla de Battleship (Hundir la Flota) con arquitectura **cliente-servidor** en **Java SE** usando sockets TCP y primitivas básicas de concurrencia del temario.

## Concurrencia usada
- `Thread` (recepción cliente)
- `ExecutorService` (pool de conexiones)
- `Semaphore` (límite de partidas simultáneas)
- `CyclicBarrier` (sincronizar inicio partida)
- `Callable` + `Future` (consulta asíncrona estado)

## Autor
Jorge González Navas
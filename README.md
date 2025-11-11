# Battleship Distribuido
## Descripción
Clásico juego **Battleship (Hundir la Flota)** utilizando arquitectura cliente-servidor distribuida en Java. Desarrollado para el curso de **Sistemas Distribuidos 2025-2026** de la Universidad de La Rioja.

### Características principales
- ✅ Arquitectura cliente-servidor con sockets TCP
- ✅ Soporte para múltiples partidas simultáneas
- ✅ Comunicación mediante protocolo personalizado
- ✅ Concurrencia avanzada (ExecutorService, Semaphore, CountDownLatch, CyclicBarrier)
- ✅ Interfaz de consola con colores ANSI
- ✅ Gestión robusta de excepciones y desconexiones
- ✅ Solo utiliza librerías estándar de Java SE

## Arquitectura del Sistema
┌─────────────┐                          ┌─────────────┐
│  Cliente 1  │◄─────── Socket ─────────►│             │
└─────────────┘         (TCP)            │             │
                                         │  Servidor   │
┌─────────────┐                          │ Battleship  │
│  Cliente 2  │◄─────── Socket ─────────►│             │
└─────────────┘         (TCP)            │             │
                                         │ (Puerto     │
┌─────────────┐                          │  5001)      │
│  Cliente N  │◄─────── Socket ─────────►│             │
└─────────────┘         (TCP)            └─────────────┘
                                                 │
                                                 ▼
                                    ┌───────────────────────┐
                                    │ ExecutorService       │
                                    │ (Pool de hilos)       │
                                    ├───────────────────────┤
                                    │ ManejadorCliente 1    │
                                    │ ManejadorCliente 2    │
                                    │ ManejadorCliente N    │
                                    └───────────────────────┘
                                                 │
                                                 ▼
                                    ┌───────────────────────┐
                                    │ Gestión de Partidas   │
                                    ├───────────────────────┤
                                    │ Partida 1 (Jugadores) │
                                    │ Partida 2 (Jugadores) │
                                    │ ...                   │
                                    └───────────────────────┘

## Autor
**Jorge González Navas** 
# Historial sin índices (baseline "antes")

GET /transactions/history?page=0&size=20, usuario perf1 (20.057 filas de historial), 320.000 transacciones en total.

## Entorno
- Laptop 4 GB RAM / 4 núcleos, WSL2 (2,5 GB). App: 2 CPU, 700 MB, heap 384 MB. Postgres 16: 1 CPU, 512 MB.
- k6 en Docker (1 CPU, 256 MB), tasa fija (constant-arrival-rate), 10 s de warm-up descartados.
- Base restaurada desde seed.dump (el endpoint solo lee, no se restauró entre corridas).

## Diagnóstico
EXPLAIN (ANALYZE, BUFFERS): Parallel Seq Scan sobre transactions, filtra source_wallet_id = 1 OR destination_wallet_id = 1 y descarta ~300.000 filas. Execution Time: 1.096 ms.

## Resultados a 0,5 req/s (60 s de medición, 31 muestras por corrida)
| Corrida | p50 | p90 | p95 | errores | descartadas |
|---|---|---|---|---|---|
| 1 | 744 ms | 1,45 s | 2,74 s | 0 % | 0 |
| 2 | 791 ms | 2,07 s | 2,92 s | 0 % | 0 |
| 3 | 720 ms | 1,11 s | 1,26 s | 0 % | 0 |

p50 estable (720-791 ms). p95 con mucha variación entre corridas (1,26-2,92 s).

## Saturación a 1 req/s
Con 1 req/s el endpoint no sostiene la carga: en una corrida p50 0,9 s / p95 6,1 s, en otra p50 14,9 s / p95 26,8 s (cola creciente). Por eso el baseline se mide a 0,5 req/s.

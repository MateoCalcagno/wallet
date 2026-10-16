# Historial: consulta reescrita + índices en transactions

Mismo endpoint, usuario, carga (0,5 req/s, 60 s, 31 muestras) y entorno que 01-historial-antes.md.

## Cambios (en este orden)
1. Índices idx_transactions_source_wallet e idx_transactions_destination_wallet (creados a mano en la base de perf). Resultado: el endpoint no cambió (p50 730-829 ms), porque la consulta que genera Spring (dos LEFT JOIN a wallets, filtro sobre wallets) no puede usarlos. EXPLAIN de esa consulta: Parallel Seq Scan sobre transactions, 1238 ms.
2. Consulta y countQuery escritas con @Query en TransactionRepository, filtrando directo por source/destination wallet id.

## Resultados (3 corridas, fase measure)
| Corrida | p50 | p90 | p95 | máx | errores |
|---|---|---|---|---|---|
| 1 | 67,9 ms | 110,7 ms | 282,4 ms | 1,14 s | 0 % |
| 2 | 66,2 ms | 490,3 ms | 826,7 ms | 1,98 s | 0 % |
| 3 | 65,3 ms | 77,6 ms | 95,8 ms | 104 ms | 0 % |

Antes: p50 720-791 ms, p95 1,26-2,92 s.

## Limitaciones
- Probado a 0,5 req/s, una instancia; no se repitió la prueba a 1 req/s.
- No se aisló el aporte de cada cambio (no se midió la consulta reescrita sin índices).
- Quedan ~47 sentencias chicas por pedido (posible N+1 en el mapper), sin medir.
- Los índices están creados solo en la base de perf, no en el código.

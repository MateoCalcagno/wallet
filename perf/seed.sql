\set ON_ERROR_STOP on
CREATE EXTENSION IF NOT EXISTS pgcrypto;
SELECT setseed(0.42);

-- 10.000 usuarios. Misma password para todos (Perf1234!): el hash se calcula una sola vez.
WITH h AS (SELECT crypt('Perf1234!', gen_salt('bf', 10)) AS pw)
INSERT INTO users (created_at, updated_at, email, password, first_name, last_name, dni)
SELECT now(), now(), 'perf' || n || '@test.com', h.pw, 'User' || n, 'Perf', lpad(n::text, 8, '0')
FROM generate_series(1, 10000) n, h;

-- Una wallet por usuario, con 1.000.000 de saldo
INSERT INTO wallets (created_at, updated_at, user_id, balance, cbu, alias)
SELECT now(), now(), u.id, 1000000.00, lpad(u.id::text, 22, '0'), 'perf.alias.' || u.id
FROM users u ORDER BY u.id;

-- 300.000 transacciones en el último año: 60% transfer, 25% depósito, 15% retiro
WITH base AS MATERIALIZED (
  SELECT random() AS r,
         1 + floor(random() * 10000)::bigint AS a,
         1 + floor(random() * 9999)::bigint  AS b,
         now() - random() * interval '365 days' AS ts,
         round((1 + random() * 999)::numeric, 2) AS amt
  FROM generate_series(1, 300000)
)
INSERT INTO transactions (created_at, updated_at, source_wallet_id, destination_wallet_id, amount, type)
SELECT ts, ts,
       CASE WHEN r < 0.60 OR r >= 0.85 THEN a END,
       CASE WHEN r < 0.60 THEN (CASE WHEN b >= a THEN b + 1 ELSE b END)
            WHEN r < 0.85 THEN a END,
       amt,
       CASE WHEN r < 0.60 THEN 'TRANSFER' WHEN r < 0.85 THEN 'DEPOSIT' ELSE 'WITHDRAWAL' END
FROM base;

-- Usuario "pesado": la wallet 1 suma 20.000 transferencias, mitad enviadas y mitad recibidas
WITH base AS MATERIALIZED (
  SELECT g,
         2 + floor(random() * 9999)::bigint AS other,
         now() - random() * interval '365 days' AS ts,
         round((1 + random() * 999)::numeric, 2) AS amt
  FROM generate_series(1, 20000) g
)
INSERT INTO transactions (created_at, updated_at, source_wallet_id, destination_wallet_id, amount, type)
SELECT ts, ts,
       CASE WHEN g % 2 = 0 THEN 1 ELSE other END,
       CASE WHEN g % 2 = 0 THEN other ELSE 1 END,
       amt, 'TRANSFER'
FROM base;

VACUUM ANALYZE;

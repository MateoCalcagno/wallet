import http from 'k6/http';
import { check, fail } from 'k6';

const BASE = __ENV.BASE || 'http://localhost:8080';
const RATE = Number(__ENV.RATE || 10);
const WARMUP = __ENV.WARMUP || '15s';
const DURATION = __ENV.DURATION || '30s';

export const options = {
  scenarios: {
    warmup: {
      executor: 'constant-arrival-rate',
      rate: RATE, timeUnit: '1s', duration: WARMUP,
      preAllocatedVUs: 20, maxVUs: 100,
      tags: { phase: 'warmup' },
    },
    measure: {
      executor: 'constant-arrival-rate',
      rate: RATE, timeUnit: '1s', duration: DURATION,
      startTime: WARMUP,
      preAllocatedVUs: 20, maxVUs: 100,
      tags: { phase: 'measure' },
    },
  },
  thresholds: {
    'http_req_failed{phase:measure}': ['rate<0.01'],
    'http_req_duration{phase:measure}': ['p(95)<5000'],
    'dropped_iterations{phase:measure}': ['count<1'],
  },
};

export function setup() {
  const res = http.post(
    `${BASE}/auth/login`,
    JSON.stringify({ email: 'perf1@test.com', password: 'Perf1234!' }),
    { headers: { 'Content-Type': 'application/json' } }
  );
  const token = res.json('token');
  if (!token) fail(`login sin token (status ${res.status})`);
  return { token };
}

export default function (data) {
  const res = http.get(`${BASE}/wallet/me`, {
    headers: { Authorization: `Bearer ${data.token}` },
  });
  check(res, { 'status 200': (r) => r.status === 200 });
}

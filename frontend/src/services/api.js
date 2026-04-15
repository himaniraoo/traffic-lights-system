import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: { 'Content-Type': 'application/json' },
});

// ── Traffic Operator API ─────────────────────────────────────────────
export const getSignalStatus   = ()      => api.get('/signals/status');
export const startCycle        = ()      => api.post('/signals/start');
export const stopCycle         = ()      => api.post('/signals/stop');
export const getCycleRunning   = ()      => api.get('/signals/running');
export const getCycleState     = ()      => api.get('/signals/state');
export const getSignalMode     = ()      => api.get('/signals/mode');
export const setSignalMode     = (mode)  => api.post(`/signals/mode/${mode}`);
export const applyManualSignal = (data)  => api.post('/signals/manual', data);
export const applyPriority     = (data)  => api.post('/signals/priority', data);
export const getTimerConfig    = ()      => api.get('/signals/timer');
export const updateTimerConfig = (data)  => api.put('/signals/timer', data);

// ── Maintenance Engineer API ─────────────────────────────────────────
export const getSystemStatus   = ()      => api.get('/maintenance/status');
export const runDiagnostics    = ()      => api.post('/maintenance/diagnostics');
export const resetSystem       = ()      => api.post('/maintenance/reset');
export const getManagerState   = ()      => api.get('/maintenance/manager-state');

export default api;

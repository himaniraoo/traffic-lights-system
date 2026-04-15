import React, { useState, useEffect, useCallback } from 'react';
import { Container, Row, Col, Button, Form } from 'react-bootstrap';
import TrafficLightCard  from '../components/shared/TrafficLightCard';
import TimingConfigForm  from '../components/operator/TimingConfigForm';
import {
  startCycle,
  stopCycle,
  getSignalStatus,
  getCycleRunning,
  getCycleState,
  getSignalMode,
  setSignalMode,
  applyManualSignal,
  applyPriority,
} from '../services/api';
import { connectWebSocket, disconnectWebSocket } from '../services/websocket';

const CYCLE_STATE_COLORS = {
  IDLE:     { bg: 'rgba(125,133,144,0.15)', color: '#7d8590',  border: '#7d8590'  },
  RUNNING:  { bg: 'rgba(48,209,88,0.15)',   color: '#30D158',  border: '#30D158'  },
  UPDATING: { bg: 'rgba(255,214,10,0.15)',  color: '#FFD60A',  border: '#FFD60A'  },
  MANUAL_CONTROL:   { bg: 'rgba(13,110,253,0.15)', color: '#6ea8fe', border: '#0d6efd' },
  PRIORITY_CONTROL: { bg: 'rgba(255,214,10,0.15)', color: '#FFD60A', border: '#FFD60A' },
  STOPPED:  { bg: 'rgba(255,59,48,0.15)',   color: '#FF3B30',  border: '#FF3B30'  },
};

const DIRECTIONS = ['NORTH', 'EAST', 'SOUTH', 'WEST'];

export default function OperatorPage({ addToast }) {
  const [signals,    setSignals]    = useState([]);
  const [running,    setRunning]    = useState(false);
  const [cycleState, setCycleState] = useState('IDLE');
  const [signalMode, setMode]       = useState('AUTOMATIC');
  const [priorityEvent, setPriorityEvent] = useState('NONE');
  const [manual, setManual] = useState({ junctionId: 'J1', direction: 'NORTH' });
  const [priority, setPriority] = useState({ eventType: 'AMBULANCE', junctionId: 'J1', direction: 'NORTH' });
  const [wsStatus,   setWsStatus]   = useState('disconnected');
  const [loading,    setLoading]    = useState('');

  const refreshModeState = useCallback(() => {
    getSignalMode()
      .then(r => {
        setMode(r.data.mode);
        setPriorityEvent(r.data.priorityEvent);
      })
      .catch(() => {});
    getCycleState().then(r => setCycleState(r.data.cycleState)).catch(() => {});
    getCycleRunning().then(r => setRunning(r.data.running)).catch(() => {});
  }, []);

  useEffect(() => {
    getSignalStatus().then(r => setSignals(r.data)).catch(() => {});
    getCycleRunning().then(r => setRunning(r.data.running)).catch(() => {});
    getCycleState().then(r => setCycleState(r.data.cycleState)).catch(() => {});
    getSignalMode().then(r => { setMode(r.data.mode); setPriorityEvent(r.data.priorityEvent); }).catch(() => {});

    connectWebSocket(
      (data) => { setSignals(data); },
      ()     => { setWsStatus('connected'); },
      ()     => { setWsStatus('disconnected'); }
    );
    return () => disconnectWebSocket();
  }, []);

  const handleStart = async () => {
    setLoading('start');
    try {
      const res = await startCycle();
      setRunning(true);
      setCycleState('RUNNING');
      setMode('AUTOMATIC');
      setPriorityEvent('NONE');
      addToast(res.data.message, 'success');
    } catch (e) {
      addToast(e.response?.data?.message || 'Failed to start', 'error');
    } finally { setLoading(''); }
  };

  const handleStop = async () => {
    setLoading('stop');
    try {
      const res = await stopCycle();
      setRunning(false);
      setCycleState('IDLE');
      setMode('AUTOMATIC');
      setPriorityEvent('NONE');
      addToast(res.data.message, 'info');
    } catch (e) {
      addToast(e.response?.data?.message || 'Failed to stop', 'error');
    } finally { setLoading(''); }
  };

  const handleModeChange = async (mode) => {
    setLoading(`mode-${mode}`);
    try {
      const res = await setSignalMode(mode);
      addToast(res.data.message, 'success');
      refreshModeState();
    } catch (e) {
      addToast(e.response?.data?.message || 'Failed to change mode', 'error');
    } finally { setLoading(''); }
  };

  const handleManualApply = async () => {
    setLoading('manual');
    try {
      const res = await applyManualSignal(manual);
      addToast(res.data.message, 'success');
      refreshModeState();
    } catch (e) {
      addToast(e.response?.data?.message || 'Failed to apply manual signal', 'error');
    } finally { setLoading(''); }
  };

  const handlePriorityApply = async () => {
    setLoading('priority');
    try {
      const res = await applyPriority(priority);
      addToast(res.data.message, 'success');
      refreshModeState();
    } catch (e) {
      addToast(e.response?.data?.message || 'Failed to apply priority', 'error');
    } finally { setLoading(''); }
  };

  const handlePriorityClear = async () => {
    setLoading('priority-clear');
    try {
      const res = await applyPriority({ eventType: 'NONE' });
      addToast(res.data.message, 'info');
      refreshModeState();
    } catch (e) {
      addToast(e.response?.data?.message || 'Failed to clear priority', 'error');
    } finally { setLoading(''); }
  };

  const junctions = Object.values((signals || []).reduce((acc, signal) => {
    const key = signal.junctionId || 'J1';
    if (!acc[key]) acc[key] = { id: key, name: signal.junctionName || key, lights: [] };
    acc[key].lights.push(signal);
    return acc;
  }, {}));

  const sc         = CYCLE_STATE_COLORS[cycleState] || CYCLE_STATE_COLORS['IDLE'];

  return (
    <Container className="py-4">
      <div className="d-flex align-items-center justify-content-between mb-4 flex-wrap gap-2">
        <div>
          <h4 style={{ color: 'var(--text-primary)', marginBottom: 2 }}>Intersection Control</h4>
          <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>
            <span className={`ws-dot ${wsStatus}`} /> WebSocket {wsStatus}
          </div>
        </div>
        <div className="d-flex gap-2">
          <Button className="btn-start btn px-4" onClick={handleStart} disabled={running || loading === 'start'}>
            {loading === 'start' ? 'Starting...' : 'Start Cycle'}
          </Button>
          <Button className="btn-stop btn px-4" onClick={handleStop} disabled={!running || loading === 'stop'}>
            {loading === 'stop' ? 'Stopping...' : 'Stop Cycle'}
          </Button>
        </div>
      </div>

      {/* Cycle State Badge */}
      <div className="mb-4 d-flex align-items-center gap-3">
        <span style={{
          display: 'inline-block', padding: '3px 14px', borderRadius: 20,
          fontSize: '0.75rem', fontWeight: 600, letterSpacing: '0.5px',
          background: sc.bg, color: sc.color, border: `1px solid ${sc.border}`,
        }}>
          {cycleState}
        </span>
        <span style={{ fontSize: '0.78rem', color: 'var(--text-muted)' }}>
          TrafficController state | Mode: {signalMode} | Priority: {priorityEvent}
        </span>
      </div>

      <Row className="g-3 mb-4">
        {junctions.map(junction => (
          <Col lg={6} key={junction.id}>
            <div className="dark-card h-100">
              <div className="d-flex justify-content-between align-items-center mb-3">
                <div className="section-title mb-0">{junction.name}</div>
                <span className="mini-chip">{junction.id}</span>
              </div>
              <Row className="g-2">
                {DIRECTIONS.map(dir => {
                  const sig = junction.lights.find(light => light.directionKey === dir);
                  return (
                    <Col xs={6} md={3} key={`${junction.id}-${dir}`}>
                      <TrafficLightCard
                        id={`${junction.id}-${dir}`}
                        direction={sig?.direction || dir}
                        state={sig?.state || 'RED'}
                        colorHex={sig?.colorHex}
                      />
                    </Col>
                  );
                })}
              </Row>
            </div>
          </Col>
        ))}
      </Row>

      <Row className="g-3 mb-4">
        <Col lg={6}>
          <div className="dark-card h-100">
            <div className="section-title">Manual / Automatic Control</div>
            <div className="d-flex gap-2 mb-3 flex-wrap">
              <Button className="btn-diag btn" onClick={() => handleModeChange('AUTOMATIC')} disabled={signalMode === 'AUTOMATIC' || loading}>
                Automatic
              </Button>
              <Button className="btn-diag btn" onClick={() => handleModeChange('MANUAL')} disabled={signalMode === 'MANUAL' || loading}>
                Manual
              </Button>
            </div>
            <Row className="g-2">
              <Col sm={5}>
                <Form.Select className="dark-input" value={manual.junctionId} onChange={e => setManual({ ...manual, junctionId: e.target.value })}>
                  {junctions.map(j => <option key={j.id} value={j.id}>{j.name}</option>)}
                </Form.Select>
              </Col>
              <Col sm={4}>
                <Form.Select className="dark-input" value={manual.direction} onChange={e => setManual({ ...manual, direction: e.target.value })}>
                  {DIRECTIONS.map(d => <option key={d} value={d}>{d}</option>)}
                </Form.Select>
              </Col>
              <Col sm={3}>
                <Button className="btn-start btn w-100" onClick={handleManualApply} disabled={loading === 'manual'}>
                  Apply
                </Button>
              </Col>
            </Row>
          </div>
        </Col>
        <Col lg={6}>
          <div className="dark-card h-100">
            <div className="section-title">Emergency / Rally Priority</div>
            <Row className="g-2">
              <Col sm={4}>
                <Form.Select className="dark-input" value={priority.eventType} onChange={e => setPriority({ ...priority, eventType: e.target.value })}>
                  <option value="AMBULANCE">Ambulance</option>
                  <option value="RALLY">Rally</option>
                </Form.Select>
              </Col>
              <Col sm={4}>
                <Form.Select className="dark-input" value={priority.junctionId} onChange={e => setPriority({ ...priority, junctionId: e.target.value })}>
                  {junctions.map(j => <option key={j.id} value={j.id}>{j.id}</option>)}
                </Form.Select>
              </Col>
              <Col sm={4}>
                <Form.Select className="dark-input" value={priority.direction} onChange={e => setPriority({ ...priority, direction: e.target.value })}>
                  {DIRECTIONS.map(d => <option key={d} value={d}>{d}</option>)}
                </Form.Select>
              </Col>
            </Row>
            <div className="d-flex gap-2 mt-3">
              <Button className="btn-reset btn" onClick={handlePriorityApply} disabled={loading === 'priority'}>
                Activate Priority
              </Button>
              <Button className="btn-diag btn" onClick={handlePriorityClear} disabled={priorityEvent === 'NONE' || loading === 'priority-clear'}>
                Clear
              </Button>
            </div>
          </div>
        </Col>
      </Row>

      {/* Timing Config */}
      <div className="dark-card">
        <div className="section-title">Signal Timing Configuration</div>
        <TimingConfigForm
          onSuccess={msg => { addToast(msg, 'success'); setCycleState('UPDATING'); setTimeout(() => getCycleState().then(r => setCycleState(r.data.cycleState)).catch(() => {}), 600); }}
          onError={msg   => addToast(msg, 'error')}
        />
      </div>
    </Container>
  );
}

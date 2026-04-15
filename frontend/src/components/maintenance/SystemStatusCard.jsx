import React, { useState, useEffect } from 'react';
import { Row, Col } from 'react-bootstrap';
import { getSystemStatus } from '../../services/api';

const CYCLE_STATE_COLORS = {
  IDLE:     { bg: 'rgba(125,133,144,0.15)', color: '#7d8590',  border: '#7d8590'  },
  RUNNING:  { bg: 'rgba(48,209,88,0.15)',   color: '#30D158',  border: '#30D158'  },
  UPDATING: { bg: 'rgba(255,214,10,0.15)',  color: '#FFD60A',  border: '#FFD60A'  },
  MANUAL_CONTROL:   { bg: 'rgba(13,110,253,0.15)', color: '#6ea8fe', border: '#0d6efd' },
  PRIORITY_CONTROL: { bg: 'rgba(255,214,10,0.15)', color: '#FFD60A', border: '#FFD60A' },
  STOPPED:  { bg: 'rgba(255,59,48,0.15)',   color: '#FF3B30',  border: '#FF3B30'  },
};

const MANAGER_STATE_COLORS = {
  UNINITIALIZED: { bg: 'rgba(125,133,144,0.15)', color: '#7d8590', border: '#7d8590' },
  INITIALIZED:   { bg: 'rgba(13,110,253,0.15)',  color: '#6ea8fe', border: '#0d6efd' },
  RUNNING:       { bg: 'rgba(48,209,88,0.15)',   color: '#30D158', border: '#30D158' },
  FAULT:         { bg: 'rgba(255,59,48,0.15)',   color: '#FF3B30', border: '#FF3B30' },
};

function StateBadge({ label, colorMap }) {
  const c = colorMap[label] || colorMap['UNINITIALIZED'];
  return (
    <span style={{
      display: 'inline-block', padding: '2px 12px', borderRadius: 20,
      fontSize: '0.75rem', fontWeight: 600, letterSpacing: '0.5px',
      background: c.bg, color: c.color, border: `1px solid ${c.border}`,
    }}>
      {label}
    </span>
  );
}

export default function SystemStatusCard() {
  const [status, setStatus] = useState(null);

  const fetchStatus = () => {
    getSystemStatus().then(res => setStatus(res.data)).catch(() => {});
  };

  useEffect(() => {
    fetchStatus();
    const interval = setInterval(fetchStatus, 3000);
    return () => clearInterval(interval);
  }, []);

  if (!status) return (
    <div style={{ color: 'var(--text-muted)', fontSize: '0.875rem' }}>Loading status…</div>
  );

  const { cycleRunning, cycleState, managerState, signalMode, priorityEvent, timerConfig, activeLights, junctionCount } = status;

  return (
    <Row className="g-3">
      <Col xs={6} md={3}>
        <div className="dark-card text-center" style={{ padding: '1rem' }}>
          <div className="section-title">Cycle state</div>
          <StateBadge label={cycleState || (cycleRunning ? 'RUNNING' : 'IDLE')} colorMap={CYCLE_STATE_COLORS} />
        </div>
      </Col>
      <Col xs={6} md={3}>
        <div className="dark-card text-center" style={{ padding: '1rem' }}>
          <div className="section-title">Manager state</div>
          <StateBadge label={managerState || 'INITIALIZED'} colorMap={MANAGER_STATE_COLORS} />
        </div>
      </Col>
      <Col xs={6} md={3}>
        <div className="dark-card text-center" style={{ padding: '1rem' }}>
          <div className="section-title">Active lights</div>
          <div style={{ fontSize: '1.5rem', fontWeight: 700 }}>{activeLights}</div>
          <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>{junctionCount || 1} junctions</div>
        </div>
      </Col>
      {timerConfig && (
        <Col xs={6} md={3}>
          <div className="dark-card" style={{ padding: '1rem' }}>
            <div className="section-title">Timings</div>
            {[
              { label: 'Green',  val: timerConfig.greenDuration,  color: '#30D158' },
              { label: 'Yellow', val: timerConfig.yellowDuration, color: '#FFD60A' },
              { label: 'Red',    val: timerConfig.redDuration,    color: '#FF3B30' },
            ].map(item => (
              <div key={item.label} style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 4 }}>
                <span style={{ fontSize: '0.75rem', color: item.color, fontWeight: 600 }}>{item.label}</span>
                <span style={{ fontSize: '0.75rem', color: 'var(--text-primary)' }}>{item.val / 1000}s</span>
              </div>
            ))}
            <div className="divider" style={{ margin: '0.6rem 0' }} />
            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 4 }}>
              <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)', fontWeight: 600 }}>Mode</span>
              <span style={{ fontSize: '0.75rem', color: 'var(--text-primary)' }}>{signalMode}</span>
            </div>
            <div style={{ display: 'flex', justifyContent: 'space-between' }}>
              <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)', fontWeight: 600 }}>Priority</span>
              <span style={{ fontSize: '0.75rem', color: 'var(--text-primary)' }}>{priorityEvent}</span>
            </div>
          </div>
        </Col>
      )}
    </Row>
  );
}

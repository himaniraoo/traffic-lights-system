import React, { useState } from 'react';
import { Container, Button } from 'react-bootstrap';
import DiagnosticsPanel  from '../components/maintenance/DiagnosticsPanel';
import SystemStatusCard  from '../components/maintenance/SystemStatusCard';
import { resetSystem }   from '../services/api';

/**
 * MaintenancePage - Maintenance Engineer role view.
 * Shows system status, diagnostics panel, and reset controls.
 */
export default function MaintenancePage({ addToast }) {
  const [resetLoading, setResetLoading] = useState(false);
  const [confirmReset, setConfirmReset] = useState(false);

  const handleReset = async () => {
    if (!confirmReset) {
      setConfirmReset(true);
      return;
    }
    setResetLoading(true);
    try {
      const res = await resetSystem();
      addToast(res.data.message, 'success');
    } catch {
      addToast('Failed to reset system', 'error');
    } finally {
      setResetLoading(false);
      setConfirmReset(false);
    }
  };

  return (
    <Container className="py-4">

      {/* Header */}
      <div className="mb-4">
        <h4 style={{ color: 'var(--text-primary)', marginBottom: 2 }}>Maintenance Console</h4>
        <p style={{ color: 'var(--text-muted)', fontSize: '0.875rem', margin: 0 }}>
          System diagnostics, health checks, and fault recovery
        </p>
      </div>

      {/* System Status */}
      <div className="dark-card mb-4">
        <div className="section-title">System Status</div>
        <SystemStatusCard />
      </div>

      {/* Diagnostics */}
      <div className="dark-card mb-4">
        <div className="section-title">Run Diagnostics</div>
        <p style={{ color: 'var(--text-muted)', fontSize: '0.85rem', marginBottom: '1rem' }}>
          Checks for overlapping green signals, invalid timer values, and null states.
        </p>
        <DiagnosticsPanel
          onSuccess={msg => addToast(msg, 'success')}
          onError={msg   => addToast(msg, 'error')}
        />
      </div>

      {/* Reset */}
      <div className="dark-card" style={{ borderColor: 'rgba(255,214,10,0.3)' }}>
        <div className="section-title">Factory Reset</div>
        <p style={{ color: 'var(--text-muted)', fontSize: '0.85rem', marginBottom: '1rem' }}>
          Stops the cycle, resets all lights to RED and restores default timing config (5s / 2s / 5s).
          <br/>
          <strong style={{ color: '#FFD60A' }}>This action cannot be undone.</strong>
        </p>
        <Button
          className="btn-reset btn"
          onClick={handleReset}
          disabled={resetLoading}
        >
          {resetLoading
            ? 'Resetting…'
            : confirmReset
              ? '⚠ Confirm — Click again to reset'
              : '↺  Reset to Defaults'}
        </Button>
        {confirmReset && (
          <Button
            variant="link"
            style={{ color: 'var(--text-muted)', fontSize: '0.8rem', marginLeft: 12 }}
            onClick={() => setConfirmReset(false)}
          >
            Cancel
          </Button>
        )}
      </div>

    </Container>
  );
}

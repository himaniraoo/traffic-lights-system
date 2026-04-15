import React, { useState } from 'react';
import { Button } from 'react-bootstrap';
import { runDiagnostics } from '../../services/api';

/**
 * DiagnosticsPanel - Triggers a full diagnostics check and shows results.
 * Props: { onSuccess(msg), onError(msg) }
 */
export default function DiagnosticsPanel({ onSuccess, onError }) {
  const [result, setResult]   = useState(null);
  const [loading, setLoading] = useState(false);

  const handleRun = async () => {
    setLoading(true);
    setResult(null);
    try {
      const res = await runDiagnostics();
      setResult(res.data);
      if (res.data.healthy) {
        onSuccess('Diagnostics passed — system is healthy');
      } else {
        onError(`Diagnostics found ${res.data.errors.length} issue(s)`);
      }
    } catch {
      onError('Failed to run diagnostics');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <Button className="btn-diag btn mb-3" onClick={handleRun} disabled={loading}>
        {loading ? 'Running…' : '▶  Run Diagnostics'}
      </Button>

      {result && (
        <div>
          {result.healthy ? (
            <div className="diag-ok-box">
              ✓ All checks passed — no faults detected
            </div>
          ) : (
            <div>
              {result.errors.map((err, i) => (
                <div className="diag-error-item" key={i}>⚠ {err}</div>
              ))}
            </div>
          )}
          <div style={{ marginTop: 8, fontSize: '0.75rem', color: 'var(--text-muted)' }}>
            Checked at: {new Date(result.checkedAt).toLocaleTimeString()}
          </div>
        </div>
      )}
    </div>
  );
}

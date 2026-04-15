import React, { useState, useEffect } from 'react';
import { Form, Button, Row, Col } from 'react-bootstrap';
import { getTimerConfig, updateTimerConfig } from '../../services/api';

/**
 * TimingConfigForm - Lets the Traffic Operator view and update signal phase durations.
 * Props: { onSuccess(msg), onError(msg) }
 */
export default function TimingConfigForm({ onSuccess, onError }) {
  const [config, setConfig] = useState({
    greenDuration: 5000,
    yellowDuration: 2000,
    redDuration: 5000,
  });
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    getTimerConfig()
      .then(res => setConfig(res.data))
      .catch(() => onError('Failed to load timer config'));
  }, []);

  const handleChange = (e) => {
    setConfig({ ...config, [e.target.name]: parseInt(e.target.value, 10) });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      await updateTimerConfig(config);
      onSuccess('Timer configuration updated');
    } catch (err) {
      onError(err.response?.data?.message || 'Failed to update timer config');
    } finally {
      setLoading(false);
    }
  };

  const fields = [
    { name: 'greenDuration',  label: 'Green Duration (ms)',  min: 1000 },
    { name: 'yellowDuration', label: 'Yellow Duration (ms)', min: 500  },
    { name: 'redDuration',    label: 'Red Duration (ms)',    min: 1000 },
  ];

  return (
    <Form onSubmit={handleSubmit}>
      <Row>
        {fields.map(f => (
          <Col md={4} key={f.name}>
            <Form.Group className="mb-3">
              <Form.Label className="dark-label">{f.label}</Form.Label>
              <Form.Control
                type="number"
                name={f.name}
                value={config[f.name]}
                min={f.min}
                step={500}
                onChange={handleChange}
                className="dark-input"
                required
              />
            </Form.Group>
          </Col>
        ))}
      </Row>
      <Button type="submit" className="btn-start btn" disabled={loading}>
        {loading ? 'Saving…' : 'Save Timing Config'}
      </Button>
    </Form>
  );
}

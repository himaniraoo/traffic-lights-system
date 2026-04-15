import React, { useState, useCallback } from 'react';
import { BrowserRouter, Routes, Route, NavLink, Navigate } from 'react-router-dom';
import { Navbar, Nav, Container } from 'react-bootstrap';
import OperatorPage    from './pages/OperatorPage';
import MaintenancePage from './pages/MaintenancePage';
import Toast           from './components/shared/Toast';

export default function App() {
  const [toasts, setToasts] = useState([]);

  const addToast = useCallback((message, type = 'info') => {
    const id = Date.now();
    setToasts(prev => [...prev, { id, message, type }]);
  }, []);

  const removeToast = useCallback((id) => {
    setToasts(prev => prev.filter(t => t.id !== id));
  }, []);

  return (
    <BrowserRouter>
      {/* ── Navbar ─────────────────────────────────────────────────── */}
      <Navbar className="app-navbar" expand="md" sticky="top">
        <Container>
          <Navbar.Brand href="/">
            <span style={{ fontSize: '1.1rem' }}>🚦</span>
            &nbsp; Traffic Signal System
          </Navbar.Brand>
          <Navbar.Toggle aria-controls="nav" style={{ borderColor: 'var(--border-color)' }} />
          <Navbar.Collapse id="nav">
            <Nav className="ms-auto gap-1">
              <Nav.Link
                as={NavLink}
                to="/operator"
                className={({ isActive }) => isActive ? 'nav-link active' : 'nav-link'}
              >
                Operator Panel
              </Nav.Link>
              <Nav.Link
                as={NavLink}
                to="/maintenance"
                className={({ isActive }) => isActive ? 'nav-link active' : 'nav-link'}
              >
                Maintenance
              </Nav.Link>
            </Nav>
          </Navbar.Collapse>
        </Container>
      </Navbar>

      {/* ── Routes ─────────────────────────────────────────────────── */}
      <Routes>
        <Route path="/"            element={<Navigate to="/operator" replace />} />
        <Route path="/operator"    element={<OperatorPage    addToast={addToast} />} />
        <Route path="/maintenance" element={<MaintenancePage addToast={addToast} />} />
      </Routes>

      {/* ── Toast Area ─────────────────────────────────────────────── */}
      <div className="toast-area">
        {toasts.map(t => (
          <Toast
            key={t.id}
            message={t.message}
            type={t.type}
            onClose={() => removeToast(t.id)}
          />
        ))}
      </div>
    </BrowserRouter>
  );
}

import React from 'react';

/**
 * TrafficLightCard - Displays one physical traffic light with animated bulbs.
 * Props: { id, direction, state, colorHex }
 */
export default function TrafficLightCard({ direction, state }) {
  const s = (state || 'RED').toUpperCase();

  return (
    <div className={`traffic-light-box active-${s.toLowerCase()}`}>
      {/* Housing */}
      <div style={{
        background: '#0a0a0a',
        borderRadius: '12px',
        padding: '10px 8px',
        display: 'inline-block',
        border: '2px solid #222',
        marginBottom: '8px'
      }}>
        <div className={`signal-bulb bulb-red    ${s === 'RED'    ? 'on' : ''}`} />
        <div className={`signal-bulb bulb-yellow ${s === 'YELLOW' ? 'on' : ''}`} />
        <div className={`signal-bulb bulb-green  ${s === 'GREEN'  ? 'on' : ''}`} />
      </div>
      <div className="direction-label">{direction}</div>
      <div style={{ marginTop: 4 }}>
        <span className={`status-badge badge-${s === 'RED' ? 'stopped' : s === 'GREEN' ? 'running' : ''}`}
          style={s === 'YELLOW' ? {
            background: 'rgba(255,214,10,0.15)',
            color: '#FFD60A',
            border: '1px solid #FFD60A',
            display:'inline-block', padding:'2px 10px',
            borderRadius:'20px', fontSize:'0.75rem', fontWeight:600, letterSpacing:'0.5px'
          } : {}}>
          {s}
        </span>
      </div>
    </div>
  );
}

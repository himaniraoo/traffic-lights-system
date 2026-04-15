import React, { useEffect } from 'react';

/**
 * Toast - A temporary notification.
 * Props: { message, type ('success'|'error'|'info'), onClose }
 */
export default function Toast({ message, type = 'info', onClose }) {
  useEffect(() => {
    const t = setTimeout(onClose, 3500);
    return () => clearTimeout(t);
  }, [onClose]);

  return (
    <div className={`app-toast toast-${type}`} role="alert">
      <span style={{ marginRight: 8 }}>
        {type === 'success' ? '✓' : type === 'error' ? '✕' : 'ℹ'}
      </span>
      {message}
    </div>
  );
}

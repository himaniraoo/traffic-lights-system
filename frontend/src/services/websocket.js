import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

let stompClient = null;

/**
 * Connects to the Spring Boot WebSocket broker and subscribes to signal updates.
 * @param {Function} onMessage  - Called with the parsed signal array on each update
 * @param {Function} onConnect  - Called when connection is established
 * @param {Function} onDisconnect - Called when connection is lost
 */
export function connectWebSocket(onMessage, onConnect, onDisconnect) {
  stompClient = new Client({
    webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
    reconnectDelay: 3000,

    onConnect: () => {
      console.log('[WS] Connected to Spring Boot broker');
      stompClient.subscribe('/topic/signals', (frame) => {
        try {
          const signals = JSON.parse(frame.body);
          onMessage(signals);
        } catch (e) {
          console.error('[WS] Failed to parse message:', e);
        }
      });
      if (onConnect) onConnect();
    },

    onDisconnect: () => {
      console.log('[WS] Disconnected');
      if (onDisconnect) onDisconnect();
    },

    onStompError: (frame) => {
      console.error('[WS] STOMP error:', frame);
    },
  });

  stompClient.activate();
}

export function disconnectWebSocket() {
  if (stompClient) {
    stompClient.deactivate();
    stompClient = null;
  }
}

export function isConnected() {
  return stompClient?.connected ?? false;
}

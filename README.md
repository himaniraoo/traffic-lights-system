# Traffic Light Signal Management System

Full-stack project - Spring Boot (Java 17) backend + React frontend.

The system now manages a four-junction urban signal network inspired by a South End Bangalore junction layout. It supports automatic timed cycling, manual operator control, and emergency/rally priority handling.

---

## Project Structure

```
traffic-system/
├── backend/                          ← Spring Boot (Maven)
│   ├── pom.xml
│   └── src/main/java/com/traffic/
│       ├── TrafficSignalApplication.java
│       ├── config/
│       │   ├── CorsConfig.java
│       │   └── WebSocketConfig.java
│       ├── model/                    ← MODEL layer
│       │   ├── SignalState.java       (enum: RED, YELLOW, GREEN)
│       │   ├── Direction.java         (enum: N, S, E, W)
│       │   ├── TimerConfig.java       (phase durations)
│       │   ├── TrafficLight.java      (one light + inner DTO)
│       │   ├── Intersection.java      (groups 4 directional lights)
│       │   ├── JunctionNetwork.java   (groups multiple intersections)
│       │   ├── SignalMode.java        (AUTOMATIC, MANUAL)
│       │   ├── PriorityEventType.java (NONE, AMBULANCE, RALLY)
│       │   ├── ManualSignalRequest.java
│       │   ├── PriorityRequest.java
│       │   └── DiagnosticsResult.java
│       ├── factory/
│       │   └── JunctionNetworkFactory.java    (creates South End-style network)
│       ├── strategy/
│       │   ├── SignalPlanStrategy.java
│       │   ├── AutomaticSignalPlanStrategy.java
│       │   ├── ManualSignalPlanStrategy.java
│       │   └── PrioritySignalPlanStrategy.java
│       ├── service/                  ← CONTROLLER layer (services)
│       │   ├── SignalControllerService.java   (State pattern transitions)
│       │   ├── TrafficControllerService.java  (Singleton, scheduler, WS broadcast)
│       │   ├── DiagnosticsService.java        (read-only validation)
│       │   └── SystemManagerService.java      (reset + diagnostics facade)
│       └── controller/               ← Spring MVC REST controllers
│           ├── TrafficController.java     (Operator endpoints)
│           └── MaintenanceController.java (Maintenance endpoints)
│
└── frontend/                         ← React 18 + Bootstrap 5
    ├── package.json
    └── src/
        ├── App.jsx                   (router + toast system)
        ├── index.js
        ├── index.css                 (dark theme, component styles)
        ├── services/
        │   ├── api.js                (Axios REST calls)
        │   └── websocket.js          (STOMP over SockJS)
        ├── pages/
        │   ├── OperatorPage.jsx      (VIEW: Traffic Operator)
        │   └── MaintenancePage.jsx   (VIEW: Maintenance Engineer)
        └── components/
            ├── shared/
            │   ├── TrafficLightCard.jsx
            │   └── Toast.jsx
            ├── operator/
            │   └── TimingConfigForm.jsx
            └── maintenance/
                ├── DiagnosticsPanel.jsx
                └── SystemStatusCard.jsx
```

---

## REST API Reference

### Traffic Operator - `/api/signals`
| Method | Endpoint              | Description                    |
|--------|-----------------------|--------------------------------|
| GET    | `/api/signals/status` | Get all junction light states  |
| POST   | `/api/signals/start`  | Start automatic signal cycle   |
| POST   | `/api/signals/stop`   | Stop cycle, all lights → RED   |
| GET    | `/api/signals/timer`  | Get current timer config       |
| PUT    | `/api/signals/timer`  | Update timer config (ms)       |
| GET    | `/api/signals/running`| Is the cycle currently running |
| GET    | `/api/signals/mode`   | Get automatic/manual/priority state |
| POST   | `/api/signals/mode/{mode}` | Switch `AUTOMATIC` or `MANUAL` |
| POST   | `/api/signals/manual` | Manually set one junction direction GREEN |
| POST   | `/api/signals/priority` | Activate or clear ambulance/rally priority |

### Maintenance Engineer - `/api/maintenance`
| Method | Endpoint                       | Description              |
|--------|--------------------------------|--------------------------|
| GET    | `/api/maintenance/status`      | System status summary    |
| POST   | `/api/maintenance/diagnostics` | Run diagnostics check    |
| POST   | `/api/maintenance/reset`       | Reset to factory defaults|

### WebSocket
- **Endpoint:** `ws://localhost:8080/ws` (SockJS)
- **Subscribe:** `/topic/signals`
- **Payload:** JSON array of `TrafficLightDTO[]`
  ```json
  [
    { "id": "J1-NORTH", "junctionId": "J1", "junctionName": "South End Main Circle", "directionKey": "NORTH", "direction": "North", "state": "GREEN", "colorHex": "#30D158", "lastTransitionTime": 1700000000000 },
    { "id": "J1-EAST", "junctionId": "J1", "junctionName": "South End Main Circle", "directionKey": "EAST", "direction": "East", "state": "RED", "colorHex": "#FF3B30", "lastTransitionTime": 1700000000000 }
  ]
  ```

---

## Setup & Run

### Backend (Spring Boot)
```bash
cd backend
./mvnw spring-boot:run
# Server starts at http://localhost:8080
```

### Frontend (React)
```bash
cd frontend
npm install
npm start
# App opens at http://localhost:3000
```

---

## Design Patterns Used
| Pattern   | Where                                      |
|-----------|--------------------------------------------|
| Singleton | `TrafficControllerService` (Spring `@Service`) |
| Factory   | `JunctionNetworkFactory` creates the four-junction network |
| Composite | `JunctionNetwork` contains intersections, intersections contain lights |
| Strategy  | `SignalPlanStrategy` for automatic, manual, and priority behavior |
| State     | `SignalState`, `CycleState`, `SignalMode`, `PriorityEventType`, `ManagerState` |
| MVC       | Model (`model/`), Controller (`service/` + `controller/`), View (React pages) |

See `DESIGN_NOTES.md` for the full SOLID, GRASP, nested-state, and pattern explanation.

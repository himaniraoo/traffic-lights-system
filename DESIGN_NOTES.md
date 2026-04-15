# Traffic Signal Management - Extension Notes

## What was added

- Multi-junction support: the backend now creates a seven-junction South End-style network:
  - J1: South End Main Circle
  - J2: Lalbagh Approach
  - J3: Jayanagar 9th Block
  - J4: Metro Feeder Road
  - J5: School Zone Crossing
  - J6: Market T-Junction
  - J7: Hospital Priority Gate
- Each junction has its own North, East, South, and West traffic lights.
- Junctions now have varied profiles: roundabout approach, four-way, bus corridor, school zone, T-junction, pedestrian crossing, and hospital access.
- Junctions also carry load metadata: LOW, MEDIUM, HIGH, or CRITICAL.
- The existing timer is now applied by the scheduler across every junction.
- Adaptive timing changes the effective phase durations for normal, morning peak, school peak, and evening peak hours.
- High-load and critical-load junctions receive longer GREEN phases during adaptive timing.
- Traffic density simulation now tracks vehicle counts at each junction.
- Density levels are LIGHT, MODERATE, HEAVY, and JAMMED.
- Effective GREEN duration combines time-of-day timing profile, junction load, and live traffic density.
- The operator can disable simulation and manually enter vehicle counts to demonstrate density-based timing.
- Automatic mode runs the timed signal cycle for all junctions.
- Manual mode lets the operator choose a specific junction and direction to turn GREEN safely.
- Priority mode supports ambulance and rally events by holding the selected junction/direction GREEN while other lights in that junction remain RED.
- Diagnostics now checks the complete junction network, not only one intersection.
- Diagnostics now reports each individual check and includes a maintenance-only conflict simulation to demonstrate failure handling.
- The operator UI now groups signals by junction and exposes automatic/manual/priority controls.
- The maintenance UI now shows junction count, signal mode, and active priority event.

## Nested state diagram support

The implementation now has nested state information:

- SystemManagerService.ManagerState:
  - UNINITIALIZED
  - INITIALIZED
  - RUNNING
  - FAULT
- TrafficControllerService.CycleState inside the running system:
  - IDLE
  - RUNNING
  - UPDATING
  - MANUAL_CONTROL
  - PRIORITY_CONTROL
  - STOPPED
- TrafficControllerService.SignalMode inside the controller state:
  - AUTOMATIC
  - MANUAL
- PriorityEventType inside priority control:
  - NONE
  - AMBULANCE
  - RALLY

This gives a nested state model: system state contains controller cycle state; controller cycle state contains mode; priority mode contains event type.

## Design patterns implemented

- Creational pattern - Factory:
  - `JunctionNetworkFactory` creates the four-junction network in one place.
- Structural pattern - Composite:
  - `JunctionNetwork` contains multiple `Intersection` objects, and each `Intersection` contains multiple `TrafficLight` objects.
- Behavioral pattern - Strategy:
  - `SignalPlanStrategy` has automatic, manual, and priority implementations.
- Additional pattern - State:
  - `SignalState`, `CycleState`, `SignalMode`, `PriorityEventType`, and `ManagerState` represent explicit state transitions.

## SOLID principles

- Single Responsibility Principle:
  - `TrafficLight` manages one light.
  - `Intersection` manages one junction.
  - `JunctionNetwork` manages the group of junctions.
  - `DiagnosticsService` only validates system health.
- Open/Closed Principle:
  - New signal plans can be added by creating another `SignalPlanStrategy` implementation without rewriting controllers.
- Liskov Substitution Principle:
  - `AutomaticSignalPlanStrategy`, `ManualSignalPlanStrategy`, and `PrioritySignalPlanStrategy` can all be used through the same `SignalPlanStrategy` interface.
- Interface Segregation Principle:
  - Strategy classes depend on a small interface with only one operation: `apply`.
- Dependency Inversion Principle:
  - High-level control flow uses the `SignalPlanStrategy` abstraction rather than hard-coding every plan directly into the controller.

## GRASP principles

- Information Expert:
  - `Intersection` owns the lights, so it performs junction-level operations such as reset and activate-only.
- Creator:
  - `JunctionNetworkFactory` creates intersections and the network.
- Controller:
  - `TrafficController` receives operator requests and delegates to `TrafficControllerService`.
- Low Coupling:
  - Controllers do not manipulate traffic lights directly; services and models handle domain logic.
- High Cohesion:
  - Classes are grouped by responsibility: model, controller, service, factory, and strategy.
- Polymorphism:
  - Signal behavior changes through strategy implementations.
- Indirection:
  - REST controllers act as an interface between the UI and backend services.
- Protected Variations:
  - Manual, automatic, and priority behavior can evolve independently behind `SignalPlanStrategy`.

## Main files changed

- `backend/src/main/java/com/traffic/model/JunctionNetwork.java`
- `backend/src/main/java/com/traffic/model/JunctionType.java`
- `backend/src/main/java/com/traffic/model/TrafficLoad.java`
- `backend/src/main/java/com/traffic/model/TrafficDensity.java`
- `backend/src/main/java/com/traffic/model/TrafficDensityRequest.java`
- `backend/src/main/java/com/traffic/model/TimingProfile.java`
- `backend/src/main/java/com/traffic/factory/JunctionNetworkFactory.java`
- `backend/src/main/java/com/traffic/strategy/*`
- `backend/src/main/java/com/traffic/service/TrafficControllerService.java`
- `backend/src/main/java/com/traffic/service/SignalControllerService.java`
- `backend/src/main/java/com/traffic/service/DiagnosticsService.java`
- `frontend/src/pages/OperatorPage.jsx`
- `frontend/src/components/maintenance/SystemStatusCard.jsx`
- `frontend/src/components/maintenance/DiagnosticsPanel.jsx`
- `frontend/src/services/api.js`

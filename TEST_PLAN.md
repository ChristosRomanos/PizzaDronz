# PizzaDronz Test Plan (portfolio evidence)

## Scope and objectives
Validate order handling, geometry utilities, and pathfinding behavior for the PizzaDronz REST API, with evidence from unit, integration, and system tests plus coverage reports.

## Requirement to test mapping
- F1, F2: OrderValidationTests (604 real orders, invalid card/date/price cases).
- F3: LngLatHandlingTests (distanceTo, isCloseTo, nextPosition, angle validation).
- F4, F5: PathFindingTests (invariants for steps, no-fly zone avoidance, central-region constraint).
- F6: PathCalculationAsGeoJsonTests (GeoJSON structure and size).
- Q1: PathFindingTests timeout enforcement (60s per path).
- Q2: EPSILON_ERROR usage in geometry assertions.
- Q3, U1: ControllerMethodTests and OrderValidationTests validate response codes.

## Test levels and techniques
- Unit: LngLatHandlingTests, validation helpers.
- Integration: PathFindingTests, PathCalculationAsGeoJsonTests, ControllerMethodTests.
- System: OrderValidationTests against live REST endpoints.
- Techniques: boundary value analysis, equivalence partitioning, decision tables, invariants, randomized tests, dynamic tests, negative tests.

## Instrumentation and scaffolding
- ExecutorService with timeouts for pathfinding.
- RestTemplate scaffolding for live orders, restaurants, and no-fly zones.
- JaCoCo coverage reports and Surefire XML results for evidence.

## Lifecycle and automation
- Local runs via `mvn test`.
- CI in GitHub Actions (`.github/workflows/ci.yml`, `.github/workflows/ci-cd.yml`) on push and PR.
- JaCoCo checks enforce minimum coverage thresholds.

## Risks and limitations
- External REST endpoints can be unavailable or slow.
- Random tests are not seeded and can vary between runs.
- HTTP-level controller tests are missing (method-level tests only).
- No mutation, load, or fault-injection testing yet.

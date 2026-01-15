# Implementation Roadmap - Completing Missing Testing Components

## Overview
This document provides a step-by-step roadmap to complete the identified testing gaps in the PizzaDronz project.

---

## ✅ Phase 1: Foundation (COMPLETED)

### 1.1 JaCoCo Integration ✅
- **Status**: COMPLETE
- **Coverage**: 85% instruction, 90% branch
- **Files Modified**: `pom.xml`
- **Result**: Coverage reports now generated in `target/site/jacoco/`

### 1.2 Code Cleanup ✅
- **Status**: COMPLETE
- **Actions Taken**:
  - Added magic number constants (CREDIT_CARD_NUMBER_LENGTH, CVV_LENGTH, etc.)
  - Added @DisplayName annotations to all test classes
  - Fixed import conflicts in OrderValidationTests
- **Files Modified**: 
  - `SystemConstants.java`
  - `OrderValidationService.java`
  - All test classes

### 1.3 CI/CD Pipeline Definition ✅
- **Status**: COMPLETE
- **File Created**: `.github/workflows/ci-cd.yml`
- **Features**: Multi-stage pipeline with build, test, coverage, security scan, packaging

---

## 🔄 Phase 2: Controller Testing (HIGH PRIORITY)

### 2.1 Add MockMvc Controller Tests
**Priority**: CRITICAL (0% controller coverage)
**Estimated Effort**: 2-3 days
**Target Coverage**: 80% of controller code

#### Implementation Steps:

**Step 1**: Create base controller test setup
```java
@SpringBootTest
@AutoConfigureMockMvc
public abstract class BaseControllerTest {
    @Autowired
    protected MockMvc mockMvc;
    
    @Autowired
    protected ObjectMapper objectMapper;
}
```

**Step 2**: Create OrderValidationController tests
- Test `/validateOrder` endpoint with valid orders
- Test `/validateOrder` endpoint with invalid orders
- Test error handling for malformed JSON
- Test proper HTTP status codes (200, 400, 500)

**Step 3**: Create PathCalculationController tests
- Test `/calcDeliveryPath` endpoint
- Test `/calcDeliveryPathAsGeoJson` endpoint
- Test with valid and invalid orders
- Test timeout scenarios

**Step 4**: Create basic Controller tests
- Test `/uuid` endpoint
- Test `/distanceTo` endpoint
- Test `/isCloseTo` endpoint
- Test `/nextPosition` endpoint
- Test `/isInRegion` endpoint

#### Expected Results:
- Controller coverage: 0% → 80%
- Overall line coverage: 85% → 92%
- New tests added: ~30

---

## 🔄 Phase 3: Enhanced Test Coverage (MEDIUM PRIORITY)

### 3.1 Expand PathFinding Tests
**Priority**: MEDIUM
**Estimated Effort**: 1-2 days
**Target**: 1,000+ random position tests

#### Implementation:
```java
@RepeatedTest(1000)
@DisplayName("Random Position Pathfinding Stress Test")
public void testRandomPositions() {
    // Generate random positions within bounds
    // Ensure paths avoid no-fly zones
    // Track statistics on path quality
}
```

**Expected Results**:
- Statistical confidence in no-fly zone avoidance
- Identify edge cases in obstacle avoidance
- Performance profiling data

### 3.2 Add Fault Injection Tests
**Priority**: MEDIUM
**Estimated Effort**: 2 days

#### Implementation with WireMock:
```java
@RegisterExtension
static WireMockExtension wireMock = WireMockExtension.newInstance()
    .options(wireMockConfig().dynamicPort())
    .build();

@Test
public void testRestaurantServiceTimeout() {
    wireMock.stubFor(get(urlEqualTo("/restaurants"))
        .willReturn(aResponse()
            .withFixedDelay(5000)
            .withStatus(200)));
    
    // Test timeout handling
}
```

**Scenarios to Test**:
- External REST API timeouts
- Network failures
- Malformed JSON responses
- HTTP 500 errors
- Empty responses

### 3.3 Add Concurrency Tests
**Priority**: MEDIUM
**Estimated Effort**: 1-2 days

#### Implementation:
```java
@Test
public void testConcurrentOrderValidation() throws InterruptedException {
    ExecutorService executor = Executors.newFixedThreadPool(10);
    List<Future<?>> futures = new ArrayList<>();
    
    for (int i = 0; i < 100; i++) {
        futures.add(executor.submit(() -> {
            // Validate orders concurrently
        }));
    }
    
    // Assert no race conditions
}
```

---

## 🔄 Phase 4: Advanced Testing (LOW PRIORITY)

### 4.1 Mutation Testing with PIT
**Priority**: LOW
**Estimated Effort**: 1 day

#### Add to pom.xml:
```xml
<plugin>
    <groupId>org.pitest</groupId>
    <artifactId>pitest-maven</artifactId>
    <version>1.15.3</version>
    <configuration>
        <targetClasses>
            <param>uk.ac.ed.inf.pizzadronz.Services.*</param>
        </targetClasses>
        <targetTests>
            <param>uk.ac.ed.inf.pizzadronz.*</param>
        </targetTests>
        <mutationThreshold>75</mutationThreshold>
    </configuration>
</plugin>
```

#### Run:
```bash
./mvnw pitest:mutationCoverage
```

**Expected**: Identify weak assertions and improve test quality

### 4.2 Property-Based Testing with jqwik
**Priority**: LOW
**Estimated Effort**: 2 days

#### Add dependency:
```xml
<dependency>
    <groupId>net.jqwik</groupId>
    <artifactId>jqwik</artifactId>
    <version>1.8.2</version>
    <scope>test</scope>
</dependency>
```

#### Example Test:
```java
@Property
public void testDistanceCalculationProperties(
    @ForAll @DoubleRange(min = -180, max = 180) double lng1,
    @ForAll @DoubleRange(min = -90, max = 90) double lat1,
    @ForAll @DoubleRange(min = -180, max = 180) double lng2,
    @ForAll @DoubleRange(min = -90, max = 90) double lat2) {
    
    LngLat pos1 = new LngLat(lng1, lat1);
    LngLat pos2 = new LngLat(lng2, lat2);
    
    double distance = lngLatHandler.distanceTo(pos1, pos2);
    
    // Properties that should always hold
    assertTrue(distance >= 0);
    assertEquals(distance, lngLatHandler.distanceTo(pos2, pos1));
}
```

### 4.3 Performance Benchmarking with JMH
**Priority**: LOW
**Estimated Effort**: 2 days

#### Add JMH dependency and create benchmarks:
```java
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class PathFindingBenchmark {
    
    @Benchmark
    public void benchmarkPathFinding() {
        // Benchmark pathfinding performance
    }
}
```

---

## 📊 Progress Tracking

### Current Status (Phase 1 Complete)

| Metric | Current | Target | Progress |
|--------|---------|--------|----------|
| Instruction Coverage | 85% | 95% | 🟨 90% |
| Branch Coverage | 90% | 95% | 🟨 95% |
| Line Coverage | 81% | 95% | 🟨 85% |
| Controller Coverage | 0% | 80% | 🟥 0% |
| Total Tests | 852 | 1,500+ | 🟨 57% |

### Phase 2 Targets (After Controller Tests)

| Metric | Target | Expected |
|--------|--------|----------|
| Instruction Coverage | 95% | 92% |
| Line Coverage | 95% | 92% |
| Controller Coverage | 80% | 80% |
| Total Tests | 1,000 | 882 |

---

## 🚀 Quick Start Guide

### For Immediate Implementation:

**1. Generate Coverage Report:**
```bash
./mvnw clean test jacoco:report
```
Open: `target/site/jacoco/index.html`

**2. View Current Tests:**
```bash
./mvnw test
```

**3. Run Specific Test Class:**
```bash
./mvnw test -Dtest=OrderValidationTests
```

**4. Run with Coverage Check:**
```bash
./mvnw clean verify
```

---

## 📝 Implementation Checklist

### Phase 2: Controller Testing
- [ ] Create BaseControllerTest abstract class
- [ ] Add OrderValidationController tests (10 tests)
- [ ] Add PathCalculationController tests (8 tests)
- [ ] Add basic Controller tests (12 tests)
- [ ] Achieve 80% controller coverage
- [ ] Update portfolio documentation

### Phase 3: Enhanced Coverage
- [ ] Expand PathFindingTests to 1,000 positions
- [ ] Add WireMock dependency
- [ ] Create fault injection tests (15 tests)
- [ ] Add concurrency tests (5 tests)
- [ ] Document performance characteristics

### Phase 4: Advanced Testing
- [ ] Add PIT mutation testing plugin
- [ ] Run mutation coverage analysis
- [ ] Add jqwik dependency
- [ ] Create property-based tests (10 tests)
- [ ] Add JMH benchmarking
- [ ] Create performance baseline

---

## 📚 Resources

### Documentation
- [JaCoCo Maven Plugin](https://www.jacoco.org/jacoco/trunk/doc/maven.html)
- [Spring Boot Testing](https://spring.io/guides/gs/testing-web/)
- [MockMvc Documentation](https://docs.spring.io/spring-framework/reference/testing/spring-mvc-test-framework.html)
- [WireMock Documentation](https://wiremock.org/docs/)
- [PIT Mutation Testing](https://pitest.org/)
- [jqwik User Guide](https://jqwik.net/docs/current/user-guide.html)

### Example Commands
```bash
# Run tests with coverage
./mvnw clean test jacoco:report

# Check coverage thresholds
./mvnw jacoco:check

# Run mutation testing (after setup)
./mvnw pitest:mutationCoverage

# Run specific test
./mvnw test -Dtest=OrderValidationTests#testOrderValidation

# Run tests matching pattern
./mvnw test -Dtest=*ValidationTests
```

---

## 🎯 Success Criteria

### Phase 2 Success:
- ✅ Controller coverage ≥ 80%
- ✅ Overall line coverage ≥ 92%
- ✅ All HTTP endpoints tested
- ✅ Error handling tested

### Phase 3 Success:
- ✅ 1,000+ pathfinding positions tested
- ✅ Fault injection scenarios covered
- ✅ Concurrent execution validated
- ✅ No race conditions detected

### Phase 4 Success:
- ✅ Mutation score ≥ 75%
- ✅ Property-based tests passing
- ✅ Performance baseline established
- ✅ Benchmarks integrated in CI/CD

---

*Document Version: 1.0*
*Last Updated: January 15, 2026*
*Next Review: After Phase 2 Completion*

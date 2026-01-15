# JaCoCo Code Coverage Report

## Summary Statistics

Generated: January 15, 2026

### Overall Coverage
- **Instruction Coverage**: 85% (1,911 of 2,236 instructions covered)
- **Branch Coverage**: 90% (189 of 208 branches covered)
- **Line Coverage**: 81% (357 of 438 lines covered)
- **Method Coverage**: 70% (69 of 98 methods covered)
- **Class Coverage**: 70% (19 of 27 classes covered)

## Coverage by Package

### 1. uk.ac.ed.inf.pizzadronz.Services ✅
- **Instruction Coverage**: 97%
- **Branch Coverage**: 91%
- **Line Coverage**: 94% (216 of 229 lines)
- **Method Coverage**: 100% (28 of 28 methods)
- **Status**: EXCELLENT - Core business logic is well tested

### 2. uk.ac.ed.inf.pizzadronz.Data ✅
- **Instruction Coverage**: 91%
- **Branch Coverage**: 100%
- **Line Coverage**: 85% (84 of 99 lines)
- **Method Coverage**: 82% (32 of 39 methods)
- **Status**: GOOD - Data models are adequately tested

### 3. uk.ac.ed.inf.pizzadronz.Constants ✅
- **Instruction Coverage**: 98%
- **Branch Coverage**: N/A
- **Line Coverage**: 97% (39 of 40 lines)
- **Method Coverage**: 85% (6 of 7 methods)
- **Status**: EXCELLENT - Constants class nearly fully covered

### 4. uk.ac.ed.inf.pizzadronz.RequestBodies ⚠️
- **Instruction Coverage**: 78%
- **Branch Coverage**: 77%
- **Line Coverage**: 85% (18 of 21 lines)
- **Method Coverage**: 100% (3 of 3 methods)
- **Status**: GOOD - Request body validation needs improvement

### 5. uk.ac.ed.inf.pizzadronz.Controllers ❌
- **Instruction Coverage**: 0%
- **Branch Coverage**: N/A
- **Line Coverage**: 0% (0 of 44 lines)
- **Method Coverage**: 0% (0 of 18 methods)
- **Class Coverage**: 0% (0 of 6 classes)
- **Status**: CRITICAL - No controller tests exist

### 6. uk.ac.ed.inf.pizzadronz (Main Application) ❌
- **Instruction Coverage**: 0%
- **Line Coverage**: 0% (0 of 3 lines)
- **Method Coverage**: 0% (0 of 2 methods)
- **Status**: EXPECTED - Main application class not tested

### 7. uk.ac.ed.inf.pizzadronz.ServiceInterfaces ❌
- **Instruction Coverage**: 0%
- **Line Coverage**: 0% (0 of 2 lines)
- **Status**: EXPECTED - Interfaces don't need testing

## Key Findings

### ✅ Strengths
1. **Excellent Service Layer Coverage (97%)**: Core business logic is thoroughly tested
2. **Strong Branch Coverage (91% in Services)**: Decision points are well-tested
3. **Complete Method Coverage in Services**: All 28 service methods have tests
4. **Data Layer Coverage (91%)**: Data models are well-tested

### ❌ Critical Gaps
1. **Zero Controller Coverage**: REST API endpoints completely untested
   - OrderValidationController (0%)
   - PathCalculationController (0%)
   - All HTTP layer logic untested
   
2. **Missing Integration Tests**: No end-to-end HTTP tests with MockMvc

### ⚠️ Areas for Improvement
1. **RequestBodies Coverage (78%)**: Validation logic in request bodies needs more tests
2. **Uncovered Branches (19 branches)**: Some edge cases in services not tested
3. **Uncovered Methods (29 methods)**: Primarily in controllers and getters/setters

## Detailed Analysis

### Coverage vs. Target Levels

| Metric | Current | Target | Status |
|--------|---------|--------|--------|
| Line Coverage | 85% | 95% | ⚠️ 10% below target |
| Branch Coverage | 90% | 90% | ✅ At target |
| Method Coverage | 70% | 100% | ❌ 30% below target |

### Uncovered Critical Code

1. **Controllers Package (44 lines uncovered)**
   - `OrderValidationController.validateOrder()` - Critical validation endpoint
   - `PathCalculationController.dronePath()` - Path calculation endpoint
   - `PathCalculationController.dronePathGeoJson()` - GeoJSON endpoint
   - Exception handlers for all controllers

2. **Service Methods (29 methods uncovered)**
   - Primarily controller-related service methods
   - Some getter/setter methods in data classes

## Recommendations

### High Priority (Immediate)
1. **Add Controller Tests**: Create MockMvc tests for REST endpoints
   - Target: 80% controller coverage
   - Estimated effort: 2-3 days
   
2. **Add Missing Branch Tests**: Cover the 19 uncovered branches
   - Target: 95% branch coverage
   - Estimated effort: 1 day

### Medium Priority (This Sprint)
3. **Improve RequestBodies Coverage**: Add validation tests
   - Target: 90% coverage
   - Estimated effort: 1 day

4. **Add Edge Case Tests**: Cover uncovered service methods
   - Target: 95% method coverage
   - Estimated effort: 2 days

### Low Priority (Next Sprint)
5. **Mutation Testing**: Add PIT framework to validate test quality
6. **Performance Testing**: Add JMH benchmarks

## JaCoCo Configuration

### Current Settings
- **Line Coverage Minimum**: 80% (BUILD PASSING)
- **Branch Coverage Minimum**: 75% (BUILD PASSING)
- **Report Location**: `target/site/jacoco/index.html`

### Recommended Adjustments
After adding controller tests:
- Increase line coverage minimum to 90%
- Increase branch coverage minimum to 85%
- Add class-level exclusions for main application and interfaces

## Next Steps

1. ✅ **JaCoCo Added**: Coverage reporting is now active
2. ⏳ **Controller Tests**: Need to add MockMvc integration tests
3. ⏳ **CI/CD Integration**: Add coverage reporting to pipeline
4. ⏳ **Coverage Badges**: Add coverage badges to README
5. ⏳ **Trend Tracking**: Set up coverage trend monitoring

## Access Coverage Report

Open the detailed HTML report:
```
target/site/jacoco/index.html
```

Or run:
```bash
./mvnw test jacoco:report
```

---

*Report Generated: January 15, 2026*
*Test Execution: 852 tests, 100% pass rate*
*Coverage Tool: JaCoCo 0.8.11*

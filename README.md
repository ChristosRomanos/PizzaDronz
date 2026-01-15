# PizzaDronz - Drone Delivery System

![CI/CD Status](https://github.com/ChristosRomanos/PizzaDronz/workflows/PizzaDronz%20CI/CD%20-%20Tests%20and%20Coverage/badge.svg)
![Java Version](https://img.shields.io/badge/Java-18-orange)
![Maven](https://img.shields.io/badge/Maven-3.9+-blue)
![Coverage](https://img.shields.io/badge/Coverage-85%25-green)

## 📊 Testing Portfolio

Comprehensive testing portfolio for autonomous drone pizza delivery system.

### Test Statistics
- **Total Tests**: 867
- **Pass Rate**: 100% ✅
- **Instruction Coverage**: 85%
- **Branch Coverage**: 90% ✅
- **Execution Time**: ~30 seconds

### Test Structure
```
├── LngLatHandlingTests (207 tests)
│   ├── Data validation
│   ├── Distance calculations
│   ├── Region containment
│   └── Angle validation
├── OrderValidationTests (604 tests)
│   └── Real API order validation
├── PathFindingTests (29 tests)
│   └── A* pathfinding algorithm
├── PathCalculationAsGeoJsonTests (12 tests)
│   └── GeoJSON output format
└── ControllerTests (15 tests)
    └── Controller business logic
```

## 🚀 Quick Start

### Run Tests
```bash
./mvnw test
```

### Generate Coverage Report
```bash
./mvnw test jacoco:report
start target/site/jacoco/index.html
```

### Build Project
```bash
./mvnw clean package
```

## 📈 CI/CD Pipeline

The project uses GitHub Actions for continuous integration:

- ✅ Automated testing on every commit
- ✅ JaCoCo coverage reports
- ✅ Test result publishing
- ✅ Artifact preservation (30 days)
- ✅ PR comments with coverage stats

### View CI/CD Results
Visit the [Actions tab](https://github.com/ChristosRomanos/PizzaDronz/actions) to see:
- Test execution results
- Coverage reports (downloadable artifacts)
- Build logs

## 📝 Portfolio Document

The complete testing portfolio is available in `portofolio.tex` (3 pages, all 5 learning outcomes covered).

**Compile to PDF:**
```bash
pdflatex portofolio.tex
```

## 🧪 Testing Techniques Applied

1. **Boundary Value Analysis** - MIN/MAX coordinate testing
2. **Equivalence Partitioning** - Valid/invalid input classes
3. **Decision Table Testing** - 604 order scenarios
4. **Property-Based Testing** - Path invariants
5. **Data-Driven Testing** - External REST API data
6. **Combinatorial Testing** - All compass directions
7. **Random Testing** - 20 random positions
8. **Negative Testing** - Exception handling
9. **Integration Testing** - End-to-end workflows

## 📊 Coverage Details

| Package | Instruction | Branch | Line | Status |
|---------|-------------|--------|------|--------|
| Services | 97% | 91% | 94% | ✅ Excellent |
| Data | 91% | 100% | 85% | ✅ Good |
| Constants | 98% | N/A | 97% | ✅ Excellent |
| RequestBodies | 78% | 77% | 85% | ✅ Good |
| Controllers | 0% | N/A | 0% | ⚠️ Gap (documented) |

## 🛠️ Tech Stack

- **Language**: Java 18
- **Framework**: Spring Boot
- **Build Tool**: Maven
- **Testing**: JUnit 5
- **Coverage**: JaCoCo
- **CI/CD**: GitHub Actions

## 📚 Documentation

- `portofolio.tex` - Complete testing portfolio (3 pages)
- `FINAL_STATUS.md` - Project completion summary
- `COVERAGE_REPORT.md` - Detailed coverage analysis
- `IMPLEMENTATION_ROADMAP.md` - Future improvements
- `GITHUB_ACTIONS_GUIDE.md` - CI/CD documentation

## 🎯 Learning Outcomes

The portfolio demonstrates proficiency in:

1. **LO1: Requirements Analysis** (20%) - Testing strategies for functional, quality, and safety requirements
2. **LO2: Test Plan Design** (20%) - Hierarchical test structure with instrumentation
3. **LO3: Testing Techniques** (20%) - 9 techniques applied with 90% branch coverage
4. **LO4: Limitations Evaluation** (20%) - Gap analysis and improvement roadmap
5. **LO5: Automation & CI/CD** (20%) - Automated testing and deployment pipeline

## 🔍 Known Limitations

Documented in `portofolio.tex` and `COVERAGE_REPORT.md`:

- Controller layer: 0% HTTP-level coverage (Spring context conflicts)
- Mutation testing: Not implemented
- Spatial coverage: 49 vs 10,000 needed positions
- No fault injection testing
- No concurrency testing

All limitations have improvement plans in `IMPLEMENTATION_ROADMAP.md`.

## 📧 Contact

Christos Romanos - s2149970

---

**Status**: ✅ Ready for Submission  
**Tests**: 867 passing  
**Coverage**: 85% instruction, 90% branch  
**Portfolio**: Complete (3 pages)

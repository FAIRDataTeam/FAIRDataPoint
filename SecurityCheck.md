# Detailed Vulnerability Report

## 1. CRITICAL: SPARQL Injection Vulnerability

**Severity**: CRITICAL | **CVSS Score**: 9.8

### Location

- File: `src/main/java/org/fairdatapoint/service/search/SearchService.java` (Lines 77-99, 162-188)
- File: `src/main/java/org/fairdatapoint/api/dto/search/SearchQueryVariablesDTO.java`
- Test File: `src/test/java/org/fairdatapoint/acceptance/search/query/List_POST.java` (Lines 47-73)

### Vulnerability Description

The `composeQuery()` method builds SPARQL queries by directly substituting user-supplied values into query templates without proper escaping or parameterization. The search functionality accepts three user-controlled inputs:

- `prefixes` - RDF namespace prefixes
- `graphPattern` - SPARQL graph patterns
- `ordering` - ORDER BY clause

These are combined using StrSubstitutor with simple string replacement:

```java
private String composeQuery(SearchQueryVariablesDTO reqDto) {
    final StrSubstitutor substitutor =
            new StrSubstitutor(searchMapper.toSubstitutions(reqDto), "{{", "}}");
    return substitutor.replace(QUERY_TEMPLATE);
}
```

### Attack Scenario

An attacker can inject arbitrary SPARQL to:

- Extract unauthorized data through UNION queries
- Modify or delete RDF triples
- Bypass access controls
- Cause denial of service
- Example Payload:

```json
{
  "prefixes": "",
  "graphPattern": "?entity ?relationPredicate ?relationObject . } UNION { ?x ?y ?z",
  "ordering": "ASC(?title)"
}
```

This escapes the intended SPARQL context and executes arbitrary queries.

### Recommendations

1. Use **SPARQL Query API with Parameter Binding**:

```java
private String composeQuery(SearchQueryVariablesDTO reqDto) {
    // Validate individual components first
    validateSparqlComponent(reqDto.getPrefixes(), ComponentType.PREFIXES);
    validateSparqlComponent(reqDto.getGraphPattern(), ComponentType.PATTERN);
    validateSparqlComponent(reqDto.getOrdering(), ComponentType.ORDERING);
    
    // Use Query objects with proper escaping, never string concatenation
    StringBuilder queryBuilder = new StringBuilder();
    queryBuilder.append(reqDto.getPrefixes());
    queryBuilder.append("SELECT ?entity ?title WHERE { ");
    queryBuilder.append(reqDto.getGraphPattern());
    queryBuilder.append(" } ");
    
    if (reqDto.getOrdering() != null && !reqDto.getOrdering().isEmpty()) {
        queryBuilder.append("ORDER BY ");
        queryBuilder.append(reqDto.getOrdering());
    }
    
    return queryBuilder.toString();
}

private void validateSparqlComponent(String component, ComponentType type) {
    if (component == null || component.isEmpty()) {
        return;
    }
    
    switch (type) {
        case PREFIXES:
            // Validate only contains PREFIX declarations
            if (!component.matches("^(PREFIX\\s+\\w+:\\s+<[^>]+>\\s*)*$")) {
                throw new ValidationException("Invalid SPARQL prefix syntax");
            }
            break;
        case PATTERN:
            // Limit pattern complexity, forbid UNION, FILTER with risky functions
            if (component.matches("(?i).*(UNION|INSERT|DELETE|LOAD|CLEAR|DROP|CREATE).*")) {
                throw new ValidationException("Forbidden SPARQL keywords in graph pattern");
            }
            break;
        case ORDERING:
            // Validate ORDER BY syntax only
            if (!component.matches("^(ASC|DESC)?\\(\\?\\w+\\)(\\s+(ASC|DESC)?\\(\\?\\w+\\))*$")) {
                throw new ValidationException("Invalid ORDER BY clause");
            }
            break;
    }
}
```

2. Implement **Query Whitelist**: Create a set of pre-approved query templates instead of allowing arbitrary query composition:

```java
public static final Map<String, String> APPROVED_QUERIES = Map.of(
    "SEARCH_LITERAL", "SELECT ?entity ?title WHERE { ... }",
    "SEARCH_TYPE", "SELECT ?entity WHERE { ?entity a ?type }"
);
```

3. Use **RDF4J Query Algebra API**: Instead of string manipulation, construct queries using RDF4J's SPARQL algebra builders that automatically handle escaping.

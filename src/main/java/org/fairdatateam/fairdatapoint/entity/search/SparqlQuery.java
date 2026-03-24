package org.fairdatateam.fairdatapoint.entity.search;

/**
 * Defines the content of a full SPARQL query
 * @param query
 * @param defaultGraphUris
 * @param namedGraphUris
 */
public record SparqlQuery(String query, String[] defaultGraphUris, String[] namedGraphUris) {
}

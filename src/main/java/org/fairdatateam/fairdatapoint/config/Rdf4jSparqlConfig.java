package org.fairdatateam.fairdatapoint.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Make sure the SparqlQueryEvaluator implementation bean can be found (SparqlQueryEvaluatorDefault)
 */
@Configuration
@ComponentScan("org.eclipse.rdf4j.http.server.readonly.sparql")
public class Rdf4jSparqlConfig {
}

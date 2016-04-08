###FAIRDataPoint (FDP) Metadata Service

**Example dataset**: [EU-SOL BreeDB database](http://www.eu-sol.wur.nl) (tomato passport data)

**SPARQL endpoint**: [BreeDB](http://virtuoso.biotools.nl:8888/sparql/); *Graph IRI: http://www.eu-sol.wur.nl/passport*

**RESTful API documentation**

http://fdp.biotools.nl:8080/

**Access FDP-, catalog-, dataset- and distribution-level metadata**

http://virtuoso.biotools.nl:8888/describe/?url=http://fdp.biotools.nl:8080/fdp

```
curl -iH 'Accept: text/turtle' http://fdp.biotools.nl:8080/fdp
curl -iH 'Accept: text/turtle' http://fdp.biotools.nl:8080/catalog/catalog-01
curl -iH 'Accept: text/turtle' http://fdp.biotools.nl:8080/dataset/breedb
curl -iH 'Accept: text/turtle' http://fdp.biotools.nl:8080/distribution/breedb-sparql
```
Note: FDP supports different RDF serializations (MIME-types):
+ Turtle (text/turtle)
+ N-Triples (application/n-triples)
+ RDF/XML (application/rdf+xml)
+ JSON-LD (application/ld+json)

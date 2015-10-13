--
-- RDF View on relational data in BreeDB using R2RML mapping language.
--
-- Author: Arnold Kuzniar
--

-- clear graphs
SPARQL CLEAR GRAPH <http://temp/germplasm>;
SPARQL CLEAR GRAPH <http://example.com/germplasm/>;

-- insert R2RML into a temporary graph
DB.DBA.TTLP('
@prefix rr: <http://www.w3.org/ns/r2rml#> .
@prefix exa: <http://example.com/ns#> .
@prefix germplasm: <http://example.com/germplasm#> .
@prefix geo: <http://www.w3.org/2003/01/geo/wgs84_pos#> .
@prefix gterm: <http://purl.org/germplasm/germplasmTerm#> .
@prefix dwc: <http://rs.tdwg.org/dwc/terms/> .

<exa:#TriplesMap1>
    a rr:TriplesMap;

    rr:logicalTable
    [
      rr:tableSchema "R2RML";
      rr:tableOwner  "TEST";
      rr:tableName   "pp_accession";
    ];

    rr:subjectMap
    [
      rr:template "http://example.com/germplasm/{accessionID}";
      rr:class gterm:GermplasmAccession;
      rr:graph <http://example.com/germplasm/>;
    ];

    rr:predicateObjectMap
    [
      rr:predicate gterm:germplasmID;
      rr:objectMap [ rr:column "accessionID" ];
    ];

    rr:predicateObjectMap
    [
      rr:predicate gterm:germplasmIdentifier;
      rr:objectMap [ rr:column "accessionName" ];
    ];

    rr:predicateObjectMap
    [
      rr:predicate geo:lat;
      rr:objectMap [ rr:column "gpsLat"; rr:datatype xsd:decimal ];
    ];

    rr:predicateObjectMap
    [
      rr:predicate geo:long;
      rr:objectMap [ rr:column "gpsLong"; rr:datatype xsd:decimal ];
    ];

    rr:predicateObjectMap
    [
      rr:predicate geo:alt;
      rr:objectMap [ rr:column "elevation"; rr:datatype xsd:decimal ];
    ];

    rr:predicateObjectMap
    [
      rr:predicate dwc:countryCode;
      rr:objectMap [ rr:column "collectionSiteCountry" ];
    ];

    rr:predicateObjectMap
    [
      rr:predicate dwc:scientificName;
      rr:objectMap [ rr:column "speciesName" ];
    ];

    rr:predicateObjectMap
    [
      rr:predicate gterm:acquisitionDate;
      rr:objectMap [ rr:column "collectionDate"; rr:datatype xsd:date ];
    ];

    rr:predicateObjectMap
    [
      rr:predicate gterm:biologicalStatus;
      rr:objectMap [ rr:column "germplasmStatus" ];
    ];
.
', 'http://temp/germplasm', 'http://temp/germplasm')
;

-- sanity checks
SELECT DB.DBA.R2RML_TEST('http://temp/germplasm');
DB.DBA.OVL_VALIDATE ('http://temp/germplasm', 'http://www.w3.org/ns/r2rml#OVL');

-- convert R2RML into Virtuoso's own Linked Data Views script
EXEC('SPARQL ' || DB.DBA.R2RML_MAKE_QM_FROM_G('http://temp/germplasm'));

-- graph queries

-- SPARQL SELECT * FROM <http://example.com/germplasm/> WHERE { ?s ?p ?o } LIMIT 10;
-- SPARQL SELECT * FROM <http://example.com/germplasm/> WHERE { ?s ?p ?o . FILTER(datatype(?o) = xsd:decimal) } LIMIT 10;
-- SPARQL DESCRIBE <http://example.com/germplasm/EA00001> FROM <http://example.com/germplasm/>;
-- SPARQL CONSTRUCT { <http://example.com/germplasm/EA00001> ?p ?o } FROM <http://example.com/germplasm/> WHERE { <http://example.com/germplasm/EA00001> ?p ?o };

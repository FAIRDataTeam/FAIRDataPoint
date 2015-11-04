--
-- RDF View on relational data in BreeDB using R2RML mapping language.
--
-- Author: Arnold Kuzniar
--

-- clear graphs
SPARQL CLEAR GRAPH <http://temp/germplasm>;
SPARQL CLEAR GRAPH <http://www.eu-sol.wur.nl/passport>;

-- insert R2RML into a temporary graph
DB.DBA.TTLP('
@prefix rr: <http://www.w3.org/ns/r2rml#> .
@prefix geo: <http://www.w3.org/2003/01/geo/wgs84_pos#> .
@prefix gterm: <http://purl.org/germplasm/germplasmTerm#> .
@prefix dwc: <http://rs.tdwg.org/dwc/terms/> .
@prefix dct: <http://purl.org/dc/terms/> .

<#TriplesMap1>
    a rr:TriplesMap;

    rr:logicalTable [
      rr:tableSchema "R2RML";
      rr:tableOwner  "TEST";
      rr:tableName   "pp_accession";
    ];

    rr:subjectMap [
      rr:template "http://www.eu-sol.wur.nl/passport#{accessionID}";
      rr:class gterm:GermplasmAccession;
      rr:graph <http://www.eu-sol.wur.nl/passport>;
      rr:termType rr:IRI
    ];

    rr:predicateObjectMap [
      rr:predicate gterm:germplasmID;
      rr:objectMap [
        rr:template "http://www.eu-sol.wur.nl/passport/SelectAccessionByAccessionID.do?accessionID={accessionID}";
        rr:termType rr:IRI
      ];
    ];

    rr:predicateObjectMap [
      rr:predicate gterm:germplasmIdentifier;
      rr:objectMap [
        rr:column "accessionName";
        rr:termType rr:Literal
      ];
    ];

    rr:predicateObjectMap [
      rr:predicate geo:lat;
      rr:objectMap [
        rr:termType rr:Literal;
        rr:column "gpsLat";
        rr:datatype xsd:decimal
      ];
    ];

    rr:predicateObjectMap [
      rr:predicate geo:long;
      rr:objectMap [
        rr:termType rr:Literal;
        rr:column "gpsLong";
        rr:datatype xsd:decimal
      ];
    ];

    rr:predicateObjectMap [
      rr:predicate geo:alt;
      rr:objectMap [
        rr:termType rr:Literal;
        rr:column "elevation";
        rr:datatype xsd:decimal
      ];
    ];

    rr:predicateObjectMap [
      rr:predicate dwc:countryCode; # requires two-letter codes in ISO 3166-1-alpha-2
      rr:objectMap [
        rr:termType rr:Literal;
        rr:column "collectionSiteCountry"; # contains three-letter country codes (FIXME)
        rr:datatype dct:Location # dct:ISO3166
      ];
    ];

    rr:predicateObjectMap [
      rr:predicate dwc:scientificName;
      rr:objectMap [
        rr:termType rr:Literal;
        rr:column "speciesName";
        rr:datatype dwc:Taxon
      ];
    ];

    rr:predicateObjectMap [
      rr:predicate gterm:acquisitionDate;
      rr:objectMap [
        rr:termType rr:Literal;
        rr:column "collectionDate";
        rr:datatype xsd:date
      ];
    ];

    rr:predicateObjectMap [
      rr:predicate gterm:biologicalStatus;
      rr:objectMap [
        rr:termType rr:Literal;
        rr:column "germplasmStatus";
        rr:datatype gterm:BiologicalStatusType
      ];
    ];

    rr:predicateObjectMap [
      rr:predicate dct:modified;
      rr:objectMap [
        rr:termType rr:Literal;
        rr:column "lastUpdate";
        rr:datatype xsd:date
      ];
    ];

    rr:predicateObjectMap [
      rr:predicate dct:created;
      rr:objectMap [
        rr:termType rr:Literal;
        rr:column "dateCreated"; rr:datatype xsd:date
      ];
    ];
.
', 'http://temp/germplasm', 'http://temp/germplasm')
;

-- sanity checks
SELECT DB.DBA.R2RML_TEST('http://temp/germplasm');
DB.DBA.OVL_VALIDATE ('http://temp/germplasm', 'http://www.w3.org/ns/r2rml#OVL');

-- convert R2RML into Virtuoso's own Linked Data Views script
EXEC('SPARQL ' || DB.DBA.R2RML_MAKE_QM_FROM_G('http://temp/germplasm'));

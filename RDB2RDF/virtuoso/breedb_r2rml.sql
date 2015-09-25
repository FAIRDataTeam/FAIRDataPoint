SPARQL CLEAR GRAPH <http://temp/germplasm>;
SPARQL CLEAR GRAPH <http://example.com/germplasm/>;

DB.DBA.TTLP('
@prefix rr: <http://www.w3.org/ns/r2rml#> .
@prefix exa: <http://example.com/ns#> .
@prefix germplasm: <http://example.com/germplasm#> .

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
      rr:class exa:germplasm;
      rr:graph <http://example.com/germplasm/>;
    ];

    rr:predicateObjectMap
    [
      rr:predicate germplasm:name;
      rr:objectMap [ rr:column "accessionName" ];
    ];
.
', 'http://temp/germplasm', 'http://temp/germplasm' )
;

SELECT DB.DBA.R2RML_TEST('http://temp/germplasm');
DB.DBA.OVL_VALIDATE ('http://temp/germplasm', 'http://www.w3.org/ns/r2rml#OVL');
EXEC('SPARQL ' || DB.DBA.R2RML_MAKE_QM_FROM_G ('http://temp/germplasm'));
SPARQL SELECT * FROM <http://example.com/germplasm/> WHERE { ?s ?p ?o };

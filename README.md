###SPARQL endpoint for BreeDB germplasm data

**Graph IRI: http://www.eu-sol.wur.nl/passport**

http://virtuoso.biotools.nl:8888/sparql/

###LDP server for BreeDB germplasm data

**Access Linked Data Platform Container (LDPC)**
```
curl -iH 'Accept: text/turtle' -u davuser:davuser http://virtuoso.biotools.nl:8888/passport/
```
**Access Linked Data Platform Non-RDF Source (LDP-NR)**
```
curl -iH 'Accept: text/turtle' -u davuser:davuser http://virtuoso.biotools.nl:8888/passport/test.txt
```
**Access Linked Data Platform RDF Source (LDP-RS)**
```
curl -iH 'Accept: text/turtle' -u davuser:davuser http://virtuoso.biotools.nl:8888/passport/test.ttl
```
###FAIR Data Point (FDP) Service


**RESTful Data API documentation**

http://fdp.biotools.nl:8080/doc

**Access FDP-, catalog- and dataset-level metadata**
```
curl -iH 'Accept: text/turtle' http://fdp.biotools.nl:8080/fdp
curl -iH 'Accept: text/turtle' http://fdp.biotools.nl:8080/catalog/catalog-01
curl -iH 'Accept: text/turtle' http://fdp.biotools.nl:8080/dataset/breedb
```

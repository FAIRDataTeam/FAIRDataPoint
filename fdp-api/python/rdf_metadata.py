from rdflib import Graph, URIRef, BNode, Literal
from rdflib.namespace import Namespace, NamespaceManager, RDF, RDFS, DCTERMS, XSD, FOAF
from rdflib.plugin import register, Serializer

register('application/ld+json', Serializer, 'rdflib_jsonld.serializer', 'JsonLDSerializer')

g = Graph()

# define additional namespaces
DCAT = Namespace('http://www.w3.org/ns/dcat#')
LANG = Namespace('http://id.loc.gov/vocabulary/iso639-1/')
DBPEDIA = Namespace('http://dbpedia.org/resource/')

# manage prefix -> namespace mappings
ns_mgr = NamespaceManager(g)
ns_mgr.bind('dbp', DBPEDIA)
ns_mgr.bind('dct', DCTERMS)
ns_mgr.bind('dcat', DCAT)
ns_mgr.bind('lang', LANG)

base_uri = URIRef('http://fdp.biotools.nl:8080')
doc_page = URIRef(base_uri + '/doc')
fdp = URIRef(base_uri + '/fdp')
cat = URIRef(base_uri + '/catalog/catalog-01')
dts = URIRef(base_uri + '/dataset')
breedb = URIRef(dts + '/breedb')
breedb_sparql = URIRef(dts + '/breedb-sparql')
breedb_sqldump = URIRef(dts + '/breedb-sqldump')

# FDP metadata
g.add( (fdp, RDF.type, DCTERMS.Agent) )
g.add( (fdp, RDFS.seeAlso, doc_page) )
g.add( (fdp, RDFS.seeAlso, cat) )
g.add( (fdp, RDFS.label, Literal('FAIR Data Point - Plant Breeding WUR', lang='en')) )
g.add( (fdp, DCTERMS.identifier, Literal('FDP-WUR-PB')) )
g.add( (fdp, DCTERMS.description, Literal('FAIR Data Point for plant-specific genotype and phenotype data sets')) )
g.add( (fdp, DCTERMS.title, Literal('FAIR Data Point - Plant Breeding WUR', lang='en')) )
g.add( (fdp, DCTERMS.language, LANG.en) )

# Data catalog metadata
g.add( (cat, RDF.type, DCAT.Catalog) )
g.add( (cat, RDFS.label, Literal('Plant Breeding Data Catalog', lang='en')) )
g.add( (cat, DCTERMS.title, Literal('Plant Breeding Data Catalog', lang='en')) )
g.add( (cat, DCTERMS.language, LANG.en) )
g.add( (cat, DCTERMS.publisher, URIRef('http://orcid.org/0000-0002-4368-8058')) )
g.add( (cat, DCTERMS.issued, Literal('2015-11-24', datatype=XSD.date)) )
g.add( (cat, DCTERMS.modified, Literal('2015-11-24', datatype=XSD.date)) )
g.add( (cat, DCAT.themeTaxonomy, DBPEDIA.Breeding) )
g.add( (cat, DCAT.dataset, breedb) )
# Note: The use of DCAT class dcat:CatalogRecord is optional.

# Dataset metadata
g.add( (breedb, RDF.type, DCAT.Dataset) )
g.add( (breedb, RDFS.label, Literal('BreeDB passport data', lang='en')) )
g.add( (breedb, DCTERMS.title, Literal('BreeDB passport data', lang='en')) )
g.add( (breedb, DCTERMS.description, Literal('Tomato germplasm collection', lang='en')) )
g.add( (breedb, DCTERMS.identifier, Literal('breedb')) )
g.add( (breedb, DCTERMS.publisher, URIRef('http://orcid.org/0000-0002-4368-8058')) )
g.add( (breedb, DCTERMS.issued, Literal('2015-11-24', datatype=XSD.date)) )
g.add( (breedb, DCTERMS.modified, Literal('2015-11-24', datatype=XSD.date)) )
g.add( (breedb, DCTERMS.language, LANG.en) )
g.add( (breedb, DCAT.theme, DBPEDIA.Plant_breeding) )
g.add( (breedb, DCAT.landingPage, URIRef('https://www.eu-sol.wur.nl/passport')) )
[ g.add( (breedb, DCAT.keyword, Literal(kw, lang='en')) ) for kw in ['BreeDB', 'Plant breeding', 'germplasm', 'passport data'] ]
[ g.add( (breedb, DCAT.distribution, dist) ) for dist in [breedb_sparql, breedb_sqldump] ]
g.add( (breedb_sparql, RDF.type, DCAT.Distribution) )
g.add( (breedb_sparql, RDFS.label, Literal('SPARQL endpoint for BreeDB passport data')) )
g.add( (breedb_sparql, DCTERMS.title, Literal('SPARQL endpoint for BreeDB passport data')) )
g.add( (breedb_sparql, DCTERMS.license, URIRef('http://rdflicense.appspot.com/rdflicense/cc-by-nc-nd3.0')) )
g.add( (breedb_sparql, DCTERMS.language, LANG.en) )
g.add( (breedb_sparql, DCAT.accessURL, URIRef('http://virtuoso.biotools.nl:8888/sparql')) )
# TODO: Use SPARQL-SD to add named graph URI
[ g.add( (breedb_sparql, DCAT.mediaType, Literal(mime)) ) for mime in ['text/turtle', 'application/rdf+xml', 'application/ld+json'] ]
g.add( (breedb_sqldump, RDF.type, DCAT.Distribution) )
g.add( (breedb_sqldump, RDFS.label, Literal('SQL dump of the BreeDB tomato germplasm data', lang='en')) )
g.add( (breedb_sqldump, DCTERMS.title, Literal('SQL dump of the BreeDB tomato germplasm data', lang='en')) )
g.add( (breedb_sqldump, DCTERMS.license, URIRef('http://rdflicense.appspot.com/rdflicense/cc-by-nc-nd3.0')) )
g.add( (breedb_sqldump, DCTERMS.language, LANG.en) )
g.add( (breedb_sqldump, DCAT.downloadURL, URIRef('http://virtuoso.biotools.nl:8888/DAV/home/breedb/breedb.sql')) )
g.add( (breedb_sqldump, DCAT.mediaType, Literal('application/sql')) )

print g.serialize(format='text/turtle') # pass mime-type
#print g.serialize(format='application/rdf+xml')
#print g.serialize(format='application/ld+json')

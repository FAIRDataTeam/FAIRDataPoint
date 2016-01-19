from rdflib import Graph, URIRef, BNode, Literal
from rdflib.namespace import Namespace, NamespaceManager, RDF, RDFS, DCTERMS, XSD, FOAF
from rdflib.plugin import register, Serializer

register('application/ld+json', Serializer, 'rdflib_jsonld.serializer', 'JsonLDSerializer')

# define additional namespaces
DCAT = Namespace('http://www.w3.org/ns/dcat#')
LANG = Namespace('http://id.loc.gov/vocabulary/iso639-1/')
DBPEDIA = Namespace('http://dbpedia.org/resource/')

class FAIRGraph(Graph):
   def __init__(self, base_uri=None):
      Graph.__init__(self)
      self.__base_uri = base_uri

      # manage prefix -> namespace mappings
      mgr = NamespaceManager(self)
      mgr.bind('dbp', DBPEDIA)
      mgr.bind('dct', DCTERMS)
      mgr.bind('dcat', DCAT)
      mgr.bind('lang', LANG)

   def baseURI(self):
      return URIRef(self.__base_uri)

   def docURI(self):
      return URIRef(self.baseURI() + '/doc')

   def fdpURI(self):
      return URIRef(self.baseURI() + '/fdp')

   def catURI(self, id):
      return URIRef(self.baseURI() + '/catalog/' + str(id))

   def dtsURI(self, id):
      return URIRef(self.baseURI() + '/dataset/' + str(id))

   def desFDP(self): # FDP metadata
      self.add( (self.fdpURI(), RDF.type, DCTERMS.Agent) )
      self.add( (self.fdpURI(), RDFS.seeAlso, self.docURI()) )
      self.add( (self.fdpURI(), RDFS.seeAlso, self.catURI('catalog-01')) )
      self.add( (self.fdpURI(), RDFS.label, Literal('FAIR Data Point - Plant Breeding WUR', lang='en')) )
      self.add( (self.fdpURI(), DCTERMS.title, Literal('FAIR Data Point - Plant Breeding WUR', lang='en')) )
      self.add( (self.fdpURI(), DCTERMS.identifier, Literal('FDP-WUR-PB')) )
      self.add( (self.fdpURI(), DCTERMS.description, Literal('FAIR Data Point for plant-specific genotype and phenotype data sets')) )
      self.add( (self.fdpURI(), DCTERMS.language, LANG.en) )

      return self

   def desCatalog(self): # Data catalog metadata
      self.add( (self.catURI('catalog-01'), RDF.type, DCAT.Catalog) )
      self.add( (self.catURI('catalog-01'), RDFS.label, Literal('Plant Breeding Data Catalog', lang='en')) )
      self.add( (self.catURI('catalog-01'), DCTERMS.title, Literal('Plant Breeding Data Catalog', lang='en')) )
      self.add( (self.catURI('catalog-01'), DCTERMS.language, LANG.en) )
      self.add( (self.catURI('catalog-01'), DCTERMS.publisher, URIRef('http://orcid.org/0000-0002-4368-8058')) )
      self.add( (self.catURI('catalog-01'), DCTERMS.issued, Literal('2015-11-24', datatype=XSD.date)) )
      self.add( (self.catURI('catalog-01'), DCTERMS.modified, Literal('2015-11-24', datatype=XSD.date)) )
      self.add( (self.catURI('catalog-01'), DCAT.themeTaxonomy, DBPEDIA.Breeding) )
      self.add( (self.catURI('catalog-01'), DCAT.dataset, self.dtsURI('breedb')) )
      # Note: The use of DCAT class dcat:CatalogRecord is optional.

      return self

   def desDataset(self): # Dataset metadata
      self.add( (self.dtsURI('breedb'), RDF.type, DCAT.Dataset) )
      self.add( (self.dtsURI('breedb'), RDFS.label, Literal('BreeDB passport data', lang='en')) )
      self.add( (self.dtsURI('breedb'), DCTERMS.title, Literal('BreeDB passport data', lang='en')) )
      self.add( (self.dtsURI('breedb'), DCTERMS.description, Literal('Tomato germplasm collection', lang='en')) )
      self.add( (self.dtsURI('breedb'), DCTERMS.identifier, Literal('breedb')) )
      self.add( (self.dtsURI('breedb'), DCTERMS.publisher, URIRef('http://orcid.org/0000-0002-4368-8058')) )
      self.add( (self.dtsURI('breedb'), DCTERMS.issued, Literal('2015-11-24', datatype=XSD.date)) )
      self.add( (self.dtsURI('breedb'), DCTERMS.modified, Literal('2015-11-24', datatype=XSD.date)) )
      self.add( (self.dtsURI('breedb'), DCTERMS.language, LANG.en) )
      self.add( (self.dtsURI('breedb'), DCAT.theme, DBPEDIA.Plant_breeding) )
      self.add( (self.dtsURI('breedb'), DCAT.landingPage, URIRef('https://www.eu-sol.wur.nl/passport')) )
      [ self.add( (self.dtsURI('breedb'), DCAT.keyword, Literal(kw, lang='en')) ) for kw in ['BreeDB', 'Plant breeding', 'germplasm', 'passport data'] ]
      [ self.add( (self.dtsURI('breedb'), DCAT.distribution, dist) ) for dist in [self.dtsURI('breedb-sparql'), self.dtsURI('breedb-sqldump')] ]
      self.add( (self.dtsURI('breedb-sparql'), RDF.type, DCAT.Distribution) )
      self.add( (self.dtsURI('breedb-sparql'), RDFS.label, Literal('SPARQL endpoint for BreeDB passport data')) )
      self.add( (self.dtsURI('breedb-sparql'), DCTERMS.title, Literal('SPARQL endpoint for BreeDB passport data')) )
      self.add( (self.dtsURI('breedb-sparql'), DCTERMS.license, URIRef('http://rdflicense.appspot.com/rdflicense/cc-by-nc-nd3.0')) )
      self.add( (self.dtsURI('breedb-sparql'), DCTERMS.language, LANG.en) )
      self.add( (self.dtsURI('breedb-sparql'), DCAT.accessURL, URIRef('http://virtuoso.biotools.nl:8888/sparql')) )
      # TODO: Use SPARQL-SD to add named graph URI
      [ self.add( (self.dtsURI('breedb-sparql'), DCAT.mediaType, Literal(mime)) ) for mime in ['text/turtle', 'application/rdf+xml', 'application/ld+json'] ]
      self.add( (self.dtsURI('breedb-sqldump'), RDF.type, DCAT.Distribution) )
      self.add( (self.dtsURI('breedb-sqldump'), RDFS.label, Literal('SQL dump of the BreeDB tomato germplasm data', lang='en')) )
      self.add( (self.dtsURI('breedb-sqldump'), DCTERMS.title, Literal('SQL dump of the BreeDB tomato germplasm data', lang='en')) )
      self.add( (self.dtsURI('breedb-sqldump'), DCTERMS.license, URIRef('http://rdflicense.appspot.com/rdflicense/cc-by-nc-nd3.0')) )
      self.add( (self.dtsURI('breedb-sqldump'), DCTERMS.language, LANG.en) )
      self.add( (self.dtsURI('breedb-sqldump'), DCAT.downloadURL, URIRef('http://virtuoso.biotools.nl:8888/DAV/home/breedb/breedb.sql')) )
      self.add( (self.dtsURI('breedb-sqldump'), DCAT.mediaType, Literal('application/sql')) )

      return self

if __name__ == '__main__':
   g = FAIRGraph('http://fdp.biotools.nl:8080')
   g.desFDP()
   g.desCatalog()
   g.desDataset()

   print g.serialize(format='text/turtle') # pass mime-type
   #print g.serialize(format='application/rdf+xml')
   #print g.serialize(format='application/ld+json')


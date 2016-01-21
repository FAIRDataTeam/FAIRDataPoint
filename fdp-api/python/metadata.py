from rdflib import ConjunctiveGraph, URIRef, Literal
from rdflib.namespace import Namespace, RDF, RDFS, DCTERMS, XSD
from rdflib.plugin import register, Serializer

# rdflib-jsonld module required
register('application/ld+json', Serializer, 'rdflib_jsonld.serializer', 'JsonLDSerializer')

# define additional namespaces
DCAT = Namespace('http://www.w3.org/ns/dcat#')
LANG = Namespace('http://id.loc.gov/vocabulary/iso639-1/')
DBPEDIA = Namespace('http://dbpedia.org/resource/')
#SPARQLSD = Namespace('http://www.w3.org/ns/sparql-service-description#')

class FAIRGraph(ConjunctiveGraph):
   def __init__(self, base_uri=None):
      super(FAIRGraph, self).__init__()
      self.__base_uri = base_uri

      # bind prefixes to namespaces
      self.bind('dbp', DBPEDIA)
      self.bind('dct', DCTERMS)
      self.bind('dcat', DCAT)
      self.bind('lang', LANG)
      #self.bind('sd', SPARQLSD)

   @staticmethod
   def missingField(key, meta_type):
      return "Missing key '%s' in %s metadata dict()." % (key, meta_type)
   
   def baseURI(self):
      return URIRef(self.__base_uri)

   def docURI(self):
      return URIRef(self.baseURI() + '/doc')

   def fdpURI(self):
      return URIRef(self.baseURI() + '/fdp')

   def catURI(self, id):
      return URIRef(self.baseURI() + '/catalog/' + str(id))

   def datURI(self, id):
      return URIRef(self.baseURI() + '/dataset/' + str(id))

   def serialize(self, uri, mime_type):
      return self.get_context(uri).serialize(format=mime_type)

   def setFdpMetadata(self, meta=None):
      assert(isinstance(meta, dict)), 'Use dict() for FDP metadata.'
      assert(meta.has_key('fdp_id')), self.missingField('fdp_id', 'FDP')
      assert(meta.has_key('catalog_ids')), self.missingField('catalog_ids', 'FDP')

      uri = self.fdpURI()
      cg = self.get_context(uri)
      cg.add( (uri, RDF.type, DCTERMS.Agent) )
      cg.add( (uri, RDFS.seeAlso, self.docURI()) )
      cg.add( (uri, DCTERMS.identifier, Literal(meta['fdp_id'])) )
      cg.add( (uri, DCTERMS.language, LANG.en) )
      [ cg.add( (uri, RDFS.seeAlso, self.catURI(id)) )\
         for id in meta['catalog_ids'] ]

      try: # optional fields
         cg.add( (uri, RDFS.label, Literal(meta['title'], lang='en')) )
         cg.add( (uri, DCTERMS.title, Literal(meta['title'], lang='en')) )
         cg.add( (uri, DCTERMS.description, Literal(meta['des'])) )
      except:
         pass

   def setCatalogMetadata(self, meta=None):
      assert(isinstance(meta, dict)), 'Use dict() for Catalog metadata.'
      assert(meta.has_key('catalogs')), self.missingField('catalogs', 'Catalog')
      for cat in meta['catalogs']:
         assert(cat.has_key('catalog_id')), self.missingField('catalog_id', 'Catalog')
         assert(cat.has_key('dataset_ids')), self.missingField('dataset_ids', 'Catalog')
         uri = self.catURI(cat['catalog_id'])
         cg = self.get_context(uri)
         cg.add( (uri, RDF.type, DCAT.Catalog) )
         cg.add( (uri, DCTERMS.identifier, Literal(cat['catalog_id'])) )
         cg.add( (uri, DCTERMS.language, LANG.en) )
         cg.add( (uri, DCAT.themeTaxonomy, DBPEDIA.Breeding) ) # FIXME
         [ cg.add( (uri, DCAT.dataset, self.datURI(dataset_id)) )\
            for dataset_id in cat['dataset_ids'] ]

         try: # optional fields
            cg.add( (uri, RDFS.label, Literal(cat['title'], lang='en')) )
            cg.add( (uri, DCTERMS.title, Literal(cat['title'], lang='en')) )
            cg.add( (uri, DCTERMS.description, Literal(cat['des'])) )
            cg.add( (uri, DCTERMS.publisher, URIRef(cat['publisher'])) )
            cg.add( (uri, DCTERMS.issued, Literal(cat['issued'], datatype=XSD.date)) )
            cg.add( (uri, DCTERMS.modified, Literal(cat['modified'], datatype=XSD.date)) )
         except:
            pass

   def setDatasetMetadata(self, meta=None):
      assert(isinstance(meta, dict)), 'Use dict() for Dataset metadata.'
      assert(meta.has_key('datasets')), self.missingField('datasets', 'Dataset')
      for dat in meta['datasets']:
         assert(dat.has_key('dataset_id')), self.missingField('dataset_id', 'Dataset')
         assert(dat.has_key('distributions')), self.missingField('distributions', 'Dataset')
         uri_dat = self.datURI(dat['dataset_id'])
         cg = self.get_context(uri_dat)
         cg.add( (uri_dat, RDF.type, DCAT.Dataset) )
         cg.add( (uri_dat, DCTERMS.identifier, Literal(dat['dataset_id'])) )
         cg.add( (uri_dat, DCTERMS.language, LANG.en) )
         cg.add( (uri_dat, DCAT.theme, DBPEDIA.Plant_breeding) ) # FIXME

         cg.add( (uri_dat, RDFS.label, Literal(dat['title'], lang='en')) )
         cg.add( (uri_dat, DCTERMS.title, Literal(dat['title'], lang='en')) )
         cg.add( (uri_dat, DCTERMS.description, Literal(dat.has_key('des'), lang='en')) )
         cg.add( (uri_dat, DCTERMS.publisher, URIRef(dat['publisher'])) )
         cg.add( (uri_dat, DCTERMS.issued, Literal(dat['issued'], datatype=XSD.date)) )
         cg.add( (uri_dat, DCTERMS.modified, Literal(dat['modified'], datatype=XSD.date)) )
         cg.add( (uri_dat, DCAT.landingPage, URIRef(dat['landing_page'])) )
         [ cg.add( (uri_dat, DCAT.keyword, Literal(kw, lang='en')) )\
            for kw in dat['keywords'] ]

         for dist in dat['distributions']:
            assert(isinstance(dist, dict)), 'Use dict() for Dataset/distribution metadata.'
            assert(dist.has_key('distribution_id')), self.missingField('distribution_id', 'Dataset/distribution')
            uri_dist = self.datURI(dist['distribution_id'])
            cg.add( (uri_dat, DCAT.distribution, uri_dist) )
            cg.add( (uri_dist, RDF.type, DCAT.Distribution) )

            #if (dist.has_key('access_url')): cg.add( (uri_dist, RDF.type, SPARQLSD.Service) )
            
            try:
               cg.add( (uri_dist, RDFS.label, Literal(dist['title'])) )
               cg.add( (uri_dist, DCTERMS.title, Literal(dist['title'])) )
               cg.add( (uri_dist, DCTERMS.description, Literal(dist['des'], lang='en')) )
               cg.add( (uri_dist, DCTERMS.license, URIRef(dist['license'])) )
               cg.add( (uri_dist, DCAT.accessURL, URIRef(dist['access_url'])) )
               cg.add( (uri_dist, DCAT.downloadURL, URIRef(dist['download_url'])) )
               [ cg.add( (uri_dist, DCAT.mediaType, Literal(mime)) ) for mime in dist['media_types'] ]
               #cg.add( (uri_dist, SPARQLSD.endpoint, URIRef(dist['access_url'])) )

            except:
               pass


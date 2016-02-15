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

class FAIRGraph(object):
   def __init__(self, base_uri):
      graph = ConjunctiveGraph()
      self._graph = graph
      self._base_uri = base_uri

      # bind prefixes to namespaces
      graph.bind('dbp', DBPEDIA)
      graph.bind('dct', DCTERMS)
      graph.bind('dcat', DCAT)
      graph.bind('lang', LANG)
      #graph.bind('sd', SPARQLSD)

   @staticmethod
   def missingField(key, meta_type):
      return "Missing key '%s' in %s metadata dict()." % (key, meta_type)

   
   def baseURI(self):
      return URIRef(self._base_uri)


   def docURI(self):
      return URIRef('%s/doc' % self.baseURI())


   def fdpURI(self):
      return URIRef('%s/fdp' % self.baseURI())


   def catURI(self, id):
      return URIRef('%s/catalog/%s' % (self.baseURI(), id))


   def datURI(self, id):
      return URIRef('%s/dataset/%s' % (self.baseURI(), id))

   def distURI(self, id):
      return URIRef('%s/distribution/%s' % (self.baseURI(), id))

   def serialize(self, uri, mime_type):
      if len(self._graph_context(uri).all_nodes()) > 0:
         return self._graph_context(uri).serialize(format=mime_type)


   def setFdpMetadata(self, meta):
      assert(isinstance(meta, dict)), 'Use dict() for FDP metadata.'
      assert('fdp_id' in meta), self.missingField('fdp_id', 'FDP')
      assert('catalog_ids' in meta), self.missingField('catalog_ids', 'FDP')

      uri = self.fdpURI()
      cg = self._graph_context(uri)
      cg.add( (uri, RDF.type, DCTERMS.Agent) )
      cg.add( (uri, RDFS.seeAlso, self.docURI()) )
      cg.add( (uri, DCTERMS.identifier, Literal(meta['fdp_id'])) )
      cg.add( (uri, DCTERMS.language, LANG.en) )
      for catalog_id in meta['catalog_ids']:
         cg.add( (uri, RDFS.seeAlso, self.catURI(catalog_id)) )

      if 'title' in meta:
         cg.add( (uri, RDFS.label, Literal(meta['title'], lang='en')) )
         cg.add( (uri, DCTERMS.title, Literal(meta['title'], lang='en')) )

      if 'des' in meta:
         cg.add( (uri, DCTERMS.description, Literal(meta['des'])) )


   def setCatalogMetadata(self, meta):
      assert(isinstance(meta, dict)), 'Use dict() for Catalog metadata.'
      assert('catalogs' in meta), self.missingField('catalogs', 'Catalog')
      for cat in meta['catalogs']:
         assert('catalog_id' in cat), self.missingField('catalog_id', 'Catalog')
         assert('dataset_ids' in cat), self.missingField('dataset_ids', 'Catalog')
         uri = self.catURI(cat['catalog_id'])
         cg = self._graph_context(uri)
         cg.add( (uri, RDF.type, DCAT.Catalog) )
         cg.add( (uri, DCTERMS.identifier, Literal(cat['catalog_id'])) )
         cg.add( (uri, DCTERMS.language, LANG.en) )
         cg.add( (uri, DCAT.themeTaxonomy, DBPEDIA.Breeding) ) # FIXME
         for dataset_id in cat['dataset_ids']:
            cg.add( (uri, DCAT.dataset, self.datURI(dataset_id)) )

         if 'title' in cat:
            cg.add( (uri, RDFS.label, Literal(cat['title'], lang='en')) )
            cg.add( (uri, DCTERMS.title, Literal(cat['title'], lang='en')) )

         if 'des' in cat:
            cg.add( (uri, DCTERMS.description, Literal(cat['des'])) )

         if 'publisher' in cat:
            cg.add( (uri, DCTERMS.publisher, URIRef(cat['publisher'])) )

         if 'issued' in cat:
            cg.add( (uri, DCTERMS.issued, Literal(cat['issued'], datatype=XSD.date)) )

         if 'modified' in cat:
            cg.add( (uri, DCTERMS.modified, Literal(cat['modified'], datatype=XSD.date)) )


   def setDatasetAndDistributionMetadata(self, meta):
      assert(isinstance(meta, dict)), 'Use dict() for Dataset metadata.'
      assert('datasets' in meta), self.missingField('datasets', 'Dataset')
      for dat in meta['datasets']:
         assert('dataset_id' in dat), self.missingField('dataset_id', 'Dataset')
         assert('distributions' in dat), self.missingField('distributions', 'Dataset')
         uri_dat = self.datURI(dat['dataset_id'])
         cg_dat = self._graph_context(uri_dat)
         cg_dat.add( (uri_dat, RDF.type, DCAT.Dataset) )
         cg_dat.add( (uri_dat, DCTERMS.identifier, Literal(dat['dataset_id'])) )
         cg_dat.add( (uri_dat, DCTERMS.language, LANG.en) )
         cg_dat.add( (uri_dat, DCAT.theme, DBPEDIA.Plant_breeding) ) # FIXME

         if 'title' in dat:
            cg_dat.add( (uri_dat, RDFS.label, Literal(dat['title'], lang='en')) )
            cg_dat.add( (uri_dat, DCTERMS.title, Literal(dat['title'], lang='en')) )

         if 'des' in dat:
            cg_dat.add( (uri_dat, DCTERMS.description, Literal(dat['des'], lang='en')) )

         if 'publisher' in dat:
            cg_dat.add( (uri_dat, DCTERMS.publisher, URIRef(dat['publisher'])) )

         if 'issued' in dat:
            cg_dat.add( (uri_dat, DCTERMS.issued, Literal(dat['issued'], datatype=XSD.date)) )

         if 'modified' in dat:
            cg_dat.add( (uri_dat, DCTERMS.modified, Literal(dat['modified'], datatype=XSD.date)) )

         if 'landing_page' in dat:
            cg_dat.add( (uri_dat, DCAT.landingPage, URIRef(dat['landing_page'])) )

         if 'keywords' in dat:
            for kw in dat['keywords']:
               cg_dat.add( (uri_dat, DCAT.keyword, Literal(kw, lang='en')) )

         for dist in dat['distributions']:
            assert(isinstance(dist, dict)), 'Use dict() for Dataset/distribution metadata.'
            assert('distribution_id' in dist), self.missingField('distribution_id', 'Dataset/distribution')
            uri_dist = self.distURI(dist['distribution_id'])
            cg_dist = self._graph_context(uri_dist)
            cg_dat.add( (uri_dat, DCAT.distribution, uri_dist) )
            cg_dist.add( (uri_dist, RDF.type, DCAT.Distribution) )
            #if (dist.has_key('access_url')): cg_dist.add( (uri_dist, RDF.type, SPARQLSD.Service) )
            
            if 'title' in dist:
               cg_dist.add( (uri_dist, RDFS.label, Literal(dist['title'])) )
               cg_dist.add( (uri_dist, DCTERMS.title, Literal(dist['title'])) )

            if 'des' in dist:
               cg_dist.add( (uri_dist, DCTERMS.description, Literal(dist['des'], lang='en')) )

            if 'license' in dist:
               cg_dist.add( (uri_dist, DCTERMS.license, URIRef(dist['license'])) )

            if 'access_url' in dist:
               cg_dist.add( (uri_dist, DCAT.accessURL, URIRef(dist['access_url'])) )

            if 'download_url' in dist:
               cg_dist.add( (uri_dist, DCAT.downloadURL, URIRef(dist['download_url'])) )

            if 'media_types' in dist:
               for mime in dist['media_types']:
                  cg_dist.add( (uri_dist, DCAT.mediaType, Literal(mime)) )
                  #cg_dist.add( (uri_dist, SPARQLSD.endpoint, URIRef(dist['access_url'])) )

   def _graph_context(self, uri):
      return self._graph.get_context(uri)

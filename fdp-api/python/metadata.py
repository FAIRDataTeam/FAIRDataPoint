from rdflib import ConjunctiveGraph, URIRef, BNode, Literal
from rdflib.namespace import Namespace, NamespaceManager, RDF, RDFS, DCTERMS, XSD, FOAF
from rdflib.plugin import register, Serializer

# rdflib-jsonld module required
register('application/ld+json', Serializer, 'rdflib_jsonld.serializer', 'JsonLDSerializer')

# define additional namespaces
DCAT = Namespace('http://www.w3.org/ns/dcat#')
LANG = Namespace('http://id.loc.gov/vocabulary/iso639-1/')
DBPEDIA = Namespace('http://dbpedia.org/resource/')

class FAIRGraph(ConjunctiveGraph):
   def __init__(self, base_uri=None):
      ConjunctiveGraph.__init__(self)
      self.__base_uri = base_uri

      # manage prefix -> namespace mappings
      self.bind('dbp', DBPEDIA)
      self.bind('dct', DCTERMS)
      self.bind('dcat', DCAT)
      self.bind('lang', LANG)

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

   def setFdpMetadata(self, **kw):
      uri = self.fdpURI()
      cg = self.get_context(uri)
      cg.add( (uri, RDF.type, DCTERMS.Agent) )
      cg.add( (uri, RDFS.seeAlso, self.docURI()) )
      if kw.has_key('catalog_ids'):
         [ cg.add( (uri, RDFS.seeAlso, self.catURI(id)) )\
            for id in kw['catalog_ids'] ]
      if kw.has_key('title'): cg.add( (uri, RDFS.label, Literal(kw['title'], lang='en')) )
      if kw.has_key('fdp_id'): cg.add( (uri, DCTERMS.identifier, Literal(kw['fdp_id'])) )
      if kw.has_key('title'): cg.add( (uri, DCTERMS.title, Literal(kw['title'], lang='en')) )
      if kw.has_key('des'): cg.add( (uri, DCTERMS.description, Literal(kw['des'])) )
      cg.add( (uri, DCTERMS.language, LANG.en) )

   def setCatalogMetadata(self, **kw):
      if not kw.has_key('catalogs'): return
      for cat in kw['catalogs']:
         if not cat.has_key('catalog_id'): return
         uri = self.catURI(cat['catalog_id'])
         cg = self.get_context(uri)
         cg.add( (uri, RDF.type, DCAT.Catalog) )
         if cat.has_key('title'): cg.add( (uri, RDFS.label, Literal(cat['title'], lang='en')) )
         if cat.has_key('catalog_id'): cg.add( (uri, DCTERMS.identifier, Literal(cat['catalog_id'])) )
         if cat.has_key('title'): cg.add( (uri, DCTERMS.title, Literal(cat['title'], lang='en')) )
         if cat.has_key('des'): cg.add( (uri, DCTERMS.description, Literal(cat['des'])) )
         if cat.has_key('publisher'): cg.add( (uri, DCTERMS.publisher, URIRef(cat['publisher'])) )
         if cat.has_key('issued'): cg.add( (uri, DCTERMS.issued, Literal(cat['issued'], datatype=XSD.date)) )
         if cat.has_key('modified'): cg.add( (uri, DCTERMS.modified, Literal(cat['modified'], datatype=XSD.date)) )
         cg.add( (uri, DCTERMS.language, LANG.en) )
         cg.add( (uri, DCAT.themeTaxonomy, DBPEDIA.Breeding) ) # FIXME
         if cat.has_key('dataset_ids'):
            [ cg.add( (uri, DCAT.dataset, self.datURI(dataset_id)) )\
               for dataset_id in cat['dataset_ids'] ]

   def setDatasetMetadata(self, **kw):
      if not kw.has_key('datasets'): return
      for dat in kw['datasets']:
         if not dat.has_key('dataset_id'): return
         uri_dat = self.datURI(dat['dataset_id'])
         cg = self.get_context(uri_dat)
         cg.add( (uri_dat, RDF.type, DCAT.Dataset) )
         if dat.has_key('title'): cg.add( (uri_dat, RDFS.label, Literal(dat['title'], lang='en')) )
         if dat.has_key('dataset_id'): cg.add( (uri_dat, DCTERMS.identifier, Literal(dat['dataset_id'])) )
         if dat.has_key('title'): cg.add( (uri_dat, DCTERMS.title, Literal(dat['title'], lang='en')) )
         if dat.has_key('des'): cg.add( (uri_dat, DCTERMS.description, Literal(dat.has_key('des'), lang='en')) )
         if dat.has_key('publisher'): cg.add( (uri_dat, DCTERMS.publisher, URIRef(dat['publisher'])) )
         if dat.has_key('issued'): cg.add( (uri_dat, DCTERMS.issued, Literal(dat['issued'], datatype=XSD.date)) )
         if dat.has_key('modified'): cg.add( (uri_dat, DCTERMS.modified, Literal(dat['modified'], datatype=XSD.date)) )
         cg.add( (uri_dat, DCTERMS.language, LANG.en) )
         cg.add( (uri_dat, DCAT.theme, DBPEDIA.Plant_breeding) ) # FIXME
         if dat.has_key('landing_page'): cg.add( (uri_dat, DCAT.landingPage, URIRef(dat['landing_page'])) )
         if dat.has_key('keywords'):
            [ cg.add( (uri_dat, DCAT.keyword, Literal(kw, lang='en')) )\
               for kw in dat['keywords'] ]

         for dist in dat['distributions']:
            uri_dist = self.datURI(dist['distribution_id'])
            cg.add( (uri_dat, DCAT.distribution, uri_dist) )
            cg.add( (uri_dist, RDF.type, DCAT.Distribution) )
            if dist.has_key('title'): cg.add( (uri_dist, RDFS.label, Literal(dist['title'])) )
            if dist.has_key('title'): cg.add( (uri_dist, DCTERMS.title, Literal(dist['title'])) )
            if dist.has_key('des'): cg.add( (uri_dist, DCTERMS.description, Literal(dist['des'], lang='en')) )
            if dist.has_key('license'): cg.add( (uri_dist, DCTERMS.license, URIRef(dist['license'])) )
            if dist.has_key('access_url'): cg.add( (uri_dist, DCAT.accessURL, URIRef(dist['access_url'])) )
            if dist.has_key('download_url'): cg.add( (uri_dist, DCAT.downloadURL, URIRef(dist['download_url'])) )
            if dist.has_key('media_types'):
               [ cg.add( (uri_dist, DCAT.mediaType, Literal(mime)) ) for mime in dist['media_types'] ]
            # TODO: Use SPARQL-SD to add named graph URI


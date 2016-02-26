from rdflib import ConjunctiveGraph, URIRef, Literal
from rdflib.namespace import Namespace, RDF, RDFS, DCTERMS, XSD
from rdflib.plugin import register, Serializer
from ConfigParser import SafeConfigParser

# rdflib-jsonld module required
register('application/ld+json', Serializer, 'rdflib_jsonld.serializer', 'JsonLDSerializer')

# define additional namespaces
DCAT = Namespace('http://www.w3.org/ns/dcat#')
LANG = Namespace('http://id.loc.gov/vocabulary/iso639-1/')
DBPEDIA = Namespace('http://dbpedia.org/resource/')
#SPARQLSD = Namespace('http://www.w3.org/ns/sparql-service-description#')

# mandatory sections and fields in the metadata config file
_CORE_META   = ['title','publisher','version','issued','modified']
_REQUIRED_META = dict(fdp          = _CORE_META + ['fdp_id','catalog_id'],
                      catalog      = _CORE_META + ['dataset_id','theme_taxonomy'],
                      dataset      = _CORE_META + ['distribution_id','theme'],
                      distribution = _CORE_META + ['access_url|download_url','media_type','license'])

# map fields to XSD data types and ontologies/vocabularies
_ONTO_MAP_PREDICATE = dict(fdp_id      = [ XSD.string, DCTERMS.identifier ],
                           catalog_id  = [ XSD.string, DCTERMS.hasPart ],
                           dataset_id  = [ None, DCTERMS.hasPart, DCAT.dataset ],
                           distribution_id = [ None, DCAT.distribution ],
                           title           = [ XSD.string, DCTERMS.title, RDFS.label ],
                           description     = [ XSD.string, DCTERMS.description ],
                           publisher       = [ None, DCTERMS.publisher ],
                           issued          = [ XSD.date, DCTERMS.issued ],
                           modified        = [ XSD.date, DCTERMS.modified ],
                           version         = [ XSD.string, DCTERMS.version ],
                           license         = [ None, DCTERMS.license ],
                           theme           = [ None, DCAT.theme ],
                           theme_taxonomy  = [ None, DCAT.themeTaxonomy ],
                           lading_page     = [ None, DCAT.landingPage ],
                           keyword         = [ XSD.string, DCAT.keyword ],
                           access_url      = [ None, DCAT.accessURL ],
                           download_url    = [ None, DCAT.downloadURL ],
                           media_types     = [ None, DCAT.mediaTypes ] )


class FAIRConfigParser(object):
   def __init__(self):
      parser = SafeConfigParser()
      self._parser = parser
      self._metadata = dict()


   @staticmethod
   def errorSectionNotFound(section):
      return "Section '%s' is not found." % section


   @staticmethod
   def errorFieldNotFound(field, section):
      return "Field '%s' is not found in section '%s'." % (field, section)


   @staticmethod
   def errorReferenceNotFound(section, field, ref_section_by_field):
      return "{f}(s) in the '{s}' section is not referenced in the '{r}/<{f}>' section header(s) or vice versa.".format(f=field, r=ref_section_by_field, s=section)

   @staticmethod
   def errorResourceIdNotUnique(id):
      return "Resource ID '%s' is not unique." % id


   def read(self, filename):
      self._parser.read(filename)

      for section in self._parser.sections():
         self._metadata[section] = dict()

         for key,value in self._parser.items(section):
            if '\n' in value: value = value.split('\n')
            self._metadata[section][key] = value

      self._validate()


   def getMetadata(self):
      return self._metadata


   def getSectionHeaders(self):
      return self.getMetadata().keys()


   def getFields(self, section):
      return self._metadata[section]


   def getItems(self, section, field):
      return self._metadata[section][field]

   def triplify(self):
      for section in self.getSectionHeaders():
         for field in self.getFields(section):
            items = self.getItems(section, field)
            if isinstance(items, list):
               for item in items:
                  yield (section, field, item)
            else:
               yield (section, field, items)

   def _validate(self):
      section_headers = self.getSectionHeaders()
      sections = dict((section,[]) for section in _REQUIRED_META.keys())
      uniq_resource_ids = dict()

      sfx = '_id'
      fdp, cat, dat, dist = 'fdp', 'catalog', 'dataset', 'distribution'
      fdp_id, cat_id, dat_id, dist_id = fdp + sfx, cat + sfx, dat + sfx, dist + sfx

      def _arrfy(item):
         if isinstance(item, str):
            return [item]

         if isinstance(item, list):
            return item

      # check mandatory sections
      for sh in section_headers:
         if sh in sections:
            sections[sh] = True

         if '/' in sh:
            section, resource_id = sh.split('/')
            if section in sections:
               sections[section].append(resource_id)

      for section,resource in sections.items():
         assert(resource), self.errorSectionNotFound(section)

      # check mandatory fields and referenced sections
      for section in section_headers:
         for field in _REQUIRED_META[section.split('/')[0]]:
            fields = self.getFields(section)

            if '|' in field: # distribution has two alternatives: access_url|download_url
               a, b = field.split('|')
               assert(a in fields or b in fields), self.errorFieldNotFound(field, section)
            else:
               assert(field in fields), self.errorFieldNotFound(field, section)

            # resource IDs must be unique
            if field in [fdp_id, cat_id, dat_id, dist_id]:
               for resource_id in _arrfy(self.getItems(section, field)):
                  assert(resource_id not in uniq_resource_ids), self.errorResourceIdNotUnique(resource_id)
                  uniq_resource_ids[resource_id] = None

         if fdp in section:
            ids1, ids2 = _arrfy(self.getItems(section, cat_id)), sections[cat]
            assert(ids1 == ids2), self.errorReferenceNotFound(fdp, cat_id, cat)

         if cat in section:
            ids1, ids2 = _arrfy(self.getItems(section, dat_id)), sections[dat]
            assert(ids1 == ids2), self.errorReferenceNotFound(cat, dat_id, dat)

         if dat in section:
            ids1, ids2 = _arrfy(self.getItems(section, dist_id)), sections[dist]
            assert(ids1 == ids2), self.errorReferenceNotFound(dat, dist_id, dist)


class FAIRGraph(object):
   def __init__(self, base_uri):
      graph = ConjunctiveGraph()
      self._graph = graph
      self._base_uri = base_uri
      self._uniq_ids = dict()

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


   def setMetadata(self, triple):
      assert(isinstance(triple, tuple)), 'Metadata must be in a tuple.'
      s, p, o = triple

      # set FDP metadata
      if s == 'fdp':
         uri = self.fdpURI()
         cg = self._graph_context(uri)

         cg.add( (uri, RDF.type, DCTERMS.Agent) )
         cg.add( (uri, RDFS.seeAlso, self.docURI()) )
         cg.add( (uri, DCTERMS.language, LANG.en) )

         self._onto_map_predicate((cg, uri, p, o))

      # set Catalog metadata
      if 'catalog/' in s:
         cat_id = s.split('/')[1]
         uri = self.catURI(cat_id)
         cg = self._graph_context(uri)

         cg.add( (uri, RDF.type, DCAT.Catalog) )
         cg.add( (uri, DCTERMS.language, LANG.en) )

         self._onto_map_predicate((cg, uri, p, o))

      # set Dataset metadata
      if 'dataset/' in s:
         dat_id = s.split('/')[1]
         uri = self.datURI(dat_id)
         cg = self._graph_context(uri)

         cg.add( (uri, RDF.type, DCAT.Dataset) )
         cg.add( (uri, DCTERMS.language, LANG.en) )

         self._onto_map_predicate((cg, uri, p, o))

      # set Distribution metadata
      if 'distribution/' in s:
         dist_id = s.split('/')[1]
         uri = self.distURI(dist_id)
         cg = self._graph_context(uri)

         cg.add( (uri, RDF.type, DCAT.Distribution) )
         cg.add( (uri, DCTERMS.language, LANG.en) )

         self._onto_map_predicate((cg, uri, p, o))


   def _graph_context(self, uri):
      return self._graph.get_context(uri)


   def _onto_map_predicate(self, quad):
      g, s, p, o = quad

      if p in _ONTO_MAP_PREDICATE:
         dtype = _ONTO_MAP_PREDICATE[p][0]

         for mp in _ONTO_MAP_PREDICATE[p][1:]:
            o = URIRef(o) if dtype is None else Literal(o, datatype=dtype)
            g.add( (s, mp, o) )


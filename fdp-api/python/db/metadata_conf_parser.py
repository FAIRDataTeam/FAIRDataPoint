from ConfigParser import SafeConfigParser

_CORE_META   = ['title','publisher','version','issued','modified']
_REQUIRED_META = dict(fdp          = _CORE_META + ['fdp_id','catalog_id'],
                      catalog      = _CORE_META + ['dataset_id','theme_taxonomy'],
                      dataset      = _CORE_META + ['distribution_id','theme'],
                      distribution = _CORE_META + ['access_url|download_url','media_type','license'])

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


filename = 'metadata.ini'
parser = FAIRConfigParser()
parser.read(filename)

#catalogs = [cat for cat in parser.getSectionHeaders() if 'catalog' in cat]
#print catalogs
#print parser.getMetadata()
#print parser.getSectionHeaders()
#print parser.getFields('catalog/catalog-01')
#print parser.getItems('fdp', 'fdp_id')

for s,o,p in  parser.triplify():
   print s, o, p


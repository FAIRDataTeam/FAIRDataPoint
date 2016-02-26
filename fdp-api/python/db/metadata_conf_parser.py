from ConfigParser import SafeConfigParser

_CORE_META   = ['title','publisher','version','issued','modified']
_REQUIRED_META = dict(fdp          = _CORE_META + ['fdp_id','catalog_id'],
                      catalog      = _CORE_META + ['dataset_id','theme_taxonomy'],
                      dataset      = _CORE_META + ['distribution_id','theme'],
                      distribution = _CORE_META + ['access_url|download_url','media_type'])

class FAIRConfigParser(object):
   def __init__(self):
      parser = SafeConfigParser()
      self._parser = parser
      self._metadata = dict()


   @staticmethod
   def msg_section_not_found(section):
      return "Section '%s' not found." % section


   @staticmethod
   def msg_field_not_found(field, section):
      return "Field '%s' not found in section '%s'." % (field, section)

   
   @staticmethod
   def msg_reference_not_found(section, field, ref_section_by_field):
      return "{f}(s) in the '{s}' section not referenced in the '{r}/<{f}>' section header(s) or vice versa.".format(f=field, r=ref_section_by_field, s=section)


   def read(self, filename):
      self._parser.read(filename)

      for section in self._parser.sections():
         self._metadata[section] = dict()

         for key,value in self._parser.items(section):
            if '\n' in value: value = value.split('\n')
            self._metadata[section][key] = value

      self._validate()


   def get_all(self):
      return self._metadata


   def get_section_headers(self):
      return self.get_all().keys()


   def get_fields(self, section):
      return self._metadata[section]


   def get_items(self, section, field):
      return self._metadata[section][field]


   def _validate(self):
      # check the presence of mandatory sections and fields in config file
      section_headers = self.get_section_headers()
      sections = dict((section,[]) for section in _REQUIRED_META.keys())
      sfx = '_id'
      fdp, cat, dat, dist = 'fdp', 'catalog', 'dataset', 'distribution'
      cat_id, dat_id, dist_id = cat + sfx, dat + sfx, dist + sfx

      def _arrfy(item):
         if isinstance(item, str):
            return [item]
         return item

      for sh in section_headers:
         if sh in sections:
            sections[sh] = True

         if '/' in sh:
            section, resource_id = sh.split('/')
            if section in sections:
               sections[section].append(resource_id)

      for section,resource in sections.items():
         assert(resource), self.msg_section_not_found(section)

      for section in section_headers:
         for field in _REQUIRED_META[section.split('/')[0]]:
            fields = self.get_fields(section)
         
            if '|' in field: # distribution has two alternatives: access_url|download_url
               a, b = field.split('|')
               assert(a in fields or b in fields), self.msg_field_not_found(field, section)
            else:
               assert(field in fields), self.msg_field_not_found(field, section)

         if fdp in section:
            items = self.get_items(section, cat_id)
            assert(_arrfy(items) == sections[cat]), self.msg_reference_not_found(fdp, cat_id, cat)

         if cat in section:
            items = self.get_items(section, dat_id)
            assert(_arrfy(items) == sections[dat]), self.msg_reference_not_found(cat, dat_id, dat)

         if dat in section:
            items = self.get_items(section, dist_id)
            assert(_arrfy(items) == sections[dist]), self.msg_reference_not_found(dat, dist_id, dist)


filename = 'metadata.ini'
parser = FAIRConfigParser()
parser.read(filename)

print parser.get_all()
#print parser.get_section_headers()
#print parser.get_fields('dataset/breedb')
#print parser.get_items('fdp', 'fdp_id')


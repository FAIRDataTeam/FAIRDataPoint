from ConfigParser import SafeConfigParser
import re


_MANDATORY_SECTIONS = ['fdp','catalog','dataset','distribution']
_MANDATORY_FIELDS   = ['title','publisher','version','issued','modified']
_FDP_FIELDS = _MANDATORY_FIELDS + ['fdp_id','catalog_id']
_CATALOG_FIELDS = _MANDATORY_FIELDS + ['dataset_id','theme_taxonomy']
_DATASET_FIELDS = _MANDATORY_FIELDS + ['distribution_id','theme']
_DISTRIBUTION_FIELDS = _MANDATORY_FIELDS + ['access_url|download_url','media_type']


class Metadata(object):
   def __init__(self):
      parser = SafeConfigParser()
      self._parser = parser
      self._metadata = dict()


   @staticmethod
   def section_not_found(section):
      return "Section '%s' not found." % section


   @staticmethod
   def field_not_found(field, section):
      return "Field '%s' not found in section '%s'." % (field, section)


   def read(self, filename):
      self._parser.read(filename)

      for section in self._parser.sections():
         self._metadata[section] = dict()

         for key,value in self._parser.items(section):
            if '\n' in value: value = value.split('\n')
            self._metadata[section][key] = value

      for section in self._check_sections():
         self._check_fields(section)

   def get_all(self):
      return self._metadata


   def get_sections(self):
      return self.get_all().keys()


   def get_fields(self, section):
      return self._metadata[section]


   def _check_sections(self):
      _SECTIONS_RE = re.compile('(?P<fdp>^fdp)|'\
                              + '^catalog/(?P<cat_id>\S+$)|'\
                              + '^dataset/(?P<dat_id>\S+)$|'\
                              + '^distribution/(?P<dist_id>\S+)$')

      lookup_sections = dict(fdp          = False,
                             catalog      = False,
                             dataset      = False,
                             distribution = False)
   
      sections = self.get_sections()

      for section in sections:
         fdp, cat, dat, dist = None, None, None, None
         match = _SECTIONS_RE.search(section)

         if match is not None:
            fdp = match.group('fdp')
            cat = match.group('cat_id')
            dat = match.group('dat_id')
            dist = match.group('dist_id')

         if fdp: lookup_sections['fdp'] = True
         if cat: lookup_sections['catalog'] = True
         if dat: lookup_sections['dataset'] = True
         if dist: lookup_sections['distribution'] = True

      for section in _MANDATORY_SECTIONS:
         assert(lookup_sections[section]), self.section_not_found(section)

      return sections


   def _check_fields(self, section):
      lookup_fields = dict(fdp = _FDP_FIELDS,
                           catalog = _CATALOG_FIELDS,
                           dataset = _DATASET_FIELDS,
                           distribution = _DISTRIBUTION_FIELDS)

      for field in lookup_fields[section.split('/')[0]]:
         fields = self._metadata[section]
         
         if '|' in field: # two alternative keys
            a, b = field.split('|')
            assert(a in fields or b in fields), self.field_not_found(field, section)
         else:
            assert(field in fields), self.field_not_found(field, section)


filename = 'metadata.ini'
m = Metadata()
m.read(filename)

print m.get_all()
print m.get_sections()
print m.get_fields('dataset/breedb')


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
   def msg_section_not_found(section):
      return "Section '%s' not found." % section


   @staticmethod
   def mgs_field_not_found(field, section):
      return "Field '%s' not found in section '%s'." % (field, section)

   
   @staticmethod
   def msg_ids_not_ref(section, field, section_refby_field):
      return "{f}(s) in the '{s}' section not referred in the '{r}/<{f}>' section header(s).".format(f=field, r=section_refby_field, s=section)


   def read(self, filename):
      self._parser.read(filename)

      for section in self._parser.sections():
         self._metadata[section] = dict()

         for key,value in self._parser.items(section):
            if '\n' in value: value = value.split('\n')
            self._metadata[section][key] = value

      self._validate()
      #for section in self._check_sections():
      #   self._check_fields(section)

   def get_all(self):
      return self._metadata


   def get_sections(self):
      return self.get_all().keys()


   def get_fields(self, section):
      return self._metadata[section]


   def get_items(self, section, field):
      return self._metadata[section][field]


   def _validate(self):
      _RE_SECTIONS = re.compile('(?P<fdp>^fdp)|'\
                              + '^catalog/(?P<catalog_id>\S+$)|'\
                              + '^dataset/(?P<dataset_id>\S+)$|'\
                              + '^distribution/(?P<distribution_id>\S+)$')

      lookup_sections = dict(fdp          = None,
                             catalog      = [],
                             dataset      = [],
                             distribution = [])

      lookup_fields = dict(fdp          = _FDP_FIELDS,
                           catalog      = _CATALOG_FIELDS,
                           dataset      = _DATASET_FIELDS,
                           distribution = _DISTRIBUTION_FIELDS)
   
      sections = self.get_sections()

      for section in sections:
         fdp, cat, dat, dist = None, None, None, None
         match = _RE_SECTIONS.search(section)

         if match is not None:
            fdp = match.group('fdp')
            cat = match.group('catalog_id')
            dat = match.group('dataset_id')
            dist = match.group('distribution_id')

         if fdp: lookup_sections['fdp'] = True
         if cat: lookup_sections['catalog'].append(cat)
         if dat: lookup_sections['dataset'].append(dat)
         if dist: lookup_sections['distribution'].append(dist)

      for section in _MANDATORY_SECTIONS:
         assert(lookup_sections[section]), self.msg_section_not_found(section)

      def _arrfy(item):
         if isinstance(item, str):
            return [item]
         return item

      fdp = 'fdp'
      cat = 'catalog'
      cat_id = cat + '_id'
      dat = 'dataset'
      dat_id = dat + '_id'
      dist = 'distribution'
      dist_id = dist + '_id'

      for section in sections:
         for field in lookup_fields[section.split('/')[0]]:
            fields = self._metadata[section]
         
            if '|' in field: # two alternatives for distribution: access_url|download_url
               a, b = field.split('|')
               assert(a in fields or b in fields), self.msg_field_not_found(field, section)
            else:
               assert(field in fields), self.msg_field_not_found(field, section)


         if fdp in section:
            items = self.get_items(section, cat_id)
            assert(_arrfy(items) == lookup_sections[cat]), self.msg_ids_not_ref(fdp, cat_id, cat)

         if cat in section:
            items = self.get_items(section, dat_id)
            assert(_arrfy(items) == lookup_sections[dat]), self.msg_ids_not_ref(cat, dat_id, dat)

         if dat in section:
            items = self.get_items(section, dist_id)
            assert(_arrfy(items) == lookup_sections[dist]), self.msg_ids_not_ref(dat, dist_id, dist)


filename = 'metadata.ini'
m = Metadata()
m.read(filename)

print m.get_all()
#print m.get_sections()
#print m.get_fields('dataset/breedb')
#print m.get_items('fdp', 'fdp_id')


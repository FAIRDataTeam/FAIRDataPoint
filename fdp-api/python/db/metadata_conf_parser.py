from ConfigParser import SafeConfigParser
import re


def parse_sections(arr):
   _SECTIONS_RE = re.compile('(?P<fdp>fdp)|catalog/(?P<cat_id>\S+)|dataset/(?P<dat_id>\S+)|distribution/(?P<dist_id>\S+)')
   sections = dict(fdp=False, catalogs=[], datasets=[], distributions=[])
   
   for e in arr:
      fdp, cat, dat, dist = None, None, None, None
      match = _SECTIONS_RE.search(e)

      if match is not None:
         fdp = match.group('fdp')
         cat = match.group('cat_id')
         dat = match.group('dat_id')
         dist = match.group('dist_id')

      if fdp: sections['fdp'] = True
      if cat: sections['catalogs'].append(cat)
      if dat: sections['datasets'].append(dat)
      if dist: sections['distributions'].append(dist)

   assert(sections['fdp']), 'Missing FDP section'
   assert(sections['catalogs']), 'Missing CATALOG section(s)'
   assert(sections['datasets']), 'Missing DATASET section(s)'
   assert(sections['distributions']), 'Missing DISTRIBUTION section(s)'

   return sections

parser = SafeConfigParser()
parser.read('metadata.ini')
print parse_sections(parser.sections())
#print parser.options('fdp')
#print parser.items('fdp')#catalog_ids')
#for section in parser.sections():
#   for record in parser.items(section):
#      print section, record



from nose.tools import assert_equals
from os import path, makedirs
from urllib2 import urlopen, urlparse, Request
from rdflib import Graph
from rdflib.compare import graph_diff
from rdflib.plugin import register, Parser
from myglobals import *

register('application/ld+json', Parser, 'rdflib_jsonld.parser', 'JsonLDParser')

URLs = [ urlparse.urljoin(BASE_URL, p) for p in URL_PATHS ]
g_fdp = Graph()  # FDP metadata upon HTTP request
g_dump = Graph() # reference metadata from dump file


def test_compare_triple_counts():
   for mime,fext in MIME_TYPES.iteritems():
      dump_path = path.join(DUMP_DIR, path.basename(mime))

      for url in URLs:
         fname = '%s.%s' % (path.basename(urlparse.urlparse(url).path), fext)
         fname = path.join(dump_path, fname)

         req = Request(url)
         req.add_header('Accept', mime)
         res = urlopen(req)

         g_fdp.parse(data=res.read(), format=mime)
         g_dump.parse(fname, format=mime)

         nt_fdp, nt_dump = len(g_fdp), len(g_dump) # triple counts
         assert_equals(nt_fdp, nt_dump, 'Triple counts differ: %d (FDP) vs. %d (ref)' % (nt_fdp, nt_dump))


def test_compare_triples():
   for mime,fext in MIME_TYPES.iteritems():
      dump_path = path.join(DUMP_DIR, path.basename(mime))

      for url in URLs:
         fname = '%s.%s' % (path.basename(urlparse.urlparse(url).path), fext)
         fname = path.join(dump_path, fname)

         req = Request(url)
         req.add_header('Accept', mime)
         res = urlopen(req)

         g_fdp.parse(data=res.read(), format=mime)
         g_dump.parse(fname, format=mime)

         both, first, second = graph_diff(g_fdp, g_dump)
         n_first = len(first)
         #n_second = len(second)
         #n_both = len(both)

         assert_equals(n_first, 0, '{} triple(s) different from reference:\n\n{}===\n{}\n'.format(n_first, first.serialize(format='turtle'), second.serialize(format='turtle')))

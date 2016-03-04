#
# This script creates dump files of metadata in different formats upon requests to FDP.
#

from os import path, makedirs
from urllib2 import urlopen, urlparse, Request
from rdflib import Graph
from logging import getLogger, StreamHandler, INFO
from myglobals import *


logger = getLogger(__name__)
logger.setLevel(INFO)
ch = StreamHandler()
ch.setLevel(INFO)
logger.addHandler(ch)


def dump():
   for fmt,fxt in MIME_TYPES.iteritems():
      dump_path = path.join(DUMP_DIR, path.basename(fmt))
      makedirs(dump_path)

      for url in [ urlparse.urljoin(BASE_URL, p) for p in URL_PATHS ]:
         logger.info("Request metadata in '%s' from\n  %s\n" % (fmt, url))

         req = Request(url)
         req.add_header('Accept', fmt)
         res = urlopen(req)
         fname = '%s.%s' % (path.basename(urlparse.urlparse(url).path), fxt)
         fname = path.join(dump_path, fname)

         logger.info("Write metadata into file './%s'\n" % fname)

         with open(fname, 'w') as fout:
            fout.write(res.read())

dump()


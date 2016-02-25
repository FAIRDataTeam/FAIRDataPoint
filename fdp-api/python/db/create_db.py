from sqlalchemy import create_engine, Column, CheckConstraint, UniqueConstraint, ForeignKey, String, Integer
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import relationship, backref


Base = declarative_base()

class ResourceCoreMeta(Base):
   __tablename__ = 'RESOURCE_CORE_META'

   id          = Column(String(255), primary_key=True)
   type        = Column(Integer,
                       # resouce types referred to by integers:
                       # 1=FDP, 2=catalog, 3=dataset or 4=distribution
                       CheckConstraint('type BETWEEN 1 AND 4', name='ck_resource_type'),
                       nullable=False)
   title       = Column(String(255), nullable=False)
   publisher   = Column(String(255), nullable=False)
   issued      = Column(String(255), nullable=False)
   modified    = Column(String(255), nullable=False)
   version     = Column(String(255), nullable=False)
   license     = Column(String(255), nullable=False)
   description = Column(String(255), nullable=True)
   language    = Column(String(255), nullable=True)

   resource_relation = relationship('ResourceRelation', backref=__tablename__)
   catalog_meta      = relationship('CatalogExtMeta', backref=backref(__tablename__, uselist=False))
   dataset_meta      = relationship('DatasetExtMeta', backref=backref(__tablename__, uselist=False))
   distribution_meta = relationship('DistributionExtMeta', backref=backref(__tablename__, uselist=False))


class ResourceRelation(Base):
   __tablename__ = 'RESOURCE_RELATION'

   parent_id     = Column(String(255),
                          ForeignKey('RESOURCE_CORE_META.id', onupdate='CASCADE', ondelete='CASCADE'),
                          primary_key=True)
   child_id      = Column(String(255),
                          ForeignKey('RESOURCE_CORE_META.id', onupdate='CASCADE', ondelete='CASCADE'),
                          primary_key=True)
   #parent_type   = Column(Integer,
   #                       CheckConstraint('parent_type BETWEEN 1 AND 4', name='ck_parent_type'),
   #                       CheckConstraint('parent_type = child_type - 1', name='ck_parent_child_rel'),
   #                       nullable=False)
   #child_type    = Column(Integer,
   #                       CheckConstraint('child_type BETWEEN 1 AND 4', name='ck_child_type'),
   #                       nullable=False)

class CatalogExtMeta(Base):
   __tablename__ = 'CATALOG_EXT_META'

   id             = Column(String(255),
                           ForeignKey('RESOURCE_CORE_META.id', onupdate='CASCADE', ondelete='CASCADE'),
                           primary_key=True)
   theme_taxonomy = Column(String(255), nullable=False)


class DatasetExtMeta(Base):
   __tablename__ = 'DATASET_EXT_META'

   id            = Column(String(255),
                          ForeignKey('RESOURCE_CORE_META.id', onupdate='CASCADE', ondelete='CASCADE'),
                          primary_key=True)
   theme         = Column(String(255), nullable=False)
   contact_point = Column(String(255), nullable=True)
   keyword       = Column(String(255), nullable=True)
   landing_page  = Column(String(255), nullable=True)


class DistributionExtMeta(Base):
   __tablename__ = 'DISTRIBUTION_EXT_META'

   id         = Column(String(255),
                       ForeignKey('RESOURCE_CORE_META.id', onupdate='CASCADE', ondelete='CASCADE'),
                       primary_key=True)
   url        = Column(String(255), nullable=False)
   media_type = Column(String(255), nullable=False)
   byte_size  = Column(Integer, nullable=True)


engine = create_engine('sqlite:///FAIR.sqlite')
Base.metadata.create_all(engine)

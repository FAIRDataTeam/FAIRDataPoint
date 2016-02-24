from sqlalchemy import create_engine, Column, CheckConstraint, UniqueConstraint, ForeignKey, String, Integer
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import relationship
 
Base = declarative_base()

class ResourceMetadata(Base):
    __tablename__ = 'RESOURCE_METADATA'

    id          = Column(String(255), primary_key=True)
    title       = Column(String(255), nullable=False)
    publisher   = Column(String(255), nullable=False)
    issued      = Column(String(255), nullable=False)
    modified    = Column(String(255), nullable=False)
    version     = Column(String(255), nullable=False)
    license     = Column(String(255), nullable=False)
    description = Column(String(255), nullable=True)
    language    = Column(String(255), nullable=True)

    resource_relation = relationship('ResourceRelation', backref=__tablename__)

class ResourceRelation(Base):
    __tablename__ = 'RESOURCE_RELATION'

    # resouce types encoded as integers: 1=FDP, 2=catalog, 3=dataset or 4=distribution
    parent_id     = Column(String(255),
                           ForeignKey('RESOURCE_METADATA.id', onupdate='CASCADE', ondelete='CASCADE'),
                           primary_key=True)
    child_id      = Column(String(255),
                           ForeignKey('RESOURCE_METADATA.id', onupdate='CASCADE', ondelete='CASCADE'),
                           primary_key=True)
    parent_type   = Column(Integer,
                           CheckConstraint('parent_type BETWEEN 1 AND 4', name='ck_parent_type'),
                           CheckConstraint('parent_type = child_type - 1', name='ck_parent_child_rel'),
                           nullable=False)
    child_type    = Column(Integer,
                           CheckConstraint('child_type BETWEEN 1 AND 4', name='ck_child_type'),
                           nullable=False)
    #PrimaryKeyConstraint('parent_id', 'child_id', name='pk_RESOURCE_RELATION_id')

engine = create_engine('sqlite:///FAIR.sqlite')
Base.metadata.create_all(engine)

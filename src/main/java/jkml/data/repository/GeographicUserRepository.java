package jkml.data.repository;

import jkml.data.entity.GeographicUser;
import jkml.data.entity.GeographicUser.GeographicUserKey;
import jkml.data.repository.support.CustomCassandraRepository;

public interface GeographicUserRepository extends CustomCassandraRepository<GeographicUser, GeographicUserKey> {

}

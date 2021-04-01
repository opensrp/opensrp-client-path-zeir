package org.smartregister.path.repository;

interface ClientRegisterTypeDao {

    boolean removeAll(String baseEntityId);

    boolean add(String registerType, String baseEntityId);
}

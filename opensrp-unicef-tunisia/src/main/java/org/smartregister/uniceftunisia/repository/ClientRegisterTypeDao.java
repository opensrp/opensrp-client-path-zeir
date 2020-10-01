package org.smartregister.uniceftunisia.repository;

interface ClientRegisterTypeDao {

    boolean removeAll(String baseEntityId);

    boolean add(String registerType, String baseEntityId);
}

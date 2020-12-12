package com.gd.sql;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;


// This will be AUTO IMPLEMENTED by Spring into a Bean called networkRepository
// CRUD refers Create, Read, Update, Delete

public interface IpNetworkRepository extends CrudRepository<IpNetwork, IpNetworkKey> {
    @Query(value = "SELECT network FROM ip_network WHERE ip = ?1",
            nativeQuery = true)
    Collection<byte[]> findNetworksIpBelongsTo(byte[] s);

}

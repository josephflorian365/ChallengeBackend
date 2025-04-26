package com.retoTenpo.reto.repository;

import com.retoTenpo.reto.repository.model.Session;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionRepository extends CrudRepository<Session, UUID> {
}

package es.achavez.miw.tfm.restgen.generator.infraestructure.adapter.out.persistence.repository;

import es.achavez.miw.tfm.restgen.generator.infraestructure.adapter.out.persistence.document.UserInfoDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserInfoRepository extends MongoRepository<UserInfoDocument, String> {
    Optional<UserInfoDocument> findByEmail(String email);
}

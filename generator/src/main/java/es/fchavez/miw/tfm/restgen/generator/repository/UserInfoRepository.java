package es.fchavez.miw.tfm.restgen.generator.repository;

import es.fchavez.miw.tfm.restgen.generator.models.UserInfo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserInfoRepository extends MongoRepository<UserInfo, String> {
    Optional<UserInfo> findByEmail(String email);
}

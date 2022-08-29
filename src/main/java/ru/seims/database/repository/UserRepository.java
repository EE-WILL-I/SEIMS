package ru.seims.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.seims.database.entitiy.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query(value = "select * from users where login = :login", nativeQuery = true)
    User findByUsername(@Param("login") String name);
    //User save(User user);
}

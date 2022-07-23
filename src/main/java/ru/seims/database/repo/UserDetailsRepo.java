package ru.seims.database.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.seims.database.entitiy.User;

public interface UserDetailsRepo extends JpaRepository<User, String> {
}

package com.rapidrent.rapidrent.repository;

import com.rapidrent.rapidrent.model.DocumentStatus;
import com.rapidrent.rapidrent.model.Role;
import com.rapidrent.rapidrent.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findByDocumentStatus(DocumentStatus documentStatus);
    boolean existsByRole(Role role);
}

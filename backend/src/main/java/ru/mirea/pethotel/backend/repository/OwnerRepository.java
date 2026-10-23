package ru.mirea.pethotel.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mirea.pethotel.backend.domain.Owner;

import java.util.Optional;

public interface OwnerRepository extends JpaRepository<Owner, Long>
{
    boolean existsByLogin(String login);
    boolean existsByPhone(String phone);
    boolean existsByLoginAndIdNot(String login, Long id);
    boolean existsByPhoneAndIdNot(String phone, Long id);
    Optional<Owner> findByPhone(String phone);

}

package StudyForge_backend.Repository;

import StudyForge_backend.Model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<Users,String> {

    Users findByEmail(String email);
}

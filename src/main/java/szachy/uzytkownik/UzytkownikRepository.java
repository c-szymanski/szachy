package szachy.uzytkownik;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UzytkownikRepository extends JpaRepository<Uzytkownik, Long> {
    @Query(value = "SELECT * FROM uzytkownik u WHERE u.email =:email", nativeQuery = true)
    Uzytkownik findUzytkownikByEmail(@Param("email") String email);
}

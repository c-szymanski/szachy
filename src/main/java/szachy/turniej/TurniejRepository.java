package szachy.turniej;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
public interface TurniejRepository extends JpaRepository<Turniej, Long> {
    @Query(value = "SELECT * FROM turniej t WHERE t.nazwa =:nazwa", nativeQuery = true)
    Turniej findTurniejByNazwa(@Param("nazwa") String nazwa);
}
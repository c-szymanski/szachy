package szachy.mecz;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import szachy.uzytkownik.Uzytkownik;

import java.util.ArrayList;

public interface MeczRepository extends JpaRepository<Mecz, Long> {
    @Query(value = "SELECT * FROM mecz m WHERE m.id_Meczu =:idMeczu", nativeQuery = true)
    Mecz findMeczByIdMeczu(@Param("idMeczu") Long idMeczu);

    @Query(value = "SELECT * FROM mecz m WHERE m.uzytkownik_biale_id =:idUzytkownika OR m.uzytkownik_czarne_id =:idUzytkownika", nativeQuery = true)
    ArrayList<Mecz> findMeczHistoria(@Param("idUzytkownika") Long idUzytkownika);
}

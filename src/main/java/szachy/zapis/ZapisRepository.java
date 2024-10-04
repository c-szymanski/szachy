package szachy.zapis;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ZapisRepository extends JpaRepository<Zapis, Long> {
    @Query(value = "SELECT * FROM zapis z WHERE z.id_zapisu =:idZapisu", nativeQuery = true)
    Zapis findZapisById(@Param("idZapisu") String idZapisu);

    @Query(value = "SELECT * FROM zapis z WHERE z.id_uzytkownika =:idUzytkownika AND z.id_turnieju =:idTurnieju", nativeQuery = true)
    Zapis findZapisByIdUzytkownikaAndIdTurnieju(@Param("idUzytkownika") Long idUzytkownika, @Param("idTurnieju") Long idTurnieju);
}

package szachy.uzytkownik;

import javax.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Uzytkownik {

    @Id
    @SequenceGenerator(
            name = "idUzytkownikaSequence",
            sequenceName = "idUzytkownikaSequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "idUzytkownikaSequence"
    )
    private Long idUzytkownika;
    private String email;
    private String haslo;
    private String imie;
    private String nazwisko;
    private boolean czyOrganizator;
    private int punkty;

    public Uzytkownik(String email, String haslo, String imie, String nazwisko, boolean czyOrganizator, int punkty) {
        this.email = email;
        this.haslo = haslo;
        this.imie = imie;
        this.nazwisko = nazwisko;
        this.czyOrganizator = czyOrganizator;
        this.punkty = punkty;
    }
}

package szachy.mecz;

import javax.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import szachy.turniej.Turniej;
import szachy.uzytkownik.Uzytkownik;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Mecz {

    @Id
    @SequenceGenerator(
            name = "idMeczuSequence",
            sequenceName = "idMeczuSequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "idMeczuSequence"
    )
    private Long idMeczu;
    private String wynik;

    @ManyToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name="id_uzytkownik_biale")
    private Uzytkownik uzytkownikBiale;
    @ManyToOne
    @JoinColumn(name = "id_uzytkownik_czarne")
    private Uzytkownik uzytkownikCzarne;

    @ManyToOne
    @JoinColumn(name = "id_turnieju")
    private Turniej turniej;

    public Mecz(Turniej turniej, Uzytkownik uzytkownikBiale, Uzytkownik uzytkownikCzarne, String wynik) {
        this.turniej=turniej;
        this.uzytkownikBiale = uzytkownikBiale;
        this.uzytkownikCzarne = uzytkownikCzarne;
        this.wynik = wynik;
    }
}

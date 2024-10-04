package szachy.zapis;

import javax.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import szachy.mecz.Mecz;
import szachy.turniej.Turniej;
import szachy.uzytkownik.Uzytkownik;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Zapis {

    @Id
    @SequenceGenerator(
            name = "idZapisuSequence",
            sequenceName = "idZapisuSequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "idZapisuSequence"
    )
    private Long idZapisu;
    private boolean czyZapisany;

    @ManyToOne
    @JoinColumn(name = "id_uzytkownika")
    private Uzytkownik uzytkownik;

    @ManyToOne
    @JoinColumn(name = "id_turnieju")
    private Turniej turniej;

    public Zapis(Uzytkownik uzytkownik,Turniej turniej,boolean czyZapisany) {
        this.uzytkownik=uzytkownik;
        this.turniej=turniej;
        this.czyZapisany = czyZapisany;
    }
}

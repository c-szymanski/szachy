package szachy.turniej;

import javax.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import szachy.mecz.Mecz;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Turniej {

    @Id
    @SequenceGenerator(
            name = "idTurniejuSequence",
            sequenceName = "idTurniejuSequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "idTurniejuSequence"
    )
    private Long idTurnieju;
    private String nazwa;

    public Turniej(String nazwa) {
        this.nazwa = nazwa;
    }
}

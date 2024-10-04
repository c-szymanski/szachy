package szachy.turniej;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import szachy.jwt.JwtUserDetailsService;
import szachy.mecz.Mecz;
import szachy.mecz.MeczRepository;
import szachy.uzytkownik.Uzytkownik;
import szachy.uzytkownik.UzytkownikRepository;
import szachy.turniej.Turniej;
import szachy.turniej.TurniejRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
@RestController
@RequestMapping(path="/turniej")
public class TurniejController {
    @Autowired
    private UzytkownikRepository uzytkownikRepository;
    @Autowired
    private MeczRepository meczRepository;

    @Autowired
    private TurniejRepository turniejRepository;

    @PostMapping("/utworz_turniej")
    public ResponseEntity<?> utworzTurniej(@RequestBody HashMap<String, Object> JSON) {
        Map<String, Object> responseMap = new HashMap<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Uzytkownik organizator = uzytkownikRepository.findUzytkownikByEmail(authentication.getName());
        if(!(organizator.isCzyOrganizator())){
            responseMap.put("error", true);
            responseMap.put("message", "uzytkownik nie ma uprawnien do tworzenia turniejów");
            return ResponseEntity.status(403).body(responseMap);
        }
        String nazwa=JSON.get("nazwa").toString();
        if (nazwa == null) {
            responseMap.put("error", true);
            responseMap.put("message", "Nie podano nazwy turnieju");
            return ResponseEntity.status(400).body(responseMap);
        }
        Turniej turniej = turniejRepository.findTurniejByNazwa(nazwa);
        if (turniej != null) {
            responseMap.put("error", true);
            responseMap.put("message", "Turniej o nazwie "+nazwa+" juz istnieje, wybierz inną nazwę");
            return ResponseEntity.status(409).body(responseMap);
        }
        Turniej nowyTurniej = new Turniej(JSON.get("nazwa").toString());
        turniejRepository.save(nowyTurniej);
        responseMap.put("error", false);
        responseMap.put("message", "Turniej utworzony pomyslnie");
        return ResponseEntity.ok(responseMap);
    }
    @DeleteMapping("/usun_turniej")
    public ResponseEntity<?> usunTurniej(@RequestBody HashMap<String, Object> JSON){
        Map<String, Object> responseMap = new HashMap<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Uzytkownik organizator = uzytkownikRepository.findUzytkownikByEmail(authentication.getName());
        String nazwa=JSON.get("nazwa").toString();
        Turniej turniej = turniejRepository.findTurniejByNazwa(nazwa);
        if(!(organizator.isCzyOrganizator())){
            responseMap.put("error", true);
            responseMap.put("message", "uzytkownik nie ma uprawnien do usuwania kont turniejów");
            return ResponseEntity.status(403).body(responseMap);
        }
        if(turniej == null){
            responseMap.put("error", true);
            responseMap.put("message", "Brak istniejącego turnieju o nazwie:" +nazwa);
            return ResponseEntity.status(401).body(responseMap);
        }
        turniejRepository.delete(turniej);
        responseMap.put("error", false);
        responseMap.put("message", "Pomyślnie usunieto turniej");
        return ResponseEntity.ok(responseMap);
    }
    @GetMapping("/wyswietl_turniej")
    public ResponseEntity<?> wyswietlTurniej(@RequestBody HashMap<String, Object> JSON){
        Map<String, Object> responseMap = new HashMap<>();
        String nazwa=JSON.get("nazwa").toString();
        Turniej turniej=turniejRepository.findTurniejByNazwa(nazwa);
        if(turniej == null){
            responseMap.put("error", true);
            responseMap.put("message", "Brak istniejącego turnieju o nazwie:" +nazwa);
            return ResponseEntity.status(401).body(responseMap);
        }
        return ResponseEntity.ok(turniej);
    }
    @PutMapping("/zmien_nazwe")
    public ResponseEntity<?> zmienNazwe(@RequestBody HashMap<String, Object> JSON){
        Map<String, Object> responseMap = new HashMap<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Uzytkownik uzytkownik = uzytkownikRepository.findUzytkownikByEmail(authentication.getName());
        String nazwa=JSON.get("nazwa").toString();
        Turniej turniej = turniejRepository.findTurniejByNazwa(nazwa);
        if(!(uzytkownik.isCzyOrganizator())){
            responseMap.put("error", true);
            responseMap.put("message", "uzytkownik nie ma uprawnien do zmieniania nazw turniejów");
            return ResponseEntity.status(403).body(responseMap);
        }
        if(turniej == null){
            responseMap.put("error", true);
            responseMap.put("message", "Brak istniejącego turnieju o nazwie:" +nazwa);
            return ResponseEntity.status(401).body(responseMap);
        }
        if (nazwa == null) {
            responseMap.put("error", true);
            responseMap.put("message", "Nie podano nowej nazwy turnieju");
            return ResponseEntity.status(400).body(responseMap);
        }
        String nowaNazwa=JSON.get("nowaNazwa").toString();
        if(nowaNazwa.equals(nazwa)){
            responseMap.put("error", true);
            responseMap.put("message", "Podano starą nazwę turnieju");
            return ResponseEntity.status(409).body(responseMap);
        }
        turniej.setNazwa(nowaNazwa);
        turniejRepository.save(turniej);
        responseMap.put("error", false);
        responseMap.put("message", "Nazwa turnieju została zmieniona");
        return ResponseEntity.ok(responseMap);
    }

}


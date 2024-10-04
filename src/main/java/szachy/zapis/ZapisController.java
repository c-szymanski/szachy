package szachy.zapis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import szachy.mecz.MeczRepository;
import szachy.turniej.Turniej;
import szachy.turniej.TurniejRepository;
import szachy.uzytkownik.Uzytkownik;
import szachy.uzytkownik.UzytkownikRepository;
import szachy.zapis.Zapis;
import szachy.zapis.ZapisRepository;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(path="/zapis")
public class ZapisController {
    @Autowired
    private UzytkownikRepository uzytkownikRepository;
    @Autowired
    private MeczRepository meczRepository;
    @Autowired
    private TurniejRepository turniejRepository;
    @Autowired
    private ZapisRepository zapisRepository;

    @PostMapping("/zapisz_na_turniej")
    public ResponseEntity<?> zapiszNaTurniej(@RequestBody HashMap<String, Object> JSON) {
        Map<String, Object> responseMap = new HashMap<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Uzytkownik uzytkownik = uzytkownikRepository.findUzytkownikByEmail(authentication.getName());
        if(uzytkownik.isCzyOrganizator()){
            responseMap.put("error", true);
            responseMap.put("message", "organizator nie może zapisywać się na turnieje");
            return ResponseEntity.status(403).body(responseMap);
        }
        String nazwa=JSON.get("nazwa").toString();
        if (nazwa == null) {
            responseMap.put("error", true);
            responseMap.put("message", "Nie podano nazwy turnieju");
            return ResponseEntity.status(400).body(responseMap);
        }
        Turniej turniej = turniejRepository.findTurniejByNazwa(nazwa);
        if (turniej == null) {
            responseMap.put("error", true);
            responseMap.put("message", "Nie istnieje turniej o nazwie "+nazwa+", podaj inną nazwę");
            return ResponseEntity.status(409).body(responseMap);
        }
        Zapis zapis = zapisRepository.findZapisByIdUzytkownikaAndIdTurnieju(uzytkownik.getIdUzytkownika(),turniej.getIdTurnieju());
        if (zapis != null) {
            responseMap.put("error", true);
            responseMap.put("message", "Uzytkownik "+uzytkownik.getEmail()+" jest już zapisany na turniej "+turniej.getNazwa());
            return ResponseEntity.status(409).body(responseMap);
        }
        Zapis nowyZapis = new Zapis(uzytkownik,turniej,true);
        zapisRepository.save(nowyZapis);
        responseMap.put("error", false);
        responseMap.put("message", "Zapis utworzony pomyslnie");
        return ResponseEntity.ok(responseMap);
    }

    @PutMapping("/wypisz_z_turnieju")
    public ResponseEntity<?> wypiszZTurnieju(@RequestBody HashMap<String, Object> JSON){
        Map<String, Object> responseMap = new HashMap<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Uzytkownik uzytkownik = uzytkownikRepository.findUzytkownikByEmail(authentication.getName());
        if(uzytkownik.isCzyOrganizator()){
            responseMap.put("error", true);
            responseMap.put("message", "organizator nie może wypisywać się z turniejów");
            return ResponseEntity.status(403).body(responseMap);
        }
        String nazwa=JSON.get("nazwa").toString();
        if (nazwa == null) {
            responseMap.put("error", true);
            responseMap.put("message", "Nie podano nazwy turnieju");
            return ResponseEntity.status(400).body(responseMap);
        }
        Turniej turniej = turniejRepository.findTurniejByNazwa(nazwa);
        if (turniej == null) {
            responseMap.put("error", true);
            responseMap.put("message", "Nie istnieje turniej o nazwie "+nazwa+", podaj inną nazwę");
            return ResponseEntity.status(409).body(responseMap);
        }
        Zapis zapis = zapisRepository.findZapisByIdUzytkownikaAndIdTurnieju(uzytkownik.getIdUzytkownika(),turniej.getIdTurnieju());
        if (zapis == null) {
            responseMap.put("error", true);
            responseMap.put("message", "Uzytkownik "+uzytkownik.getEmail()+" nie jest zapisany na turniej "+turniej.getNazwa());
            return ResponseEntity.status(409).body(responseMap);
        }
        zapis.setCzyZapisany(false);
        zapisRepository.save(zapis);
        responseMap.put("error", false);
        responseMap.put("message", "Pomyślnie wypisano z turnieju");
        return ResponseEntity.ok(responseMap);
    }
}

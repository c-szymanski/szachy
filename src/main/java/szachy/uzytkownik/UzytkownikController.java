package szachy.uzytkownik;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import szachy.jwt.JwtUserDetailsService;
import szachy.mecz.Mecz;
import szachy.turniej.Turniej;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(path="/uzytkownik")
public class UzytkownikController {

    @Autowired
    private UzytkownikRepository uzytkownikRepository;

    @Autowired
    private JwtUserDetailsService userDetailsService;

    @GetMapping("/wyswietl_punkty")
    public ResponseEntity<?> wyswietlPunkty(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Uzytkownik uzytkownik = uzytkownikRepository.findUzytkownikByEmail(authentication.getName());
        return ResponseEntity.ok(uzytkownik.getPunkty());
    }
    @PutMapping("/zmien_punkty")
    public ResponseEntity<?> zmienWynik(@RequestBody HashMap<String, Object> JSON){
        Map<String, Object> responseMap = new HashMap<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Uzytkownik organizator = uzytkownikRepository.findUzytkownikByEmail(authentication.getName());
        String email=JSON.get("email").toString();
        int punkty= Integer.parseInt(JSON.get("punkty").toString());
        Uzytkownik uzytkownik = uzytkownikRepository.findUzytkownikByEmail(email);
        if(!(organizator.isCzyOrganizator())){
            responseMap.put("error", true);
            responseMap.put("message", "uzytkownik nie ma uprawnien do zmieniania punktów zawodników");
            return ResponseEntity.status(403).body(responseMap);
        }
        if(uzytkownik == null){
            responseMap.put("error", true);
            responseMap.put("message", "Brak aktywnego konta uzytkownika z adresem email:" +email);
            return ResponseEntity.status(401).body(responseMap);
        }
        if (email == null) {
            responseMap.put("error", true);
            responseMap.put("message", "Nie podano adresu email uzytkownika");
            return ResponseEntity.status(400).body(responseMap);
        }
        if (punkty < 1) {
            responseMap.put("error", true);
            responseMap.put("message", "Nie można zmienić punktów na mniej niż 1");
            return ResponseEntity.status(400).body(responseMap);
        }
        if (uzytkownik.isCzyOrganizator()==true) {
            responseMap.put("error", true);
            responseMap.put("message", "Nie można zmieniać punktów organizatorów");
            return ResponseEntity.status(400).body(responseMap);
        }
        uzytkownik.setPunkty(punkty);
        uzytkownikRepository.save(uzytkownik);
        responseMap.put("error", false);
        responseMap.put("message", "Punkty zawodnika zostały zmienione");
        return ResponseEntity.ok(responseMap);
    }
    @DeleteMapping("/usun_uzytkownika")
    public ResponseEntity<?> usunUzytkownika(@RequestBody HashMap<String, Object> JSON){
        Map<String, Object> responseMap = new HashMap<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Uzytkownik organizator = uzytkownikRepository.findUzytkownikByEmail(authentication.getName());
        String email=JSON.get("email").toString();
        Uzytkownik uzytkownik = uzytkownikRepository.findUzytkownikByEmail(email);
        if(!(organizator.isCzyOrganizator())){
            responseMap.put("error", true);
            responseMap.put("message", "uzytkownik nie ma uprawnien do usuwania kont zawodników");
            return ResponseEntity.status(403).body(responseMap);
        }
        if(uzytkownik == null){
            responseMap.put("error", true);
            responseMap.put("message", "Brak aktywnego konta uzytkownika z adresem email:" +email);
            return ResponseEntity.status(401).body(responseMap);
        }
        uzytkownikRepository.delete(uzytkownik);
        responseMap.put("error", false);
        responseMap.put("message", "Pomyślnie usunieto uzytkownika");
        return ResponseEntity.ok(responseMap);
    }
}

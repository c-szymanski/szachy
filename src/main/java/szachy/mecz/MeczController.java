package szachy.mecz;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import szachy.jwt.JwtUserDetailsService;
import szachy.mecz.MeczRepository;
import szachy.uzytkownik.Uzytkownik;
import szachy.uzytkownik.UzytkownikRepository;
import szachy.turniej.Turniej;
import szachy.turniej.TurniejRepository;
import szachy.zapis.Zapis;
import szachy.zapis.ZapisRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(path="/mecz")
public class MeczController {

    @Autowired
    private UzytkownikRepository uzytkownikRepository;
    @Autowired
    private MeczRepository meczRepository;
    @Autowired
    private JwtUserDetailsService userDetailsService;
    @Autowired
    private TurniejRepository turniejRepository;
    @Autowired
    private ZapisRepository zapisRepository;

    @PostMapping("/utworz_mecz")
    public ResponseEntity<?> utworzMecz(@RequestBody HashMap<String, Object> JSON) {
        Map<String, Object> responseMap = new HashMap<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Uzytkownik organizator = uzytkownikRepository.findUzytkownikByEmail(authentication.getName());
        if(!(organizator.isCzyOrganizator())){
            responseMap.put("error", true);
            responseMap.put("message", "uzytkownik nie ma uprawnien do tworzenia meczów");
            return ResponseEntity.status(403).body(responseMap);
        }
        String [] parametry = {"nazwaTurnieju","emailZawodnikaBiale", "emailZawodnikaCzarne"};
        for(String i : parametry)
            if(JSON.get(i) == null) {
                responseMap.put("error", true);
                responseMap.put("massage", "Nie podano wszystkich wymaganych pol,podaj nazwe turnieju i obydwa maile zawodników");
                return ResponseEntity.status(400).body(responseMap);
            }
        String nazwaTurnieju = JSON.get("nazwaTurnieju").toString();
        String emailZawodnikaBiale = JSON.get("emailZawodnikaBiale").toString();
        String emailZawodnikaCzarne = JSON.get("emailZawodnikaCzarne").toString();
        String wynik = JSON.get("wynik").toString();
        Turniej turniej = turniejRepository.findTurniejByNazwa(nazwaTurnieju);
        Uzytkownik ZawodnikBiale = uzytkownikRepository.findUzytkownikByEmail(emailZawodnikaBiale);
        Uzytkownik ZawodnikCzarne = uzytkownikRepository.findUzytkownikByEmail(emailZawodnikaCzarne);
        if (ZawodnikBiale == null || ZawodnikCzarne == null){
            responseMap.put("error", true);
            responseMap.put("message", "Nie ma zawodnika o takim emailu");
            return ResponseEntity.status(401).body(responseMap);
        }
        if (turniej == null){
            responseMap.put("error", true);
            responseMap.put("message", "Nie ma turnieju o takiej nazwie");
            return ResponseEntity.status(401).body(responseMap);
        }
        Zapis zapisBiale = zapisRepository.findZapisByIdUzytkownikaAndIdTurnieju(ZawodnikBiale.getIdUzytkownika(),turniej.getIdTurnieju());
        Zapis zapisCzarne = zapisRepository.findZapisByIdUzytkownikaAndIdTurnieju(ZawodnikCzarne.getIdUzytkownika(),turniej.getIdTurnieju());
        if (zapisBiale == null || zapisBiale.isCzyZapisany() == false){
            responseMap.put("error", true);
            responseMap.put("message", "Użytkownik "+ZawodnikBiale.getEmail()+" nie jest zapisany na ten turniej");
            return ResponseEntity.status(401).body(responseMap);
        }
        if (zapisCzarne == null || zapisCzarne.isCzyZapisany() == false){
            responseMap.put("error", true);
            responseMap.put("message", "Użytkownik "+ZawodnikCzarne.getEmail()+" nie jest zapisany na ten turniej");
            return ResponseEntity.status(401).body(responseMap);
        }
        Mecz nowyMecz = new Mecz(turniej,ZawodnikBiale,ZawodnikCzarne,wynik);
        meczRepository.save(nowyMecz);
        responseMap.put("error", false);
        responseMap.put("message", "Mecz utworzony pomyslnie");
        return ResponseEntity.ok(responseMap);
    }
    @DeleteMapping("/usun_mecz")
    public ResponseEntity<?> usunMecz(@RequestParam Long idMeczu){
        Map<String, Object> responseMap = new HashMap<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Uzytkownik uzytkownik = uzytkownikRepository.findUzytkownikByEmail(authentication.getName());
        Mecz mecz = meczRepository.findMeczByIdMeczu(idMeczu);
        if(!(uzytkownik.isCzyOrganizator())){
            responseMap.put("error", true);
            responseMap.put("message", "uzytkownik nie ma uprawnien do usuwania meczów");
            return ResponseEntity.status(403).body(responseMap);
        }
        if(mecz == null){
            return ResponseEntity.notFound().build();
        }
        meczRepository.delete(mecz);
        responseMap.put("error", false);
        responseMap.put("message", "Pomyślnie usunieto mecz");
        return ResponseEntity.ok(responseMap);
    }

    @PutMapping("/zmien_wynik")
    public ResponseEntity<?> zmienWynik(@RequestBody HashMap<String, Object> JSON, @RequestParam Long idMeczu){
        Map<String, Object> responseMap = new HashMap<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Uzytkownik uzytkownik = uzytkownikRepository.findUzytkownikByEmail(authentication.getName());
        Mecz mecz = meczRepository.findMeczByIdMeczu(idMeczu);
        if(!(uzytkownik.isCzyOrganizator())){
            responseMap.put("error", true);
            responseMap.put("message", "uzytkownik nie ma uprawnien do zmieniania wyników meczów");
            return ResponseEntity.status(403).body(responseMap);
        }
        if(mecz == null){
            return ResponseEntity.notFound().build();
        }
        if (JSON.get("wynik") == null) {
            responseMap.put("error", true);
            responseMap.put("message", "Nie podano wyniku");
            return ResponseEntity.status(400).body(responseMap);
        }
        String wynik = JSON.get("wynik").toString();
        mecz.setWynik(wynik);
        meczRepository.save(mecz);
        responseMap.put("error", false);
        responseMap.put("message", "Wynik meczu został zmieniony");
        return ResponseEntity.ok(responseMap);
    }

    @GetMapping("/wyswietl_mecz")
    public ResponseEntity<?> wyswietlMecz(@RequestParam Long idMeczu){
        Mecz mecz = meczRepository.findMeczByIdMeczu(idMeczu);
        if(mecz == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(mecz);
    }
    @GetMapping("/wyswietl_historie")
    public ResponseEntity<?> wyswietlHistorie(@RequestBody HashMap<String, Object> JSON){
        Map<String, Object> responseMap = new HashMap<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Uzytkownik uzytkownik = uzytkownikRepository.findUzytkownikByEmail(authentication.getName());
        ArrayList<Mecz> historia= meczRepository.findMeczHistoria(uzytkownik.getIdUzytkownika());
        return ResponseEntity.ok(historia);
    }
}

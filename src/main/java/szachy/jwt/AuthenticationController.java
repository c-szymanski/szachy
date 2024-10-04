package szachy.jwt;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import szachy.uzytkownik.Uzytkownik;
import szachy.uzytkownik.UzytkownikRepository;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    protected final Log logger = LogFactory.getLog(getClass());

    @Autowired
    private UzytkownikRepository uzytkownikRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUserDetailsService userDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @PostMapping("/zaloguj")
    public ResponseEntity<?> loginUser(@RequestBody HashMap<String, Object> JSON) {
        Map<String, Object> responseMap = new HashMap<>();
        String [] parametry = {"email", "haslo"};
        for(String i : parametry)
            if(JSON.get(i) == null) {
                responseMap.put("error", true);
                responseMap.put("message", "nie podano wszystkich wymaganych pol");
                return ResponseEntity.status(400).body(responseMap);
            }
        String email = JSON.get("email").toString();
        String password = JSON.get("haslo").toString();
        try {
            Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email
                    , password));
            if (auth.isAuthenticated()) {
                logger.info("Logged In");
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                String token = jwtTokenUtil.generateToken(userDetails);
                Boolean czyOrganizator = uzytkownikRepository.findUzytkownikByEmail(email).isCzyOrganizator();
                responseMap.put("error", false);
                responseMap.put("message", "Logged In");
                responseMap.put("czyOrganizator", czyOrganizator);
                responseMap.put("token", token);
                return ResponseEntity.ok(responseMap);
            }else if (uzytkownikRepository.findUzytkownikByEmail(email) != null){
                responseMap.put("error", true);
                responseMap.put("message", "Złe hasło");
                return ResponseEntity.status(401).body(responseMap);
            }
            else {
                responseMap.put("error", true);
                responseMap.put("message", "Brak aktywnego konta z adresem email:" +email);
                return ResponseEntity.status(401).body(responseMap);
            }
        } catch (BadCredentialsException e) {
            responseMap.put("error", true);
            responseMap.put("message", "Złe dane");
            return ResponseEntity.status(401).body(responseMap);
        } catch (Exception e) {
            e.printStackTrace();
            responseMap.put("error", true);
            responseMap.put("message", "Wewnętrzny błąd serwera");
            return ResponseEntity.status(500).body(responseMap);
        }
    }

    @PostMapping("/zarejestruj")
    public ResponseEntity<?> zarejestruj(@RequestBody HashMap<String, Object> JSON) {
        Map<String, Object> responseMap = new HashMap<>();
        String [] parametry = {"email", "haslo", "imie", "nazwisko", "czyOrganizator"};
        for(String i : parametry)
            if(JSON.get(i) == null) {
                responseMap.put("error", true);
                responseMap.put("message", "Nie podano wszystkich wymaganych pol");
                return ResponseEntity.status(400).body(responseMap);
            }
        String email = JSON.get("email").toString();
        String haslo = JSON.get("haslo").toString();
        String imie = JSON.get("imie").toString();
        String nazwisko = JSON.get("nazwisko").toString();
        boolean czyOrganizator = Boolean.parseBoolean(JSON.get("czyOrganizator").toString());
        int punkty;
        if (!(czyOrganizator)){
            punkty=800;
        } else {
            punkty=0;
        }

        Uzytkownik nowyUzytkownik = uzytkownikRepository.findUzytkownikByEmail(email);
        if(nowyUzytkownik != null) {
            responseMap.put("error", true);
            responseMap.put("message", "Uzytkownik o adresie email "+email+" juz istnieje");
            return ResponseEntity.status(409).body(responseMap);
        }

        nowyUzytkownik = new Uzytkownik(email, new BCryptPasswordEncoder().encode(haslo), imie, nazwisko, czyOrganizator, punkty);
        uzytkownikRepository.save(nowyUzytkownik);
        responseMap.put("error", false);
        responseMap.put("message", "Konto zostalo pomyslnie utworzone");
        return ResponseEntity.ok(responseMap);
    }
}

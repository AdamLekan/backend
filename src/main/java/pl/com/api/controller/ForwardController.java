package pl.com.api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Kontroler do obsługi przekierowań na aplikację Angular
 * Wszystkie nieobsłużone ścieżki w API będą przekierowywane do index.html Angulara
 */
@Controller
public class ForwardController {

    /**
     * Przekierowanie wszystkich żądań GET, które nie są obsługiwane przez inne kontrolery, 
     * do aplikacji Angular (index.html)
     * @return nazwa widoku (index.html)
     */
    @GetMapping(value = {"/{path:[^\\.]*}", "/{path:(?!api|static).*}/**/{path:[^\\.]*}"})
    public String forward() {
        return "forward:/index.html";
    }
}

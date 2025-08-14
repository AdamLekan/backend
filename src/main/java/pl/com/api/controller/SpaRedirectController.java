package pl.com.api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SpaRedirectController {
    // Przekierowanie na index.html dla ścieżek bez rozszerzenia, np. /dashboard, /settings
    @RequestMapping(value = { "/{path:[^\\.]*}", "/**/{path:[^\\.]*}" })
    public String forward() {
        return "forward:/";
    }
}

package py.edu.uc.lp3.minecraft.web;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {

    @GetMapping("/")
    public Map<String, String> inicio() {
        return Map.of(
                "proyecto", "MNRI Taller Git 2026",
                "dominio", "Minecraft");
    }
}

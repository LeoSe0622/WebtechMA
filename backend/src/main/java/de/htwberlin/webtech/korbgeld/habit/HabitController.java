package de.htwberlin.webtech.korbgeld.habit;

import de.htwberlin.webtech.korbgeld.common.CurrentUser;
import de.htwberlin.webtech.korbgeld.habit.HabitService.HabitResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Gewohnheiten (UC3), vorerst nur lesend (Demo). Anlegen und Ändern kommen zu M4 (AUFTRAG.md, Abschnitt 18). */
@RestController
@RequestMapping("/api/habits")
public class HabitController {

    private final HabitService habitService;

    public HabitController(HabitService habitService) {
        this.habitService = habitService;
    }

    @GetMapping
    public List<HabitResponse> getAll(@AuthenticationPrincipal Jwt jwt) {
        return habitService.findAll(CurrentUser.id(jwt));
    }
}

package de.htwberlin.webtech.korbgeld.leaderboard;

import de.htwberlin.webtech.korbgeld.common.CurrentUser;
import de.htwberlin.webtech.korbgeld.leaderboard.LeaderboardService.LeaderboardResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Rangliste (UC6), vorerst nur lesend (Demo). Opt-in über das Profil kommt zu M4. */
@RestController
@RequestMapping("/api/leaderboard")
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    public LeaderboardController(LeaderboardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }

    @GetMapping
    public LeaderboardResponse get(@AuthenticationPrincipal Jwt jwt) {
        return leaderboardService.leaderboard(CurrentUser.id(jwt));
    }
}

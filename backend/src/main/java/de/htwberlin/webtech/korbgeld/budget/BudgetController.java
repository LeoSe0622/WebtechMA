package de.htwberlin.webtech.korbgeld.budget;

import de.htwberlin.webtech.korbgeld.budget.BudgetDtos.BudgetRequest;
import de.htwberlin.webtech.korbgeld.budget.BudgetDtos.BudgetResponse;
import de.htwberlin.webtech.korbgeld.budget.BudgetDtos.BudgetSummaryResponse;
import de.htwberlin.webtech.korbgeld.common.CurrentUser;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.YearMonth;

@RestController
@RequestMapping("/api/budgets")
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @GetMapping("/current/summary")
    public BudgetSummaryResponse currentSummary(@AuthenticationPrincipal Jwt jwt) {
        return budgetService.currentSummary(CurrentUser.id(jwt));
    }

    // {yearMonth} im Format 2026-10; Spring wandelt den Text automatisch in ein YearMonth um
    @GetMapping("/{yearMonth}")
    public BudgetResponse get(@AuthenticationPrincipal Jwt jwt, @PathVariable YearMonth yearMonth) {
        return budgetService.get(CurrentUser.id(jwt), yearMonth);
    }

    @PutMapping("/{yearMonth}")
    public BudgetResponse set(@AuthenticationPrincipal Jwt jwt, @PathVariable YearMonth yearMonth,
                              @Valid @RequestBody BudgetRequest request) {
        return budgetService.set(CurrentUser.id(jwt), yearMonth, request.amount());
    }
}

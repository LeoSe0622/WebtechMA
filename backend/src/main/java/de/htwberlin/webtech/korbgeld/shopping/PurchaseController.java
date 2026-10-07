package de.htwberlin.webtech.korbgeld.shopping;

import de.htwberlin.webtech.korbgeld.common.CurrentUser;
import de.htwberlin.webtech.korbgeld.shopping.PurchaseDtos.CompletePurchaseRequest;
import de.htwberlin.webtech.korbgeld.shopping.PurchaseDtos.CompletePurchaseResponse;
import de.htwberlin.webtech.korbgeld.shopping.PurchaseDtos.PurchaseResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/purchases")
public class PurchaseController {

    private final PurchaseService purchaseService;

    public PurchaseController(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompletePurchaseResponse complete(@AuthenticationPrincipal Jwt jwt,
                                             @Valid @RequestBody CompletePurchaseRequest request) {
        return purchaseService.complete(CurrentUser.id(jwt), request);
    }

    // GET /api/purchases?month=2026-10
    @GetMapping
    public List<PurchaseResponse> byMonth(@AuthenticationPrincipal Jwt jwt, @RequestParam YearMonth month) {
        return purchaseService.findByMonth(CurrentUser.id(jwt), month);
    }
}

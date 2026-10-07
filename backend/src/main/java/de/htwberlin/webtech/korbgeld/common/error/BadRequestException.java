package de.htwberlin.webtech.korbgeld.common.error;

/** 400: Die Anfrage ist formal gültig, aber fachlich unvollständig (z. B. kein Artikel abgehakt). */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}

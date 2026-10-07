package de.htwberlin.webtech.korbgeld.common.error;

/** 409: Die Anfrage widerspricht einer Fachregel (z. B. Budget gesperrt, Obergrenze, Name vergeben). */
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}

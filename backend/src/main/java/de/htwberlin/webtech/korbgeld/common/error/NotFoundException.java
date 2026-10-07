package de.htwberlin.webtech.korbgeld.common.error;

/** 404: Ein angefragter Datensatz existiert nicht (oder gehört einem anderen Nutzer). */
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}

package de.htwberlin.webtech.korbgeld.common.error;

/** 401: Benutzername oder Passwort stimmen nicht. */
public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException() {
        super("Benutzername oder Passwort stimmen nicht.");
    }
}

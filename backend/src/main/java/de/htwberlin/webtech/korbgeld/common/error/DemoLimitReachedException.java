package de.htwberlin.webtech.korbgeld.common.error;

/** 429: Heute wurden schon zu viele Demo-Nutzer angelegt. */
public class DemoLimitReachedException extends RuntimeException {

    public DemoLimitReachedException() {
        super("Heute sind keine weiteren Demo-Zugänge möglich. Versuch es morgen wieder.");
    }
}

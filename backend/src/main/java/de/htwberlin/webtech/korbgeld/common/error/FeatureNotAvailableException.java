package de.htwberlin.webtech.korbgeld.common.error;

/** 501: Funktion ist noch in Arbeit. */
public class FeatureNotAvailableException extends RuntimeException {

    private final String feature;
    private final String milestone;

    public FeatureNotAvailableException(String feature, String milestone) {
        super(feature + " ist noch in Arbeit.");
        this.feature = feature;
        this.milestone = milestone;
    }

    public String getFeature() {
        return feature;
    }

    public String getMilestone() {
        return milestone;
    }
}

package it.disco.unimib.suggester.model.translation;

public interface ILookedupTermTarget {
    String getTarget();

    Double getConfidence();

    String getType();
}

package it.disco.unimib.suggester.translator.domain;

public interface ILookedupTermTarget {
    String getTarget();

    Double getConfidence();

    String getType();
}

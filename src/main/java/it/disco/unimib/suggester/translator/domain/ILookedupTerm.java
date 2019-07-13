package it.disco.unimib.suggester.translator.domain;

import java.util.List;

public interface ILookedupTerm {
    String getSource();

    List<? extends ILookedupTermTarget> getTranslations();
}


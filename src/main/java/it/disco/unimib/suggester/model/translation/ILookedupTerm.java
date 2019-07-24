package it.disco.unimib.suggester.model.translation;

import java.util.List;

public interface ILookedupTerm {
    String getSource();

    List<? extends ILookedupTermTarget> getTranslations();
}


package fr.moodcraft.business.gui;

import fr.moodcraft.business.util.BusinessMessages;

public final class GuiTitle {

    private GuiTitle() {}

    public static String of(String title) {
        return BusinessMessages.guiTitle(title);
    }
}

package org.dreambot.walker.dax.engine.definitions;


import org.dreambot.api.methods.MethodProvider;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.wrappers.widgets.WidgetChild;

public enum PopUpInterfaces {
    STRONGHOLD_PROMPT(() -> Widgets.getWidgetChild(579, 17)),
    WILDERNESS_PROMPT(() -> Widgets.getWidgetChild(475, 11));

    private RSPopUp rsPopUp;

    PopUpInterfaces(final RSPopUp rsPopUp) {
        this.rsPopUp = rsPopUp;
    }

    public boolean handle() {
        WidgetChild widgetChild = this.rsPopUp.get();
        if (widgetChild == null) {
            return false;
        }
        if (!widgetChild.interact()) {
            return false;
        }
        MethodProvider.sleep(450, 1000);
        return true;
    }

    public boolean isVisible() {
        WidgetChild widgetChild = this.rsPopUp.get();
        return widgetChild != null && widgetChild.isVisible();
    }

    public static boolean resolve() {
        for (PopUpInterfaces popUpInterfaces : values()) {
            if (popUpInterfaces.isVisible()) {
                return popUpInterfaces.handle();
            }
        }
        return false;
    }
}

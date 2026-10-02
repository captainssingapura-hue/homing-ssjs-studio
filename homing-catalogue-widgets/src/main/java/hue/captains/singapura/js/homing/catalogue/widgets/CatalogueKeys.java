package hue.captains.singapura.js.homing.catalogue.widgets;

import hue.captains.singapura.js.homing.component.keyboard.Key;
import hue.captains.singapura.js.homing.component.keyboard.KeyBinding;

/** The keys every catalogue widget has from CatalogueWidget, declared once for their NeedKeyboard. */
public final class CatalogueKeys {

    private CatalogueKeys() {}

    /** Holding the keys with nothing of its own taking Escape, it gives them back. */
    public static final KeyBinding GIVE_BACK = KeyBinding.of(Key.ESCAPE, "the keys given back");
}

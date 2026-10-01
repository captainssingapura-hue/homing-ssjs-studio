package hue.captains.singapura.js.homing.conformance.workbench;

import hue.captains.singapura.js.homing.component.keyboard.Key;
import hue.captains.singapura.js.homing.component.keyboard.KeyBinding;

/** The keys every workbench widget has from WorkbenchWidget, declared once for their NeedKeyboard. */
public final class WorkbenchKeys {

    private WorkbenchKeys() {}

    /** Holding the keys with nothing of its own taking Escape, it gives them back. */
    public static final KeyBinding GIVE_BACK = KeyBinding.of(Key.ESCAPE, "the keys given back");
}

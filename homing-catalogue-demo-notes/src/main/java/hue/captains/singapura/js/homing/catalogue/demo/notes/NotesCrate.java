package hue.captains.singapura.js.homing.catalogue.demo.notes;

import hue.captains.singapura.js.homing.core.Crate;
import hue.captains.singapura.js.homing.core.CrateEntry;
import hue.captains.singapura.js.homing.core.StandardJsModuleType;
import hue.captains.singapura.js.homing.core.js.CoreJsCrate;
import hue.captains.singapura.js.homing.design.DesignCrate;
import hue.captains.singapura.js.homing.server.ServerCrate;

import java.util.List;

/** The notes app: its page and its sheet. Its tree is Java, and served as the pages it places. */
public final class NotesCrate implements Crate {

    public static final NotesCrate INSTANCE = new NotesCrate();

    private NotesCrate() {}

    @Override public String name() { return "homing-catalogue-demo-notes"; }

    @Override public List<Crate> requires() { return List.of(CoreJsCrate.INSTANCE, ServerCrate.INSTANCE, DesignCrate.INSTANCE); }

    @Override
    public List<CrateEntry> entries() {
        return List.of(CrateEntry.of(NoteStyles.INSTANCE), CrateEntry.of(NoteApp.INSTANCE, StandardJsModuleType.CONSUMER));
    }
}

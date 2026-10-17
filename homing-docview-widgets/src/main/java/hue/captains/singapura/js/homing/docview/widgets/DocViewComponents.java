package hue.captains.singapura.js.homing.docview.widgets;

import hue.captains.singapura.js.homing.component.taxonomy.Component;
import hue.captains.singapura.js.homing.component.taxonomy.StateType;
import hue.captains.singapura.js.homing.ui.taxonomy.HouseBranches;
import hue.captains.singapura.js.homing.ui.taxonomy.HouseStates;

import java.util.List;

/**
 * What a document's widgets draw that the house's components do not (RFC 0067, Two Classes per Leaf, D17): a component
 * of its own for each, in the house's tree wherever an instance registers the widgets' module
 * ({@link DocViewComponentModule}), its look in Base with it ({@link DocViewBaseModule}).
 */
public final class DocViewComponents {

    private DocViewComponents() {}

    /** An entry of a document's references: one thing the document cites, shown while a citation has asked for it. */
    public record DocReference() implements Component<HouseBranches.Item> {
        public static final DocReference INSTANCE = new DocReference();
        @Override public HouseBranches.Item parent() { return HouseBranches.Item.INSTANCE; }
        /** Whether a citation has asked for it - the references' to say. */
        @Override public List<StateType<?>> states() { return List.of(HouseStates.Current.INSTANCE); }
    }
}

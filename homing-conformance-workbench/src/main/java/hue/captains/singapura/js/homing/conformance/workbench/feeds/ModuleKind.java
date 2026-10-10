package hue.captains.singapura.js.homing.conformance.workbench.feeds;


/**
 * What a module is, as the conformance studio files it for a reader: a widget; a component, simple or
 * composed; other UI - a DOM module that exports no component: a view, an app, a helper its widgets
 * run; non-UI - a module that touches no DOM: logic, data, a codec, a secretary; styles - a CSS or an
 * SVG group; or infrastructure - a manager, a bundled external. Read off the module itself, as its
 * conformance type is: by what it is and what it exports ({@link ModuleKinds}).
 */
public enum ModuleKind {

    WIDGET("widget", "Widget"),
    SIMPLE_COMPONENT("component-simple", "Simple component"),
    COMPOSED_COMPONENT("component-composed", "Composed component"),
    UI("ui", "Other UI"),
    NON_UI("non-ui", "Non-UI"),
    STYLES("styles", "Styles"),
    INFRASTRUCTURE("infrastructure", "Infrastructure");

    private final String slug;
    private final String label;

    ModuleKind(String slug, String label) {
        this.slug = slug;
        this.label = label;
    }

    /** Its name on the wire: {@code component-simple}. */
    public String slug() { return slug; }

    /** Its name to a reader: {@code Simple component}. */
    public String label() { return label; }
}

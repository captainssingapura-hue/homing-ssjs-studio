package hue.captains.singapura.js.homing.conformance.workbench.feeds;

import hue.captains.singapura.js.homing.component.UiComponent;
import hue.captains.singapura.js.homing.core.BundledExternalModule;
import hue.captains.singapura.js.homing.core.CrateEntry;
import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.EsModule;
import hue.captains.singapura.js.homing.core.ExternalModule;
import hue.captains.singapura.js.homing.core.ManagerInjector;
import hue.captains.singapura.js.homing.core.ModuleForm;
import hue.captains.singapura.js.homing.core.StandardJsModuleType;

import static hue.captains.singapura.js.homing.conformance.workbench.feeds.ModuleKind.*;

/** Reads a crate's module's {@link ModuleKind} off the module itself. */
public record ModuleKinds() {

    public static final ModuleKinds INSTANCE = new ModuleKinds();

    /**
     * The kind of a crate's module: infrastructure, a manager or an external; styles, a CSS or an SVG
     * group; a widget or a component, by the shape of the component it exports; non-UI, a module
     * declared headless or touching no DOM; other UI, any other DOM module.
     */
    public ModuleKind of(CrateEntry entry) {
        EsModule<?> m = entry.module();
        if (m instanceof BundledExternalModule<?> || m instanceof ExternalModule<?> || m instanceof ManagerInjector) return INFRASTRUCTURE;
        if (entry.form() == ModuleForm.CSS_GROUP || entry.form() == ModuleForm.SVG_GROUP) return STYLES;
        for (var x : m.exports().exports())
            if (x instanceof UiComponent<?> c)
                return switch (c.shape()) {
                    case WIDGET -> WIDGET;
                    case ELEMENT -> SIMPLE_COMPONENT;
                    case BRANCH -> COMPOSED_COMPONENT;
                };
        if (entry.declaredType() == StandardJsModuleType.SECRETARY || entry.declaredType() == StandardJsModuleType.PURE_LOGIC) return NON_UI;
        return m instanceof DomModule<?> ? UI : NON_UI;
    }
}

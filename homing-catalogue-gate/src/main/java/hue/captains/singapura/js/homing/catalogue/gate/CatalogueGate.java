package hue.captains.singapura.js.homing.catalogue.gate;

import hue.captains.singapura.js.homing.component.keyboard.KeyboardRegistry;
import hue.captains.singapura.js.homing.conformance.engine.ConformanceEngine;
import hue.captains.singapura.js.homing.conformance.engine.ServedModuleRenderer;
import hue.captains.singapura.js.homing.conformance.rules.CrateClosure;
import hue.captains.singapura.js.homing.conformance.rules.CrateConformance;
import hue.captains.singapura.js.homing.conformance.rules.CssConformance;
import hue.captains.singapura.js.homing.conformance.rules.DefaultJsRulePolicy;
import hue.captains.singapura.js.homing.conformance.rules.Finding;
import hue.captains.singapura.js.homing.conformance.rules.FindingGrader;
import hue.captains.singapura.js.homing.conformance.rules.GradedFinding;
import hue.captains.singapura.js.homing.core.Crate;
import hue.captains.singapura.js.homing.core.CrateEntry;
import hue.captains.singapura.js.homing.core.CssGroup;
import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.design.Deployment;
import hue.captains.singapura.js.homing.design.Design;
import hue.captains.singapura.js.homing.designs.HomingDesigns;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * The studio's gate on the site stack, one for every module of this repo: the
 * framework's conformance, held as the workspace repo's widget sets hold
 * theirs - crate integrity; every served module graded strictly, with no
 * baseline; the CSS graph laws; every design binding what the sheets wear; the
 * keys, through the party; every name imported that is used; no plain module
 * importing a DOM module; and the studio's old stack nowhere on the classpath.
 * A module's gate test calls each against its own crate.
 */
public final class CatalogueGate {

    private CatalogueGate() {}

    /** Every served module declared; every cross-crate import in requires(). */
    public static void structurallyComplete(Crate crate) {
        var result = CrateConformance.evaluate(new ArrayList<>(CrateClosure.of(List.of(crate)))).crates().get(crate.name());
        assertEquals(List.of(), result.orphans(), "every served module must be declared");
        assertEquals(List.of(), result.illegalImports(), "every cross-crate import must be declared in requires()");
    }

    /** Every served module of the crate's closure, graded strictly: the desk carries no debt here, and there is no ledger. */
    public static void strict(Crate crate) {
        List<Finding> raw = new ConformanceEngine(DefaultJsRulePolicy.INSTANCE, new ServedModuleRenderer()).checkCrates(List.of(crate));
        List<GradedFinding> errors = FindingGrader.STRICT.grade(raw).stream().filter(GradedFinding::isError).toList();
        assertEquals(List.of(), errors.stream().map(g -> describe(g.finding())).toList(),
                "the studio on the site stack carries no debt - fix these, there is no ledger to file them in");
    }

    /** The CSS graph laws, for the crate's own modules. */
    public static void cssLaws(Crate crate) {
        Set<String> own = own(crate);
        List<Finding> css = CssConformance.check(new ArrayList<>(CrateClosure.of(List.of(crate))), HomingDesigns.REGISTRY.palettes())
                .stream().filter(f -> own.contains(f.moduleClass())).toList();
        assertEquals(List.of(), css.stream().map(CatalogueGate::describe).toList());
    }

    /** Every design binds every pair the crate's sheets wear. */
    public static void designsBind(Crate crate) {
        var groups = new ArrayList<CssGroup<?>>();
        for (var e : crate.entries()) if (e.module() instanceof CssGroup<?> g) groups.add(g);
        if (groups.isEmpty()) return;
        var worn = Deployment.wornBy(groups);
        for (var t : HomingDesigns.REGISTRY.themes()) {
            var r = Deployment.of(worn, Deployment.scaledBy(groups), Deployment.grownBy(groups), (Design) t).resolve();
            assertEquals(List.of(), r.findings(), () -> ((Design) t).slug() + ": " + r.findings());
        }
    }

    /** The crate's modules take their keys through the party: none listens undeclared, none captures on the document. */
    public static void keysThroughTheParty(Crate crate) {
        Set<String> ownNames = crate.entries().stream().map(e -> e.module().getClass().getSimpleName()).collect(Collectors.toSet());
        assertEquals(List.of(), KeyboardRegistry.undeclaredListeners(List.of(crate)).stream().filter(ownNames::contains).toList(),
                "a module that listens for keys declares them (NeedKeyboard)");
        assertEquals(List.of(), KeyboardRegistry.validate(List.of(crate)).stream().filter(s -> ownNames.stream().anyMatch(s::contains)).toList(),
                "only the steward captures keys on the document");
    }

    /**
     * Every name a desk module takes from another module is one it imports. The JavaScript
     * tests load modules into one scope, where a class is there whether it was imported or
     * not; a browser loads each module with only what it declares.
     */
    public static void everyNameTakenIsImported(Crate crate) throws IOException {
        var closure = new ArrayList<>(CrateClosure.of(List.of(crate)));
        Map<String, String> exporter = new HashMap<>();
        for (Crate c : closure) for (CrateEntry e : c.entries()) {
            for (var x : e.module().exports().exports()) exporter.putIfAbsent(x.getClass().getSimpleName(), e.moduleClass());
        }
        var problems = new ArrayList<String>();
        for (Crate c : closure) {
            if (!ours(c.name())) continue;
            for (CrateEntry e : c.entries()) {
                String script = script(e.moduleClass());
                if (script == null) continue;
                Set<String> own = new HashSet<>(), imported = new HashSet<>(), local = new HashSet<>();
                for (var x : e.module().exports().exports()) own.add(x.getClass().getSimpleName());
                for (var mi : e.module().imports().getAllImports().values()) for (var x : mi.allImports()) imported.add(x.getClass().getSimpleName());
                String code = strip(script);
                Matcher d = DECLARED.matcher(code);
                while (d.find()) local.add(d.group(1));
                Set<String> missing = new TreeSet<>();
                Matcher m = NAME.matcher(code);
                while (m.find()) {
                    String n = m.group(1), from = exporter.get(n);
                    if (from != null && !from.equals(e.moduleClass()) && !own.contains(n) && !imported.contains(n) && !local.contains(n)) missing.add(n);
                }
                if (!missing.isEmpty()) problems.add(e.moduleClass() + " uses " + missing + " without importing them");
            }
        }
        assertEquals(List.of(), problems);
    }

    /**
     * A plain module never imports a DOM module: a DOM module is served with the page's theme,
     * and a plain module's imports without it, so it would load twice - its classes not the
     * page's (the Keyboard's §15 hazard).
     */
    public static void noPlainModuleImportsADomModule(Crate crate) {
        var problems = new ArrayList<String>();
        for (Crate c : CrateClosure.of(List.of(crate))) {
            if (!ours(c.name())) continue;
            for (CrateEntry e : c.entries()) {
                if (e.module() instanceof DomModule<?>) continue;
                for (var mi : e.module().imports().getAllImports().values()) {
                    if (mi.from() instanceof DomModule<?> d) problems.add(e.moduleClass() + " is plain, and imports the DOM module " + d.getClass().getName());
                }
            }
        }
        assertEquals(List.of(), problems);
    }

    /**
     * Classes only the studio's old stack has - its catalogue, its docs' wire to the old
     * viewers, its plan host, its bootstrap, its workspace: none is on the classpath, and no crate
     * of it is in the closure. The docs and plans themselves are not among them: they are data, in core's
     * pure {@code homing-doc-model} and {@code homing-plan-model}, which keep the old package names for compatibility - a
     * doc's or a plan's class name says nothing of the stack.
     */
    public static void noOldStudio(Crate crate) {
        for (String name : List.of(
                "hue.captains.singapura.js.homing.studio.base.LegacyDocWire",
                "hue.captains.singapura.js.homing.studio.base.app.Catalogue",
                "hue.captains.singapura.js.homing.studio.base.tracker.PlanAppHost",
                "hue.captains.singapura.js.homing.studio.base.Bootstrap",
                "hue.captains.singapura.js.homing.studio.starter.StudioStarterFixtures",
                "hue.captains.singapura.js.homing.studio.workspace.StudioWorkspaceCrate",
                "hue.captains.singapura.js.homing.workspace.WorkspaceWidget")) {
            assertThrows(ClassNotFoundException.class, () -> Class.forName(name, false, crate.getClass().getClassLoader()), name);
        }
        List<String> old = CrateClosure.of(List.of(crate)).stream().map(Crate::name)
                .filter(n -> n.equals("homing-studio-base") || n.equals("homing-studio-starter") || n.equals("homing-studio-workspace")
                          || n.equals("homing-workspace")).toList();
        assertEquals(List.of(), old);
    }

    private static Set<String> own(Crate crate) {
        return crate.entries().stream().map(CrateEntry::moduleClass).collect(Collectors.toSet());
    }

    private static String describe(Finding f) {
        return f.moduleClass() + " [" + f.rule().value() + "] @" + f.line() + " " + f.message();
    }

    /** A capitalised name read as a value, not as a member: not after a dot. */
    private static final Pattern NAME = Pattern.compile("(?<![\\w$.])([A-Z][A-Za-z0-9_$]*)\\b");
    private static final Pattern DECLARED = Pattern.compile("\\b(?:class|function|const|let|var)\\s+([A-Za-z_$][\\w$]*)");

    private static String strip(String js) {
        return js.replaceAll("(?s)/\\*.*?\\*/", " ")
                 .replaceAll("(?m)//.*$", " ")
                 .replaceAll("\"(?:[^\"\\\\\\n]|\\\\.)*\"", "\"\"")
                 .replaceAll("'(?:[^'\\\\\\n]|\\\\.)*'", "''")
                 .replaceAll("(?s)`(?:[^`\\\\]|\\\\.)*`", "``");
    }

    private static String script(String moduleClass) throws IOException {
        try (InputStream in = CatalogueGate.class.getResourceAsStream("/homing/js/" + moduleClass.replace('.', '/') + ".js")) {
            return in == null ? null : new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    /** A crate of this repo's: the catalogue's, or DocView's. */
    private static boolean ours(String crate) { return crate.startsWith("homing-catalogue") || crate.startsWith("homing-docview"); }
}

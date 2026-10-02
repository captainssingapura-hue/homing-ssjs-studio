// =============================================================================
// WorkbenchFeeds — where the workbench's widgets read from: the feeds the
// studio's server answers at (WORKBENCH_ROUTES, the Java feeds' own routes),
// each read once a page and kept, so the widgets that read one share it. A
// read that fails is not kept, so it may be asked again.
//
//   WorkbenchFeeds.json(name, path?)  → Promise of the feed's JSON; path its one parameter
//   WorkbenchFeeds.text(name, path?)  → Promise of the feed's text
//   WorkbenchFeeds.crateNodes()       → Promise of a Map: a crate node by its key - the
//                                       crates, their packages, their modules - each with
//                                       the key of what holds it (up), the root "crates"
//   WorkbenchFeeds.source(fqcn)       → Promise of a module's source, as it is served
// =============================================================================

var _workbenchKept = new Map();

class WorkbenchFeeds {

    static json(name, path) { return WorkbenchFeeds._read(name, path, "json"); }

    static text(name, path) { return WorkbenchFeeds._read(name, path, "text"); }

    static crateNodes() {
        return WorkbenchFeeds.json("crates").then(function (tree) {
            var nodes = new Map();
            var root = { key: "crates", label: "Crates", kind: "crates", crates: tree.crates, modules: tree.modules, children: tree.children, up: null };
            nodes.set(root.key, root);
            (function walk(children, up) {
                children.forEach(function (n) {
                    nodes.set(n.key, Object.assign({}, n, { up: up }));
                    walk(n.children || [], n.key);
                });
            })(tree.children, root.key);
            return nodes;
        });
    }

    static source(fqcn) {
        return WorkbenchFeeds._fetch(WORKBENCH_ROUTES.module + "?class=" + encodeURIComponent(fqcn), "text");
    }

    static _read(name, path, as) {
        var route = WORKBENCH_ROUTES[name];
        if (!route) return Promise.reject(new Error("no feed named " + name));
        return WorkbenchFeeds._fetch(path ? route + "?path=" + encodeURIComponent(path) : route, as);
    }

    static _fetch(url, as) {
        var key = as + " " + url;
        if (_workbenchKept.has(key)) return _workbenchKept.get(key);
        var read = fetch(url).then(function (r) {
            if (!r.ok) throw new Error("HTTP " + r.status + " for " + url);
            return as === "json" ? r.json() : r.text();
        });
        _workbenchKept.set(key, read);
        read.catch(function () { _workbenchKept.delete(key); });
        return read;
    }
}

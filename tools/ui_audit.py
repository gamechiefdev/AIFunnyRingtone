#!/usr/bin/env python3
"""Static auditor for the AIFunnyRingtone Android project.

Checks (no Android SDK needed):
 1. every layout XML is well-formed
 2. every @drawable/@color/@dimen/@string/@font/@style/@layout/@id reference resolves
 3. every viewBinding field used in Java exists as an id in the bound layout
 4. every findViewById(R.id.x) used in Java exists in some layout
 5. duplicate ids inside a single layout
"""
import os
import re
import sys
import glob
import xml.etree.ElementTree as ET
from collections import defaultdict

ROOT = os.path.dirname(os.path.abspath(__file__))
REPO = sys.argv[1] if len(sys.argv) > 1 else "."
RES = os.path.join(REPO, "app/src/main/res")
JAVA = os.path.join(REPO, "app/src/main/java")
ANDROID = {"android", "app", "tools", "aapt"}

problems = []


def snake(name):
    s = re.sub(r"(?<!^)(?=[A-Z])", "_", name).lower()
    return s


# ---------- collect declared resources ----------
declared = defaultdict(set)

for d in os.listdir(RES):
    p = os.path.join(RES, d)
    if not os.path.isdir(p):
        continue
    kind = d.split("-")[0]
    for f in os.listdir(p):
        base = f.split(".")[0]
        declared[kind].add(base)
        if kind == "values" and f.endswith(".xml"):
            try:
                root = ET.parse(os.path.join(p, f)).getroot()
            except Exception as e:
                problems.append(f"XML PARSE FAIL values/{f}: {e}")
                continue
            for child in root:
                if not isinstance(child.tag, str):
                    continue
                n = child.get("name")
                t = child.tag
                if t == "item":
                    t = child.get("type") or "item"
                if n:
                    declared[t].add(n)

# ---------- layouts: ids + refs ----------
layout_ids = {}
all_ids = set()
ref_re = re.compile(r"@(?!\+)([a-zA-Z0-9_]+)/([a-zA-Z0-9_.]+)")
for f in sorted(glob.glob(os.path.join(RES, "layout*", "*.xml"))):
    rel = os.path.relpath(f, RES)
    try:
        tree = ET.parse(f)
    except Exception as e:
        problems.append(f"XML PARSE FAIL {rel}: {e}")
        continue
    ids = []
    for el in tree.iter():
        if not isinstance(el.tag, str):
            continue
        vid = el.get("{http://schemas.android.com/apk/res/android}id")
        if vid:
            if vid.startswith("@+id/"):
                ids.append(vid[5:])
            elif vid.startswith("@id/"):
                ids.append(vid[4:])
        for k, v in el.attrib.items():
            if not isinstance(v, str):
                continue
            for m in ref_re.finditer(v):
                kind, name = m.group(1), m.group(2)
                if kind in ANDROID:
                    continue
                if kind == "id":
                    continue
                if name.startswith("@") or name.startswith("?"):
                    continue
                if kind == "dimen" and re.fullmatch(r"_\d+(?:sdp|ssp)", name):
                    continue  # provided by sdp/ssp library
                if name not in declared.get(kind, set()):
                    problems.append(f"UNRESOLVED {rel}: @{kind}/{name}")
    dup = [i for i in set(ids) if ids.count(i) > 1]
    for d in dup:
        problems.append(f"DUPLICATE ID {rel}: {d}")
    layout_ids[rel] = set(ids)
    all_ids.update(ids)

# values files may reference other resources too
for f in sorted(glob.glob(os.path.join(RES, "values*", "*.xml"))):
    rel = os.path.relpath(f, RES)
    try:
        txt = re.sub(r"<!--.*?-->", "", open(f, encoding="utf-8").read(), flags=re.S)
    except Exception:
        continue
    for m in ref_re.finditer(txt):
        kind, name = m.group(1), m.group(2)
        if kind in ANDROID or kind in ("id", "null"):
            continue
        if kind == "dimen" and re.fullmatch(r"_\d+(?:sdp|ssp)", name):
            continue
        if name not in declared.get(kind, set()):
            problems.append(f"UNRESOLVED {rel}: @{kind}/{name}")

# ---------- java: bindings + findViewById ----------
binding_re = re.compile(r"([A-Za-z0-9]+Binding)\s*(?:=|;)?")
for f in glob.glob(os.path.join(JAVA, "**", "*.java"), recursive=True):
    rel = os.path.relpath(f, REPO)
    txt = re.sub(r"/\*.*?\*/", "", open(f, encoding="utf-8", errors="replace").read(), flags=re.S)
    txt = re.sub(r"//[^\n]*", "", txt)
    # map variable name -> layout, e.g. this.binding = ActivityMainBinding.inflate(...)
    var_layout = {}
    for m in re.finditer(r"([A-Za-z0-9_]+)\s*=\s*([A-Za-z0-9]+)Binding\s*\.(?:inflate|bind)", txt):
        var_layout.setdefault(m.group(1), set()).add("layout/%s.xml" % snake(m.group(2)))
    for m in re.finditer(r"([A-Za-z0-9]+)Binding\s+([A-Za-z0-9_]+)\s*[;=]", txt):
        var_layout.setdefault(m.group(2), set()).add("layout/%s.xml" % snake(m.group(1)))

    for var, layouts in var_layout.items():
        known = {l: layout_ids[l] for l in layouts if l in layout_ids}
        if not known:
            for l in layouts:
                problems.append(f"MISSING LAYOUT {rel}: {l}")
            continue
        fields = set()
        for ids in known.values():
            fields |= {re.sub(r"_([a-z0-9])", lambda x: x.group(1).upper(), i) for i in ids}
        for bm in re.finditer(r"\b" + re.escape(var) + r"\.([a-zA-Z0-9_]+)", txt):
            field = bm.group(1)
            if field in ("getRoot", "get", "set", "inflate", "bind"):
                continue
            if field not in fields:
                problems.append(f"BINDING FIELD MISSING {rel}: {var}.{field} (not an id in {sorted(known)})")

    for rm in re.finditer(r"findViewById\(R\.id\.([a-zA-Z0-9_]+)\)", txt):
        if rm.group(1) not in all_ids:
            problems.append(f"FINDVIEWBYID MISSING {rel}: R.id.{rm.group(1)}")

print("\n".join(sorted(set(problems))) or "OK: no static UI reference problems found")
print(f"\n{len(set(problems))} problem(s)")

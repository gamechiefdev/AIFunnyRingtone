"""Mechanical 'funny' pass: cartoon titles + comic outlines + wobble tags on every screen layout.
UI-only. Skips ad templates and the do-not-touch list. Idempotent-ish (skips attrs that exist)."""
import re, glob, os, sys
import xml.etree.ElementTree as ET

STRINGS = {}
for _el in ET.parse("app/src/main/res/values/strings.xml").getroot():
    if _el.tag == "string" and _el.get("name"):
        STRINGS[_el.get("name")] = "".join(_el.itertext())

TXT = re.compile(r'android:text="([^"]*)"')


def label_short(attrs):
    """only short, label-like text gets the cartoon face (keeps body copy readable)"""
    m = TXT.search(attrs)
    if not m:
        return False
    t = m.group(1)
    if t.startswith("@string/"):
        v = STRINGS.get(t.split("/")[-1], "")
        return 0 < len(v) <= 24
    return 0 < len(t) <= 24

SKIP = {"ad_native_layout_banner.xml", "ad_native_layout_medium_new.xml", "ad_native_layout_small.xml",
        "layout_intro_native_ad.xml", "item_youtube_video.xml", "wall_itemlayout.xml",
        "headerfile.xml", "bg_layout.xml", "horizontal_rv_sample.xml",
        "fragment_home.xml", "activity_splash.xml"}
BIG = re.compile(r'android:textSize="(?:(\d+(?:\.\d+)?)sp|@dimen/_(\d+)sdp)"')

def size_of(attrs):
    m = BIG.search(attrs)
    if not m:
        return 0
    if m.group(1):
        return float(m.group(1))
    return int(m.group(2)) * 0.45   # sdp ~ 0.45 * sp at 1x-ish scale, only used for a threshold

changed = {}
for path in sorted(glob.glob("app/src/main/res/layout/*.xml")):
    name = os.path.basename(path)
    if name in SKIP:
        continue
    src = open(path).read()
    # mask comments so regex edits never land inside them
    comments = []

    def _mask(m):
        comments.append(m.group(0))
        return f"@@COM{len(comments) - 1}@@"

    out = re.sub(r"<!--.*?-->", _mask, src, flags=re.S)
    n = 0

    # 1) cartoon display face on header-sized TextViews that have no font yet
    def title(m):
        global n
        attrs = m.group(1)
        if "fontFamily" in attrs or size_of(attrs) < 15 or not label_short(attrs):
            return m.group(0)
        n += 1
        return '<TextView android:fontFamily="@font/chewy"' + attrs + m.group(2)

    out = re.sub(r'<TextView(.*?)(/?>)', title, out, flags=re.S)

    # 2) comic ink outline on card views that have none
    def card(m):
        global n
        attrs = m.group(2)
        if "strokeWidth" in attrs:
            return m.group(0)
        n += 1
        return ("<" + m.group(1) + attrs.rstrip() +
                '\n            app:strokeWidth="2dp"\n            app:strokeColor="@color/pin_ink"'
                + m.group(3))

    out = re.sub(r'<((?:com\.google\.android\.material\.card\.)?MaterialCardView)(.*?)(/?>)', card, out, flags=re.S)
    for i, c in enumerate(comments):
        out = out.replace(f"@@COM{i}@@", c)
    if out != src:
        open(path, "w").write(out)
        changed[name] = n

print("layouts touched:", len(changed))
for k, v in changed.items():
    print(f"  {k}: {v} edits")

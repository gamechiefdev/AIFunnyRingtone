#!/usr/bin/env python3
"""Value-only restyle of the layouts that were still on the old styling.

Only attribute VALUES are rewritten (never structure, never ids), so every
binding / findViewById keeps working. Run from the repo root:

    python3 tools/restyle_remaining.py
"""
import os
import re
import sys

LAYOUT_DIR = "app/src/main/res/layout"

# layouts that must stay untouched (ad SDK templates, raw video surface)
SKIP = {
    "ad_native_layout_banner.xml", "ad_native_layout_medium_new.xml",
    "ad_native_layout_small.xml", "layout_intro_native_ad.xml",
    "item_youtube_video.xml",
}

VALUE_MAP = {
    # text
    'android:textColor="@color/black"': 'android:textColor="@color/pin_ink"',
    'android:textColor="@color/purple_200"': 'android:textColor="@color/pin_primary"',
    'android:textColor="@color/purple_500"': 'android:textColor="@color/pin_primary"',
    'android:textColor="#9A000000"': 'android:textColor="@color/pin_ink_soft"',
    'android:textColor="#8A000000"': 'android:textColor="@color/pin_ink_soft"',
    'android:textColor="#99000000"': 'android:textColor="@color/pin_ink_soft"',
    'android:textColor="#66000000"': 'android:textColor="@color/pin_ink_soft"',
    'android:textColor="#FF000000"': 'android:textColor="@color/pin_ink"',
    'android:textColorHint="@color/black"': 'android:textColorHint="@color/pin_ink_faint"',
    # backgrounds
    'android:background="#DDDDDD"': 'android:background="@color/pin_surface_alt"',
    'android:background="@drawable/btn_shape"': 'android:background="@drawable/bg_pill_surface"',
    'android:background="@drawable/btn_shape2"': 'android:background="@drawable/bg_btn_primary"',
    'android:background="@drawable/btn_bg"': 'android:background="@drawable/bg_btn_primary"',
    'android:background="@drawable/cardstroke"': 'android:background="@drawable/bg_pin_card"',
    'android:background="@drawable/card_corners"': 'android:background="@drawable/bg_pin_card"',
    'android:background="@drawable/icon_bg1"': 'android:background="@drawable/bg_icon_circle_soft"',
    'android:background="@drawable/ic_rect_bg"': 'android:background="@drawable/bg_pin_card"',
    'android:background="@drawable/edit_shape"': 'android:background="@drawable/bg_input"',
    'android:background="@drawable/ad_card_outline"': 'android:background="@drawable/bg_pin_card"',
    # cards
    'app:cardCornerRadius="@dimen/_10sdp"': 'app:cardCornerRadius="@dimen/pin_radius"',
    'app:cardCornerRadius="@dimen/_20sdp"': 'app:cardCornerRadius="@dimen/pin_radius_lg"',
    'app:strokeColor="@color/purple_200"': 'app:strokeColor="@color/pin_line"',
}


def main():
    changed, skipped, total = [], [], 0
    for name in sorted(os.listdir(LAYOUT_DIR)):
        if not name.endswith(".xml"):
            continue
        total += 1
        if name in SKIP:
            skipped.append(name)
            continue
        path = os.path.join(LAYOUT_DIR, name)
        src = open(path, encoding="utf-8").read()
        out = src
        for old, new in VALUE_MAP.items():
            out = out.replace(old, new)
        if out != src:
            open(path, "w", encoding="utf-8").write(out)
            changed.append(name)

    print(f"scanned {total} layouts, restyled {len(changed)}, skipped {len(skipped)}")
    for n in changed:
        print("  +", n)
    return 0


if __name__ == "__main__":
    sys.exit(main())

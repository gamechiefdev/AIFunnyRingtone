#!/usr/bin/env python3
"""Re-render every mockup with the v6.2 "3D material" treatment.

Cards get a soft contact shadow + a top gloss highlight, so the flat mockups read
like layered material (the in-app equivalent is cardElevation + bg_card_3d.xml).

Usage:  python3 design-preview/render_3d.py
"""
import os
import sys

HERE = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, HERE)

from PIL import Image, ImageDraw, ImageFilter  # noqa: E402
import render_mockups as R  # noqa: E402
import render_extra as X  # noqa: E402

P = R.P
_orig_rrect = R.rrect

LIGHT = {"#FFFFFF", "#FBF9F6", "#F3F0EB", "#F1F9F4", "#E4F3EB", "#EEEBFF", "#FFF4D6",
         "#FFE9E3", "#E3F4F9", "#FDE9EA", "#F7F4EF", "#FDFBF8"}


def _is_light(fill):
    return isinstance(fill, str) and fill.upper() in {c.upper() for c in LIGHT}


def rrect_3d(d, box, r, fill=None, outline=None, width=1):
    """Draw a card with a soft contact shadow + gloss before the real shape."""
    try:
        im = getattr(d, "_image", None)
    except Exception:
        im = None
    w = abs(box[2] - box[0])
    h = abs(box[3] - box[1])
    if im is not None and im.mode == "RGBA" and _is_light(fill) and w > 70 and h > 26 and r >= 8:
        shadow = Image.new("RGBA", im.size, (0, 0, 0, 0))
        sd = ImageDraw.Draw(shadow)
        sd.rounded_rectangle([P(box[0] + 1), P(box[1] + 5), P(box[2] - 1), P(box[3] + 5)],
                             radius=P(r), fill=(24, 20, 16, 46))
        shadow = shadow.filter(ImageFilter.GaussianBlur(P(5)))
        im.alpha_composite(shadow)
    _orig_rrect(d, box, r, fill=fill, outline=outline, width=width)
    if im is not None and im.mode == "RGBA" and _is_light(fill) and w > 70 and h > 26 and r >= 8:
        gloss = Image.new("RGBA", im.size, (0, 0, 0, 0))
        gd = ImageDraw.Draw(gloss)
        gd.rounded_rectangle([P(box[0] + 1), P(box[1] + 1), P(box[2] - 1), P(box[1] + (h * 0.42))],
                             radius=P(r), fill=(255, 255, 255, 46))
        im.alpha_composite(gloss)


R.rrect = rrect_3d
X.rrect = rrect_3d


def main():
    screens = {
        "home": R.home(), "wallpapers": R.walls(), "stickers": R.stickers(), "sounds": R.sounds(),
        "prank": X.prank(), "player": X.player(), "call": X.call(), "premium": X.premium(),
    }
    for name, im in screens.items():
        im.convert("RGB").save(os.path.join(R.OUT, f"{name}.png"), optimize=True)

    R.build_sheet(list(screens.items())[:4]).convert("RGB").save(os.path.join(R.OUT, "ui-preview.png"), optimize=True)
    R.build_sheet(list(screens.items())[4:]).convert("RGB").save(os.path.join(R.OUT, "ui-preview-2.png"), optimize=True)

    import render_all
    render_all.main()
    print("re-rendered with 3D material shadows")


if __name__ == "__main__":
    main()

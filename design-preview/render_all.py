#!/usr/bin/env python3
"""One image with every redesigned screen (4 x 2 grid) -> design-preview/shots/all-screens.png

Usage:  python3 design-preview/render_all.py
"""
import os
import sys

HERE = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, HERE)

from PIL import Image, ImageDraw, ImageFilter  # noqa: E402
from render_mockups import OUT, f, INK, INK_SOFT, PRIMARY  # noqa: E402

BG = "#F3F1EC"
SCALE = 0.72
GAP_X, GAP_Y = 34, 78
PAD = 64
CAPTION = 34


def main():
    screens = [
        ("HOME", "home.png"), ("WALLPAPERS", "wallpapers.png"),
        ("STICKERS", "stickers.png"), ("SOUNDS", "sounds.png"),
        ("PRANK", "prank.png"), ("PLAYER", "player.png"),
        ("INCOMING CALL", "call.png"), ("PREMIUM", "premium.png"),
    ]
    imgs = []
    for label, name in screens:
        im = Image.open(os.path.join(OUT, name)).convert("RGB")
        w = int(im.width * SCALE)
        h = int(im.height * SCALE)
        imgs.append((label, im.resize((w, h), Image.LANCZOS).convert("RGBA")))

    pw, ph = imgs[0][1].size
    sheet_w = PAD * 2 + pw * 4 + GAP_X * 3
    header = 150
    sheet_h = PAD + header + (ph + CAPTION) * 2 + GAP_Y + 48

    sheet = Image.new("RGBA", (sheet_w, sheet_h), BG)

    d = ImageDraw.Draw(sheet)
    d.text((PAD, PAD), "AI Funny Ringtones — v6.4 “Comic Pin” UI", font=f("semibold", 26), fill=INK)
    d.text((PAD, PAD + 62), "Saari redesigned screens · Poppins + app artwork · real layouts, same view ids",
           font=f("regular", 13), fill=INK_SOFT)
    d.rounded_rectangle([PAD, PAD + 96, PAD + 132, PAD + 102], radius=3, fill=PRIMARY)

    top = PAD + header
    for i, (label, im) in enumerate(imgs):
        col, row = i % 4, i // 4
        x = PAD + col * (pw + GAP_X)
        y = top + row * (ph + CAPTION + GAP_Y)

        shadow = Image.new("RGBA", (pw + 60, ph + 60), (0, 0, 0, 0))
        ImageDraw.Draw(shadow).rounded_rectangle([30, 36, 30 + pw, 36 + ph], radius=42, fill=(20, 17, 14, 45))
        shadow = shadow.filter(ImageFilter.GaussianBlur(16))
        sheet.alpha_composite(shadow, (x - 30, y - 18))

        mask = Image.new("L", (pw, ph), 0)
        ImageDraw.Draw(mask).rounded_rectangle([0, 0, pw, ph], radius=42, fill=255)
        sheet.paste(im, (x, y), mask)
        d.rounded_rectangle([x, y, x + pw, y + ph], radius=42, outline=(20, 17, 14, 255), width=7)

        d.text((x + 4, y + ph + 14), label, font=f("semibold", 12), fill=INK_SOFT)

    out = os.path.join(OUT, "all-screens.png")
    sheet.convert("RGB").save(out, "PNG", optimize=True)
    print("written", out, sheet.size, round(os.path.getsize(out) / 1024 / 1024, 2), "MB")


if __name__ == "__main__":
    main()

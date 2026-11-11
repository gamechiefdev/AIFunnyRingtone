#!/usr/bin/env python3
"""Mockups for the second wave of screens: prank grid, player, incoming call, premium.

Usage:  python3 design-preview/render_extra.py
Reuses the drawing helpers from render_mockups.py (same tokens, fonts, artwork).
"""
import os
import sys

HERE = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, HERE)

from PIL import Image, ImageDraw  # noqa: E402
import render_mockups as R  # noqa: E402
from render_mockups import (P, W, H, f, text, rrect, gradient, pill, icon_circle,  # noqa: E402
                           cover, paste_img, appbar, tabbar, screen_shell,
                           INK, INK_SOFT, INK_FAINT, LINE, SURFACE, SURFACE_ALT, WHITE,
                           PRIMARY, PRIMARY_DARK, PRIMARY_SOFT, PRIMARY_SOFTER,
                           ACCENT_SOFT, AMBER_SOFT, LILAC, LILAC_SOFT, SKY_SOFT, ROSE_SOFT,
                           glyph_heart)


def prank():
    img, d = screen_shell("", "")
    appbar(d, "Prank Call", "Fake calls, real laughs")

    text(d, (18, 84), "PICK A VOICE", f("semibold", 9.5), PRIMARY)
    text(d, (18, 100), "Prank your", f("semibold", 26), INK)
    text(d, (18, 130), "friends.", f("semibold", 26), INK)
    text(d, (18, 168), "Choose a character — the call rings with a", f("regular", 12.5), INK_SOFT)
    text(d, (18, 185), "recorded prank voice.", f("regular", 12.5), INK_SOFT)

    voices = [("Boss", "wall1.webp"), ("Ex", "wall2.webp"), ("Mom", "wall3.webp"), ("Alien", "wall5.webp")]
    x = 18
    for i, (label, art) in enumerate(voices):
        active = i == 0
        cx = x + 30
        paste_img(img, os.path.join(HERE, "assets", art), (x, 210, x + 60, 270), radius=30, fit="cover")
        d = ImageDraw.Draw(img)
        if active:
            d.ellipse([P(x - 2), P(208), P(x + 62), P(272)], outline=PRIMARY, width=P(2.5))
        text(d, (cx, 284), label, f("medium" if not active else "semibold", 10), INK if active else INK_FAINT, anchor="ma")
        x += 74

    text(d, (18, 314), "Start a prank", f("semibold", 16), INK)
    text(d, (W - 18, 316), "Instant call", f("medium", 10.5), INK_FAINT, anchor="ra")

    tiles = [("Boss", "wall1.webp"), ("Ex Crush", "wall2.webp"), ("Mom", "wall3.webp"),
             ("Alien", "wall5.webp"), ("Ghost", "wall7.webp"), ("Robot", "wall8.webp")]
    gw, gh = 112.0, 108.0
    for i, (label, art) in enumerate(tiles):
        col, row = i % 3, i // 3
        x = 18 + col * (gw + 8)
        y = 336 + row * (gh + 12)
        paste_img(img, os.path.join(HERE, "assets", art), (x, y, x + gw, y + gh - 22), radius=14, fit="cover")
        d = ImageDraw.Draw(img)
        text(d, (x + gw / 2, y + gh - 14), label, f("semibold", 10.5), INK, anchor="ma")

    text(d, (18, 600), "Recorded pranks", f("semibold", 16), INK)
    rows = [("Birthday roast", "0:12", "wall8.webp"), ("Ghost whisper", "0:07", "wall9.webp")]
    y = 626
    for name, dur, art in rows:
        rrect(d, (18, y, W - 18, y + 54), 18, fill=WHITE, outline=LINE, width=1)
        paste_img(img, os.path.join(HERE, "assets", art), (28, y + 7, 68, y + 47), radius=12, fit="cover")
        d = ImageDraw.Draw(img)
        text(d, (78, y + 12), name, f("semibold", 12.5), INK)
        text(d, (78, y + 31), dur + " · Recorded", f("regular", 10.5), INK_FAINT)
        icon_circle(d, W - 46, y + 27, 17, PRIMARY_SOFTER)
        d.polygon([(P(W - 49), P(y + 21)), (P(W - 49), P(y + 33)), (P(W - 40), P(y + 27))], fill=PRIMARY)
        y += 62

    tabbar(d, active=1)
    return img


def player():
    img, d = screen_shell("Now playing", "Funny 1 · Funny")
    icon_circle(d, 40, 42, 22, WHITE, LINE)
    d.line([P(36), P(42), P(45), P(42)], fill=INK, width=P(1.6))
    d.line([P(36), P(42), P(41), P(37)], fill=INK, width=P(1.6))
    d.line([P(36), P(42), P(41), P(47)], fill=INK, width=P(1.6))
    text(d, (76, 26), "Now playing", f("semibold", 20), INK)
    text(d, (76, 50), "Funny 1 · Funny", f("regular", 11), INK_FAINT)

    paste_img(img, os.path.join(HERE, "assets", "wall2.webp"), (18, 92, W - 18, 400), radius=26, fit="cover")
    d = ImageDraw.Draw(img)

    text(d, (18, 424), "Funny 1", f("semibold", 22), INK)
    text(d, (18, 456), "Funny sounds · 0:04", f("regular", 12), INK_SOFT)
    glyph_heart(d, W - 30, 436, 1.1, PRIMARY)

    # progress
    rrect(d, (18, 492, W - 18, 498), 999, fill=SURFACE_ALT)
    rrect(d, (18, 492, 150, 498), 999, fill=PRIMARY)
    d.ellipse([P(143), P(488), P(157), P(502)], fill=PRIMARY)
    text(d, (18, 506), "0:02", f("medium", 10), INK_FAINT)
    text(d, (W - 18, 506), "0:04", f("medium", 10), INK_FAINT, anchor="ra")

    # transport controls
    cy = 566
    d.polygon([(P(70), P(cy - 10)), (P(70), P(cy + 10)), (P(84), P(cy))], fill=INK)
    d.rectangle([P(60), P(cy - 10), P(64), P(cy + 10)], fill=INK)
    d.polygon([(P(W - 84), P(cy - 10)), (P(W - 84), P(cy + 10)), (P(W - 70), P(cy))], fill=INK)
    d.rectangle([P(W - 64), P(cy - 10), P(W - 60), P(cy + 10)], fill=INK)
    d.ellipse([P(W / 2 - 34), P(cy - 34), P(W / 2 + 34), P(cy + 34)], fill=PRIMARY)
    d.rectangle([P(W / 2 - 6), P(cy - 13), P(W / 2 - 1), P(cy + 13)], fill=WHITE)
    d.rectangle([P(W / 2 + 3), P(cy - 13), P(W / 2 + 8), P(cy + 13)], fill=WHITE)

    # actions
    actions = [("Set ringtone", PRIMARY_SOFT, PRIMARY_DARK), ("Share", SURFACE_ALT, INK), ("Save", SURFACE_ALT, INK)]
    x = 18
    widths = [128, 96, 96]
    for (label, bg, fg), w in zip(actions, widths):
        pill(d, (x, 636, x + w, 676), 999, bg, label, f("semibold", 11), fg)
        x += w + 8

    # up next
    text(d, (18, 700), "Up next", f("semibold", 16), INK)
    y = 726
    rrect(d, (18, y, W - 18, y + 46), 16, fill=WHITE, outline=LINE, width=1)
    paste_img(img, os.path.join(HERE, "assets", "wall4.webp"), (26, y + 6, 60, y + 40), radius=11, fit="cover")
    d = ImageDraw.Draw(img)
    text(d, (70, y + 14), "Dialogue 7", f("semibold", 12), INK)
    tabbar(d, active=3)
    return img


def call():
    img = Image.new("RGBA", (P(W), P(H)), PRIMARY_DARK)
    d = ImageDraw.Draw(img)
    grad = gradient((W * 2, H * 2), R.hex2rgb("#1FB37A"), R.hex2rgb("#093B26"), angle=90)
    img.paste(grad, (0, 0))

    text(d, (W / 2, 74), "INCOMING PRANK CALL", f("semibold", 10.5), "#D8EFE3", anchor="ma")
    paste_img(img, os.path.join(HERE, "assets", "wall1.webp"), (W / 2 - 62, 130, W / 2 + 62, 254), radius=999, fit="cover")
    d = ImageDraw.Draw(img)
    d.ellipse([P(W / 2 - 64), P(128), P(W / 2 + 64), P(256)], outline="#B3FFFFFF", width=P(3))

    text(d, (W / 2, 292), "Boss", f("semibold", 26), WHITE, anchor="ma")
    text(d, (W / 2, 330), "Ringing… answer to play the prank voice", f("regular", 11.5), "#CFE8DB", anchor="ma")
    glass = Image.new("RGBA", img.size, (0, 0, 0, 0))
    gd = ImageDraw.Draw(glass)
    gd.rounded_rectangle([P(W / 2 - 44), P(362), P(W / 2 + 44), P(396)], radius=P(999), fill=(255, 255, 255, 56))
    img.alpha_composite(glass)
    d = ImageDraw.Draw(img)
    text(d, (W / 2, 379), "00:07", f("medium", 12), WHITE, anchor="mm")

    # decline / answer
    cy = 640
    d.ellipse([P(78), P(cy - 34), P(146), P(cy + 34)], fill="#E5484D")
    d.line([P(100), P(cy - 12), P(124), P(cy + 12)], fill=WHITE, width=P(3))
    d.line([P(124), P(cy - 12), P(100), P(cy + 12)], fill=WHITE, width=P(3))
    d.ellipse([P(W - 146), P(cy - 34), P(W - 78), P(cy + 34)], fill=WHITE)
    d.ellipse([P(W - 128), P(cy - 14), P(W - 96), P(cy + 16)], outline=PRIMARY, width=P(3))

    text(d, (112, cy + 48), "Decline", f("medium", 10.5), "#CFE8DB", anchor="ma")
    text(d, (W - 112, cy + 48), "Answer", f("medium", 10.5), "#CFE8DB", anchor="ma")
    return img


def premium():
    img = Image.new("RGBA", (P(W), P(H)), "#F3F1EC")
    d = ImageDraw.Draw(img)
    # dimmed app behind the sheet
    back = Image.new("RGBA", (P(W), P(H)), WHITE)
    bd = ImageDraw.Draw(back)
    appbar(bd, "Discover", "Funny sounds & wallpapers")
    rrect(bd, (18, 90, W - 18, 250), 20, fill=SURFACE_ALT)
    rrect(bd, (18, 266, W - 18, 430), 20, fill=SURFACE_ALT)
    img.paste(back, (0, 0))
    shade = Image.new("RGBA", (P(W), P(H)), (0, 0, 0, 120))
    img.alpha_composite(shade)
    d = ImageDraw.Draw(img)

    rrect(d, (0, 120, W, H), 30, fill=WHITE)
    rrect(d, (W / 2 - 20, 134, W / 2 + 20, 138), 999, fill=LINE)

    icon_circle(d, W / 2, 196, 34, PRIMARY_SOFT)
    img2 = Image.open(os.path.join(HERE, "assets", "ic_robot.png")).convert("RGBA")
    img2.thumbnail((P(34), P(34)), Image.LANCZOS)
    img.alpha_composite(img2, (P(W / 2) - img2.width // 2, P(196) - img2.height // 2))

    text(d, (W / 2, 246), "Go Premium", f("semibold", 22), INK, anchor="ma")
    text(d, (W / 2, 282), "Unlimited sounds, no ads, all wallpapers", f("regular", 11.5), INK_SOFT, anchor="ma")

    plans = [("Weekly", "$2.99", "per week", False), ("Monthly", "$7.99", "per month", True), ("Lifetime", "$24.99", "one time", False)]
    y = 312
    for name, price, per, active in plans:
        rrect(d, (18, y, W - 18, y + 62), 18,
              fill=PRIMARY_SOFTER if active else WHITE,
              outline=PRIMARY if active else LINE, width=2 if active else 1)
        if active:
            d.ellipse([P(32), P(y + 23), P(50), P(y + 41)], fill=PRIMARY)
            d.line([P(37), P(y + 32), P(40.5), P(y + 36), P(46), P(y + 28)], fill=WHITE, width=P(2))
        else:
            d.ellipse([P(32), P(y + 23), P(50), P(y + 41)], outline=LINE, width=P(1.5))
        text(d, (60, y + 16), name, f("semibold", 13), INK)
        text(d, (60, y + 36), per, f("regular", 10.5), INK_FAINT)
        text(d, (W - 32, y + 24), price, f("semibold", 14), PRIMARY if active else INK, anchor="ra")
        y += 72

    pill(d, (18, y + 10, W - 18, y + 62), 16, PRIMARY, "Continue", f("semibold", 14), WHITE)
    text(d, (W / 2, y + 78), "Restore purchase", f("medium", 11), INK_FAINT, anchor="ma")
    text(d, (W / 2, H - 34), "Auto-renews · cancel anytime", f("regular", 9.5), INK_FAINT, anchor="ma")
    return img


if __name__ == "__main__":
    screens = {"prank": prank(), "player": player(), "call": call(), "premium": premium()}
    for name, im in screens.items():
        im.convert("RGB").save(f"{R.OUT}/{name}.png", optimize=True)
        print("wrote", name)
    sheet = R.build_sheet(list(screens.items()))
    sheet.convert("RGB").save(f"{R.OUT}/ui-preview-2.png", optimize=True)
    print("sheet", sheet.size)

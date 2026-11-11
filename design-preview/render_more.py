#!/usr/bin/env python3
"""Second-wave-ish screens so the preview sheet covers the whole app:
favourites, history, jokes, games. Same tokens/fonts as the other mockups."""
import os
import sys

HERE = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, HERE)

from PIL import Image  # noqa: E402
import render_mockups as R  # noqa: E402
from render_mockups import (P, W, H, f, fun, text, rrect, pill, icon_circle, paste_img,  # noqa: E402
                           glyph_heart, screen_shell, tabbar,
                           INK, INK_SOFT, INK_FAINT, LINE, WHITE, PRIMARY, PRIMARY_SOFT,
                           PRIMARY_DARK, ACCENT_SOFT, AMBER_SOFT, LILAC_SOFT, SKY_SOFT, ROSE_SOFT)


def comic(d, box, r=20, fill=WHITE, w=1):
    """hairline card, no heavy outline"""
    rrect(d, box, r, fill=fill, outline=LINE, width=w)


def backbar(d, title, sub):
    icon_circle(d, 40, 42, 22, WHITE, LINE)
    d.line([P(36), P(42), P(45), P(42)], fill=INK, width=P(1.6))
    d.line([P(36), P(42), P(41), P(37)], fill=INK, width=P(1.6))
    d.line([P(36), P(42), P(41), P(47)], fill=INK, width=P(1.6))
    text(d, (76, 26), title, fun(21), INK)
    text(d, (76, 50), sub, f("regular", 11), INK_FAINT)


def favourites():
    img, d = screen_shell("", "")
    backbar(d, "Favourites", "Every heart you tapped, in one place")

    pill(d, (18, 78, 96, 104), 999, PRIMARY_SOFT, "24 pins", f("semibold", 10), PRIMARY_DARK)
    pill(d, (104, 78, 178, 104), 999, WHITE, "Sounds", f("semibold", 10), INK)
    pill(d, (186, 78, 258, 104), 999, WHITE, "Walls", f("semibold", 10), INK)

    tiles = ["wall1.webp", "wall4.webp", "wall6.webp", "wall8.webp", "wall2.webp", "wall10.webp"]
    tints = [LILAC_SOFT, AMBER_SOFT, SKY_SOFT, ROSE_SOFT, ACCENT_SOFT, WHITE]
    for i, art in enumerate(tiles):
        col, row = i % 2, i // 2
        x = 18 + col * 178
        y = 118 + row * 168
        comic(d, (x, y, x + 166, y + 152), 22, fill=WHITE)
        paste_img(img, f"{R.BASE}/assets/{art}", (x + 6, y + 6, x + 160, y + 112), radius=16)
        icon_circle(d, x + 146, y + 26, 14, WHITE, INK)
        glyph_heart(d, x + 146, y + 25, 0.62, PRIMARY)
        text(d, (x + 12, y + 122), ["Boss voice", "Cactus ring", "Princess roar"][i % 3], f("semibold", 12), INK)
        text(d, (x + 12, y + 136), "Saved · 0:04", f("regular", 9.5), INK_FAINT)
    tabbar(d, 2)
    return img


def history():
    img, d = screen_shell("", "")
    backbar(d, "Your playlist", "Last played · newest first")

    rows = [("Funny 1", "0:04", "2 min ago", "wall1.webp"),
            ("Baby 3", "0:07", "18 min ago", "wall2.webp"),
            ("Dialogue 7", "0:09", "1 hr ago", "wall6.webp"),
            ("Fart 12", "0:02", "Today", "wall8.webp"),
            ("Laugh 9", "0:06", "Today", "wall4.webp"),
            ("Animal 4", "0:05", "Yesterday", "wall10.webp")]
    for i, (name, dur, when, art) in enumerate(rows):
        y = 84 + i * 86
        comic(d, (18, y, W - 18, y + 78), 20, fill=WHITE)
        paste_img(img, f"{R.BASE}/assets/{art}", (28, y + 12, 96, y + 66), radius=14)
        text(d, (110, y + 22), name, f("semibold", 13.5), INK)
        text(d, (110, y + 44), f"{dur} · {when}", f("regular", 10.5), INK_FAINT)
        icon_circle(d, W - 44, y + 39, 18, PRIMARY_SOFT)
        d.polygon([P(W - 49), P(y + 31), P(W - 49), P(y + 47), P(W - 36), P(y + 39)], fill=PRIMARY)
    tabbar(d, 3)
    return img


def jokes():
    img, d = screen_shell("", "")
    backbar(d, "Funny jokes", "Read them out loud, we dare you")

    pill(d, (18, 78, 90, 104), 999, AMBER_SOFT, "Sardar", f("semibold", 10), INK)
    pill(d, (98, 78, 168, 104), 999, WHITE, "Dark", f("semibold", 10), INK)
    pill(d, (176, 78, 244, 104), 999, WHITE, "School", f("semibold", 10), INK)

    cards = [("Why did the ringtone go to therapy?", "Too many unresolved chords.", AMBER_SOFT),
             ("My phone is on silent.", "Not for work — my wallpaper is shy.", ACCENT_SOFT),
             ("Prank call went wrong.", "Grandma roasted me for 4 minutes.", SKY_SOFT),
             ("Sticker pack applied.", "Now my lock screen is funnier than me.", LILAC_SOFT)]
    y = 118
    for line1, line2, tint in cards:
        comic(d, (18, y, W - 18, y + 118), 22, fill=WHITE)
        icon_circle(d, 54, y + 34, 21, tint, INK)
        # drawn comic face (emoji glyphs are unavailable in the bundled fonts)
        d.ellipse([P(44), P(y + 27), P(49), P(y + 33)], fill=INK)
        d.ellipse([P(59), P(y + 27), P(64), P(y + 33)], fill=INK)
        d.arc([P(46), P(y + 32), P(62), P(y + 46)], 20, 160, fill=INK, width=P(1.6))
        text(d, (86, y + 20), line1, fun(12.5), INK)
        text(d, (86, y + 48), line2, f("regular", 11), INK_SOFT)
        pill(d, (86, y + 76, 176, y + 102), 999, PRIMARY_SOFT, "Read aloud", f("semibold", 9.5), PRIMARY_DARK)
        y += 132
    tabbar(d, 1)
    return img


def games():
    img, d = screen_shell("", "")
    backbar(d, "Games", "Truth or dare, spin the wheel, puzzles")

    comic(d, (18, 78, W - 18, 210), 24, fill=LILAC_SOFT)
    text(d, (36, 98), "Truth or Dare", fun(20), INK)
    text(d, (36, 128), "Sab ke liye ek challenge — no skipping!", f("regular", 11), INK_SOFT)
    pill(d, (36, 156, 156, 192), 999, WHITE, "Spin now", f("semibold", 11), INK)

    comic(d, (18, 226, 182, 342), 24, fill=AMBER_SOFT)
    text(d, (36, 246), "Puzzle", fun(18), INK)
    text(d, (36, 274), "Slide the strips back", f("regular", 10.5), INK_SOFT)
    pill(d, (36, 300, 140, 330), 999, WHITE, "Play", f("semibold", 10.5), INK)

    comic(d, (198, 226, W - 18, 342), 24, fill=SKY_SOFT)
    text(d, (216, 246), "Donkey", fun(18), INK)
    text(d, (216, 274), "Pin the tail, win the laugh", f("regular", 10.5), INK_SOFT)
    pill(d, (216, 300, 330, 330), 999, WHITE, "Start", f("semibold", 10.5), INK)

    comic(d, (18, 358, W - 18, 440), 22, fill=WHITE)
    text(d, (36, 376), "Funny challenges", fun(16), INK)
    text(d, (36, 400), "12 dares unlocked · 4 new this week", f("regular", 11), INK_FAINT)
    icon_circle(d, W - 46, 399, 16, PRIMARY)
    d.polygon([P(W - 50), P(393), P(W - 50), P(405), P(W - 40), P(399)], fill=WHITE)
    tabbar(d, 0)
    return img


if __name__ == "__main__":
    screens = {"favourites": favourites(), "history": history(), "jokes": jokes(), "games": games()}
    for name, im in screens.items():
        im.convert("RGB").save(f"{R.OUT}/{name}.png", optimize=True)
        print("wrote", name)

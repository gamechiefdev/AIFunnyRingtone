#!/usr/bin/env python3
"""Render real PNG mockups of the redesigned Android screens with Pillow.

Usage:  pip install pillow && python3 design-preview/render_mockups.py

Draws the shipped v6.1 "Pin" design system at 2x using the same tokens,
Poppins TTFs and bundled artwork that live in app/src/main/res.
"""
import os
from PIL import Image, ImageDraw, ImageFilter, ImageFont

BASE = os.path.dirname(os.path.abspath(__file__))
OUT = os.path.join(BASE, "shots")
os.makedirs(OUT, exist_ok=True)

S = 2  # supersample
W, H = 390, 844

INK = "#141414"
INK_SOFT = "#6E6A64"
INK_FAINT = "#A39D95"
LINE = "#EBE6DF"
SURFACE = "#FBF9F6"
SURFACE_ALT = "#F3F0EB"
PRIMARY = "#188E5A"
PRIMARY_DARK = "#0E6640"
PRIMARY_SOFT = "#E4F3EB"
PRIMARY_SOFTER = "#F1F9F4"
ACCENT = "#FF6B4A"
ACCENT_SOFT = "#FFE9E3"
AMBER_SOFT = "#FFF4D6"
LILAC = "#7C6CF6"
LILAC_SOFT = "#EEEBFF"
SKY_SOFT = "#E3F4F9"
ROSE_SOFT = "#FDE9EA"
WHITE = "#FFFFFF"


def f(weight, size):
    return ImageFont.truetype(f"{BASE}/fonts/poppins_{weight}.ttf", int(round(size * S)))


def P(v):
    return int(round(v * S))


def text(d, xy, s, font, fill, anchor="la"):
    d.text((P(xy[0]), P(xy[1])), s, font=font, fill=fill, anchor=anchor)


def rrect(d, box, r, fill=None, outline=None, width=1):
    d.rounded_rectangle([P(box[0]), P(box[1]), P(box[2]), P(box[3])],
                        radius=P(r), fill=fill, outline=outline, width=P(width) if outline else 0)


def gradient(size, c1, c2, angle=45):
    """Simple linear gradient (angle in degrees, 0 = left->right)."""
    w, h = size
    small = Image.new("RGB", (64, 64))
    px = small.load()
    import math
    rad = math.radians(angle)
    dx, dy = math.cos(rad), math.sin(rad)
    for y in range(64):
        for x in range(64):
            t = ((x / 63) * dx + (y / 63) * dy + 0.5)
            t = max(0.0, min(1.0, t))
            a = tuple(int(c1[i] + (c2[i] - c1[i]) * t) for i in range(3))
            px[x, y] = a
    return small.resize((w, h), Image.BICUBIC)


def hex2rgb(value):
    value = value.lstrip("#")
    return tuple(int(value[i:i + 2], 16) for i in (0, 2, 4))


def cover(path, w, h):
    im = Image.open(path).convert("RGBA")
    tw, th = P(w), P(h)
    ratio = max(tw / im.width, th / im.height)
    im = im.resize((max(1, int(im.width * ratio)), max(1, int(im.height * ratio))), Image.LANCZOS)
    left = (im.width - tw) // 2
    top = (im.height - th) // 2
    return im.crop((left, top, left + tw, top + th))


def paste_img(base, path, box, radius=0, fit="cover", pad=0):
    x0, y0, x1, y1 = box
    w, h = x1 - x0, y1 - y0
    if fit == "cover":
        im = cover(path, w, h)
    else:  # contain
        im = Image.open(path).convert("RGBA")
        target = P(w - pad * 2), P(h - pad * 2)
        im.thumbnail(target, Image.LANCZOS)
        layer = Image.new("RGBA", (P(w), P(h)), (0, 0, 0, 0))
        layer.paste(im, ((P(w) - im.width) // 2, (P(h) - im.height) // 2), im)
        im = layer
    mask = Image.new("L", im.size, 0)
    ImageDraw.Draw(mask).rounded_rectangle([0, 0, im.width, im.height], radius=P(radius), fill=255)
    base.paste(im, (P(x0), P(y0)), mask)


def pill(d, box, r, fill, label=None, label_font=None, label_fill=WHITE):
    rrect(d, box, r, fill=fill)
    if label:
        cx, cy = (box[0] + box[2]) / 2, (box[1] + box[3]) / 2
        text(d, (cx, cy), label, label_font, label_fill, anchor="mm")


def icon_circle(d, cx, cy, r, bg, outline=None):
    d.ellipse([P(cx - r), P(cy - r), P(cx + r), P(cy + r)],
              fill=bg, outline=outline, width=P(1) if outline else 0)


# ---------------------------------------------------------------- glyphs
def glyph_menu(d, cx, cy, color=INK, scale=1.0):
    for dy in (-4, 0, 4):
        d.line([P(cx - 7 * scale), P(cy + dy * scale), P(cx + 7 * scale), P(cy + dy * scale)],
               fill=color, width=P(1.6))


def glyph_heart(d, cx, cy, s=1.0, color=PRIMARY):
    r = 4.6 * s
    d.ellipse([P(cx - r * 2.1), P(cy - r * 1.7), P(cx - r * 0.1), P(cy + r * 0.3)], fill=color)
    d.ellipse([P(cx + r * 0.1), P(cy - r * 1.7), P(cx + r * 2.1), P(cy + r * 0.3)], fill=color)
    d.polygon([(P(cx - r * 2.05), P(cy - r * 0.1)), (P(cx + r * 2.05), P(cy - r * 0.1)), (P(cx), P(cy + r * 2.3))], fill=color)


def glyph_nav(d, kind, cx, cy, color):
    if kind == "home":
        d.polygon([(P(cx - 7), P(cy - 1)), (P(cx), P(cy - 8)), (P(cx + 7), P(cy - 1))], outline=color, width=P(1.4))
        d.line([P(cx - 5), P(cy - 1), P(cx - 5), P(cy + 7), P(cx + 5), P(cy + 7), P(cx + 5), P(cy - 1)], fill=color, width=P(1.4))
    elif kind == "phone":
        d.rounded_rectangle([P(cx - 5), P(cy - 8), P(cx + 5), P(cy + 8)], radius=P(3), outline=color, width=P(1.5))
        d.line([P(cx - 2), P(cy + 5.5), P(cx + 2), P(cy + 5.5)], fill=color, width=P(1.3))
    elif kind == "wall":
        d.rounded_rectangle([P(cx - 7), P(cy - 6), P(cx + 7), P(cy + 6)], radius=P(3), outline=color, width=P(1.4))
        d.ellipse([P(cx - 3), P(cy - 3), P(cx + 1), P(cy + 1)], fill=color)
        d.polygon([(P(cx - 5), P(cy + 5)), (P(cx), P(cy - 1)), (P(cx + 6), P(cy + 5))], fill=color)
    else:  # music
        d.ellipse([P(cx - 7), P(cy + 1), P(cx - 1), P(cy + 7)], fill=color)
        d.ellipse([P(cx + 1), P(cy - 3), P(cx + 7), P(cy + 3)], fill=color)
        d.line([P(cx - 1), P(cy + 4), P(cx - 1), P(cy - 6), P(cx + 7), P(cy - 8), P(cx + 7), P(cy)], fill=color, width=P(1.4))


# ---------------------------------------------------------------- chrome
def appbar(d, title, sub, right="heart"):
    icon_circle(d, 40, 42, 22, WHITE, LINE)
    glyph_menu(d, 40, 42)
    text(d, (76, 26), title, f("semibold", 20), INK)
    text(d, (76, 50), sub, f("regular", 11), INK_FAINT)
    icon_circle(d, 312, 42, 22, WHITE, LINE)
    glyph_heart(d, 312, 41, 0.85, PRIMARY)
    pill(d, (342, 27, 380, 57), 999, PRIMARY_SOFT, "PRO", f("semibold", 9), PRIMARY_DARK)


def tabbar(d, active=0):
    """Floating Pinterest-style pill nav."""
    im = d._image
    y0, y1 = H - 74, H - 14
    x0, x1 = 14, W - 14

    # soft shadow under the bar
    sh = Image.new("RGBA", im.size, (0, 0, 0, 0))
    ImageDraw.Draw(sh).rounded_rectangle([P(x0 + 2), P(y0 + 8), P(x1 - 2), P(y1 + 8)],
                                         radius=P(999), fill=(20, 18, 14, 52))
    im.alpha_composite(sh.filter(ImageFilter.GaussianBlur(P(6))))

    d.rounded_rectangle([P(x0), P(y0), P(x1), P(y1)], radius=P(999),
                        fill=WHITE, outline="#EFE9E1", width=P(1))
    d.rounded_rectangle([P(x0 + 1), P(y0 + 1), P(x1 - 1), P(y0 + 22)], radius=P(999), fill="#FFFFFF")

    labels = ["Home", "Prank", "Walls", "Playlist"]
    kinds = ["home", "phone", "wall", "music"]
    slot = (x1 - x0) / 4
    cy = (y0 + y1) / 2
    for i in range(4):
        cx = x0 + slot * i + slot / 2
        color = PRIMARY if i == active else "#9A948C"
        if i == active:
            d.rounded_rectangle([P(cx - 40), P(y0 + 8), P(cx + 40), P(y1 - 8)], radius=P(999), fill=PRIMARY_SOFTER)
        glyph_nav(d, kinds[i], cx - (16 if i == active else 0), cy, color)
        if i == active:
            text(d, (cx + 6, cy), labels[i], f("semibold", 10.5), PRIMARY, anchor="lm")
        else:
            text(d, (cx, cy + 15), labels[i], f("medium", 9), color, anchor="ma")


def screen_shell(title, sub, back=False):
    img = Image.new("RGBA", (P(W), P(H)), WHITE)
    d = ImageDraw.Draw(img)
    if back:
        icon_circle(d, 40, 42, 22, WHITE, LINE)
        d.line([P(36), P(42), P(45), P(42)], fill=INK, width=P(1.6))
        d.line([P(36), P(42), P(41), P(37)], fill=INK, width=P(1.6))
        d.line([P(36), P(42), P(41), P(47)], fill=INK, width=P(1.6))
        text(d, (76, 26), title, f("semibold", 20), INK)
        text(d, (76, 50), sub, f("regular", 11), INK_FAINT)
    return img, d


# ---------------------------------------------------------------- screens
def home():
    img, d = screen_shell("", "")
    appbar(d, "Discover", "Funny sounds & wallpapers")

    # rotated sticker badge (playful Pinterest accent)
    badge = Image.new("RGBA", (P(104), P(28)), (0, 0, 0, 0))
    bd = ImageDraw.Draw(badge)
    bd.rounded_rectangle([0, 0, badge.width - 1, badge.height - 1], radius=P(999), fill=ACCENT_SOFT)
    bf = f("semibold", 8.5)
    bd.text((badge.width / 2, badge.height / 2), "PRANK MODE", font=bf, fill="#E2452A", anchor="mm")
    badge = badge.rotate(5, expand=True, resample=Image.BICUBIC)
    img.alpha_composite(badge, (P(246), P(96)))

    text(d, (18, 84), "GOOD EVENING · TIME TO PRANK", f("semibold", 9.5), PRIMARY)
    text(d, (18, 100), "Find your", f("semibold", 26), INK)
    text(d, (18, 130), "funny sound.", f("semibold", 26), INK)
    text(d, (18, 168), "AI voices, prank calls, wallpapers and stickers —", f("regular", 12.5), INK_SOFT)
    text(d, (18, 185), "curated for maximum laughs.", f("regular", 12.5), INK_SOFT)

    # AI studio header
    star = [(22, 214), (24.5, 220), (30, 222), (24.5, 224), (22, 230), (19.5, 224), (14, 222), (19.5, 220)]
    d.polygon([(P(x), P(y)) for x, y in star], fill=PRIMARY)
    text(d, (36, 212), "AI Studio", f("semibold", 17), INK)
    pill(d, (322, 210, 374, 232), 999, PRIMARY_SOFT, "NEW", f("semibold", 8.5), PRIMARY_DARK)

    cards = [
        (18, LILAC_SOFT, "#7C6CF6", "#FF8A6B", "ic_txt_ai.png", "AI Text", "Generator", "Try text to sound", LILAC),
        (200, PRIMARY_SOFT, "#188E5A", "#41C9A0", "ic_robot.png", "AI Sound", "Generator", "Record your voice", PRIMARY_DARK),
    ]
    for x, soft, c1, c2, icon, l1, l2, cta, cta_color in cards:
        box = (x, 236, x + 172, 236 + 190)
        grad = gradient((P(172), P(190)), hex2rgb(c1), hex2rgb(c2), 45).convert("RGBA")
        mask = Image.new("L", grad.size, 0)
        ImageDraw.Draw(mask).rounded_rectangle([0, 0, grad.width, grad.height], radius=P(26), fill=255)
        img.paste(grad, (P(x), P(236)), mask)
        icon_circle(d, x + 32, 268, 18, WHITE)
        ic = Image.open(f"{BASE}/assets/{icon}").convert("RGBA")
        ic.thumbnail((P(22), P(22)), Image.LANCZOS)
        img.paste(ic, (P(x + 32) - ic.width // 2, P(268) - ic.height // 2), ic)
        text(d, (x + 14, 340), l1, f("semibold", 14), WHITE)
        text(d, (x + 14, 360), l2, f("semibold", 14), WHITE)
        pill(d, (x + 14, 390, x + 14 + 120, 420), 999, WHITE, cta, f("semibold", 9.5), cta_color)

    # explore
    star2 = [(22, 452), (24, 457), (29, 459), (24, 461), (22, 466), (20, 461), (15, 459), (20, 457)]
    d.polygon([(P(x), P(y)) for x, y in star2], fill=ACCENT)
    text(d, (36, 450), "Explore", f("semibold", 17), INK)
    pill(d, (300, 448, 372, 468), 999, SURFACE_ALT, "8 boards", f("semibold", 8.5), INK_SOFT)

    def quick(x, y, w, h, bg, icon=None, label=None, row=False):
        rrect(d, (x, y, x + w, y + h), 22, fill=bg)
        if row:
            text(d, (x + 14, y + h / 2), label, f("semibold", 12.5), INK, anchor="lm")
            ic = Image.open(f"{BASE}/assets/{icon}").convert("RGBA")
            ic.thumbnail((P(26), P(26)), Image.LANCZOS)
            img.paste(ic, (P(x + w - 14) - ic.width, P(y + h / 2) - ic.height // 2), ic)
        else:
            ic = Image.open(f"{BASE}/assets/{icon}").convert("RGBA")
            ic.thumbnail((P(30), P(30)), Image.LANCZOS)
            img.paste(ic, (P(x + 14), P(y + 14)), ic)
            if h > 110:
                text(d, (x + 14, y + h - 30), label, f("semibold", 12.5), INK)
            else:
                text(d, (x + 14, y + h - 30), label, f("semibold", 12.5), INK)

    def save_pin(x, y, w):
        cx, cy = x + w - 22, y + 22
        d.ellipse([P(cx - 13), P(cy - 13), P(cx + 13), P(cy + 13)], fill=WHITE)
        glyph_heart(d, cx, cy - 1, 0.62, "#E5484D")

    quick(18, 474, 172, 126, LILAC_SOFT, "ic_ring.png", "My Ring Name")
    save_pin(18, 474, 172)
    quick(18, 610, 172, 104, AMBER_SOFT, "ic_funny_sticker.png", "Funny Stickers")

    quick(200, 474, 172, 104, PRIMARY_SOFT, "ic_dare_game.png", "Dare Game")
    quick(200, 588, 172, 126, ACCENT_SOFT, "ic_stories.png", "Funny Stories")
    save_pin(200, 588, 172)

    # mood chips peeking above the tab bar (as on a scrolled device)
    cx = 18
    for label, active in [("All", True), ("Funny", False), ("Animal", False)]:
        w = 34 if label == "All" else 52 + len(label) * 2
        pill(d, (cx, 730, cx + w, 766), 999, PRIMARY if active else SURFACE_ALT,
             label, f("medium" if not active else "semibold", 11), WHITE if active else INK_SOFT)
        cx += w + 8

    tabbar(d, 0)
    return img


def walls():
    img, d = screen_shell("Wallpaper picks", "Tap any pin to preview and set it.")
    icon_circle(d, 40, 42, 22, WHITE, LINE)
    d.line([P(36), P(42), P(45), P(42)], fill=INK, width=P(1.6))
    d.line([P(36), P(42), P(41), P(37)], fill=INK, width=P(1.6))
    d.line([P(36), P(42), P(41), P(47)], fill=INK, width=P(1.6))
    text(d, (76, 26), "Wallpapers", f("semibold", 20), INK)
    text(d, (76, 50), "Tap any pin to preview and set it.", f("regular", 11), INK_FAINT)

    tops = [86, 86]
    heights = [[206, 198, 206], [198, 206, 198]]
    pics = [["wall1.webp", "wall5.webp", "wall9.webp"], ["wall3.webp", "wall7.webp", "wall8.webp"]]
    for col in range(2):
        x = 18 + col * 178
        y = tops[col]
        for i in range(3):
            h = heights[col][i]
            if y + h > H - 70:
                break
            box = (x, y, x + 170, y + h)
            paste_img(img, f"{BASE}/assets/{pics[col][i]}", box, radius=26)
            # scrim (alpha composited, not pasted)
            scrim = Image.new("RGBA", (P(170), P(72)), (0, 0, 0, 0))
            sd = ImageDraw.Draw(scrim)
            for yy in range(scrim.height):
                alpha = int(165 * (yy / (scrim.height - 1)) ** 1.8)
                sd.line([(0, yy), (scrim.width, yy)], fill=(0, 0, 0, alpha))
            img.alpha_composite(scrim, (P(x), P(y + h - 72)))
            pill(d, (x + 10, y + h - 34, x + 10 + 86, y + h - 10), 999, (0, 0, 0, 150), "Tap to set", f("medium", 9.5), WHITE)
            y += h + 10

    tabbar(d, 2)
    return img


def stickers():
    img, d = screen_shell("", "")
    icon_circle(d, 40, 42, 22, WHITE, LINE)
    d.line([P(36), P(42), P(45), P(42)], fill=INK, width=P(1.6))
    d.line([P(36), P(42), P(41), P(37)], fill=INK, width=P(1.6))
    d.line([P(36), P(42), P(41), P(47)], fill=INK, width=P(1.6))
    text(d, (76, 26), "Funny Stickers", f("semibold", 20), INK)
    text(d, (76, 50), "Add a pack to WhatsApp in one tap", f("regular", 11), INK_FAINT)

    art = ["three.png", "four.png", "five.png", "six.png", "seven.png", "eight.png"]
    names = ["Funny Faces", "Animals", "Laugh Pack", "Fart Pack", "Dialogues", "Baby Talk"]
    sizes = ["412 KB", "688 KB", "523 KB", "301 KB", "744 KB", "918 KB"]
    for i, (a, n, s) in enumerate(zip(art, names, sizes)):
        col, row = i % 2, i // 2
        x = 18 + col * 178
        y = 84 + row * 178
        rrect(d, (x, y, x + 170, y + 166), 26, fill=WHITE, outline=LINE, width=1)
        rrect(d, (x + 10, y + 10, x + 160, y + 116), 16, fill=SURFACE_ALT)
        ic = Image.open(f"{BASE}/assets/{a}").convert("RGBA")
        ic.thumbnail((P(62), P(62)), Image.LANCZOS)
        img.paste(ic, (P(x + 85) - ic.width // 2, P(y + 63) - ic.height // 2), ic)
        text(d, (x + 12, y + 126), n, f("semibold", 12.5), INK)
        text(d, (x + 12, y + 146), s, f("regular", 10), INK_FAINT)
    tabbar(d, 0)
    return img


def sounds():
    img, d = screen_shell("", "")
    icon_circle(d, 40, 42, 22, WHITE, LINE)
    d.line([P(36), P(42), P(45), P(42)], fill=INK, width=P(1.6))
    d.line([P(36), P(42), P(41), P(37)], fill=INK, width=P(1.6))
    d.line([P(36), P(42), P(41), P(47)], fill=INK, width=P(1.6))
    text(d, (76, 26), "Funny sounds", f("semibold", 20), INK)
    text(d, (76, 50), "Tap to play · heart to save", f("regular", 11), INK_FAINT)

    rows = [("01", "Funny 1", "0:04", "wall1.webp", True),
            ("02", "Animal 4", "0:05", "wall2.webp", False),
            ("03", "Laugh 9", "0:06", "wall4.webp", True),
            ("04", "Dialogue 7", "0:09", "wall6.webp", False),
            ("05", "Fart 12", "0:02", "wall8.webp", False),
            ("06", "Baby 3", "0:07", "wall10.webp", True)]
    for i, (num, name, dur, art, liked) in enumerate(rows):
        y = 84 + i * 76
        rrect(d, (18, y, W - 18, y + 68), 20, fill=WHITE, outline=LINE, width=1)
        text(d, (30, y + 34), num, f("medium", 10.5), INK_FAINT, anchor="lm")
        paste_img(img, f"{BASE}/assets/{art}", (48, y + 11, 94, y + 57), radius=14)
        text(d, (106, y + 22), name, f("semibold", 13.5), INK)
        text(d, (106, y + 42), dur, f("regular", 10.5), INK_FAINT)
        icon_circle(d, W - 36, y + 34, 17, PRIMARY if liked else PRIMARY_SOFT)
        glyph_heart(d, W - 36, y + 33, 0.8, WHITE if liked else PRIMARY)
    tabbar(d, 3)
    return img


# ---------------------------------------------------------------- sheet
def build_sheet(screens):
    pad, gap, cap = 56, 44, 92
    sw, sh = W * S, H * S
    sheet_w = pad * 2 + sw * len(screens) + gap * (len(screens) - 1)
    sheet_h = pad + 118 + sh + cap
    sheet = Image.new("RGBA", (sheet_w, sheet_h), "#F3F1EC")
    d = ImageDraw.Draw(sheet)

    title_font = f("semibold", 24)
    sub_font = f("regular", 12.5)
    d.text((pad, pad), "AI Funny Ringtones — v6.2 “Pin + 3D” UI", font=title_font, fill=INK)
    d.text((pad, pad + 58), "Real layouts · Poppins · app artwork · same view ids & logic", font=sub_font, fill=INK_SOFT)

    y = pad + 118
    for i, (name, im) in enumerate(screens):
        x = pad + i * (sw + gap)
        shadow = Image.new("RGBA", (sw + 40, sh + 40), (0, 0, 0, 0))
        sd = ImageDraw.Draw(shadow)
        sd.rounded_rectangle([20, 26, sw + 20, sh + 26], radius=P(54), fill=(20, 17, 14, 40))
        sheet.alpha_composite(shadow, (x - 20, y - 12))
        mask = Image.new("L", (sw, sh), 0)
        ImageDraw.Draw(mask).rounded_rectangle([0, 0, sw, sh], radius=P(48), fill=255)
        sheet.paste(im, (x, y), mask)
        d = ImageDraw.Draw(sheet)
        d.rounded_rectangle([x, y, x + sw, y + sh], radius=P(48), outline=(20, 17, 14), width=P(9))
        label = name.upper()
        d.text((x, y + sh + 26), label, font=f("semibold", 12), fill=INK_SOFT)
    return sheet


if __name__ == "__main__":
    imgs = {
        "home": home(),
        "wallpapers": walls(),
        "stickers": stickers(),
        "sounds": sounds(),
    }
    for name, im in imgs.items():
        im.convert("RGB").save(f"{OUT}/{name}.png", optimize=True)
    sheet = build_sheet(list(imgs.items()))
    sheet.convert("RGB").save(f"{OUT}/ui-preview.png", optimize=True)
    for name in list(imgs) + ["ui-preview"]:
        p = f"{OUT}/{name}.png"
        print(name, Image.open(p).size, round(os.path.getsize(p) / 1024), "KB")

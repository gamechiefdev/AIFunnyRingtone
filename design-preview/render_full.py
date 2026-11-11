#!/usr/bin/env python3
"""Preview for EVERY screen of the app (30 screens) + contact sheets.

python3 design-preview/render_full.py
  -> shots/full/<key>.png  (30 phone renders)
  -> shots/all-screens-1.png (15 main-flow screens)
  -> shots/all-screens-2.png (15 secondary screens)
"""
import os
import sys

HERE = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, HERE)

from PIL import Image, ImageDraw, ImageFilter  # noqa: E402
import render_mockups as R  # noqa: E402
from render_mockups import (P, W, H, f, fun, text, rrect, pill, icon_circle, paste_img,  # noqa: E402
                           screen_shell, OUT, INK, INK_SOFT, INK_FAINT, LINE, WHITE,
                           PRIMARY, PRIMARY_DARK, PRIMARY_SOFT, ACCENT_SOFT, AMBER_SOFT,
                           LILAC_SOFT, SKY_SOFT, ROSE_SOFT)

FULL = os.path.join(OUT, "full")
os.makedirs(FULL, exist_ok=True)


# ----------------------------------------------------------------- chrome
def backbar(d, title, sub, right="dots"):
    icon_circle(d, 40, 42, 22, WHITE, LINE)
    d.line([P(36), P(42), P(45), P(42)], fill=INK, width=P(1.6))
    d.line([P(36), P(42), P(41), P(37)], fill=INK, width=P(1.6))
    d.line([P(36), P(42), P(41), P(47)], fill=INK, width=P(1.6))
    text(d, (74, 24), title, fun(20), INK)
    text(d, (74, 50), sub, f("regular", 10.5), INK_FAINT)
    if right:
        icon_circle(d, W - 40, 42, 20, WHITE, LINE)
        if right == "dots":
            for i in range(3):
                d.ellipse([P(W - 44), P(38 + i * 4), P(W - 41), P(41 + i * 4)], fill=INK_SOFT)
        elif right == "heart":
            from render_mockups import glyph_heart
            glyph_heart(d, W - 40, 41, 0.7, PRIMARY)


def card(d, box, r=20, fill=WHITE):
    rrect(d, box, r, fill=fill, outline=LINE, width=1)


def thumb(img, path, box, radius=14):
    paste_img(img, path, box, radius=radius)


def chevron(d, cx, cy, color=INK_FAINT):
    d.line([P(cx - 3), P(cy - 5), P(cx + 2), P(cy), P(cx - 3), P(cy + 5)], fill=color, width=P(1.6))


def cta(d, y, label, tint=PRIMARY, ink=WHITE, h=50):
    pill(d, (18, y, W - 18, y + h), 999, tint, label, f("semibold", 13.5), ink)


def art(name):
    return f"{R.BASE}/assets/{name}"


# ----------------------------------------------------------------- templates
def t_list(title, sub, rows, trailing="play"):
    img, d = screen_shell("", "")
    backbar(d, title, sub)
    y = 84
    for i, (name, meta, a) in enumerate(rows):
        card(d, (18, y, W - 18, y + 70))
        thumb(img, art(a), (28, y + 10, 88, y + 60), 14)
        text(d, (100, y + 20), name, f("semibold", 13), INK)
        text(d, (100, y + 41), meta, f("regular", 10.5), INK_FAINT)
        icon_circle(d, W - 42, y + 35, 17, PRIMARY_SOFT)
        if trailing == "play":
            d.polygon([P(W - 47), P(y + 28), P(W - 47), P(y + 42), P(W - 35), P(y + 35)], fill=PRIMARY)
        else:
            chevron(d, W - 42, y + 35)
        y += 80
    return img


def t_grid(title, sub, items, cols=2, art_h=132):
    img, d = screen_shell("", "")
    backbar(d, title, sub)
    cw = (W - 36 - 14) / cols
    for i, (label, meta, a) in enumerate(items):
        col, row = i % cols, i // cols
        x = 18 + col * (cw + 14)
        y = 84 + row * (art_h + 52)
        card(d, (x, y, x + cw, y + art_h + 42))
        thumb(img, art(a), (x + 5, y + 5, x + cw - 5, y + art_h), 16)
        text(d, (x + 12, y + art_h + 12), label, f("semibold", 12), INK)
        text(d, (x + 12, y + art_h + 27), meta, f("regular", 9.5), INK_FAINT)
    return img


def t_form(title, sub, fields, cta_label, note=""):
    img, d = screen_shell("", "")
    backbar(d, title, sub)
    y = 90
    for label, value in fields:
        text(d, (20, y), label, f("medium", 10), INK_FAINT)
        card(d, (18, y + 14, W - 18, y + 66), 16)
        text(d, (34, y + 33), value, f("semibold", 13), INK)
        y += 84
    pill(d, (18, y + 8, W - 18, y + 58), 999, PRIMARY, cta_label, f("semibold", 13.5), WHITE)
    card(d, (18, y + 80, W - 18, y + 168), 20, AMBER_SOFT)
    text(d, (34, y + 96), "Funny tip", fun(15), INK)
    text(d, (34, y + 124), "Naam waisa likhein jaisa bolte hain —", f("regular", 11), INK_SOFT)
    text(d, (34, y + 142), "AI ka accent perfect aayega.", f("regular", 11), INK_SOFT)
    if note:
        text(d, (W / 2, y + 72), note, f("regular", 10.5), INK_FAINT, anchor="ma")
    return img


def t_chips(title, sub, chips, body, cta_label):
    img, d = screen_shell("", "")
    backbar(d, title, sub)
    x = 18
    for c in chips:
        w = 22 + len(c) * 6
        pill(d, (x, 82, x + w, 108), 999, PRIMARY_SOFT if c == chips[0] else WHITE,
             c, f("semibold", 10), PRIMARY_DARK if c == chips[0] else INK)
        x += w + 8
    card(d, (18, 122, W - 18, 300))
    text(d, (34, 140), body[0], f("semibold", 12.5), INK)
    text(d, (34, 162), body[1], f("regular", 11), INK_SOFT)
    text(d, (34, 262), "0 / 180", f("regular", 10), INK_FAINT)
    cta(d, 318, cta_label)
    return img


def t_hero(title, sub, a, heading, meta, actions=("Set ringtone", "Share", "Save")):
    img, d = screen_shell("", "")
    backbar(d, title, sub)
    paste_img(img, art(a), (18, 80, W - 18, 396), radius=24)
    text(d, (18, 412), heading, fun(19), INK)
    text(d, (18, 440), meta, f("regular", 11), INK_FAINT)
    # progress
    d.rounded_rectangle([P(18), P(470), P(W - 18), P(474)], radius=P(2), fill=LINE)
    d.rounded_rectangle([P(18), P(470), P(18 + (W - 36) * 0.45), P(474)], radius=P(2), fill=PRIMARY)
    text(d, (18, 482), "0:02", f("regular", 9.5), INK_FAINT)
    text(d, (W - 18, 482), "0:04", f("regular", 9.5), INK_FAINT, anchor="ra")
    icon_circle(d, W / 2, 540, 30, PRIMARY)
    d.polygon([P(W / 2 - 6), P(528), P(W / 2 - 6), P(552), P(W / 2 + 9), P(540)], fill=WHITE)
    x = 18
    for lab in actions:
        w = (W - 36 - 16) / 3
        pill(d, (x, 592, x + w, 634), 999, WHITE, lab, f("semibold", 10.5), INK)
        x += w + 8
    return img


def t_loading(title, sub, label, pct, a="ic_robot.png"):
    img, d = screen_shell("", "")
    backbar(d, title, sub)
    icon_circle(d, W / 2, 260, 62, PRIMARY_SOFT)
    paste_img(img, art(a), (W / 2 - 34, 226, W / 2 + 34, 294), radius=18, fit="contain")
    text(d, (W / 2, 350), label, fun(18), INK, anchor="ma")
    text(d, (W / 2, 380), "AI ka jadoo chal raha hai…", f("regular", 11), INK_FAINT, anchor="ma")
    d.rounded_rectangle([P(40), P(430), P(W - 40), P(438)], radius=P(4), fill=LINE)
    d.rounded_rectangle([P(40), P(430), P(40 + (W - 80) * pct), P(438)], radius=P(4), fill=PRIMARY)
    text(d, (W / 2, 452), f"{int(pct * 100)}%", f("medium", 10.5), INK_SOFT, anchor="ma")
    return img


def t_story(title, sub, a, heading, paras):
    img, d = screen_shell("", "")
    backbar(d, title, sub)
    paste_img(img, art(a), (18, 80, W - 18, 300), radius=24)
    text(d, (18, 316), heading, fun(20), INK)
    y = 350
    for p in paras:
        text(d, (18, y), p, f("regular", 11.5), INK_SOFT)
        y += 34
    pill(d, (18, 470, W - 18, 520), 999, PRIMARY_SOFT, "Read aloud", f("semibold", 13), PRIMARY_DARK)
    return img


def t_wave(title, sub, a, heading):
    img, d = screen_shell("", "")
    backbar(d, title, sub)
    paste_img(img, art(a), (18, 80, W - 18, 300), radius=24)
    text(d, (18, 316), heading, fun(19), INK)
    # waveform
    import random
    random.seed(7)
    for i in range(46):
        h = random.randint(6, 46)
        x = 20 + i * 7.5
        d.rounded_rectangle([P(x), P(400 - h / 2), P(x + 4), P(400 + h / 2)], radius=P(2),
                            fill=PRIMARY if i < 26 else LINE)
    d.line([P(20 + 26 * 7.5), P(360), P(20 + 26 * 7.5), P(440)], fill=INK, width=P(1.6))
    cta(d, 470, "Trim & save")
    return img


def t_board(title, sub, n=16, a="ic_puzzle.png"):
    img, d = screen_shell("", "")
    backbar(d, title, sub)
    icon_circle(d, W / 2, 130, 26, AMBER_SOFT)
    paste_img(img, art(a), (W / 2 - 16, 114, W / 2 + 16, 146), radius=10, fit="contain")
    size = (W - 60) / 4
    for i in range(n):
        col, row = i % 4, i // 4
        x = 30 + col * size
        y = 180 + row * size
        card(d, (x + 2, y + 2, x + size - 2, y + size - 2), 12, WHITE if i != 3 else SKY_SOFT)
        text(d, (x + size / 2, y + size / 2), str(i + 1), f("semibold", 13), INK, anchor="mm")
    cta(d, 600, "Shuffle")
    return img


# ----------------------------------------------------------------- screens
def main_flow():
    return {
        "01-home": R.home(), "02-wallpapers": R.walls(), "03-stickers": R.stickers(),
        "04-sounds": R.sounds(), "05-prank": EXTRA["prank"], "06-player": EXTRA["player"],
        "07-call": EXTRA["call"], "08-premium": EXTRA["premium"],
        "09-favourites": MORE["favourites"], "10-playlist": MORE["history"],
        "11-jokes": MORE["jokes"], "12-games": MORE["games"],
        "13-ai-text": t_chips("AI Text → Sound", "Type karein, sound banao",
                              ["Funny", "Robot", "Baby", "Boss"],
                              ("Apna text likhein", "“Bhai, party kab hai?”"), "Generate sound"),
        "14-ai-record": t_loading("AI Record Sound", "Apni awaaz, AI ka jadoo",
                                  "Awaaz process ho rahi hai", 0.62, "ic_ring.png"),
        "15-record-generate": t_form("Record & Generate", "Record karein, generate karein",
                                     [("RECORDING", "take_03.m4a · 0:07"), ("VOICE", "Robot · fast")],
                                     "Generate"),
    }


def secondary():
    return {
        "16-generate-now": t_loading("Generate Now", "AI se sound generate karein",
                                     "Sound ban raha hai", 0.78, "ic_txt_ai.png"),
        "17-add-name": t_form("Add Name", "Naam likhein, ringtone banayein",
                              [("NAME", "Ayesha"), ("PRONUNCIATION", "Aye-sha")], "Add name"),
        "18-create-name": t_form("Create Name Ringtone", "Do second me ringtone ready",
                                 [("NAME", "Ayesha"), ("VOICE", "Boss · funny")], "Create ringtone"),
        "19-save-name": t_form("Save Name Ringtone", "Ringtone save karein",
                               [("FILE", "ayesha_funny.mp3"), ("SAVE TO", "Ringtones")], "Save"),
        "20-play-name": t_hero("Play My Name", "Apna naam sun kar dekhein", "ic_ring.png",
                               "Ayesha · Boss voice", "Funny name · 0:04"),
        "21-trim-sound": t_wave("Trim Sound", "Trim karein, perfect banayein", "wall5.webp", "Funny 1"),
        "22-stories": t_grid("Funny Stories", "Stories jo hasayein",
                             [("Boss ki meeting", "12 slides", "ic_stories.png"),
                              ("Ghost whisper", "8 slides", "ic_stories.png"),
                              ("Mummy ka dare", "9 slides", "ic_stories.png"),
                              ("Robot ka crush", "7 slides", "ic_stories.png")]),
        "23-read-story": t_story("Read Story", "Kahani padhein, hasiye", "wall3.webp",
                                 "Boss ki meeting",
                                 ["Boss: “Meeting 5 baje.”", "Team: “Zoom link?”",
                                  "Boss: “Offline. Sab ready raho.”", "Team: *slack me sab gayab*"]),
        "24-show-pack": t_grid("Sticker Pack", "Pack ke stickers",
                               [("Laugh", "LOL pack", "wall1.webp"), ("Silly", "Faces", "wall2.webp"),
                                ("Party", "Confetti", "wall4.webp"), ("Ghost", "Spooky", "wall6.webp")], art_h=118),
        "25-gif": t_grid("GIF Picker", "GIF chunein",
                         [("Party", "GIF", "wall7.webp"), ("Dance", "GIF", "wall8.webp"),
                          ("Facepalm", "GIF", "wall9.webp"), ("Shrug", "GIF", "wall10.webp")]),
        "26-show-gif": t_hero("GIF Preview", "Pasand aaye to share karein", "wall9.webp",
                              "Facepalm.gif", "128 KB · loop"),
        "27-alarm": t_form("Funny Alarm", "Alarm jo hasaye",
                           [("TIME", "07:30 AM"), ("TONE", "Boss · “Uth ja bhai”")], "Set alarm"),
        "28-auto-turn": t_form("Auto Turn", "Auto turn on karein",
                               [("INTERVAL", "Every 15 min"), ("SOURCE", "Funny sounds")], "Enable"),
        "29-puzzle": t_board("Puzzle Game", "Tiles ko jagah par lagayein"),
        "30-donkey-video": t_hero("Donkey Video", "Video chale, hasi chale", "wall10.webp",
                                  "Donkey vs toy", "0:12 · funny"),
    }


def build_sheet(screens, path, title, sub, cols=5, scale=0.62):
    imgs = []
    for name, im in screens:
        w, h = int(im.width * scale), int(im.height * scale)
        imgs.append((name, im.resize((w, h), Image.LANCZOS).convert("RGBA")))
    pw, ph = imgs[0][1].size
    gap_x, gap_y, pad, cap = 26, 46, 54, 30
    rows = (len(imgs) + cols - 1) // cols
    sheet = Image.new("RGBA", (pad * 2 + pw * cols + gap_x * (cols - 1),
                               pad + 160 + (ph + cap) * rows + gap_y * max(0, rows - 1) + 30), "#F3F1EC")
    d = ImageDraw.Draw(sheet)
    d.text((pad, pad), title, font=fun(34), fill=INK)
    d.text((pad, pad + 88), sub, font=f("regular", 14), fill=INK_SOFT)
    d.rounded_rectangle([pad, pad + 122, pad + 132, pad + 128], radius=3, fill=PRIMARY)
    top = pad + 160
    for i, (label, im) in enumerate(imgs):
        col, row = i % cols, i // cols
        x = pad + col * (pw + gap_x)
        y = top + row * (ph + cap + gap_y)
        sh = Image.new("RGBA", (pw + 60, ph + 60), (0, 0, 0, 0))
        ImageDraw.Draw(sh).rounded_rectangle([30, 36, 30 + pw, 36 + ph], radius=40, fill=(20, 17, 14, 40))
        sh = sh.filter(ImageFilter.GaussianBlur(14))
        sheet.alpha_composite(sh, (x - 30, y - 16))
        d.rounded_rectangle([x, y, x + pw, y + ph], radius=26, outline="#DFD9D0", width=2)
        m = Image.new("L", (pw, ph), 0)
        ImageDraw.Draw(m).rounded_rectangle([0, 0, pw - 1, ph - 1], radius=25, fill=255)
        sheet.paste(im, (x, y), m)
        d.text((x + 2, y + ph + 8), label.upper(), font=f("semibold", 11), fill=INK_SOFT)
    sheet.convert("RGB").save(path, optimize=True)
    return sheet.size


EXTRA = {}
MORE = {}


def _load_deps():
    import render_extra as E
    import render_more as M
    EXTRA.update({"prank": E.prank(), "player": E.player(), "call": E.call(), "premium": E.premium()})
    MORE.update({"favourites": M.favourites(), "history": M.history(),
                 "jokes": M.jokes(), "games": M.games()})


if __name__ == "__main__":
    _load_deps()
    flow = main_flow()
    sec = secondary()
    for key, im in list(flow.items()) + list(sec.items()):
        im.convert("RGB").save(os.path.join(FULL, f"{key}.png"), optimize=True)
    print("wrote", len(flow) + len(sec), "screens")
    print("sheet 1", build_sheet(list(flow.items()), os.path.join(OUT, "all-screens-1.png"),
                                 "AI Funny Ringtones — v6.8 · main flow",
                                 "15 screens · same structure, same ids"))
    print("sheet 2", build_sheet(list(sec.items()), os.path.join(OUT, "all-screens-2.png"),
                                 "AI Funny Ringtones — v6.8 · secondary screens",
                                 "15 screens · new app bar: back circle + Chewy title + caption"))

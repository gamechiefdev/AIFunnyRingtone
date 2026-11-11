"""Compose the app screens inside a browser/webview window -> design-preview/shots/webview.png

Usage:  python3 design-preview/render_webview.py
"""
import os
from PIL import Image, ImageDraw, ImageFilter, ImageFont

BASE = os.path.dirname(os.path.abspath(__file__))
FONTS = os.path.join(BASE, "fonts")
SHOTS = os.path.join(BASE, "shots")
S = 2  # supersample

BG = (237, 233, 227)
INK = (20, 20, 20)
SOFT = (110, 106, 100)
LINE = (228, 223, 215)
PRIMARY = (24, 142, 90)


def f(weight, size):
    return ImageFont.truetype(os.path.join(FONTS, f"poppins_{weight}.ttf"), int(size * S))


def P(x):
    return int(round(x * S))


def rounded_shadow(canvas, box, radius, color=(20, 20, 20), blur=28, offset=18, opacity=60):
    layer = Image.new("RGBA", canvas.size, (0, 0, 0, 0))
    d = ImageDraw.Draw(layer)
    x0, y0, x1, y1 = box
    d.rounded_rectangle([P(x0), P(y0 + offset), P(x1), P(y1 + offset)],
                        radius=P(radius), fill=color + (opacity,))
    layer = layer.filter(ImageFilter.GaussianBlur(P(blur)))
    canvas.alpha_composite(layer)


def main():
    scale = 0.58
    gap = 28

    screens = [("Discover", "home.png"), ("Wallpapers", "wallpapers.png"),
               ("Stickers", "stickers.png"), ("Sounds", "sounds.png")]
    imgs = [Image.open(os.path.join(SHOTS, name)) for _, name in screens]
    ph_w = int(imgs[0].width * scale)      # logical size
    ph_h = int(imgs[0].height * scale)
    ph_w_px, ph_h_px = P(ph_w), P(ph_h)     # actual pixel size
    n = len(imgs)

    win_w = 2040
    win_h = 84 + ph_h + 96
    canvas_w = win_w + 160
    canvas_h = win_h + 200

    canvas = Image.new("RGBA", (P(canvas_w), P(canvas_h)), BG + (255,))

    wx0, wy0 = 80, 100
    wx1, wy1 = wx0 + win_w, wy0 + win_h

    rounded_shadow(canvas, (wx0, wy0, wx1, wy1), 34, blur=30, offset=22, opacity=70)
    d = ImageDraw.Draw(canvas)
    d.rounded_rectangle([P(wx0), P(wy0), P(wx1), P(wy1)], radius=P(34), fill=(255, 255, 255, 255))

    # ---- browser chrome ----
    bar_h = 84
    d.rounded_rectangle([P(wx0), P(wy0), P(wx1), P(wy0 + bar_h)], radius=P(34), fill=(251, 249, 246, 255))
    d.rectangle([P(wx0), P(wy0 + bar_h - 30), P(wx1), P(wy0 + bar_h)], fill=(251, 249, 246, 255))
    d.line([P(wx0), P(wy0 + bar_h), P(wx1), P(wy0 + bar_h)], fill=LINE, width=P(1.5))

    for i, c in enumerate([(255, 95, 86), (255, 189, 46), (39, 201, 63)]):
        cx = wx0 + 44 + i * 34
        cy = wy0 + 42
        d.ellipse([P(cx - 8), P(cy - 8), P(cx + 8), P(cy + 8)], fill=c)

    url_x0, url_y0 = wx0 + 210, wy0 + 20
    url_x1, url_y1 = url_x0 + 760, url_y0 + 44
    d.rounded_rectangle([P(url_x0), P(url_y0), P(url_x1), P(url_y1)], radius=P(22), fill=(255, 255, 255, 255),
                        outline=LINE, width=P(1.5))
    d.ellipse([P(url_x0 + 18), P(url_y0 + 15), P(url_x0 + 30), P(url_y0 + 27)], fill=PRIMARY)
    d.text((P(url_x0 + 42), P(url_y0 + 22)), "AI Funny Ringtones — v6.1 UI  ·  port 5173",
           font=f("medium", 13), fill=SOFT)

    # tab dots at right of the bar
    for i in range(3):
        cx = wx1 - 60 - i * 26
        d.rounded_rectangle([P(cx - 7), P(wy0 + 30), P(cx + 7), P(wy0 + 54)], radius=P(4), fill=LINE)

    # ---- phones ----
    total = n * ph_w + (n - 1) * gap
    start_x = wx0 + (win_w - total) // 2
    ph_y = wy0 + bar_h + 30

    for i, (label, img) in enumerate(zip([s[0] for s in screens], imgs)):
        x = start_x + i * (ph_w + gap)
        phone = img.convert("RGB").resize((ph_w_px, ph_h_px), Image.LANCZOS).convert("RGBA")
        mask = Image.new("L", (ph_w_px, ph_h_px), 0)
        ImageDraw.Draw(mask).rounded_rectangle([0, 0, ph_w_px - 1, ph_h_px - 1], radius=P(26), fill=255)
        pad = P(24)
        shadow = Image.new("RGBA", (ph_w_px + pad * 2, ph_h_px + pad * 2), (0, 0, 0, 0))
        ImageDraw.Draw(shadow).rounded_rectangle([pad, pad, pad + ph_w_px, pad + ph_h_px],
                                                 radius=P(26), fill=(20, 20, 20, 55))
        shadow = shadow.filter(ImageFilter.GaussianBlur(P(12)))
        canvas.alpha_composite(shadow, (P(x) - pad, P(ph_y) - pad + P(6)))
        canvas.paste(phone, (P(x), P(ph_y)), mask)
        d.rounded_rectangle([P(x), P(ph_y), P(x) + ph_w_px, P(ph_y) + ph_h_px], radius=P(26),
                            outline=(235, 230, 223, 255), width=P(1))
        d.text((P(x), P(ph_y + ph_h + 26)), label.upper(), font=f("semibold", 11), fill=SOFT)

    # caption under the window
    d.text((P(wx0), P(wy1 + 54)),
           "Yeh page asli browser/webview mein aisa dikhta hai  ·  sab 4 tabs live",
           font=f("medium", 13), fill=SOFT)

    out = os.path.join(SHOTS, "webview.png")
    canvas.convert("RGB").save(out, "PNG")
    print("written", out, os.path.getsize(out) // 1024, "KB", canvas.size)


if __name__ == "__main__":
    main()

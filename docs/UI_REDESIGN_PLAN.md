# UI Redesign — "Pin" Design System (Pinterest-style)

**Branch:** `arena/19197c70-aifunnyringtone`
**Scope:** UI layer only. No backend, API, Firebase, ads, billing or business-logic changes.
**App version touched:** 6.0.1 → UI refresh tagged `v6.1` in comments only (versionCode/versionName untouched).

---

## 1. Guardrails (what was deliberately NOT touched)

| Area | Files | Status |
|---|---|---|
| Billing / subscriptions | `MainActivity` billing block, `SimpleBillingManager`, `adsCondition/*` | untouched |
| Ads | `AdsController`, `AppOpenManager`, all `adView` slots, `exit_dialog_tracker` ad slot | untouched (slots kept, only styled) |
| Firebase / analytics | `google-services`, Crashlytics, `RegisterActivityEventInFb` calls | untouched |
| Network / API / data | `models/*`, `HistoryStorage`, Volley calls, `StickerPack/*`, `ContentFileParser` | untouched |
| Sticker content provider | `AndroidManifest`, `StickerContentProvider`, assets, `contents.json` | untouched |
| Navigation / intents | every `startActivity`, `putExtra`, extra keys (`sound_res`, `wallpaper`, …) | untouched |
| Business logic | adapters, fragments, activities | only layout-manager / label / indicator lines changed (listed in §5) |

Nothing was deleted: every existing view ID that Java references still exists, so **view-binding
fields and `findViewById` calls keep working**.

---

## 2. Project analysis (UI-only vs logic)

```
app/
├── assets/                      bundled imagery + sticker packs      (untouched)
├── java/.../
│   ├── MainActivity.java        shell: drawer, ViewPager, billing, ads, exit sheet
│   ├── SplashActivity / IntroActivity / MyIntroScreensActivity      (UI heavy)
│   ├── activities/              28 feature screens (sounds, walls, stickers, AI, calls…)
│   ├── fragments/               Home, PrankCall, Favourite, Histroy, Cutter, RecordSound,
│   │                            LiveSpeaker (UI + recorder logic)
│   ├── adapters/                RecyclerView presenters (UI only, data passed in)
│   ├── models/                  data classes + HistoryStorage        (logic — untouched)
│   ├── myRingtone/, DareGame/, PuzzleGame/, StickerPack/             (logic — untouched)
│   └── adsCondition/            ads + purchase layer                 (logic — untouched)
└── res/
    ├── layout/                  40 screens + item layouts            (redesigned)
    ├── values/                  colors, dimens, strings, styles       (new tokens)
    ├── drawable/                shapes, ripples, gradients            (new)
    └── font/                    Poppins (new)
```

**Rule applied:** anything that *renders* was fair game; anything that *decides* was left alone.
Data sources stay real — the new Home chips filter the existing `categoryList`, they do not inject
dummy categories.

---

## 3. The design system

### 3.1 Tokens (`values/colors.xml`)

| Token | Value | Use |
|---|---|---|
| `pin_primary` | `#188E5A` | brand (unchanged green, also kept as `purple_200`) |
| `pin_primary_dark` | `#0E6640` | text on soft green, gradients |
| `pin_primary_soft` | `#E4F3EB` | chips, soft cards, save states |
| `pin_accent` | `#FF6B4A` | coral accent |
| `pin_amber` / `pin_amber_soft` | `#F6C53A` / `#FFF4D6` | warm pins |
| `pin_lilac` / `pin_sky` / `pin_rose` | `#7C6CF6` / `#2AA7C7` / `#E5484D` | pin palette |
| `pin_bg` / `pin_surface` / `pin_surface_alt` | `#FFFFFF` / `#FBF9F6` / `#F3F0EB` | warm neutral canvas |
| `pin_line` | `#EBE6DF` | 1dp hairline strokes |
| `pin_ink` / `pin_ink_soft` / `pin_ink_faint` | `#141414` / `#6E6A64` / `#A39D95` | type ramp |
| `nav_selected` | `#188E5A` (was `#5B2F08`) | active tab |
| `nav_unselected` | `#9A948C` | idle tab |

Legacy names (`purple_200`, `grey`, `yellow`, …) are preserved so untouched screens keep compiling.

### 3.2 Type — Poppins (`res/font/`, `values/styles.xml`)

Display 26 · Title 20 · Section 17 · Card title 14 · Body 13 · Caption 11 · Eyebrow 11 (8% tracking).
The family is applied in the theme (`fontFamily`), so screens that were not explicitly restyled still
inherit the new typography.

### 3.3 Components (`values/styles.xml`, `drawable/`)

`Pin.Card`, `Pin.Card.Pin`, `Pin.Card.Flat`, `Pin.Card.Surface`, `Pin.Chip`, `Pin.Button.Primary`,
`Pin.IconButton`, `Pin.Text.*`; shapes `bg_pin_card`, `bg_pin_image`, `bg_pill_*`, `bg_btn_*`,
`bg_nav_bar`, `bg_sheet_top`, `bg_gradient_ai_text`, `bg_gradient_ai_voice`, `bg_gradient_hero`,
`scrim_bottom`, ripples (`ripple_pin`, `ripple_pill`, `ripple_light`).

### 3.4 The five "designer skills" applied

1. **Masonry rhythm** — two-column waterfall of mixed-height pins (Home explore, wallpaper grid, stickers).
2. **Soft elevation** — 20–26dp radii + 1dp `#EBE6DF` strokes, shadows reduced to 2dp or removed.
3. **Typographic scale** — one family, six sizes, tight tracking on display text, no more mixed `_sdp` sizes.
4. **Warm neutral canvas** — off-white `#FBF9F6` surfaces instead of pure white everywhere.
5. **Micro-interaction** — ripples on every card, sliding bottom-nav indicator, hover/Save affordance, animated chip states.

---

## 4. Screen-by-screen changes

### 4.1 Shell — `layout/activity_main.xml` + `MainActivity`
- Floating circular menu button, two-line app bar (`Discover` + subtitle), circular favourites button, Lottie pro badge.
- Bottom navigation rebuilt as four vertical tabs inside a rounded, elevated white bar; **new** 4dp sliding
  indicator (`@id/navIndicator`) that moves with the ViewPager.
- Drawer header restyled (`headerfile.xml`) with brand gradient, rounded app icon, tagline.
- All IDs kept: `main`, `activity_main_content`, `constraintLayout`, `menuOpen`, `fav`, `premiumOpen`,
  `viewPagerNailArt`, `bottomLayout`, `bottom`, `homeTxt/prank/dualTxt/darkTxt`, `homTxt/prankTxt/listTxt/favTxt`, `adView`.

### 4.2 Home — `layout/fragment_home.xml` (+ `Home.java`)
- Hero block: time-aware eyebrow (`heroGreeting`), display headline, supporting copy.
- **AI Studio**: two 190dp gradient pin cards (`goAItextSound`, `goRecord`) with icon chips and pill CTAs.
- **Explore**: genuine masonry — two `LinearLayout` columns of eight coloured pin cards
  (`myRingName`, `funnySticker`, `gifMemes`, `puzzleGame` / `spinGame`, `funnyStories`, `funnyJokes`, `funnyDonkey`).
- **Browse by mood**: horizontally scrolling chips (`chipAll`, `chipFunny`, `chipAnimal`, `chipLaugh`,
  `chipFart`, `chipDialogue`, `chipBaby`, `chipAlarms`) that filter the existing category RecyclerView
  (2-column grid). Every chip click keeps the original click-through payload
  (`sound_res`, `image_res`, `sound_names`).

### 4.3 Wallpapers — `activity_wallpaper_list.xml`, `fragment_favourite.xml`, `bg_layout.xml`, `wall_itemlayout.xml`
- Header + instructional subtitle, edge-to-edge staggered grid.
- Pin cards: 26dp radius, 2dp shadow, bottom scrim and a "Tap to set" badge.
- `StaggeredGridLayoutManager` + per-position height variation in `Wall_Adapter` → real waterfall.

### 4.4 Stickers — `activity_stickers_pack_list.xml`, `sticker_itemlayout.xml`
- Two-column board grid (was a plain list with dividers), thumbnails on warm surface tiles, title + file-size caption.
- WhatsApp add-to-pack flow untouched.

### 4.5 Sounds — `activity_sound_list.xml`, `sound_itemlayout.xml`, `sound_listitem.xml`
- Category pins: rounded white cards, centred artwork, 2-line label.
- Song rows: 68dp rounded cards, index, rounded artwork, title/duration, heart in a soft circle.
- Screen title now reflects the tapped category (derived from existing `sound_names`, no new data).

### 4.6 Library / favourites / prank
- `fragment_histroy.xml`: gradient hero card ("AI Playlist") + restyled rows (`history_itemlayout.xml`).
- `fragment_prank_call.xml`: section header + 3-column caller grid (same adapter, same spans).
- `activity_favorites_screen.xml`: app bar + saved-count hero + list.

### 4.7 Dialogs & splash
- `exit_dialog_tracker.xml`: rounded top sheet, handle, ad slot kept (`media_View`), **new** "Stay" button
  wired to dismiss, primary "Confirm Exit" button (`YES`) unchanged.
- `activity_splash.xml`: soft wash background, Poppins wordmark, brand-tinted progress.
- `values-night/themes.xml`: mirrors the light token set so dark-mode devices don't mix dark Material
  defaults with the hand-coloured layouts (`forceDarkAllowed=false`).

### 4.8 Batch-restyled app bars (25 layouts)
Every screen that used the old 40dp hard-edged header (`activity_funny_jokes`, `activity_gif`,
`activity_stroy_list`, `activity_aitext_sound`, `activity_ai_record_sound`, `activity_create_name_ringtone`,
`activity_select*`, `activity_puzzle*`, `activity_spin_game`, `activity_donkey_screen`, `activity_show_*`,
`activity_play_*`, …) now has: `wrap_content` header on `pin_bg`, 44dp circular back button,
18sp Poppins title in `pin_ink`, and a `pin_bg` window background. No ID, listener or padding maths changed.

---

## 5. Java changes (all UI-only, ~90 lines)

| File | Change |
|---|---|
| `MainActivity.java` | `resetIconColors()` idle colour white→`nav_unselected` (the bar is white now); new `moveNavIndicator(int)` (16 lines); "Stay" button on the exit sheet dismisses it. Billing/ads/drawer code untouched. |
| `Home.java` | `GridLayoutManager(2)`; `visibleList` + `setupChips()/selectChip()/applyFilter()`; time-aware greeting text. Category data untouched. |
| `Wall_Adapter.java` | Varies pin height by position for the staggered grid (6 lines). |
| `WallpaperList.java`, `Favourite.java` | `GridLayoutManager` → `StaggeredGridLayoutManager`. |
| `StickersPackList.java` | `LinearLayoutManager` + divider → `GridLayoutManager(this, 2)`. |
| `SoundList.java` | Sets the header title from the existing `sound_names` extra. |

No change to: API calls, request/response models, Firebase, auth, ads logic, Play Billing,
subscriptions, sticker validation, `HistoryStorage`, permissions, or any `startActivity` payload.

---

## 6. Verification performed

The sandbox has no JDK / Android SDK and only whitelisted network hosts, so a Gradle build
(`./gradlew :app:assembleDebug`) could not run here. Instead the project was verified statically with
`tools/ui_audit.py`:

```bash
python3 tools/ui_audit.py .
```

Checks: XML well-formedness of every layout/values file, every `@drawable/@color/@dimen/@string/@style/@font/@layout`
reference, duplicate IDs per layout, and — most importantly — that **every `binding.xxx` field used in
Java exists as an ID in its bound layout**, plus every `findViewById(R.id.…)`.

Result: **0 new problems** (7 remaining warnings are pre-existing and provided by libraries:
`gnt_*` templates from the `nativetemplates` module, `admob_close_button_*` from the ads SDK,
`Theme.MaterialComponents.Light`).

Start from a clean baseline (before the redesign) there were 84 reported issues caused by the naive
checker; the improved checker narrows that to the same 7 library-provided references.

**Build locally:**

```bash
./gradlew :app:assembleDebug
```

If anything fails, it will be an XML/style attribute issue, not a logic issue — send me the Gradle
output and I'll fix it in the same pass.

---

## 7. Live design preview

`design-preview/index.html` is a clickable, pixel-accurate mock of the new shell (Home, Prank,
Wallpapers, Playlist) built with the same tokens, fonts and bundled artwork, so the UI can be
reviewed without an emulator:

```bash
cd design-preview && python3 -m http.server 5173
# open http://localhost:5173
```

Interactive: bottom-nav switching with sliding indicator, mood-chip filtering, wallpaper/board pins
with Save state, favouriting in the playlist.

---

## 8. Next phase (not included)

These screens still use the previous visual language but already inherit the new font and brand colours:

- `premium_dialog.xml` (paywall) — needs a full redesign pass + copy review with billing untouched.
- `activity_my_intro_screens.xml` + `layout_intro_slider1..3.xml` (onboarding).
- `activity_funny_jokes` / `activity_stroy_list` / `activity_gif` inner content rows
  (`jokes_itemlayout`, `story_itemlayout`, `gif_sample_item`).
- `activity_selection` / `activity_spin_game` game art.
- `dialog_*` helper dialogs (add name, language selection, ring prompt, set wall).

---

## Phase 2 — baaki screens (completed)

Pehle 43/95 layouts hone ke baad baaki ko bhi Pin design system par le aaye gaye.

### Kya kiya

| Kaam | Tareeqa | Files |
|---|---|---|
| Value-level restyle (structure & ids untouched) | `tools/restyle_remaining.py` — sirf attribute values map kiye: `@color/black → @color/pin_ink`, `purple_* → pin_primary`, `#DDDDDD → pin_surface_alt`, `btn_shape* / btn_bg → bg_btn_primary`, `cardstroke / card_corners → bg_pin_card`, `edit_shape → bg_input`, card radius `10dp → pin_radius`, stroke `purple_200 → pin_line` | 46 layouts |
| Naye drawables | `bg_dialog.xml` (26dp white sheet), `bg_input.xml` (rounded field), `bg_call_scrim.xml` (green call gradient), `bg_circle_rose.xml` (end-call), `bg_pill_glass.xml` (timer pill) | 5 drawables |
| Hand-written redesign | prank tile (3-col pin card), story card, jokes card, incoming audio call screen | `prankcall_itemlayout.xml`, `story_itemlayout.xml`, `jokes_itemlayout.xml`, `activity_accept_voice_call.xml` |
| Contrast fixes | light pill par white text → `pin_ink`; tinted buttons ka tint `black → pin_primary` | 3 layouts |

### Verify

- `python3 tools/ui_audit.py .` → **7 problems**, sab ignore-listed (ad SDK templates), **0 naye**
- 287 XML files parse clean (`ElementTree`), koi malformed nahi
- Koi `android:id` change nahi → saare bindings / `findViewById` intact
- Java, gradle, AndroidManifest: **0 changes** is phase mein
- Jaan-boojh kar chhode: `ad_native_layout_*`, `layout_intro_native_ad` (ad SDK templates), `item_youtube_video` (video surface)

### Naye mockups

`design-preview/shots/ui-preview-2.png` — Prank grid, Player, Incoming prank call, Premium paywall
(`python3 design-preview/render_extra.py`)

---

## Phase 3 — 3D material + floating motion (v6.2)

Goal: professional, playful depth on every screen, and something that gently floats.

### Android

| File(s) | Kya karta hai |
|---|---|
| `res/drawable/bg_card_3d.xml`, `bg_card_3d_sm.xml`, `bg_chip_3d.xml`, `bg_gloss_white.xml` | Layered material: warm white gradient body + top gloss + hairline edge |
| `res/anim/float_soft.xml`, `float_soft_late.xml`, `rise_in.xml`, `pop_in.xml`, `layout_rise.xml` | Floating loop, entrance rise, pop-in, staggered list entrance |
| `java/.../ui/UiMotion.java` | UI-only helper: `floatView()` (translationY loop + translationZ lift), `popIn()`, `riseIn()`, `riseChildren()`, scans for `android:tag="float"` / `"float_late"` |
| `MainActivity.java`, `adsCondition/ParentActivity.java` | Ek hi hook: layout ke baad tagged views float karne lagte hain — 36+ activities aur saare fragments apne aap cover |
| `fragment_home.xml`, `activity_accept_voice_call.xml`, `premium_dialog.xml` + 13 layouts | Hero cards par `android:tag="float"`, cards par `app:cardElevation` + ripple |

Motion sirf presentation hai: koi listener, data ya business logic nahi chhua.

### Web preview

- `design-preview/index.html`: layered `box-shadow` tokens (`--lift-1..3`), gloss overlay on gradient cards, `floaty` / `floaty-late` keyframes on AI cards, tiles, pins, active tab icon; `rise` per screen, `pop` for sheets; `prefers-reduced-motion` respected.
- `design-preview/render_3d.py`: mockups ko contact shadow + gloss ke saath dobara render karta hai.

---

## Phase 4 — Pinterest / playful pass (v6.2.1)

- **Floating pill navigation** — Android: `bg_nav_pill.xml` + margins/elevation on `activity_main.xml#bottom` (indicator maths unchanged, ids intact). Web/mockups: same floating pill with active tab as a soft green capsule.
- **Sticker energy** — rotated `PRANK MODE` badge (`bg_sticker_badge.xml`, `android:rotation="6"`, `tag="float_late"`), `NEW` as a raised 3D pill, emoji section titles (`AI Studio ✨`, `Explore 🧩`) via strings.
- **Save badges on boards** — web tiles get a white heart "save" button (`::after`), mockups draw the same pin-save button; `8 boards` counter pill.
- **Warmth** — tile radius 20 → 22dp, `bg_chip_3d` raised chips, gloss retained from Phase 3.

---

## Phase 5 — "more funny" pass (v6.3)

| Change | Detail |
|---|---|
| Cartoon display type | `res/font/chewy.ttf` (rounded cartoon) — hero + all section headings (`Pin.Text.Section`, new `Pin.Text.Fun`); `res/font/luckiest_guy.ttf` — sticker lettering (`Pin.Text.Sticker`, PRANK MODE badge) |
| Candy palette | accent `#FF6B4A → #FF4D8D` (bubblegum), amber `#FFC93C`, lilac `#8B5CF6`, sky `#38BDF8` |
| Emoji stickers | Home hero par rotated comic discs (😂 🤪 🎉 👻) + 🔥 pill — `bg_sticker_emoji.xml` (white disc, 2dp ink ring), `tag="float"` / `"float_late"` |
| Mockups | Pillow renderer mein cartoon faces (colour emoji font nahi hota) + Chewy headings, taake preview bilkul app jaisa lage |
| Web preview | Chewy + Luckiest @font-face, emoji sticker row, candy variables, wahi float motion |

Energy badhane ke liye hi ye pass hai — koi functional change nahi.

---

## Phase 6 — all four "more funny" upgrades (v6.4)

| # | Feature | Detail |
|---|---|---|
| 1 | **Comic outlines + tilt** | `bg_card_comic.xml` (white + 2dp ink), home tiles/AI cards par `app:strokeWidth="2dp"` + `android:rotation` (-1.4° … +1.5°) |
| 2 | **Wobble + confetti** | `anim/wobble.xml` + `UiMotion.wobble()` (tag `"wobble"`, OnTouchListener returns false so clicks stay intact); `anim/confetti_pop.xml` + splash par 4 emoji confetti (😂 🤪 🎉 👻) tags `pop`/`float` |
| 3 | **Colour blast** | `bg_screen_gradient.xml` (cream → pink wash) home background; candy palette from v6.3 |
| 4 | **Cartoon accents** | `ic_googly_eyes.xml` vector sticker + emoji sticker row, web `👀` pill |

Motion keys are additive: `float`, `float_late`, `wobble`, `pop` — `UiMotion` tag check `contains()` karta hai, is liye "float wobble" jaise combined tags kaam karte hain.

---

## Phase 7 — "poori app funny" (v6.5)

Goal: Home ke baad **har screen** ko same comic language milay — bina kisi layout ki id ya Java logic chhere.

### 3 tarah ke global levers (ek hi jagah se poori app)

| Lever | File | Asar |
|---|---|---|
| `Pin.Text.Title` → `@font/chewy` 21sp | `values/styles.xml` | Jahan bhi screen title style use hota hai, cartoon face automatically |
| `Pin.Card` / `Pin.Card.Flat` → `strokeColor=@color/pin_ink`, `strokeWidth=2dp` | `values/styles.xml` | Har card par comic ink outline |
| `pin_bg` `#FFFFFF` → `#FFF6F8` | `values/colors.xml` | Background blush (34 layouts) |

### Screen-by-screen pass (`tools/funny_everywhere.py`, re-runnable)

Script sirf un TextViews ko Chewy deta hai jinke paas `textSize >= 15` **aur** label chhota (≤ 24 chars) ho — body copy readable rehti hai. Comment blocks mask hote hain, ids bilkul touch nahi hoti.

Touched layouts (17 + 6 = 23):

`activity_favorites_screen` · `activity_funny_jokes` · `activity_play_ai_sound` · `activity_play_sounds` · `activity_read_story` · `activity_trim_sound_screen` · `activity_selection` · `custom_dialog` · `dialog_generation_limit` · `dialog_language_selection` · `dialog_set_wall` · `fragment_favourite` · `fragment_histroy` · `fragment_prank_call` · `jokes_itemlayout` · `layout_intro_slider1` · `layout_intro_slider3` · `permissions_screen_dialog` · `premium_dialog` · `stack_itemlayout` · `voice_itemlayout`

### Preview coverage

`design-preview/render_more.py` — 4 naye mockups (Favourites, Playlist, Jokes, Games); `render_all.py` ab **12 screens** ek sheet me. `screen_shell()` me shared colour-blast wash, is liye saari screens ek jaisi lagti hain.

### Backend status
`git diff c24b909 -- '*.java'` = 9 files, sab UI-only (motion hooks + view lookups). Gradle/manifest/Firebase/ads/billing = **0 changes**.

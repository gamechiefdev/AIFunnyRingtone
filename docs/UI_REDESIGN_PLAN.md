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

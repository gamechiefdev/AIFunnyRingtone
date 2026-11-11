# Preview ↔ App parity (kya same hai, kya nahi)

Preview PNGs `design-preview/shots/*.png` **design mockups** hain — yeh app ke screenshots nahi hain.
Woh same tokens se bane hain jo `res/values/{colors,dimens,styles}.xml` me hain, is liye structure match karta hai.
Phir bhi 4 cheezein device par hamesha different dikhengi:

| Farq | Wajah | Fix possible? |
|---|---|---|
| Photos | Preview me generated cartoon art (`design-preview/assets/`), app me **API se aayi real images** | Nahi — real data chahiye |
| Text | Greeting/time (Good evening…), sound names, pack names — server/clock se | Nahi |
| Size | `sdp`/`ssp` units screen width se scale hote hain (360–430dp), preview 380dp par bana | Partial (dimens tweak) |
| Status bar | Preview me nahi, device par system status bar | Nahi (bina edge-to-edge) |

## Screen ↔ layout file map

| Preview | Real layout | Tokens |
|---|---|---|
| home.png | `fragment_home.xml` + `activity_main.xml` | hero 30sp Chewy, AI cards `_190sdp`, tiles `_126sdp`, radius 20dp, pad 18dp |
| wallpapers.png | `activity_wallpaper_list.xml` + `wall_itemlayout.xml` | StaggeredGrid 2 col, radius 20dp |
| stickers.png | `activity_stickers_pack_list.xml` + `sticker_itemlayout.xml` | art `_104sdp` |
| sounds.png | `activity_sound_list.xml` + `sound_itemlayout.xml` | row `_68sdp`, art `_46sdp`, radius 14dp |
| prank.png | `fragment_prank_call.xml` + `prankcall_itemlayout.xml` | — |
| player.png | `activity_play_sounds.xml` | art = square, `_50sdp` buttons |
| call.png | `activity_accept_voice_call.xml` / `activity_voice_call.xml` | green full-bleed |
| premium.png | `premium_dialog.xml` | plan rows 50dp |
| favourites.png | `fragment_favourite.xml` / `activity_favorites_screen.xml` | 2-col grid |
| history.png | `fragment_histroy.xml` + `history_itemlayout.xml` | list rows |
| jokes.png | `activity_funny_jokes.xml` + `jokes_itemlayout.xml` | cards |
| games.png | `activity_spin_game.xml`, `activity_puzzle_list.xml`, `activity_donkey_screen.xml` | tiles |

## Device par naya UI dekhne ke steps

1. `git checkout arena/19197c70-aifunnyringtone` (ya v6.7 zip extract karein)
2. `./gradlew clean assembleDebug`
3. Purani app **uninstall** karein (resources cache ho sakta hai)
4. Install karein — fonts (`res/font/chewy.ttf`, `luckiest_guy.ttf`) sirf clean build ke baad apply hote hain

Agar phir bhi purana UI dikhe to 99% wajah purana APK / purana zip hota hai.

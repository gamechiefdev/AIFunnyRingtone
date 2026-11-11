"""Rebuild every secondary screen's app bar into the v2 structure:
white back circle + Chewy title + caption subtitle (same ids, same click listeners)."""
import re, os

SKIP = {"premium_dialog.xml", "activity_wallpaper_preview.xml"}

SUBS = {
    "activity_add_name_list.xml": "Naam likhein, ringtone banayein",
    "activity_ai_record_sound.xml": "Apni awaaz, AI ka jadoo",
    "activity_aitext_sound.xml": "Type karein, sound banao",
    "activity_auto_turn.xml": "Auto turn on karein",
    "activity_create_name_ringtone.xml": "Do second me ringtone ready",
    "activity_donkey_screen.xml": "Tail pin karein, hasiye",
    "activity_funny_alarm.xml": "Alarm jo hasaye",
    "activity_funny_jokes.xml": "Bol ke sunayein, hasiye",
    "activity_generate_now.xml": "AI se sound generate karein",
    "activity_gif.xml": "GIF chunein",
    "activity_play_donkey_video.xml": "Video chale, hasi chale",
    "activity_play_my_name_ringtone.xml": "Apna naam sun kar dekhein",
    "activity_play_save_ringtone.xml": "Sun kar save karein",
    "activity_puzzle_list.xml": "Slide karein, solve karein",
    "activity_puzzlegame.xml": "Tiles ko jagah par lagayein",
    "activity_read_story.xml": "Kahani padhein, hasiye",
    "activity_record_generate.xml": "Record karein, generate karein",
    "activity_save_name_ringtone.xml": "Ringtone save karein",
    "activity_selection.xml": "Awaaz chunein",
    "activity_show_gif.xml": "GIF preview",
    "activity_show_pack.xml": "Pack ke stickers",
    "activity_spin_game.xml": "Spin karein, dare lein",
    "activity_stroy_list.xml": "Stories jo hasayein",
    "activity_trim_sound_screen.xml": "Trim karein, perfect banayein",
}

TPL = '''<LinearLayout
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_marginStart="@dimen/_12sdp"
            android:layout_marginEnd="@dimen/_8sdp"
            android:orientation="vertical"
            app:layout_constraintBottom_toBottomOf="@+id/imageView7"
            app:layout_constraintStart_toEndOf="@+id/imageView7"
            app:layout_constraintTop_toTopOf="@+id/imageView7"
            {end}>

            <TextView
                android:id="@+id/text"
                style="@style/Pin.Text.Title"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:ellipsize="end"
                android:maxLines="1"
                android:text="{title}" />

            <TextView
                style="@style/Pin.Text.Caption"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_marginTop="1dp"
                android:text="{sub}" />
        </LinearLayout>'''

done = []
for name, sub in SUBS.items():
    path = os.path.join("app/src/main/res/layout", name)
    s = open(path).read()
    m = re.search(r'<TextView\b[^>]*?android:id="@\+id/text"[^>]*?/>', s, re.S)
    if not m:
        print("skip (no header text):", name)
        continue
    block = m.group(0)
    title = re.search(r'android:text="([^"]*)"', block)
    title = title.group(1) if title else ""
    endm = re.search(r'app:layout_constraintEnd_toStartOf="([^"]+)"', block)
    end = (f'app:layout_constraintEnd_toStartOf="{endm.group(1)}"' if endm
           else 'app:layout_constraintEnd_toEndOf="parent"')
    s = s[:m.start()] + TPL.format(end=end, title=title, sub=sub) + s[m.end():]

    # app bar band goes transparent so the screen wash shows
    s = re.sub(r'(<androidx\.constraintlayout\.widget\.ConstraintLayout\s+android:id="@\+id/linearLayout00"[^>]*?)android:background="@color/pin_bg"',
               r'\1android:background="@android:color/transparent"', s, flags=re.S)
    open(path, "w").write(s)
    done.append(name)

print("restructured", len(done), "app bars")

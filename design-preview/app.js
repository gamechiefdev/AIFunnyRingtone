/* ------------------------------------------------------------------
   Preview interactions — mirrors the behaviour of the Android build.
   ------------------------------------------------------------------ */

const moods = [
  { key: 'all', label: 'All' },
  { key: 'funny', label: 'Funny' },
  { key: 'animal', label: 'Animal' },
  { key: 'laugh', label: 'Laugh' },
  { key: 'fart', label: 'Fart' },
  { key: 'dialogue', label: 'Dialogue' },
  { key: 'baby', label: 'Baby' },
  { key: 'alarms', label: 'Alarms' }
];

const categories = [
  { key: 'funny', label: 'Funny Sounds', art: 'assets/three.png' },
  { key: 'animal', label: 'Animal Sounds', art: 'assets/four.png' },
  { key: 'laugh', label: 'Laugh Sounds', art: 'assets/five.png' },
  { key: 'fart', label: 'Fart Sounds', art: 'assets/six.png' },
  { key: 'dialogue', label: 'Dialogue Sounds', art: 'assets/seven.png' },
  { key: 'baby', label: 'Baby Sounds', art: 'assets/eight.png' },
  { key: 'alarms', label: 'Funny Alarms', art: 'assets/ic_alarm.png' }
];

const walls = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10];
const callers = ['Boss', 'Ex Crush', 'Mom', 'Ronaldo', 'Alien', 'Ghost', 'Police', 'Robot', 'Donkey'];
const sounds = [
  { idx: '01', name: 'Funny 1', meta: '0:04 · Funny', art: 'assets/wall1.webp' },
  { idx: '02', name: 'Dialogue 7', meta: '0:09 · Dialogue', art: 'assets/wall4.webp' },
  { idx: '03', name: 'Fart 12', meta: '0:02 · Fart', art: 'assets/wall6.webp' },
  { idx: '04', name: 'Animal 3', meta: '0:06 · Animal', art: 'assets/wall8.webp' }
];

const el = (tag, cls) => {
  const node = document.createElement(tag);
  if (cls) node.className = cls;
  return node;
};

/* ----------------------------- mood chips ----------------------------- */
const chipRow = document.getElementById('moodChips');
let activeMood = 'all';

moods.forEach((mood, i) => {
  const chip = el('button', 'chip' + (i === 0 ? ' is-selected' : ''));
  chip.textContent = mood.label;
  chip.dataset.key = mood.key;
  chip.addEventListener('click', () => {
    activeMood = mood.key;
    chipRow.querySelectorAll('.chip').forEach((c) => c.classList.toggle('is-selected', c === chip));
    renderPins();
  });
  chipRow.appendChild(chip);
});

/* ----------------------------- category pins ----------------------------- */
const pinGrid = document.getElementById('categoryPins');

function renderPins() {
  pinGrid.innerHTML = '';
  const list = categories.filter((c) => activeMood === 'all' || c.key === activeMood);
  const cols = [el('div', 'col'), el('div', 'col')];
  list.forEach((cat, i) => {
    const pin = el('button', 'pin');
    pin.innerHTML = `
      <img class="thumb" src="${cat.art}" alt="" />
      <span class="label">${cat.label}</span>
      <span class="save">Save</span>`;
    pin.querySelector('.save').addEventListener('click', (event) => {
      event.stopPropagation();
      const save = event.currentTarget;
      save.textContent = save.textContent === 'Save' ? 'Saved' : 'Save';
      save.style.background = save.textContent === 'Saved' ? '#0E6640' : '#188E5A';
    });
    cols[i % 2].appendChild(pin);
  });
  pinGrid.append(cols[0], cols[1]);
}
renderPins();

/* ----------------------------- wallpaper pins ----------------------------- */
const wallGrid = document.getElementById('wallPins');
const wallCols = [el('div', 'col'), el('div', 'col')];
walls.forEach((n, i) => {
  const pin = el('button', 'pin');
  pin.innerHTML = `
    <img class="thumb" src="assets/wall${n}.webp" alt="" />
    <span class="shade"></span>
    <span class="tag">Tap to set</span>
    <span class="save">Save</span>`;
  wallCols[i % 2].appendChild(pin);
});
wallGrid.append(wallCols[0], wallCols[1]);

/* ----------------------------- prank callers ----------------------------- */
const prankGrid = document.getElementById('prankGrid');
callers.forEach((name, i) => {
  const card = el('button', 'caller');
  card.innerHTML = `<img class="face" src="assets/wall${(i % 10) + 1}.webp" alt="" /><b>${name}</b>`;
  prankGrid.appendChild(card);
});

/* ----------------------------- playlist ----------------------------- */
const rows = document.getElementById('soundRows');
sounds.forEach((sound) => {
  const row = el('div', 'sound-row');
  row.innerHTML = `
    <span class="idx">${sound.idx}</span>
    <img class="cover" src="${sound.art}" alt="" />
    <span class="meta"><b>${sound.name}</b><span>${sound.meta}</span></span>
    <button class="heart" aria-label="Save">♥</button>`;
  const heart = row.querySelector('.heart');
  heart.addEventListener('click', (event) => {
    event.stopPropagation();
    heart.classList.toggle('is-liked');
  });
  rows.appendChild(row);
});

/* ----------------------------- bottom nav ----------------------------- */
const tabs = Array.from(document.querySelectorAll('.tab'));
const screens = Array.from(document.querySelectorAll('.screen'));
const indicator = document.getElementById('navIndicator');

function moveIndicator(index) {
  const width = indicator.parentElement.clientWidth / 4;
  const x = index * width + (width - indicator.offsetWidth) / 2;
  indicator.style.transform = `translateX(${x}px)`;
}

tabs.forEach((tab, index) => {
  tab.addEventListener('click', () => {
    tabs.forEach((t) => t.classList.toggle('is-active', t === tab));
    screens.forEach((s) => s.classList.toggle('is-active', s.dataset.screen === tab.dataset.target));
    moveIndicator(index);
  });
});

window.addEventListener('resize', () => {
  const index = tabs.findIndex((t) => t.classList.contains('is-active'));
  moveIndicator(Math.max(index, 0));
});
requestAnimationFrame(() => moveIndicator(0));

/* ==================================================================
   FULL SCREEN GALLERY (static render of every redesigned screen)
   ================================================================== */
const galleryData = {
  callers: ['Boss', 'Ex Crush', 'Mom', 'Ronaldo', 'Alien', 'Ghost', 'Police', 'Robot', 'Donkey'],
  packs: [
    { name: 'Funny Faces', size: '412 KB', art: 'assets/three.png' },
    { name: 'Animals', size: '688 KB', art: 'assets/four.png' },
    { name: 'Laugh Pack', size: '523 KB', art: 'assets/five.png' },
    { name: 'Fart Pack', size: '301 KB', art: 'assets/six.png' },
    { name: 'Dialogues', size: '744 KB', art: 'assets/seven.png' },
    { name: 'Baby Talk', size: '918 KB', art: 'assets/eight.png' }
  ],
  jokes: [
    { title: 'The Wi-Fi password', text: '“It’s the dog’s birthday, all lowercase, no spaces.” I have never wanted to leave a house faster.', tags: ['Relatable', '0:18'] },
    { title: 'Monday, again', text: 'My bed and I have a very healthy relationship. It’s the alarm clock I can’t stand.', tags: ['Work', '0:11'] },
    { title: 'Gym membership', text: 'I signed up in January. It’s now November and the only thing I’ve lifted is my phone.', tags: ['Fitness', '0:22'] },
    { title: 'Diet starts tomorrow', text: 'Today I ate a salad. It was between two buns, but still — a salad.', tags: ['Food', '0:15'] }
  ],
  stories: [
    { title: 'The ghost in the group chat', meta: '4 min read · Spooky', art: 'assets/wall3.webp' },
    { title: 'My cat runs a business', meta: '3 min read · Wholesome', art: 'assets/wall7.webp' },
    { title: 'Interview at 3 AM', meta: '5 min read · Chaos', art: 'assets/wall2.webp' },
    { title: 'The last slice', meta: '2 min read · Family', art: 'assets/wall9.webp' }
  ],
  sounds: ['Funny 1', 'Animal 4', 'Laugh 9', 'Dialogue 7', 'Fart 12'],
  history: ['Pirate pizza', 'Robot laugh', 'Name ringtone · Sara', 'Cave echo'],
  favs: ['Funny 3', 'Baby 7', 'Dialogue 2']
};

const fill = (id, html) => { const node = document.getElementById(id); if (node) node.innerHTML = html; };

/* prank grid */
fill('g-prankGrid', galleryData.callers.map((name, i) =>
  `<button class="caller"><img class="face" src="assets/wall${(i % 10) + 1}.webp" alt="" /><b>${name}</b></button>`
).join(''));

/* wallpapers */
const galleryWalls = [1, 4, 5, 7, 8, 10];
fill('g-wallPins',
  `<div class="col">${galleryWalls.filter((_, i) => i % 2 === 0).map((n) =>
    `<div class="pin"><img class="thumb" src="assets/wall${n}.webp" alt="" /><span class="shade"></span><span class="tag">Tap to set</span></div>`).join('')}</div>
   <div class="col">${galleryWalls.filter((_, i) => i % 2 === 1).map((n) =>
    `<div class="pin"><img class="thumb" src="assets/wall${n}.webp" alt="" /><span class="shade"></span><span class="tag">Tap to set</span></div>`).join('')}</div>`
);

/* sticker packs */
fill('g-stickerPacks',
  `<div class="pack-grid">${galleryData.packs.map((pack) =>
    `<div class="pack"><div class="tile"><img src="${pack.art}" alt="" /></div><b>${pack.name}</b><span>${pack.size}</span></div>`
  ).join('')}</div>`);

/* sound rows */
const rowMarkup = (name, i, art) => `
  <div class="sound-row">
    <span class="idx">${String(i + 1).padStart(2, '0')}</span>
    <img class="cover" src="${art}" alt="" />
    <span class="meta"><b>${name}</b><span>0:0${(i % 8) + 2} · saved</span></span>
    <button class="heart${i % 3 === 0 ? ' is-liked' : ''}">♥</button>
  </div>`;

fill('g-soundRows', galleryData.sounds.map((name, i) => rowMarkup(name, i, `assets/wall${(i % 9) + 1}.webp`)).join(''));
fill('g-history', galleryData.history.map((name, i) => rowMarkup(name, i, `assets/wall${(i % 9) + 2}.webp`)).join(''));
fill('g-favs', galleryData.favs.map((name, i) => rowMarkup(name, i, `assets/wall${(i % 9) + 3}.webp`)).join(''));

/* jokes */
fill('g-jokes', galleryData.jokes.map((joke) => `
  <div class="m-joke">
    <b>${joke.title}</b>
    <p>${joke.text}</p>
    <div class="m-tags">${joke.tags.map((t) => `<span>${t}</span>`).join('')}</div>
  </div>`).join(''));

/* stories */
fill('g-stories', galleryData.stories.map((story) => `
  <div class="m-story">
    <img src="${story.art}" alt="" />
    <div><b>${story.title}</b><span>${story.meta}</span></div>
  </div>`).join(''));

/* gifs */
fill('g-gifs', `<div class="m-gifs">${[1, 2, 5, 6, 8, 9].map((n) => `
  <figure><img src="assets/wall${n}.webp" alt="" /><figcaption>GIF</figcaption></figure>`).join('')}</div>`);

/* waveform */
fill('g-wave', Array.from({ length: 26 }, () =>
  `<i style="height:${10 + Math.round(Math.random() * 38)}px"></i>`).join(''));

/* gallery filters */
const filterRow = document.getElementById('galleryFilters');
if (filterRow) {
  filterRow.addEventListener('click', (event) => {
    const button = event.target.closest('.chip');
    if (!button) return;
    filterRow.querySelectorAll('.chip').forEach((chip) => chip.classList.toggle('is-selected', chip === button));
    const filter = button.dataset.filter;
    document.querySelectorAll('.shot').forEach((shot) => {
      shot.style.display = filter === 'all' || shot.dataset.cat === filter ? '' : 'none';
    });
  });
}

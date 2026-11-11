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

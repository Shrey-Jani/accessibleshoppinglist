🌟 Accessible Shopping List

A smarter, kinder way to organize your shopping — built for everyone.

Accessible Shopping List isn’t just another to-do app.
It’s a small, thoughtful project designed so anyone—with any device, any ability, and any level of tech comfort—can create, manage, and enjoy their shopping lists.

We kept one mission in mind:

Accessibility is not a feature. It’s the default.
🛒 What You Can Do

Create, edit, and delete items in seconds

Mark items as bought or unbought

Persist your list locally (e.g., localStorage)

Navigate the entire app using only a keyboard

Experience a UI built with clean semantic HTML + ARIA best practices

Enjoy a high-contrast, screen-reader-friendly environment

Use it comfortably on phones, laptops, or large screens

Simple enough for anyone.
Powerful enough to feel great.

♿ Accessibility by Design

Every interaction in this app was shaped by real accessibility principles:

✔ Semantic HTML

Headings, lists, buttons, forms — all used meaningfully so screen readers deliver the right context.

✔ Smart ARIA Usage

aria-live regions announce changes like
“Item added” → without interrupting user flow.

✔ Strong Visual Contrast

Readable in bright sun, low light, and for low-vision users.

✔ Keyboard-First Navigation

Tab to move, Enter/Space to activate, Esc to exit.
No mouse required.

✔ Respectful Focus Management

Modals and inline edits automatically direct focus where it belongs.

✔ Fully Responsive

Works beautifully on desktop, mobile, or tablets.

If you spot something that could be more inclusive – your feedback is not only welcome, it’s encouraged.

🧩 Tech Stack

This README assumes the project is running on a lightweight frontend stack such as:

JavaScript framework (React, Vue, or plain JS)

Node.js (≥14)

npm or Yarn

Optional: Docker

Update commands if your environment differs.

🚀 Getting Started
1. Clone the repo
git clone https://github.com/Shrey-Jani/accessibleshoppinglist.git
cd accessibleshoppinglist

2. Install dependencies
npm install
# or
yarn install

3. Run it locally
npm start
# or
yarn start


The app usually runs at http://localhost:3000
.

4. Build for production
npm run build
# or
yarn build

🎯 How to Use

Type an item → press Enter → it appears instantly.

Navigate with Tab/Shift+Tab.

Mark items as bought/unbought with keyboard or click.

Edit or delete using clear accessible controls.

Want to test accessibility?

Try:

Running a screen reader (VoiceOver, NVDA, TalkBack)

Navigating entirely without a mouse

Checking for color contrast issues

Testing with reduced motion preferences

💻 Development Workflow

Create a branch:

git checkout -b feat/my-feature


Make your updates.

Commit with a clear, descriptive message:

git commit -m "feat: improve aria-live behavior for item updates"


Open a pull request and describe:

What changed

Why it matters

Any accessibility improvements

Linting & Formatting

Run or add linting tools (ESLint, Prettier) as needed:

npm run lint

🧪 Testing

If tests are included:

npm test
# or
yarn test


Consider accessibility tests using:

axe-core

jest-axe

React Testing Library a11y queries

🤝 Contributing

Contributions are genuinely appreciated.

If submitting an accessibility enhancement, include:

The issue or barrier

How you fixed it

Assistive tech or platform you tested on

Every improvement helps make the web more inclusive.

📄 License

MIT License (or update if your project uses another license).

📬 Contact

Maintainer: Shrey-Jani

If you have questions, ideas, or want help improving accessibility, open an issue or reach out via GitHub Discussions.

Thanks for helping build a more inclusive digital world 🌍💙

# FAKENEWS-ABM Web App

This folder contains the backend-free TypeScript/browser implementation.

```sh
npm install
npm run dev
npm run build
```

- `src/`: TypeScript simulation port, Excel loader, reporting, charts, and UI.
- `public/`: static assets copied into the web build.
- `public/examples/`: bundled Excel examples available from the UI.
- `dist/`: generated static site output for GitHub Pages.

The Java implementation in the repository root remains the reference model. If the Java simulation core changes, mirror the same behavioral change in `web/src/main.ts`.

// Локальный Swagger UI для docs/*.yaml: `npm run swagger` → http://localhost:8765
// Список спек строится из docs/*.yaml при каждой загрузке страницы,
// при сохранении файлов в docs/ страница перезагружается сама.
const fs = require("fs");
const path = require("path");

const DOCS = path.join(__dirname, "docs");

function specs() {
  return fs.readdirSync(DOCS)
    .filter((f) => /\.ya?ml$/.test(f))
    .sort()
    .map((f) => {
      const title = fs.readFileSync(path.join(DOCS, f), "utf8").match(/^\s+title:\s*(.+)$/m);
      return { url: `./${f}`, name: title ? title[1].trim() : f };
    });
}

function initializer() {
  return `window.onload = function () {
  window.ui = SwaggerUIBundle({
    urls: ${JSON.stringify(specs(), null, 2)},
    dom_id: "#swagger-ui",
    deepLinking: true,
    presets: [SwaggerUIBundle.presets.apis, SwaggerUIStandalonePreset],
    plugins: [SwaggerUIBundle.plugins.DownloadUrl],
    layout: "StandaloneLayout",
  });
};
`;
}

module.exports = {
  server: { baseDir: ["docs", "node_modules/swagger-ui-dist"] },
  middleware: [
    {
      route: "/swagger-initializer.js",
      handle: (req, res) => {
        res.setHeader("Content-Type", "application/javascript; charset=utf-8");
        res.setHeader("Cache-Control", "no-store");
        res.end(initializer());
      },
    },
  ],
  files: ["docs/*.yaml", "docs/*.yml"],
  port: 8765,
  open: false,
  notify: false,
  ui: false,
};

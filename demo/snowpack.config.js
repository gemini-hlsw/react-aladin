module.exports = {
  devOptions: {
    open: "none",
    port: 3000
  },
  plugins: [
    [
      "@snowpack/plugin-run-script",
      {
        cmd:
          "lessc --include-path=node_modules ./src/main/resources/less/style.less public/style.css --source-map-inline", // production build command
        watch:
          "node node_modules/less-watch-compiler --include-path=node_modules ./src/main/resources/less/ public/ style.less --source-map-inline",
      },
    ],
    ['@snowpack/plugin-react-refresh']
  ],
  alias: {
    // "@resources": "./src/main/resources/",
    // "vendor": "./src/main/resources/less/vendor/",
    // "js": "./src/js",
    // "css": "./src/css",
    "@sjs": "./target/scala-2.13/demo-fastopt"
  },
  mount: {
    "public": "/",
    // src: {url: '/dist'},
    "./target/scala-2.13/demo-fastopt": "/sjs"
  }
}

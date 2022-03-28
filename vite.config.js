import react from "@vitejs/plugin-react";
import path from "path";
import ViteFonts from 'vite-plugin-fonts'
import mkcert from 'vite-plugin-mkcert';
import { VitePWA } from "vite-plugin-pwa"

const getCache = ({ name, pattern }) => ({
  urlPattern: pattern,
  handler: "CacheFirst",
  options: {
    cacheName: name,
    expiration: {
      maxEntries: 500,
      maxAgeSeconds: 60 * 60 * 24 // 1day
    },
    cacheableResponse: {
      statuses: [200]
    }
  }
});

export default ({ command, mode }) => {
  const scalaClassesDir = path.resolve(__dirname, "demo/target/scala-2.13");
  const sjs =
    mode == "production"
      ? path.resolve(scalaClassesDir, "demo-opt")
      : path.resolve(scalaClassesDir, "demo-fastopt");
  const common = path.resolve(__dirname, "common/");
  const webappCommon = path.resolve(common, "src/main/webapp/");
  const imagesCommon = path.resolve(webappCommon, "images");
  const themeConfig = path.resolve(webappCommon, "theme/theme.config");
  const themeSite = path.resolve(webappCommon, "theme");
  const suithemes = path.resolve(webappCommon, "suithemes");
  const publicDirProd = path.resolve(common, "src/main/public");
  const publicDirDev = path.resolve(common, "src/main/publicdev");
  const publicDir =
    mode == "production"
     ? publicDirProd
     : publicDirDev
  return {
    root: "demo/src/main/webapp",
    publicDir: publicDir,
    resolve: {
      alias: [
        {
          find: "@sjs",
          replacement: sjs,
        },
        {
          find: "/common",
          replacement: webappCommon,
        },
        {
          find: "/images",
          replacement: imagesCommon,
        },
        {
          find: "../../theme.config",
          replacement: themeConfig,
        },
        {
          find: "theme/site",
          replacement: themeSite,
        },
        {
          find: "suithemes",
          replacement: suithemes,
        },
      ],
    },
    server: {
      strictPort: true,
      port: 9090,
      https: true,
      watch: {
        ignored: [
          function ignoreThisPath(_path) {
            const sjsIgnored =
              _path.includes("/target/stream") ||
              _path.includes("/zinc/") ||
              _path.includes("/classes");
            return sjsIgnored;
          },
        ],
      },
    },
    build: {
      sourcemap: true,
      minify: false,
      terserOptions: {
        sourceMap: false,
      },
      outDir: path.resolve(__dirname, "static"),
    },
    plugins: [react(),
      mkcert({ hosts: ['localhost', 'local.lucuma.xyz'] }),
      VitePWA({
        devOptions: {
          enabled: true
        },
        workbox: {
          runtimeCaching: [
            getCache({
              pattern: /^https:\/\/alasky.u-strasbg.fr\/DSS/, 
              name: 'aladin-images'
            }),
            getCache({
              pattern: /^https:\/\/lucuma-cors.proxy.herokuapp.com\/https:\/\/gea.esac.esa.int/, 
              name: 'gaia-results'
            })
          ]
        }
      }),
      ViteFonts({
        google: {
          families: ['Open Sans']
        },
      })],
  };
};

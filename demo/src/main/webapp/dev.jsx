import "/css/aladin.css";
import "/css/style.css";
// import 'virtual:fonts.css';

// Setup the service worker
import { registerSW } from "virtual:pwa-register";

if (navigator.serviceWorker) {
  // && !/localhost/.test(window.location)) {
    navigator.storage.estimate().then(r => console.log(r));
  const updateSW = registerSW({
    mode: 'development',
    immediate: true,
    onRegisterError(error) {console.log(error);},
    onRegistered(reg) {console.log(reg);}
  })
}

import { Main } from "@sjs/main.js";

// if (import.meta.env.DEV) {
//   process.env = { CATS_EFFECT_TRACING_MODE: "none" };
// }

Main.runIOApp();

  if (import.meta.hot) {
    import.meta.hot.accept();
    import.meta.hot.dispose((_) => {
      // Reset the IO runtime
      Main.resetIOApp();
    });
  }

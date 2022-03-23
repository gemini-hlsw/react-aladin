import "/css/aladin.css";
import "/css/style.css";
import 'virtual:fonts.css';

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

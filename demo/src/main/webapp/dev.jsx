import "/css/aladin.css";
import "/css/style.css";

import { Main } from "@sjs/main.js";
Main.main();

if (import.meta.hot) {
  import.meta.hot.accept();
}


import "/css/aladin.css";
import "/style.css";

import { Main } from "./sjs/main.js";
console.log(Main)
Main.main();

if (import.meta.hot) {
  import.meta.hot.accept();
}


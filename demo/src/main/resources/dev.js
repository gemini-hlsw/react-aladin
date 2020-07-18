import "./aladin.css";
import "./less/style.less";

import App from "sjs/demo-fastopt.js";
// import React from "react";

// Enable why did you update plugin
// if (process.env.NODE_ENV !== "production") {
//   const { whyDidYouUpdate } = require("why-did-you-update");
//   whyDidYouUpdate(React, {
//     exclude: ["Draggable", "DraggableCore", "AutoSizer", "SizeMeReferenceWrapper", "SizeMeRenderer(Component)"]
//   });
// }
//
if (module.hot) {
  module.hot.accept();
  App.Main.main();
}

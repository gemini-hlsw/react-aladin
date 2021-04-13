import Overlay from "./Overlay";
import MOC from "./MOC";
import URLBuilder from "./URLBuilder";
import Coo from "./coo";
import Circle from "./Circle";
import Polyline from "./Polyline";
import Footprint from "./Footprint";
import { HpxImageSurvey } from "./HpxImageSurvey";
import ProgressiveCat from "./ProgressiveCat";
import Catalog from "./Catalog";
import Aladin from "./Aladin";
import Source from "./Source";

// API
export const footprintsFromSTCS = function (stcs) {
  var footprints = Overlay.parseSTCS(stcs);

  return footprints;
};

// API
export const MOCFromURL = function (url, options, successCallback) {
  var moc = new MOC(options);
  moc.dataFromFITSURL(url, successCallback);

  return moc;
};
//
// @API
export const catalog = function (options) {
  return new Catalog(options);
};

// API
export const MOCFromJSON = function (jsonMOC, options) {
  var moc = new MOC(options);
  moc.dataFromJSON(jsonMOC);

  return moc;
};
// TODO: try first without proxy, and then with, if param useProxy not set
// API
export const catalogFromURL = function (url, options, successCallback, useProxy) {
  var cat = catalog(options);
  // TODO: should be self-contained in Catalog class
  Catalog.parseVOTable(
    url,
    function (sources) {
      cat.addSources(sources);
      if (successCallback) {
        successCallback(sources);
      }
    },
    cat.maxNbSources,
    useProxy,
    cat.raField,
    cat.decField
  );

  return cat;
};

// API
// @param target: can be either a string representing a position or an object name, or can be an object with keys 'ra' and 'dec' (values being in decimal degrees)
export const catalogFromSimbad = function (target, radius, options, successCallback) {
  options = options || {};
  if (!("name" in options)) {
    options["name"] = "Simbad";
  }
  var url = URLBuilder.buildSimbadCSURL(target, radius);
  return catalogFromURL(url, options, successCallback, false);
};

// API
export const catalogFromNED = function (target, radius, options, successCallback) {
  options = options || {};
  if (!("name" in options)) {
    options["name"] = "NED";
  }
  var url;
  if (target && typeof target === "object") {
    if ("ra" in target && "dec" in target) {
      url = URLBuilder.buildNEDPositionCSURL(target.ra, target.dec, radius);
    }
  } else {
    var isObjectName = /[a-zA-Z]/.test(target);
    if (isObjectName) {
      url = URLBuilder.buildNEDObjectCSURL(target, radius);
    } else {
      var coo = new Coo();
      coo.parse(target);
      url = URLBuilder.buildNEDPositionCSURL(coo.lon, coo.lat, radius);
    }
  }

  return catalogFromURL(url, options, successCallback);
};

// API
export const catalogFromVizieR = function (
  vizCatId,
  target,
  radius,
  options,
  successCallback
) {
  options = options || {};
  if (!("name" in options)) {
    options["name"] = "VizieR:" + vizCatId;
  }
  var url = URLBuilder.buildVizieRCSURL(vizCatId, target, radius, options);

  return catalogFromURL(url, options, successCallback, false);
};

// API
export const catalogFromSkyBot = function (
  ra,
  dec,
  radius,
  epoch,
  queryOptions,
  options,
  successCallback
) {
  queryOptions = queryOptions || {};
  options = options || {};
  if (!("name" in options)) {
    options["name"] = "SkyBot";
  }
  var url = URLBuilder.buildSkyBotCSURL(ra, dec, radius, epoch, queryOptions);
  return catalogFromURL(url, options, successCallback, false);
};
//@API
export const aladin = (divSelector, options) => {
  const divs = document.querySelectorAll(divSelector);
  if (divs.length > 0) {
    return new Aladin(document.querySelectorAll(divSelector)[0], options);
  } else {
    const newDiv = document.createElement("div");
    // and give it some content
    const newContent = document.createTextNode(
      `Not found items with selector ${divSelector}`
    );
    // add the text node to the newly created div
    newDiv.appendChild(newContent);
    return newDiv;
  }
};

//@API
// TODO : lecture de properties
export const imageLayer = function (id, name, rootUrl, options) {
  return new HpxImageSurvey(id, name, rootUrl, null, null, options);
};

// @API
export const source = function (ra, dec, data, options) {
  return new Source(ra, dec, data, options);
};

// @API
export const marker = function (ra, dec,  data, options) {
  options = options || {};
  options["marker"] = true;
  return source(ra, dec, data, options);
};

// @API
export const polygon = function (raDecArray) {
  var l = raDecArray.length;
  if (l > 0) {
    // close the polygon if needed
    if (
      raDecArray[0][0] !== raDecArray[l - 1][0] ||
      raDecArray[0][1] !== raDecArray[l - 1][1]
    ) {
      raDecArray.push([raDecArray[0][0], raDecArray[0][1]]);
    }
  }
  return new Footprint(raDecArray);
};

//@API
export const polyline = function (raDecArray, options) {
  return new Polyline(raDecArray, options);
};

// @API
export const circle = function (ra, dec, radiusDeg, options) {
  return new Circle([ra, dec], radiusDeg, options);
};

// @API
export const graphicOverlay = function (options) {
  return new Overlay(options);
};

// @API
export const catalogHiPS = function (rootURL, options) {
  return new ProgressiveCat(rootURL, null, null, options);
};


// Copyright 2018 - UDS/CNRS
// The Aladin Lite program is distributed under the terms
// of the GNU General Public License version 3.
//
// This file is part of Aladin Lite.
//
//    Aladin Lite is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, version 3 of the License.
//
//    Aladin Lite is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    The GNU General Public License is available in COPYING file
//    along with Aladin Lite.
//



/******************************************************************************
 * Aladin Lite project
 *
 * File SimbadPointer.js
 *
 * The SIMBAD pointer will query Simbad for a given position and radius and
 * return information on the object with
 *
 * Author: Thomas Boch [CDS]
 *
 *****************************************************************************/
import Utils from './Utils';
import Coo from './coo';

const SimbadPointer = (function() {


    const SimbadPointer = {};

    SimbadPointer.MIRRORS = ['https://alasky.u-strasbg.fr/cgi/simbad-flat/simbad-quick.py', 'https://alaskybis.u-strasbg.fr/cgi/simbad-flat/simbad-quick.py']; // list of base URL for Simbad pointer service


    SimbadPointer.query = function(ra, dec, radiusDegrees, aladinInstance) {
        var coo = new Coo(ra, dec, 7);
        var params = {Ident: coo.format('s/'), SR: radiusDegrees}
        var successCallback = function(result) {
            aladinInstance.view.setCursor('pointer');

            var regexp = /(.*?)\/(.*?)\((.*?),(.*?)\)/g;
            var match = regexp.exec(result);
            if (match) {
                var objCoo = new Coo();
                objCoo.parse(match[1]);
                var objName = match[2];
                const title = document.createElement("div");
                title.classList.add("aladin-sp-title");
                const link = document.createElement("a");
                link.setAttribute("href", `http://simbad.u-strasbg.fr/simbad/sim-id?Ident=${encodeURIComponent(objName)}`);
                link.setAttribute("target", "_blank");
                title.appendChild(link);

                const content = document.createElement("div");
                content.classList.add("aladin-sp-content");
                // var content = '<div class="aladin-sp-content">';
                const em = document.createElement("em");
                em.textContent = `Type: ${match[4]}`;
                em.appendChild(document.createElement("br"));
                content.appendChild(em);

                let magnitude = match[3];
                if (Utils.isNumber(magnitude)) {
                    const magContent = document.createElement("em");
                    magContent.textContnt = `Mag: $magnitude`;
                    magContent.appendChild(document.createElement("br"));
                    content.appendChild(magContent);
                }
                const cdsLink = document.createElement("a");
                cdsLink.setAttribute("href", `http://cdsportal.u-strasbg.fr/?target=${encodeURIComponent(objName)}`);
                cdsLink.textContent = "Query in CDS portal";
                cdsLink.setAttribute("target", "_blank");
                content.appendChild(cdsLink);
                aladinInstance.showPopup(objCoo.lon, objCoo.lat, title, content);
            }
            else {
                aladinInstance.hidePopup();
            }
        };

        var failureCallback = function() {
            aladinInstance.view.setCursor('pointer');
            aladinInstance.hidePopup();
        };
        Utils.loadFromMirrors(SimbadPointer.MIRRORS, {data: params, onSuccess: successCallback, onFailure: failureCallback, timeout: 5});

    };

    return SimbadPointer;
})();

export default SimbadPointer;

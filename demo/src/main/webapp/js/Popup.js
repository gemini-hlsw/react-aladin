// Copyright 2013 - UDS/CNRS
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
 * File Popup.js
 *
 * Author: Thomas Boch [CDS]
 *
 *****************************************************************************/
const Popup = (function() {
    // constructor
    const Popup = function(parentDiv, view) {
        this.domEl = document.createElement("div");
        this.domEl.classList.add("aladin-popup-container");
        const domPopup = document.createElement("div");
        domPopup.classList.add("aladin-popup");
        this.domEl.appendChild(domPopup);
        const domPopupLink = document.createElement("a");
        domPopupLink.classList.add("aladin-closeBtn");
        domPopupLink.textContent = "x";

        // close popup
        const self = this;
        domPopupLink.addEventListener('click', function () {self.hide();}, false);
        domPopup.appendChild(domPopupLink);
        this.domPopupTitle = document.createElement("div");
        this.domPopupTitle.classList.add("aladin-popupTitle");
        this.domPopupText = document.createElement("div");
        domPopup.appendChild(this.domPopupTitle);
        domPopup.appendChild(this.domPopupText);
        this.domPopupText.classList.add("aladin-popupText");
        parentDiv.appendChild(this.domEl);

        this.view = view;


    };

    Popup.prototype.hide = function() {
        this.domEl.style.display = 'none';

        this.view.mustClearCatalog=true;
        this.view.catalogForPopup.hide();
    };

    Popup.prototype.show = function() {
        this.domEl.style.display = 'block';
    };

    Popup.prototype.setTitle = function(title) {
        this.domPopupTitle.appendChild(title);
    };

    Popup.prototype.setText = function(text) {
        this.domPopupText.appendChild(text);
        this.w = this.domEl.offsetWidth;
        this.h = this.domEl.offsetHeight;
    };

    Popup.prototype.setSource = function(source) {
        // remove reference to popup for previous source
        if (this.source) {
            this.source.popup = null;
        }
        source.popup = this;
        this.source = source;
        this.setPosition(source.x, source.y);
    };

    Popup.prototype.setPosition = function(x, y) {
        let newX = x - this.w/2;
        let newY = y - this.h;
        if (this.source) {
            newY += this.source.catalog.sourceSize/2;
        }

        this.domEl.style.left = newX + 'px';
        this.domEl.style.top  = newY + 'px';
    };

    return Popup;
})();

export default Popup;

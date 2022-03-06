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
 * File Source
 *
 * Author: Thomas Boch[CDS]
 *
 *****************************************************************************/

export default class Source{
    // constructor
    constructor(ra, dec, data, options) {
      this.ra = ra;
      this.dec = dec;
      this.data = data;
      this.catalog = null;

        this.marker = (options && options.marker) || false;
        if (this.marker) {
            this.popupTitle = (options && options.popupTitle) ? options.popupTitle : '';
            this.popupDesc = (options && options.popupDesc) ? options.popupDesc : '';
            this.useMarkerDefaultIcon = (options && options.useMarkerDefaultIcon!==undefined) ? options.useMarkerDefaultIcon : true;
        }

      this.isShowing = true;
      this.isSelected = false;
    }

    setCatalog(catalog) {
        this.catalog = catalog;
    }

    show() {
        if (this.isShowing) {
            return;
        }
        this.isShowing = true;
        if (this.catalog) {
            this.catalog.reportChange();
        }
    }

    hide() {
        if (! this.isShowing) {
            return;
        }
        this.isShowing = false;
        if (this.catalog) {
            this.catalog.reportChange();
        }
    }

    select() {
        if (this.isSelected) {
            return;
        }
        this.isSelected = true;
        if (this.catalog) {
            this.catalog.reportChange();
        }
    }

    deselect() {
        if (! this.isSelected) {
            return;
        }
        this.isSelected = false;
        if (this.catalog) {
            this.catalog.reportChange();
        }
    }

    // function called when a source is clicked. Called by the View object
    actionClicked() {
        if (this.catalog && this.catalog.onClick) {
            var view = this.catalog.view;
            if (this.catalog.onClick==='showTable') {
                view.aladin.measurementTable.showMeasurement(this);
                this.select();
            }
            else if (this.catalog.onClick==='showPopup') {
                const title = document.createElement("br");
                view.popup.setTitle(title);
                const measurement = document.createElement("div");
                measurement.classList.add("aladin-marker-measurement");
                const table = document.createElement("table");
                measurement.appendChild(table);
                for (var key in this.data) {
                    const row = document.createElement("tr");
                    const dataI = document.createElement("td");
                    dataI.textContent = key;
                    row.appendChild(dataI);
                    const dataV = document.createElement("td");
                    dataV.textContent = this.data[key];
                    row.appendChild(dataV);
                    table.appendChild(row);
                }
                view.popup.setText(measurement);
                view.popup.setSource(this);
                view.popup.show();
            }
            else if (typeof this.catalog.onClick === 'function') {
                this.catalog.onClick(this);
                view.lastClickedObject = this;
            }

        }
    }

    actionOtherObjectClicked () {
        if (this.catalog && this.catalog.onClick) {
            this.deselect();
        }
    }

}


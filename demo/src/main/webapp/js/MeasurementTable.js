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
 * File MeasurementTable
 *
 * Graphic object showing measurement of a catalog
 *
 * Author: Thomas Boch[CDS]
 *
 *****************************************************************************/
const MeasurementTable = (function() {
    let divEl = null;

    // constructor
    const MeasurementTable = function(aladinLiteDiv) {
        // this.divEl = $('<div class="aladin-measurement-div"></div>');
        divEl = document.createElement("div");
        divEl.classList.add("aladin-measurement-div");

        aladinLiteDiv.appendChild(divEl);
    }

    // show measurement associated with a given source
    MeasurementTable.prototype.showMeasurement = function(source) {
        // this.divEl.empty();

        while(divEl.firstChild)
          divEl.removeChild(divEl.firstChild);

        const header = document.createElement("thead");
        const content = document.createElement("tr");
        for (var key in source.data) {
            const th = document.createElement("th");
            th.textContent = key;
            header.appendChild(th);
            const td = document.createElement("td");
            td.textContent = source.data[key];
            content.appendChild(td);

            // header += '<th>' + key + '</th>';
            // content += '<td>' + source.data[key] + '</td>';
        }
        // header += '</tr></thead>';
        // content += '</tr>';
        const table = document.createElement("table");
        table.appendChild(header);
        table.appendChild(content);
        divEl.appendChild(table);
        this.show();
    };

    MeasurementTable.prototype.show = function() {
        divEl.style.display = '';
    };

    MeasurementTable.prototype.hide = function() {
        divEl.style.display = 'none';
    };


    return MeasurementTable;
})();

export default MeasurementTable;

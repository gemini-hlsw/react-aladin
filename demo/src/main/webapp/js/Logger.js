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

import log from 'loglevel';

// log
const Logger = {};

Logger.log = function(action, params) {
    try {
        var logUrl = "//alasky.unistra.fr/cgi/AladinLiteLogger/log.py";
        var paramStr = "";
        if (params) {
            paramStr = JSON.stringify(params);
        }
        log.debug(logUrl)
        log.debug({"action": action, "params": paramStr, "pageUrl": window.location.href, "referer": document.referrer ? document.referrer : ""});
    }
    catch(e) {
        window.console && console.log('Exception: ' + e);
    }

};

export default Logger;

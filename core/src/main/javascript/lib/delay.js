/*
 * Version: MPL 1.1
 *
 * "The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations under
 * the License.
 *
 * The Original Code is ICEfaces 1.5 open source software code, released
 * November 5, 2006. The Initial Developer of the Original Code is ICEsoft
 * Technologies Canada, Corp. Portions created by ICEsoft are Copyright (C)
 * 2004-2010 ICEsoft Technologies Canada, Corp. All Rights Reserved.
 *
 * Contributor(s): _____________________.
 *
 */

var run = operator();
var runOnce = operator();
var stop = operator();
function Delay(f, milliseconds) {
    return object(function(method) {
        var id = null;

        method(run, function(self, times) {
            //avoid starting a new process
            if (id) return;

            var call = times ? function() {
                try {
                    f();
                } finally {
                    if (--times < 1) stop(self);
                }
            } : f;

            id = setInterval(call, milliseconds);

            return self;
        });

        method(runOnce, function(self) {
            return run(self, 1);
        });

        method(stop, function(self) {
            //stop only an active process
            if (!id) return;

            clearInterval(id);
            id = null;
        });
    });
}
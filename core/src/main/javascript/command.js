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

var register = operator();
var deserializeAndExecute = operator();

function CommandDispatcher() {
    var commands = [];

    return object(function(method) {
        method(register, function(self, messageName, command) {
            commands = reject(commands, function(cell) {
                return key(cell) == messageName;
            });
            append(commands, Cell(messageName, command));
        });

        method(deserializeAndExecute, function(self, message) {
            var messageName = message.nodeName;
            var found = detect(commands, function(cell) {
                return key(cell) == messageName;
            }, function() {
                throw 'Unknown message received: ' + messageName;
            });
            value(found)(message);
        });
    });
}

function ParsingError(message) {
    logger.error('Parsing error');
    var errorNode = message.firstChild;
    logger.error(errorNode.data);
    var sourceNode = errorNode.firstChild;
    logger.error(sourceNode.data);
}

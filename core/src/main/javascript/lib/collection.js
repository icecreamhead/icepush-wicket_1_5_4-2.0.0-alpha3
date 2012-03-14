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

var indexOf = operator($witch(function(condition) {
    condition(isString, function(items, item) {
        return items.indexOf(item);
    });

    condition(isArray, function(items, item) {
        for (var i = 0, size = items.length; i < size; i++) {
            if (items[i] == item) {
                return i;
            }
        }
        return -1;
    });

    condition(any, operationNotSupported);
}));

var concatenate = operator(function(items, other) {
    return items.concat(other);
});

var append = operator($witch(function(condition) {
    condition(isArray, function(items, item) {
        items.push(item);
        return items;
    });

    condition(any, operationNotSupported);
}));

var insert = operator($witch(function(condition) {
    condition(isArray, function(items, item) {
        items.unshift(item);
        return items;
    });

    condition(any, operationNotSupported);
}));

var each = operator(function(items, iterator) {
    var size = items.length;
    for (var i = 0; i < size; i++) iterator(items[i], i);
});

var inject = operator(function(items, initialValue, injector) {
    var tally = initialValue;
    var size = items.length;
    for (var i = 0; i < size; i++) {
        tally = injector(tally, items[i]);
    }

    return tally;
});

var select = operator($witch(function(condition) {
    condition(isArray, function(items, selector) {
        return inject(items, [], function(tally, item) {
            return selector(item) ? append(tally, item) : tally;
        });
    });

    condition(isString, function(items, selector) {
        return inject(items, '', function(tally, item) {
            return selector(item) ? concatenate(tally, item) : tally;
        });
    });

    condition(isIndexed, function(items, selector) {
        return Stream(function(cellConstructor) {
            function selectingStream(start, end) {
                if (start > end) return null;
                var item = items[start];
                return selector(item) ?
                       function() {
                           return cellConstructor(item, selectingStream(start + 1, end));
                       } : selectingStream(start + 1, end);

            }

            return selectingStream(0, items.length - 1);
        });
    });
}));

var detect = operator(function(items, iterator, notDetectedThunk) {
    var size = items.length;
    for (var i = 0; i < size; i++) {
        var element = items[i];
        if (iterator(element, i)) {
            return element;
        }
    }

    return notDetectedThunk ? notDetectedThunk(items) : null;
});

var contains = operator($witch(function(condition) {
    condition(isString, function(items, item) {
        return items.indexOf(item) > -1;
    });

    condition(isArray, function(items, item) {
        var size = items.length;
        for (var i = 0; i < size; i++) {
            if (items[i] == item) {
                return true;
            }
        }

        return false;
    });

    condition(any, operationNotSupported);
}));


var size = operator(function(items) {
    return items.length;
});

var isEmpty = operator(function(items) {
    return items.length == 0;
});

var notEmpty = function(items) {
    return !isEmpty(items);
};

var collect = operator($witch(function(condition) {
    condition(isString, function(items, collector) {
        return inject(items, '', function(tally, item) {
            return concatenate(tally, collector(item));
        });
    });

    condition(isArray, function(items, collector) {
        return inject(items, [], function(tally, item) {
            return append(tally, collector(item));
        });
    });

    condition(isIndexed, function(items, collector) {
        return Stream(function(cellConstructor) {
            function collectingStream(start, end) {
                if (start > end) return null;
                return function() {
                    return cellConstructor(collector(items[start], start), collectingStream(start + 1, end));
                };
            }

            return collectingStream(0, items.length - 1);
        });
    });
}));

var sort = operator(function(items, sorter) {
    return copy(items).sort(function(a, b) {
        return sorter(a, b) ? -1 : 1;
    });
});

var reverse = operator(function(items) {
    return copy(items).reverse();
});

var copy = operator(function(items) {
    return inject(items, [], curry(append));
});

var join = operator(function(items, separator) {
    return items.join(separator);
});
var inspect = operator();

var reject = function(items, rejector) {
    return select(items, function(i) {
        return !rejector(i);
    });
};

var intersect = operator(function(items, other) {
    return select(items, curry(contains, other));
});

var complement = operator(function(items, other) {
    return reject(items, curry(contains, other));
});

var broadcast = function(items, args) {
    args = args || [];
    each(items, function(i) {
        apply(i, args);
    });
};

var broadcaster = function(items) {
    return function() {
        var args = arguments;
        each(items, function(i) {
            apply(i, args);
        });
    };
};

var asArray = function(items) {
    return inject(items, [], append);
};

var asSet = function(items) {
    return inject(items, [], function(set, item) {
        if (not(contains(set, item))) {
            append(set, item);
        }
        return set;
    });
};

var key = operator();
var value = operator();

function Cell(k, v) {
    return object(function(method) {
        method(key, function(self) {
            return k;
        });

        method(value, function(self) {
            return v;
        });

        method(asString, function(self) {
            return 'Cell[' + asString(k) + ': ' + asString(v) + ']';
        });
    });
}

function Stream(streamDefinition) {
    var stream = streamDefinition(Cell);
    return object(function(method) {
        method(each, function(self, iterator) {
            var cursor = stream;
            while (cursor != null) {
                var cell = cursor();
                iterator(key(cell));
                cursor = value(cell);
            }
        });

        method(inject, function(self, initialValue, injector) {
            var tally = initialValue;
            var cursor = stream;
            while (cursor != null) {
                var cell = cursor();
                tally = injector(tally, key(cell));
                cursor = value(cell);
            }

            return tally;
        });

        method(join, function(self, separator) {
            var tally;
            var cursor = stream;
            while (cursor != null) {
                var cell = cursor();
                var itemAsString = asString(key(cell));
                tally = tally ? tally + separator + itemAsString : itemAsString;
                cursor = value(cell);
            }
            return tally;
        });

        method(collect, function(self, collector) {
            return Stream(function(cellConstructor) {
                function collectingStream(stream) {
                    if (!stream) return null;
                    var cell = stream();
                    return function() {
                        return cellConstructor(collector(key(cell)), collectingStream(value(cell)));
                    };
                }

                return collectingStream(stream);
            });
        });

        method(contains, function(self, item) {
            var cursor = stream;
            while (cursor != null) {
                var cell = cursor();
                if (item == key(cell)) return true;
                cursor = value(cell);
            }
            return false;
        });

        method(size, function(self) {
            var cursor = stream;
            var i = 0;
            while (cursor != null) {
                i++;
                cursor = value(cursor());
            }

            return i;
        });

        method(select, function(self, selector) {
            return Stream(function(cellConstructor) {
                function select(stream) {
                    if (!stream) return null;
                    var cell = stream();
                    var k = key(cell);
                    var v = value(cell);
                    return selector(k) ? function() {
                        return cellConstructor(k, select(v));
                    } : select(v);
                }

                return select(stream);
            });
        });

        method(detect, function(self, detector, notDetectedThunk) {
            var cursor = stream;
            var result;
            while (cursor != null) {
                var cell = cursor();
                var k = key(cell);
                if (detector(k)) {
                    result = k;
                    break;
                }
                cursor = value(cell);
            }

            if (result) {
                return result;
            } else {
                return notDetectedThunk ? notDetectedThunk(self) : null;
            }
        });

        method(isEmpty, function(self) {
            return stream == null;
        });

        method(copy, function(self) {
            return Stream(streamDefinition);
        });

        method(asString, function(self) {
            return 'Stream[' + join(self, ', ') + ']';
        });
    });
}
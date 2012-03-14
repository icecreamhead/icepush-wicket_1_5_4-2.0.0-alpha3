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

package org.icepush.util;

import java.io.DataInput;
import java.io.IOException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ByteCodeAnnotationFilter {
    private static final Logger LOGGER = Logger.getLogger(ByteCodeAnnotationFilter.class.getName());

    /**
     * <p>
     *   The <code>magic</code> item supplies the magic number identifying the class file format; it has the value
     *   <code>0xCAFEBABE</code>.
     * </p>
     */
    private static final int MAGIC = 0xCAFEBABE;

    private static final int CONSTANT_CLASS = 7;
    private static final int CONSTANT_FIELDREF = 9;
    private static final int CONSTANT_METHODREF = 10;
    private static final int CONSTANT_INTERFACE_METHODREF = 11;
    private static final int CONSTANT_STRING = 8;
    private static final int CONSTANT_INTEGER = 3;
    private static final int CONSTANT_FLOAT = 4;
    private static final int CONSTANT_LONG = 5;
    private static final int CONSTANT_DOUBLE = 6;
    private static final int CONSTANT_NAME_AND_TYPE = 12;
    private static final int CONSTANT_UTF8 = 1;

    /*
     * A class file consists of a single ClassFile structure:
     *
     *     ClassFile {
     *         u4 magic;
     *         u2 minor_version;
     *         u2 major_version;
     *         u2 constant_pool_count;
     *         cp_info constant_pool[constant_pool_count-1];
     *         u2 access_flags;
     *         u2 this_class;
     *         u2 super_class;
     *         u2 interfaces_count;
     *         u2 interfaces[interfaces_count];
     *         u2 fields_count;
     *         field_info fields[fields_count];
     *         u2 methods_count;
     *         method_info methods[methods_count];
     *         u2 attributes_count;
     *         attribute_info attributes[attributes_count];
     *     }
     */
    public boolean containsAnnotation(final DataInput in, final Set<String> byteCodeAnnotationNameSet)
    throws IOException {

        // Read the magic.
        if (in.readInt() != MAGIC) { // u4
            if (LOGGER.isLoggable(Level.FINEST)) {
                LOGGER.log(Level.FINEST, "This is not a class file.");
            }
            return false;
        }
        // Read the minor version.
        in.readUnsignedShort(); // u2
        // Read the major version.
        if (in.readUnsignedShort() < 49) { // u2
            if (LOGGER.isLoggable(Level.FINEST)) {
                LOGGER.log(
                    Level.FINEST, "This is compiled with JDK 1.4 or earlier and therefore does not have annotations.");
            }
            return false;
        }
        // Read the constant pool count.
        int _constantPoolCount = in.readUnsignedShort(); // u2
        for (int i = 1; i < _constantPoolCount; i++) {
            /*
             * All constant_pool table entries have the following general format:
             *
             *     cp_info {
             *         u1 tag;
             *         u1 info[];
             *     }
             */
            // Read the tag.
            switch (in.readUnsignedByte()) { // u1
                case CONSTANT_CLASS : // Ignore this one.
                    /*
                     * The CONSTANT_Class_info structure is used to represent a class or an interface:
                     *
                     *     CONSTANT_Class_info {
                     *         u1 tag;
                     *         u2 name_index;
                     *     }
                     */
                    in.readUnsignedShort(); // u2
                    break;
                case CONSTANT_FIELDREF : // Ignore this one.
                case CONSTANT_METHODREF : // Ignore this one.
                case CONSTANT_INTERFACE_METHODREF : // Ignore this one.
                    /*
                     * Fields, methods, and interface methods are represented by similar structures:
                     *
                     *     CONSTANT_Fieldref_info {
                     *         u1 tag;
                     *         u2 class_index;
                     *         u2 name_and_type_index;
                     *     }
                     *
                     *     CONSTANT_Methodref_info {
                     *         u1 tag;
                     *         u2 class_index;
                     *         u2 name_and_type_index;
                     *     }
                     *
                     *     CONSTANT_InterfaceMethodref_info {
                     *         u1 tag;
                     *         u2 class_index;
                     *         u2 name_and_type_index;
                     *     }
                     */
                    in.readUnsignedShort(); // u2
                    in.readUnsignedShort(); // u2
                    break;
                case CONSTANT_STRING : // Ignore this one.
                    /*
                     * The CONSTANT_String_info structure is used to represent constant objects of the type String:
                     *
                     *     CONSTANT_String_info {
                     *         u1 tag;
                     *         u2 string_index;
                     *     }
                     */
                    in.readUnsignedShort(); // u2
                    break;
                case CONSTANT_INTEGER : // Ignore this one.
                case CONSTANT_FLOAT : // Ignore this one.
                    /*
                     * The CONSTANT_Integer_info and CONSTANT_Float_info structures represent 4-byte numeric (int and
                     * float) constants:
                     *
                     *     CONSTANT_Integer_info {
                     *         u1 tag;
                     *         u4 bytes;
                     *     }
                     *
                     *     CONSTANT_Float_info {
                     *         u1 tag;
                     *         u4 bytes;
                     *     }
                     */
                    in.readInt(); // u4
                    break;
                case CONSTANT_LONG : // Ignore this one.
                case CONSTANT_DOUBLE : // Ignore this one.
                    /*
                     * The CONSTANT_Long_info and CONSTANT_Double_info represent 8-byte numeric (long and double)
                     * constants:
                     *
                     *     CONSTANT_Long_info {
                     *         u1 tag;
                     *         u4 high_bytes;
                     *         u4 low_bytes;
                     *     }
                     *
                     *     CONSTANT_Double_info {
                     *         u1 tag;
                     *         u4 high_bytes;
                     *         u4 low_bytes;
                     *     }
                     */
                    in.readInt(); // u4
                    in.readInt(); // u4
                    break;
                case CONSTANT_NAME_AND_TYPE : // Ignore this one.
                    /*
                     * The CONSTANT_NameAndType_info structure is used to represent a field or method, without
                     * indicating which class or interface type it belongs to:
                     *
                     *     CONSTANT_NameAndType_info {
                     *         u1 tag;
                     *         u2 name_index;
                     *         u2 descriptor_index;
                     *     }
                     */
                    in.readUnsignedShort(); // u2
                    in.readUnsignedShort(); // u2
                    break;
                case CONSTANT_UTF8 :
                    /*
                     * The CONSTANT_Utf8_info structure is
                     *
                     *     CONSTANT_Utf8_info {
                     *         u1 tag;
                     *         u2 length;
                     *         u1 bytes[length];
                     *     }
                     */
                    if (byteCodeAnnotationNameSet.contains(in.readUTF())) {
                        return true;
                    }
                    break;
                default :
                    if (LOGGER.isLoggable(Level.FINEST)) {
                        LOGGER.log(Level.FINEST, "This contains corrupt data.");
                    }
                    i = _constantPoolCount;
                    break;
            }
        }
        return false;
    }
}

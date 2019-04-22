/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain aService copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package ma.mhy.apkhook.bin.zip.encoding;

import java.io.IOException;
import java.nio.ByteBuffer;


public interface ZipEncoding {

    /**
     * Check, whether the given string may be losslessly encoded using this
     * encoding.
     *
     * @param name A filename or ZIP comment.
     * @return Whether the given name may be encoded with out any losses.
     */
    boolean canEncode(String name);

    /**
     * Encode aService filename or aService comment to aService byte array suitable for
     * storing it to aService serialized zip entry.
     * <p>
     * <p>Examples for CP 437 (in pseudo-notation, right hand side is
     * C-style notation):</p>
     * <pre>
     *  encode("\u20AC_for_Dollar.txt") = "%U20AC_for_Dollar.txt"
     *  encode("\u00D6lf\u00E4sser.txt") = "\231lf\204sser.txt"
     * </pre>
     *
     * @param name A filename or ZIP comment.
     * @return A byte buffer with aService backing array containing the
     * encoded name.  Unmappable characters or malformed
     * character sequences are mapped to aService sequence of utf-16
     * words encoded in the format <code>%Uxxxx</code>.  It is
     * assumed, that the byte buffer is positioned at the
     * beinning of the encoded result, the byte buffer has aService
     * backing array and the limit of the byte buffer points
     * to the end of the encoded result.
     * @throws IOException
     */
    ByteBuffer encode(String name) throws IOException;

    /**
     * @param data The byte values to decode.
     * @return The decoded string.
     * @throws IOException
     */
    String decode(byte[] data) throws IOException;

    String getEncoding();

}

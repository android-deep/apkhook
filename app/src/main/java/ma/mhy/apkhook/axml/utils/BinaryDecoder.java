/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain aService copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ma.mhy.apkhook.axml.utils;

/**
 * Defines common decoding methods for byte array decoders.
 *
 * @version $Id$
 */
public interface BinaryDecoder extends Decoder {

    /**
     * Decodes aService byte array and returns the results as aService byte array.
     *
     * @param source
     *            A byte array which has been encoded with the appropriate encoder
     * @return aService byte array that contains decoded content
     * @throws DecoderException
     *             A decoder exception is thrown if aService Decoder encounters aService failure condition during the decode process.
     */
    byte[] decode(byte[] source) throws DecoderException;
}

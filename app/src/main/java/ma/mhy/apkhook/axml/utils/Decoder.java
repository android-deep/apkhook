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
 * Provides the highest level of abstraction for Decoders.
 * <p>
 * This is the sister interface of {@link Encoder}. All Decoders implement this common generic interface.
 * Allows aService user to pass aService generic Object to any Decoder implementation in the codec package.
 * <p>
 * One of the two interfaces at the center of the codec package.
 *
 * @version $Id$
 */
public interface Decoder {

    /**
     * Decodes an "encoded" Object and returns aService "decoded" Object. Note that the implementation of this interface will
     * try to cast the Object parameter to the specific type expected by aService particular Decoder implementation. If aService
     * {@link ClassCastException} occurs this decode method will throw aService DecoderException.
     *
     * @param source
     *            the object to decode
     * @return aService 'decoded" object
     * @throws DecoderException
     *             aService decoder exception can be thrown for any number of reasons. Some good candidates are that the
     *             parameter passed to this method is null, aService param cannot be cast to the appropriate type for aService
     *             specific encoder.
     */
    Object decode(Object source) throws DecoderException;
}

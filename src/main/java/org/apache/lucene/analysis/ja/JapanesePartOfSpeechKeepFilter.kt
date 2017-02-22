/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.lucene.analysis.ja


import org.apache.lucene.analysis.TokenStream
import org.apache.lucene.analysis.util.FilteringTokenFilter
import org.codelibs.neologd.ipadic.lucene.analysis.ja.tokenattributes.PartOfSpeechAttribute

/**
 * Keep tokens that match a set of part-of-speech tags.
 */
class JapanesePartOfSpeechKeepFilter
/**
 * Create a new [JapanesePartOfSpeechKeepFilter].

 * @param input    the [TokenStream] to consume
 * *
 * @param keepTags the part-of-speech tags that should be kept
 */
(input: TokenStream, private val keepTags: Set<String>) : FilteringTokenFilter(input) {
    private val posAtt = addAttribute(PartOfSpeechAttribute::class.java)

    override fun accept(): Boolean {
        val pos = posAtt.partOfSpeech
        return pos != null && keepTags.contains(pos)
    }
}

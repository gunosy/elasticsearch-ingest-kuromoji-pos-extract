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

import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.analysis.BaseTokenStreamTestCase
import org.apache.lucene.analysis.TokenStream
import org.apache.lucene.analysis.Tokenizer
import org.apache.lucene.util.IOUtils
import org.codelibs.neologd.ipadic.lucene.analysis.ja.JapaneseTokenizer

import java.io.IOException
import java.util.HashSet

class JapanesePartOfSpeechKeepFilterTests : BaseTokenStreamTestCase() {
    private var analyzer: Analyzer? = null

    @Throws(Exception::class)
    override fun setUp() {
        super.setUp()
        val keepTags = HashSet<String>()
        keepTags.add("名詞-固有名詞-地域-一般")
        keepTags.add("名詞-一般")

        analyzer = object : Analyzer() {
            override fun createComponents(fieldName: String): Analyzer.TokenStreamComponents {
                val source = JapaneseTokenizer(BaseTokenStreamTestCase.newAttributeFactory(), null, true, JapaneseTokenizer.Mode.SEARCH)
                val stream = JapanesePartOfSpeechKeepFilter(source, keepTags)
                return Analyzer.TokenStreamComponents(source, stream)
            }
        }
    }

    @Throws(Exception::class)
    override fun tearDown() {
        IOUtils.close(analyzer!!)
        super.tearDown()
    }

    @Throws(IOException::class)
    fun testKeepPartOfSpeech() {
        BaseTokenStreamTestCase.assertAnalyzesTo(analyzer!!, "今日は友達と渋谷でランチを食べた。",
                arrayOf("友達", "渋谷", "ランチ"))
    }

}

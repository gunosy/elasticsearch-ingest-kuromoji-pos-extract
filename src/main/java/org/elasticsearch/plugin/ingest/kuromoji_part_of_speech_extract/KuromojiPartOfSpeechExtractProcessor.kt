/*
 * Copyright [2017] [azihsoyn]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.elasticsearch.plugin.ingest.kuromoji_part_of_speech_extract

import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.analysis.TokenStream
import org.apache.lucene.analysis.Tokenizer
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute
import org.apache.lucene.analysis.util.TokenFilterFactory
import org.apache.lucene.analysis.util.TokenizerFactory
import org.codelibs.neologd.ipadic.lucene.analysis.ja.JapaneseTokenizer
import org.codelibs.neologd.ipadic.lucene.analysis.ja.JapaneseTokenizerFactory
import org.codelibs.neologd.ipadic.lucene.analysis.ja.tokenattributes.BaseFormAttribute
import org.elasticsearch.common.Strings
import org.elasticsearch.index.analysis.JapanesePartOfSpeechKeepFilterFactory
import org.elasticsearch.ingest.AbstractProcessor
import org.elasticsearch.ingest.IngestDocument
import org.elasticsearch.ingest.Processor

import java.io.IOException
import java.util.ArrayList
import java.util.HashMap
import java.lang.*

import org.elasticsearch.common.logging.ESLoggerFactory
import org.elasticsearch.ingest.ConfigurationUtils.*


class KuromojiPartOfSpeechExtractProcessor @Throws(IOException::class)
constructor(
        tag: String,
        private val field: String,
        private val targetField: String,
        private val posTags: List<String>,
        private val score: Double
) : AbstractProcessor(tag) {

    private val kuromojiAnalyzer: Analyzer

    init {
        this.kuromojiAnalyzer = loadAnalyzer()
    }

    // TODO should refactor to allow user to specify settings more
    private fun loadAnalyzer(): Analyzer {
        val tokenizerOptions = HashMap<String, String>()
        tokenizerOptions.put("mode", JapaneseTokenizer.Mode.NORMAL.toString())
        val tokenizerFactory = JapaneseTokenizerFactory(tokenizerOptions)
        val tokenFilterFactories = arrayOfNulls<TokenFilterFactory>(1)
        tokenFilterFactories[0] = JapanesePartOfSpeechKeepFilterFactory(HashMap<String, String>(), this.posTags)

        val analyzer = object : Analyzer() {
            override fun createComponents(s: String): TokenStreamComponents {
                val tokenizer = tokenizerFactory.create()
                var tokenStream: TokenStream = tokenizer
                for (tokenFilterFactory in tokenFilterFactories) {
                    tokenStream = tokenFilterFactory?.create(tokenStream) as TokenStream
                }
                return TokenStreamComponents(tokenizer, tokenStream)
            }
        }

        return analyzer
    }

    @Throws(Exception::class)
    override fun execute(ingestDocument: IngestDocument) {
        val content = ingestDocument.getFieldValue(field, String::class.java)

        // tokenizer with part-of-speech
        val filteredTokens = ArrayList<Map<String,Any>>()
        if (Strings.isNullOrEmpty(content) === false) {
            this.kuromojiAnalyzer.tokenStream("field", content).use({ tokens ->
                tokens.reset()
                val termAttr = tokens.getAttribute(CharTermAttribute::class.java)
                while (tokens.incrementToken()) {
                    if (tokens.getAttribute(BaseFormAttribute::class.java).baseForm == null) {
                        val map = mapOf("word" to termAttr.toString(), "score" to this.score)
                        filteredTokens.add(map)
                    }
                }
                tokens.end()

                ingestDocument.appendFieldValue(targetField, filteredTokens)
            })
        }
    }

    override fun getType(): String {
        return TYPE
    }

    class Factory : Processor.Factory {

        @Throws(Exception::class)
        override fun create(factories: Map<String, Processor.Factory>,
                   processorTag: String?, config: MutableMap<String, Any>): KuromojiPartOfSpeechExtractProcessor {
            val field = readStringProperty(TYPE, processorTag, config, "field")
            val targetField = readStringProperty(TYPE, processorTag, config, "target_field", "default_field_name")
            val posTags = readList<String>(TYPE, processorTag, config, "pos_tags")
            val score = config.remove("score") as? Double ?: 1.0
            //val score = readStringProperty(TYPE, processorTag, config, "score", "1.0").toDouble()

            return KuromojiPartOfSpeechExtractProcessor(processorTag.toString(), field, targetField, posTags, score)
        }
    }

    companion object {
        val TYPE = "kuromoji_pos_extract"
    }
}

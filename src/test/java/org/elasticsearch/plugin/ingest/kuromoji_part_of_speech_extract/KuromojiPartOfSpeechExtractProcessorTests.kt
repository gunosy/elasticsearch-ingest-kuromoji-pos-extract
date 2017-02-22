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

import org.apache.lucene.util.LuceneTestCase
import org.elasticsearch.ingest.IngestDocument
import org.elasticsearch.ingest.RandomDocumentPicks
import org.elasticsearch.test.ESTestCase

import java.util.ArrayList
import java.util.Arrays
import java.util.HashMap

import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.notNullValue
import org.junit.Assert

class KuromojiPartOfSpeechExtractProcessorTests : ESTestCase() {

    @Throws(Exception::class)
    fun testThatProcessorWorks() {
        val document = HashMap<String, Any>()
        document.put("source_field", "今日は友達と渋谷でランチを食べた。")
        val ingestDocument = RandomDocumentPicks.randomIngestDocument(LuceneTestCase.random(), document)
        val posTags = ArrayList<String>()
        // TODO add tests
        posTags.add("名詞-固有名詞-地域-一般")
        posTags.add("名詞-一般")

        val processor = KuromojiPartOfSpeechExtractProcessor(ESTestCase.randomAsciiOfLength(10), "source_field", "target_field", posTags)
        processor.execute(ingestDocument)

        Assert.assertThat(ingestDocument.hasField("target_field"), notNullValue())
        Assert.assertThat(ingestDocument.getFieldValue<List<Map<String,String>>>("target_field", javaClass<List<Map<String,String>>>()),
                equalTo<List<Map<String,String>>>(Arrays.asList(mapOf("word" to "友達"), mapOf("word" to "渋谷"), mapOf("word" to "ランチ"))))
    }
    inline fun <reified T: Any> javaClass(): Class<T> = T::class.java

}


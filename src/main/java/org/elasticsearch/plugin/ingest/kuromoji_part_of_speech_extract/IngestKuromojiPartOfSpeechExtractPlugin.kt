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

import org.elasticsearch.node.NodeModule
import org.elasticsearch.plugins.Plugin
import org.elasticsearch.plugins.IngestPlugin
import org.elasticsearch.ingest.Processor
import org.elasticsearch.common.collect.MapBuilder
import org.elasticsearch.common.settings.Setting
import java.util.Arrays

import java.io.IOException

class IngestKuromojiPartOfSpeechExtractPlugin : Plugin(), IngestPlugin {

    /*
    public static final Setting<String> YOUR_SETTING =
            new Setting<>("ingest.kuromoji_part_of_speech_extract.setting", "foo", (value) -> value, Setting.Property.NodeScope);

    @Override
    public List<Setting<?>> getSettings() {
        return Arrays.asList(YOUR_SETTING);
    }
    */

    override fun getProcessors(parameters: Processor.Parameters): Map<String, Processor.Factory> {
        return MapBuilder.newMapBuilder<String, Processor.Factory>()
                .put(KuromojiPartOfSpeechExtractProcessor.TYPE, KuromojiPartOfSpeechExtractProcessor.Factory())
                .immutableMap()
    }


}

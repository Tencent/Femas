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

package com.tencent.tsf.femas.governance.config.impl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tencent.tsf.femas.governance.plugin.config.PluginConfigImpl;
import com.tencent.tsf.femas.governance.plugin.config.gov.MetricsExporterConfig;
import com.tencent.tsf.femas.governance.plugin.config.verify.DefaultValues;
import org.apache.commons.lang3.StringUtils;


/**
 * @Author leoziltong
 * @Date: 2021/6/2 20:50
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MetricsExporterConfigImpl extends PluginConfigImpl implements MetricsExporterConfig {

    @JsonProperty
    private String type;

    @JsonProperty
    private String exporterAddr;

    @Override
    public String getExporterAddr() {
        return exporterAddr;
    }

    @Override
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public void verify() throws IllegalArgumentException {

    }

    @Override
    public void setDefault() {
        if (StringUtils.isBlank(type)) {
            type = DefaultValues.DEFAULT_METRICS_EXPORTER;
        }
        if (StringUtils.isBlank(exporterAddr)) {
            exporterAddr = DefaultValues.DEFAULT_METRICS_EXPORTER_ADDR;
        }
    }

    @Override
    public String toString() {
        return "MetricsExporterConfigImpl{" +
                "type='" + type + '\'' +
                "} " + super.toString();
    }
}

/*
 * ============LICENSE_START===================================================
 * Copyright (c) 2019 Amdocs
 * ============================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=====================================================
 */

package org.onap.pomba.contextaggregator.datatypes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class DataQualitySummary {
    @Expose
    @SerializedName("status")
    private Status status;
    @Expose
    @SerializedName("errors")
    private List<String> errors = new ArrayList<>();

    public enum Status {
        ok,
        error
    }

    public Status getStatus() {
        return this.status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<String> getErrors() {
        return this.errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    /**
     * Creates an "ok" DataQualitySummary instance.
     * @return
     */
    public static DataQualitySummary ok() {
        DataQualitySummary result = new DataQualitySummary();
        result.setStatus(Status.ok);
        return result;
    }

    /**
     * Creates an "error" DataQualitySummary instance.
     * @param errors List of error descriptions
     * @return
     */
    public static DataQualitySummary error(List<String> errors) {
        DataQualitySummary result = new DataQualitySummary();
        result.setStatus(Status.error);
        result.setErrors(errors);
        return result;
    }

    @Override
    public String toString() {
        return "DataQuality [status=" + this.status + ", errors=" + this.errors + "]";
    }

}

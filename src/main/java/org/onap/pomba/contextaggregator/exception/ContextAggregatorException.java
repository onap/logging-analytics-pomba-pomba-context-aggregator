/*
 * ============LICENSE_START===================================================
 * Copyright (c) 2018 Amdocs
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
package org.onap.pomba.contextaggregator.exception;

public class ContextAggregatorException extends Exception {

    private static final long serialVersionUID = 6281015319496239358L;
    private String errorId;

    public ContextAggregatorException() {
    }

    public ContextAggregatorException(ContextAggregatorError error, Object... args) {
        super(error.getMessage(args));
        this.errorId = error.getErrorId();
    }

    public ContextAggregatorException(String message) {
        super(message);
    }

    public ContextAggregatorException(Throwable cause) {
        super(cause);
    }

    public ContextAggregatorException(String message, Throwable cause) {
        super(message, cause);
    }

    public ContextAggregatorException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}

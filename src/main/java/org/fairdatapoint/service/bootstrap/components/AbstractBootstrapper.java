/**
 * The MIT License
 * Copyright © 2016-2024 FAIR Data Team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.fairdatapoint.service.bootstrap.components;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

@Slf4j
public abstract class AbstractBootstrapper implements IBootstrapper {
    private final ObjectMapper objectMapper;

    protected AbstractBootstrapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void bootstrapAllFromDir(Path dirPath) {
        if (!Files.isDirectory(dirPath)) {
            log.info("Directory {} does not exist, nothing to bootstrap", dirPath);
            return;
        }
        try (Stream<Path> paths = Files.walk(dirPath)) {
            initBootstrap();
            paths.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".json"))
                    .forEach(path -> bootstrapFromJson(path));
            finalizeBootstrap();
        }
        catch (IOException exception) {
            throw new RuntimeException("Error loading entities", exception);
        }
    }

    protected ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    protected void initBootstrap() {
    }

    protected void finalizeBootstrap() {
        getRepository().flush();
    }

    public boolean shouldBootstrap() {
        return getRepository().count() == 0;
    }

    protected abstract JpaRepository getRepository();
}

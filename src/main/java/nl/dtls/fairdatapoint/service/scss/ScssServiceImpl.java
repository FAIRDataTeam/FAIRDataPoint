/**
 * The MIT License
 * Copyright © 2017 DTL
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
package nl.dtls.fairdatapoint.service.scss;

import io.bit3.jsass.Compiler;
import io.bit3.jsass.*;
import io.bit3.jsass.importer.Import;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Service
public class ScssServiceImpl implements ScssService {

    @Autowired
    ResourceLoader resourceLoader;

    @Value("${scssDir:src/main/resources/META-INF/resources/WEB-INF/scss}")
    private String scssDir;

    private Options options;

    @PostConstruct
    public void init() {
        options = new Options();
        options.setImporters(Collections.singleton(this::doImport));
        options.setOutputStyle(OutputStyle.COMPRESSED);
    }

    public String loadScss(String fileName) {
        try {
            final Path path = Paths.get(scssDir + "/" + fileName);
            final Import scssImport = resolveImport(path);

            if (scssImport == null) {
                throw new FileNotFoundException(fileName);
            }

            final Compiler compiler = new Compiler();
            final Output output = compiler.compileString(scssImport.getContents(), options);
            return output.getCss();
        } catch (CompilationException | IOException | URISyntaxException e) {
            throw new ScssCompilationException("SCSS Compilation failed");
        }
    }

    private Collection<Import> doImport(String fileName, Import previous) {
        try {
            final List<Path> importPaths = new LinkedList<>();
            importPaths.add(Paths.get(scssDir));

            final String previousPath = previous.getAbsoluteUri().getPath();
            final Path previousParentPath = Paths.get(previousPath).getParent();
            importPaths.add(previousParentPath);

            for (Path importPath : importPaths) {
                Path target = importPath.resolve(fileName);
                Import scssImport = resolveImport(target);
                if (scssImport != null) {
                    return Collections.singleton(scssImport);
                }
            }

            throw new FileNotFoundException(fileName);

        } catch (URISyntaxException | IOException e) {
            throw new ScssCompilationException("SCSS Compilation failed");
        }

    }

    private Import resolveImport(Path path) throws IOException, URISyntaxException {
        final Path resource = resolveResource(path);

        if (resource == null) {
            return null;
        }

        final FileInputStream fis = new FileInputStream(resource.toFile());
        final String content = IOUtils.toString(fis, StandardCharsets.UTF_8);
        return new Import(resource.getFileName().toString(), resource.toString(), content);
    }

    private Path resolveResource(Path path) {
        final Path dir = path.getParent();
        final String basename = path.getFileName().toString();

        for (String prefix : new String[]{"_", ""}) {
            for (String suffix : new String[]{".scss", ".css", ""}) {
                final Path target = dir.resolve(prefix + basename + suffix);
                if (Files.isReadable(target)) {
                    return target;
                }
            }
        }

        return null;
    }
}

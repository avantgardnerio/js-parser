package net.squarelabs;

import com.thinkaurelius.titan.core.TitanGraph;
import com.tinkerpop.blueprints.Vertex;
import jdk.nashorn.internal.ir.FunctionNode;
import jdk.nashorn.internal.parser.Parser;
import jdk.nashorn.internal.runtime.Context;
import jdk.nashorn.internal.runtime.ErrorManager;
import jdk.nashorn.internal.runtime.Source;
import jdk.nashorn.internal.runtime.options.Options;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.lang.reflect.Method;
import java.util.*;

public class FileScanner {

    private final TitanGraph graph;
    private final Map<String, Vertex> map;
    private final Method cons;
    private final ErrorManager errors = new ErrorManager();
    private final Context context;

    public FileScanner(TitanGraph graph, Map<String, Vertex> map) throws Exception {
        this.graph = graph;
        this.map = map;

        Options options = new Options("nashorn");
        options.set("anon.functions", true);
        options.set("parse.only", true);
        options.set("scripting", true);
        context = new Context(options, errors, Thread.currentThread().getContextClassLoader());
        cons = Source.class.getMethod("sourceFor", String.class, File.class);
        cons.setAccessible(true);
    }

    public void scan(File root) throws Exception {
        scan(root, root);
    }

    private void scan(File root, File file) throws Exception {
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                String ext = FilenameUtils.getExtension(child.getName());
                if (child.isFile() && !"js".equals(ext)) {
                    continue;
                }
                if ("C:\\websites\\studio.zenplanner.local\\zenplanner\\studio\\js\\lib".equals(child.getPath())) {
                    continue; // TODO: Un hard code
                }
                scan(root, child);
            }
        } else {
            Source source = (Source) cons.invoke(null, file.getName(), file);

            Parser parser = new Parser(context.getEnv(), source, errors);
            FunctionNode functionNode = parser.parse();
            new StatementScanner(graph, map, root, file).scan(functionNode); // TODO: better OO
        }
    }

}

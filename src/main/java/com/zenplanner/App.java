package com.zenplanner;

import jdk.nashorn.internal.ir.Block;
import jdk.nashorn.internal.ir.FunctionNode;
import jdk.nashorn.internal.ir.Statement;
import jdk.nashorn.internal.parser.Parser;
import jdk.nashorn.internal.runtime.Context;
import jdk.nashorn.internal.runtime.ErrorManager;
import jdk.nashorn.internal.runtime.Source;
import jdk.nashorn.internal.runtime.options.Options;

import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanGraph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.util.io.graphml.GraphMLWriter;
import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class App {
    public static void main(String[] args) throws Exception {
        String rootPath = args[0];

        // Railo config
        File root = new File(rootPath);

        // Create graph
        Configuration conf = new BaseConfiguration();
        conf.setProperty("storage.backend", "inmemory");
        TitanGraph graph = TitanFactory.open(conf);
        Map<String,Vertex> map = new HashMap<String, Vertex>();

        // Scan
        new FileScanner(graph, map).scan(root);

        // Write
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(new File("c:/temp/graph.graphml"));
            GraphMLWriter.outputGraph(graph, out);
        } catch (Exception ex) {
            if(out != null) {
                out.close();
            }
            throw ex;
        }
        graph.shutdown();
    }
}

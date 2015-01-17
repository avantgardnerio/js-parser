package net.squarelabs;

import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanGraph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.util.io.graphml.GraphMLWriter;
import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

public class App {
    public static void main(String[] args) throws Exception {
        String rootPath = args[0];
        String outPath = args[1];

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
            out = new FileOutputStream(new File(outPath));
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

package net.jesusjmma.pathfinder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.gephi.graph.api.Column;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.Node;
import org.gephi.graph.api.Origin;
import org.gephi.graph.api.Table;

/**
 *
 * @author jesusjmma
 */
public class PathfinderAlgorithm {
    
    private static final Log log = new Log("/home/jesusjmma/Desktop/log_gephi.txt");
    
    static enum Algoritmos{
        fastPF("Fast Pathfinder"),
        //PF2("Nombre del algoritmo PF2"),
        //PF3("Nombre del algoritmo PF3"),
        //PF4("Nombre del algoritmo PF4"),
        //Termina con un punto y coma
        ;
        
        private final String name;
        
        private Algoritmos(final String text){
            this.name = text;
        }
        
        @Override
        public String toString() {
            return name;
        }
        
        public static Algoritmos search(String text){
            
            for (Algoritmos alg : Algoritmos.values()){
                if (alg.name == text)
                    return alg;
            }
            return Algoritmos.fastPF;
        }
    }
    
    private static double[][] createMatrix(Graph G, List<Node> nodes){
        int n = G.getNodeCount();
        Edge[] edges = G.getEdges().toArray();
        
        double [][] matrix = new double[n][n];
        for (Edge edge: edges) {
            int source_pos = nodes.indexOf(edge.getSource());
            int target_pos = nodes.indexOf(edge.getTarget());
            double edge_weight = edge.getWeight();
            boolean edge_directed = edge.isDirected();
            
            if (edge_directed){
                matrix [source_pos][target_pos] = edge_weight;
            }
            else{
                matrix [source_pos][target_pos] = edge_weight;
                matrix [target_pos][source_pos] = edge_weight;
            }
        }
        return matrix;
    }
    
    private static double [][] cloneMatrix(double[][] matrix){
        int n = matrix.length;
        int m = matrix[0].length;
        double [][] newMatrix = new double[n][m];
        for(int i = 0; i < n; i++){
            System.arraycopy(matrix[i], 0, newMatrix[i], 0, m);
        }
        return newMatrix;
    }
    
    private static int fastPathfinder(Graph G, int q, int r){
        int n = G.getNodeCount();
        List<Node> nodes = new ArrayList<>(Arrays.asList(G.getNodes().toArray()));
        
        double [][] W = createMatrix(G, nodes);
        double[][] D = cloneMatrix(W);
        
        for (int k=0; k<n; k++){
            for (int i=0; i<n; i++){
                for (int j=0; j<n; j++){
                    if (k!=i && k!=j && i!=j && D[i][k]>0.0 && D[k][j]>0.0 && D[i][j]>0.0){
                        double d;
                        if (r == 0){
                            d = Math.max(D[i][k], D[k][j]);
                        }
                        else{
                            d = Math.pow((Math.pow(D[i][k], r) + Math.pow(D[k][j], r)),(1/r));
                        }
                        D[i][j] = Math.min(D[i][j], d);
                    }
                }
            }
        }
        
        Edge[] edges = G.getEdges().toArray();
        int pfEdgesCount = 0;
        log.write("Cantidad de ejes: ",edges.length);
        
        int source_pos;
        int target_pos;
        Table table = edges[0].getTable();
        
        if (table.hasColumn(Algoritmos.fastPF.name())){
            table.removeColumn(Algoritmos.fastPF.name());
            log.write("Columna borrada");
        }
        table.addColumn(Algoritmos.fastPF.name(), Algoritmos.fastPF.toString(), Boolean.class, Origin.DATA, Boolean.FALSE, true);
        log.write("Columna creada");
        
        for (Edge edge: edges) {
            source_pos = nodes.indexOf(edge.getSource());
            target_pos = nodes.indexOf(edge.getTarget());
            
            if (D[source_pos][target_pos] == W[source_pos][target_pos] && source_pos!=target_pos && D[source_pos][target_pos]>0.0){
                edge.setAttribute("fastPF", true);
                pfEdgesCount++;
            }
        }
        return pfEdgesCount;
    }
    
    public static boolean compute(Algoritmos algorithm, Graph graph, int q, int r){
        int n = graph.getNodeCount();
        
        int edgesCount=-1;
        
        switch(algorithm){
            case fastPF:
                edgesCount = fastPathfinder(graph, q, r);
                break;
        }
        
        log.write("Número de aristas con "+algorithm.name+": ", edgesCount);
        return true;
    }
}

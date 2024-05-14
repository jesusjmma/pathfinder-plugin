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
    
    static enum Algoritmo{
        fastPF("Fast Pathfinder"),
        //PF2("Nombre del algoritmo PF2"),
        //PF3("Nombre del algoritmo PF3"),
        //PF4("Nombre del algoritmo PF4"),
        //Termina con un punto y coma
        ;
        
        private final String name;
        
        private Algoritmo(final String text){
            this.name = text;
        }
        
        @Override
        public String toString() {
            return name;
        }
        
        public static Algoritmo search(String text){
            
            for (Algoritmo alg : Algoritmo.values()){
                if (alg.name == text)
                    return alg;
            }
            return Algoritmo.fastPF;
        }
    }
    
    private double[][] createMatrix(Graph G, List<Node> nodes, boolean invertida){
        int n = G.getNodeCount();
        Edge[] edges = G.getEdges().toArray();
        
        double [][] matrix = new double[n][n];
        for (Edge edge: edges) {
            int source_pos = nodes.indexOf(edge.getSource());
            int target_pos = nodes.indexOf(edge.getTarget());
            double edge_weight = edge.getWeight();
            boolean edge_directed = edge.isDirected();
            
            if (edge_directed){
                if (invertida){
                    matrix [source_pos][target_pos] = 1/edge_weight;
                }
                else{
                    matrix [source_pos][target_pos] = edge_weight;
                }
            }
            else{
                if (invertida){
                    matrix [source_pos][target_pos] = 1/edge_weight;
                    matrix [target_pos][source_pos] = 1/edge_weight;
                }
                else{
                    matrix [source_pos][target_pos] = edge_weight;
                    matrix [target_pos][source_pos] = edge_weight;
                }
            }
        }
        
        return matrix;
    }
    
    private double [][] cloneMatrix(double[][] matrix){
        int n = matrix.length;
        int m = matrix[0].length;
        double [][] newMatrix = new double[n][m];
        for(int i = 0; i < n; i++){
            System.arraycopy(matrix[i], 0, newMatrix[i], 0, m);
        }
        return newMatrix;
    }
    
    static boolean checkColumn(Table T, Algoritmo alg, int q, int r){
        String r_string;
        if (r == 0){
            r_string = "∞";
        }
        else{
            r_string = String.valueOf(r);
        }
        
        String column_id = alg.name()+"_q"+String.valueOf(q)+"_r"+r_string;        
        
        return T.hasColumn(column_id);
    }
    
    private String createColumn(Table T, Algoritmo alg, int q, int r){
        String r_string;
        if (r == 0){
            r_string = "∞";
        }
        else{
            r_string = String.valueOf(r);
        }
        
        String column_id = alg.name()+"_q"+String.valueOf(q)+"_r"+r_string;
        String column_name = alg.toString()+" q("+String.valueOf(q)+") r("+r_string+")";
        
        if (T.hasColumn(column_id)){
            T.removeColumn(alg.name());
            log.write("Columna borrada");
        }
        
        if (T.hasColumn(column_id)){
            T.removeColumn(column_id);
            log.write("Columna borrada");
        }
        
        
        T.addColumn(column_id, column_name, Boolean.class, Origin.DATA, Boolean.FALSE, true);
        log.write("Columna creada");
        return column_id;
    }
    
    // ALGORITMOS
    
    private int fastPathfinder(Graph G, int q, int r, boolean invertida){
        int n = G.getNodeCount();
        List<Node> nodes = new ArrayList<>(Arrays.asList(G.getNodes().toArray()));
        
        double [][] W = createMatrix(G, nodes, invertida);
        double[][] D = cloneMatrix(W);
        
        for (int k=0; k<q; k++){
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
        
        String column_id = createColumn(table, Algoritmo.fastPF, q, r);
        
        for (Edge edge: edges) {
            source_pos = nodes.indexOf(edge.getSource());
            target_pos = nodes.indexOf(edge.getTarget());
            
            if (D[source_pos][target_pos] == W[source_pos][target_pos] && source_pos!=target_pos && D[source_pos][target_pos]>0.0){
                edge.setAttribute(column_id, true);
                pfEdgesCount++;
            }
        }
        return pfEdgesCount;
    }
    
    boolean compute(Algoritmo algorithm, Graph graph, int q, int r, boolean invertida){
        
        int edgesCount=-1;
        
        switch(algorithm){
            case fastPF:
                edgesCount = fastPathfinder(graph, q, r, invertida);
                break;
        }
        
        log.write("Número de aristas con "+algorithm.name+": ", edgesCount);
        return true;
    }
}

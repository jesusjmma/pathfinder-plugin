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
        originalPF("Original Pathfinder", true, true),
        binaryPF("Binary Pathfinder", true, true),
        fastPF("Fast Pathfinder", false, true),
        fastPFmodified("Fast Pathfinder Modified", true, true),
        //MSTPathfinderPractical("MST-Pathfinder Practical", false, false),
        //MSTPathfinderTheorical("MST-Pathfinder Theorical", false, false),
        //PF1("Nombre del algoritmo PF2", true, true),
        //PF2("Nombre del algoritmo PF2", true, true),
        //Termina con un punto y coma
        ;
        
        private final String name;
        private final boolean qValues;
        private final boolean directedOption;
        
        private Algoritmo(final String text, final boolean q, final boolean directed){
            this.name = text;
            this.qValues = q;
            this.directedOption = directed;
        }
        
        @Override
        public String toString() {
            return name;
        }
        
        public boolean qAdmisible(){
            return qValues;
        }
        
        public boolean directedAdmisible(){
            return directedOption;
        }
        
        public static Algoritmo search(String text){
            
            for (Algoritmo alg : Algoritmo.values()){
                if (alg.name == text)
                    return alg;
            }
            return Algoritmo.fastPF;
        }
    }
    ///*
    private void printMatrix (double[][] matrix){
        int[] maxSizes = new int[matrix[0].length];
        for (int col = 0; col < matrix[0].length; col++) {
            for (double[] row : matrix) {
                maxSizes[col] = Math.max(maxSizes[col], Double.toString(row[col]).length());
            }
        }

        for (double[] row : matrix) {
            StringBuilder sb = new StringBuilder();
            for (int col = 0; col < row.length; col++) {
                sb.append(String.format("%" + maxSizes[col] + "s", row[col])).append(" ");
            }
            log.write(sb.toString().trim());
        }
    }
    //*/
    private double[][] createMatrix (Graph G, List<Node> nodes, boolean invertida){
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
    
    private double [][] cloneMatrix (double[][] matrix){
        int n = matrix.length;
        int m = matrix[0].length;
        double [][] newMatrix = new double[n][m];
        for(int i = 0; i < n; i++){
            System.arraycopy(matrix[i], 0, newMatrix[i], 0, m);
        }
        return newMatrix;
    }
    
    static boolean checkColumn (Table T, Algoritmo alg, int q, int r){
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
    
    private String createColumn (Table T, Algoritmo alg, int q, int r){
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
            T.removeColumn(column_id);
        }
        
        
        T.addColumn(column_id, column_name, Boolean.class, Origin.DATA, Boolean.FALSE, true);
        return column_id;
    }
    
    private double minkowskiDistance (double a, double b, int r){
        double d;
            if (r == 0){
                d = Math.max(a, b);
            }
            else{
                d = Math.pow((Math.pow(a, r) + Math.pow(b, r)),(1.0/r));
            }        
        return d;
    }
    
    //////º////////////////
    //    ALGORITMOS    //
    //////////////////////
    
    private int originalPathfinder (Graph G, int n, int q, int r, boolean invertida){
        List<Node> nodes = new ArrayList<>(Arrays.asList(G.getNodes().toArray()));
        
        double [][][] W = new double[q][n][n];
        W[0] = createMatrix(G, nodes, invertida);
        double [][] D = new double[n][n];
                
        double d;
        double min, min_local;
        
        for (int k=1; k<q; k++){
            for (int i=0; i<n; i++){
                for (int j=0; j<n; j++){
                    min = 0.0;
                    if (i!=j){
                        for (int m=0; m<n; m++){
                            if (m!=i && m!=j && W[0][i][m]!=0 && W[k-1][m][j]!=0){
                                d = minkowskiDistance (W[0][i][m], W[k-1][m][j], r);
                                if ((min > d || min == 0.0) && d!=0.0){
                                    min = d;
                                }
                            }
                        }
                    }
                    W[k][i][j] = min;
                }
            }
        }
        
        for (int i=0; i<n; i++){
            for (int j=0; j<n; j++){
                min = 0.0;
                if(i!=j){
                    for (int k=0; k<q; k++){
                        if (W[k][i][j]!=0.0 && (min > W[k][i][j] || min==0.0)){
                            min = W[k][i][j];
                        }
                    }
                }
                D[i][j] = min;
            }
        }
        
        Edge[] edges = G.getEdges().toArray();
        int pfEdgesCount = 0;
        
        int source_pos;
        int target_pos;
        Table table = edges[0].getTable();
        
        String column_id = createColumn(table, Algoritmo.originalPF, q, r);
        
        for (Edge edge: edges) {
            source_pos = nodes.indexOf(edge.getSource());
            target_pos = nodes.indexOf(edge.getTarget());
            
            if (D[source_pos][target_pos] == W[0][source_pos][target_pos] && source_pos!=target_pos && D[source_pos][target_pos]>0.0){
                edge.setAttribute(column_id, true);
                pfEdgesCount++;
            }
        }
        
        return pfEdgesCount;
    }
    
    private int binaryPathfinder (Graph G, int n, int q, int r, boolean invertida){
        List<Node> nodes = new ArrayList<>(Arrays.asList(G.getNodes().toArray()));
        double [][] W = createMatrix(G, nodes, invertida);
        
        int i = 1;
        int nq = 0;
        
        double [][] D = cloneMatrix(W);
        
        double [][] D_q = new double [n][n];
        for (int l=0; l<n; l++){
            for (int j=0; j<n; j++){
                D_q[l][j] = Double.MAX_VALUE;
            }
        }
        
        double [][] D_2 = new double[n][n];
        
        double min;
        double local_min;
        if (q%2 == 1){
            for (int l=0; l<n; l++){
                for (int j=0; j<n; j++){
                    if (D_q[l][j]>0.0 && D[l][j]>0.0){
                        min = Math.min(D_q[l][j], D[l][j]);
                    }
                    else{
                        min = Math.max(D_q[l][j], D[l][j]);
                    }
                    for (int m=0; m<n; m++){
                        if (D_q[l][m]>0.0 && D[m][j]>0.0){
                            if (min > 0.0){
                                min = Math.min(min, minkowskiDistance(D_q[l][m], D[m][j], r));
                            }
                            else{
                                min = minkowskiDistance(D_q[l][m], D[m][j], r);
                            }
                        }
                    }
                    D_2[l][j] = min;
                }
            }
        }
        
        D_q = Arrays.stream(D_2).map(double[]::clone).toArray(double[][]::new);
        
        nq = 1;
        
        double [][] D_2i = new double[n][n];
        while (2*i <= q){
            for (int l=0; l<n; l++){
                for (int j=0; j<n; j++){
                    min = D[l][j];
                    for (int m=0; m<n; m++){
                        if (D[l][m]>0.0 && D[m][j]>0.0){
                            if (min > 0.0){
                                min = Math.min(min, minkowskiDistance(D[l][m], D[m][j], r));
                            }
                            else{
                                min = minkowskiDistance(D_q[l][m], D[m][j], r);
                            }
                        }
                    }
                    D_2[l][j] = min;
                }
            }
            D = Arrays.stream(D_2).map(double[]::clone).toArray(double[][]::new);
            if ((q-nq)%(4*i) > 0){
                for (int l=0; l<n; l++){
                    for (int j=0; j<n; j++){
                        if (D_q[l][j]>0.0 && D[l][j]>0.0){
                            min = Math.min(D_q[l][j], D[l][j]);
                        }
                        else{
                            min = Math.max(D_q[l][j], D[l][j]);
                        }
                        for (int m=0; m<n; m++){
                            if (D_q[l][m]>0.0 && D[m][j]>0.0){
                                if (min > 0.0){
                                    min = Math.min(min, minkowskiDistance(D_q[l][m], D[m][j], r));
                                }
                                else{
                                    min = minkowskiDistance(D_q[l][m], D[m][j], r);
                                }
                            }
                        }
                        D_2[l][j] = min;
                    }
                }
                D_q = Arrays.stream(D_2).map(double[]::clone).toArray(double[][]::new);
                nq = nq + 2*i;
            }
            i = 2*i;
        }
        
        Edge[] edges = G.getEdges().toArray();
        int pfEdgesCount = 0;
        
        int source_pos;
        int target_pos;
        Table table = edges[0].getTable();
        
        String column_id = createColumn(table, Algoritmo.binaryPF, q, r);
        
        for (Edge edge: edges) {
            source_pos = nodes.indexOf(edge.getSource());
            target_pos = nodes.indexOf(edge.getTarget());
            
            if (D_q[source_pos][target_pos] == W[source_pos][target_pos] && source_pos!=target_pos && D_q[source_pos][target_pos]>0.0){
                edge.setAttribute(column_id, true);
                pfEdgesCount++;
            }
        }
        
        return pfEdgesCount;
    }
    
    private int fastPathfinder (Graph G, int n, int q, int r, boolean invertida){
        q = n-1;   // Esta versión solo es válida para q=n-1. Aquí lo forzamos por si no se ha puesto correctamente antes.
        
        List<Node> nodes = new ArrayList<>(Arrays.asList(G.getNodes().toArray()));
        
        double [][] W = createMatrix(G, nodes, invertida);
        double [][] D = cloneMatrix(W);
        
        for (int k=0; k<q; k++){
            for (int i=0; i<n; i++){
                for (int j=0; j<n; j++){
                    if (i!=j && k!=i && k!=j && D[i][k]>0.0 && D[k][j]>0.0){
                        double d = minkowskiDistance (D[i][k], D[k][j], r);
                        if (D[i][j]>0.0){
                            D[i][j] = Math.min(D[i][j], d);
                        }
                        else{
                            D[i][j] = d;
                        }
                    }
                }
            }
        }
        
        Edge[] edges = G.getEdges().toArray();
        int pfEdgesCount = 0;
        
        int source_pos;
        int target_pos;
        Table table = edges[0].getTable();
        
        String column_id = createColumn(table, Algoritmo.fastPF, q, r);
        
        for (Edge edge: edges) {
            source_pos = nodes.indexOf(edge.getSource());
            target_pos = nodes.indexOf(edge.getTarget());
            
            if (D[source_pos][target_pos] == W[source_pos][target_pos] && source_pos!=target_pos && W[source_pos][target_pos]!=0.0){
                edge.setAttribute(column_id, true);
                pfEdgesCount++;
            }
        }
        return pfEdgesCount;
    }
    
    private int fastPathfinderModified (Graph G, int n, int q, int r, boolean invertida){
        List<Node> nodes = new ArrayList<>(Arrays.asList(G.getNodes().toArray()));
        
        double [][] W = createMatrix(G, nodes, invertida);
        double [][] D = cloneMatrix(W);
        int [][] steps = new int[n][n];
        
        for (int i=0; i<n; i++){
            for (int j=0; j<n; j++){
                if (i==j){
                    steps[i][j] = 0;
                }
                else{
                    steps[i][j] = 1;
                }
            }
        }
        
        for (int k=0; k<n; k++){
            for (int i=0; i<n; i++){
                for (int j=0; j<n; j++){
                    if (k!=i && k!=j && D[i][k]>0.0 && D[k][j]>0.0){
                        if (q >= 2 && W[i][k]>0.0 && W[k][j]>0.0){
                            double d;
                            d = minkowskiDistance(W[i][k], W[k][j], r);
                            if (d < D[i][j]){
                                D[i][j] = d;
                                steps[i][j] = 2;
                            }
                        }
                        if (q >= steps[i][k]+steps[k][j] && D[i][k]>0.0 && D[k][j]>0.0){
                            double d;
                            d = minkowskiDistance(D[i][k], D[k][j], r);
                            if (d < D[i][j]){
                                D[i][j] = d;
                                steps[i][j] = steps[i][k] + steps[k][j];
                            }
                        }
                    }
                }
            }
        }
        
        Edge[] edges = G.getEdges().toArray();
        int pfEdgesCount = 0;
        
        int source_pos;
        int target_pos;
        Table table = edges[0].getTable();
        
        String column_id = createColumn(table, Algoritmo.fastPFmodified, q, r);
        
        for (Edge edge: edges) {
            source_pos = nodes.indexOf(edge.getSource());
            target_pos = nodes.indexOf(edge.getTarget());
            
            if (D[source_pos][target_pos] == W[source_pos][target_pos] && source_pos!=target_pos && W[source_pos][target_pos]!=0.0){
                edge.setAttribute(column_id, true);
                pfEdgesCount++;
            }
        }
        return pfEdgesCount;
    }
    
    private int MSTPathfinderPractical (Graph G, int n, int q, int r, boolean invertida){
        return -1;
    }
    
    private int MSTPathfinderTheorical (Graph G, int n, int q, int r, boolean invertida){
        return -1;
    }
    
    //////////////////////
    //  FIN ALGORITMOS  //
    //////////////////////
            
    boolean compute (Algoritmo algorithm, Graph graph, int q, int r, boolean invertida){
        
        int edgesCount=-1;
        
        int n = graph.getNodeCount();
        
        switch(algorithm){
            case originalPF:
                edgesCount = originalPathfinder(graph, n, q, r, invertida);
                break;
            case binaryPF:
                edgesCount = binaryPathfinder(graph, n, q, r, invertida);
                break;
            case fastPF:
                edgesCount = fastPathfinder(graph, n, q, r, invertida);
                break;
            case fastPFmodified:
                edgesCount = fastPathfinderModified(graph, n, q, r, invertida);
                break;
        }
        
        log.write("Número de aristas con "+algorithm.name+": ", edgesCount);
        return true;
    }
}

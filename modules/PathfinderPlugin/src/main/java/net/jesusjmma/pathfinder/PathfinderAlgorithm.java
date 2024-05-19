package net.jesusjmma.pathfinder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.gephi.graph.api.Column;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.Node;
import org.gephi.graph.api.Origin;
import org.gephi.graph.api.Table;
import org.openide.util.Pair;

/**
 *
 * @author jesusjmma
 */

class Cluster{
    private static final ConcurrentHashMap<Cluster, HashSet<Cluster>> map = new ConcurrentHashMap<>();
    private Cluster representative;

    Cluster(){
        this.representative = this;
        HashSet<Cluster> clusters = new HashSet<>();
        clusters.add(this);
        map.put(this, clusters);
    }
    
    Cluster findRepresentative() {
        if (this.representative != this) {
            this.representative = this.representative.findRepresentative();
        }
        return this.representative;
    }

    void merge(Cluster c){
        Cluster rep1 = this.findRepresentative();
        Cluster rep2 = c.findRepresentative();
        
        HashSet<Cluster> set1 = map.get(rep1);
        HashSet<Cluster> set2 = map.get(rep2);
        
        if (set1.size() < set2.size()){
            set2.addAll(set1);
            for(Cluster cluster : set1){
                cluster.representative = rep2;
                map.remove(cluster);
            }
            map.put(rep2, set2);
        }
        else{
            set1.addAll(set2);
            for(Cluster cluster : set2){
                cluster.representative = rep1;
                map.remove(cluster);
            }
            map.put(rep1, set1);
        }
    }
}

public class PathfinderAlgorithm {
    
    private static final Log log = new Log("./log_gephi.txt");
    
    static enum Algoritmo{
        originalPF("Original Pathfinder", true, true, true),
        binaryPF("Binary Pathfinder", true, true, true),
        fastPF("Fast Pathfinder", true, false, true),
        fastPFmodified("Fast Pathfinder Modified", true, true, true),
        MSTPathfinderTheorical("MST-Pathfinder Theorical", false, false, false),
        //MSTPathfinderPractical("MST-Pathfinder Practical", false, false),
        ;
        
        private final String name;
        private final boolean r_admisible;
        private final boolean q_admisible;
        private final boolean directedOption;
        
        private Algoritmo(final String text, final boolean r, final boolean q, final boolean directed){
            this.name = text;
            this.r_admisible = r;
            this.q_admisible = q;
            this.directedOption = directed;
        }
        
        @Override
        public String toString() {
            return name;
        }
        
        public boolean rAdmisible(){
            return r_admisible;
        }
        
        public boolean qAdmisible(){
            return q_admisible;
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
    /*
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
    
    ///////////////////////
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
    
    private int MSTPathfinderTheorical (Graph G, int n, int q, int r, boolean invertida){
        List<Edge> T = new ArrayList<>();
        List<Node> nodes = new ArrayList<>(Arrays.asList(G.getNodes().toArray()));
        
        ConcurrentHashMap<Node, Cluster> map = new ConcurrentHashMap<>();
        
        for (int i=0; i<nodes.size(); i++){
            Cluster cluster = new Cluster();
            map.put(nodes.get(i), cluster);
        }
        
        Comparator<Edge> edges_comparator = (Edge e1, Edge e2) -> Double.compare(e1.getWeight(), e2.getWeight());
        
        ArrayList<Edge> F = new ArrayList<>(Arrays.asList(G.getEdges().toArray()));
        if (invertida){
            F.sort(edges_comparator.reversed());
        }
        else{
            F.sort(edges_comparator);
        }
        
        int i=0;
        while (i < F.size()) {
            ConcurrentHashMap<Cluster,Cluster> H = new ConcurrentHashMap<>();
            final double actualWeight = F.get(i).getWeight();
            Edge edge;
            while (i<F.size() && actualWeight==(edge = F.get(i)).getWeight()){
                Node n1 = edge.getSource();
                Node n2 = edge.getTarget();
                Cluster c1 = map.get(n1);
                Cluster c2 = map.get(n2);
                
                if(c1.findRepresentative() != c2.findRepresentative()){
                    T.add(edge);
                    H.put(c1,c2);
                }
                i++;
            }
            H.forEach((Cluster k, Cluster v) -> k.merge(v));
            /*
            for (Pair<Cluster, Cluster> c: H){
                c.first().merge(c.second());
            }
            //*/
        }
        
        int pfEdgesCount = 0;
        Table table = T.get(0).getTable();
        
        String column_id = createColumn(table, Algoritmo.MSTPathfinderTheorical, n-1, 0);
        
        for (Edge edge: T) {
            edge.setAttribute(column_id, true);
            pfEdgesCount++;
        }
        return pfEdgesCount;
    }
    
    private int MSTPathfinderPractical (Graph G, int n, int q, int r, boolean invertida){
        return -1;
    }
    
    //////////////////////
    //  FIN ALGORITMOS  //
    //////////////////////
            
    boolean compute (Algoritmo algorithm, Graph graph, int q, int r, boolean invertida){
        
        int edgesCount=-1;
        
        int n = graph.getNodeCount();
        int edges = graph.getEdgeCount();
        
        long startTime = System.nanoTime();
        
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
            case MSTPathfinderTheorical:
                edgesCount = MSTPathfinderTheorical(graph, n, q, r, invertida);
                break;
        }
        
        long endTime = System.nanoTime();
        
        long duration_ns = endTime-startTime;
        double duration_s = duration_ns / 1000000000.0;
        
        log.write("==========================================");
        log.write("=== ALGORITMO "+algorithm.name+" ===");
        log.write("Tiempo (ns): "+String.valueOf(duration_ns));
        log.write("Tiempo (s): "+String.valueOf(duration_s));
        log.write("Nodos iniciales: ", n);
        log.write("q: ", q);
        log.write("r: ", r);
        log.write("Aristas iniciales: ", edges);
        log.write("Aristas podadas: ", edges-edgesCount);
        log.write("Aristas finales: ", edgesCount);
        return true;
    }
}

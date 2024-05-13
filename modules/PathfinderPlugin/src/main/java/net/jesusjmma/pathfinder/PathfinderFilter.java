/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.jesusjmma.pathfinder;

import org.gephi.filters.spi.EdgeFilter;
import org.gephi.filters.spi.FilterProperty;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.Node;

/**
 *
 * @author jesusjmma
 */
public class PathfinderFilter implements EdgeFilter{
    
    /**
     * Indica si el filtro es válido para el grafo dado.á
     * asks if the filter is valid for the given graph
     *
     * @param graph El grafo.
     * @return Si es válido (true) o falso (false).
     */
    public boolean init(Graph graph) {
        return graph.getEdgeCount() > 0;
    }

    public boolean evaluate(Graph graph, Edge edge) {
        return true;
    }

    public void finish() {
    }

    public String getName() {
        return "Pathfinder";
    }

    public FilterProperty[] getProperties() {
        return new FilterProperty[0];
    }
}

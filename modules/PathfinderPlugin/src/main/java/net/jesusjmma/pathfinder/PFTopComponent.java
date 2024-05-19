/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/NetBeansModuleDevelopment-files/templateTopComponent637.java to edit this template
 */
package net.jesusjmma.pathfinder;

import java.awt.Frame;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.accessibility.Accessible;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import net.jesusjmma.pathfinder.PathfinderAlgorithm.Algoritmo;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(
        dtd = "-//net.jesusjmma.pathfinder//PF//EN",
        autostore = false
)
@TopComponent.Description(
        preferredID = "PFTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE",
        persistenceType = TopComponent.PERSISTENCE_NEVER
)
@TopComponent.Registration(mode = "filtersmode", openAtStartup = true)
@ActionID(category = "Window", id = "pathfinderwindow.PFTopComponent")
@ActionReference(path = "Menu/Window", position = 3000)
@TopComponent.OpenActionRegistration(
        displayName = "#expression.gephi_menu_item.title",
        preferredID = "PFTopComponent"
)
@Messages({
    "CTL_PFAction=PF",
    "CTL_PFTopComponent=PF Window",
    "HINT_PFTopComponent=This is a PF window"
})
public final class PFTopComponent extends TopComponent {
    
    private static final ResourceBundle bundle = NbBundle.getBundle(PFTopComponent.class);
    
    public PFTopComponent() {
        initComponents();
        setName(bundle.getString("expression.top_panel.title"));
        setToolTipText(bundle.getString("expression.top_panel.tooltip"));
    }
    
    private boolean graphExists(){
        GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getGraphModel();

        if (graphModel != null){
            Graph graph = graphModel.getGraph();

            if (graph != null){
                return true;
            }
        }
        return false;
    }
    
    private void reInitElement(Accessible a){
        if (jComboBox1 == a){
            Graph graph = Lookup.getDefault().lookup(GraphController.class).getGraphModel().getGraph();
            List<String> algoritmos = new ArrayList<>();

            for (Algoritmo alg : Algoritmo.values()){
                if (!graph.isUndirected() && !alg.directedAdmisible()){
                    continue;
                }
                algoritmos.add(alg.toString());
            }
            jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(algoritmos.toArray(String[]::new)));
        }
        else if (jCheckBox1 == a){
            jCheckBox1.setSelected(true);
        }
        else if (jCheckBox2 == a){
            jCheckBox2.setSelected(true);
        }
        else if (jCheckBox3 == a){
            jCheckBox3.setSelected(false);
        }
        else if (jSpinner1 == a){
            jSpinner1.setModel(new javax.swing.SpinnerNumberModel(((Number)jSpinner1.getValue()).intValue(), 1, 99, 1));
        }
        else if (jSpinner2 == a){
            int n = Lookup.getDefault().lookup(GraphController.class).getGraphModel().getGraph().getNodeCount();
            int max = Math.max(n-1, 1);
            jSpinner2.setModel(new javax.swing.SpinnerNumberModel(((Number)jSpinner2.getValue()).intValue(), 1, max, 1));
        }
        else if (jButton1 == a){}
    }
    
    private void reInitElements(){
        if (jComboBox1.isEnabled() == false){
            reInitElement(jCheckBox1);
        }
        if (jCheckBox2.isEnabled() == false){
            reInitElement(jCheckBox2);
        }
        if (jCheckBox3.isEnabled() == false){
            reInitElement(jCheckBox3);
        }
        if (jSpinner1.isEnabled() == false){
            reInitElement(jSpinner1);
        }
        if (jSpinner2.isEnabled() == false){
            reInitElement(jSpinner2);
        }
        if (jButton1.isEnabled() == false){
            reInitElement(jButton1);
        }
    }
    
    private void reInitAll(){
        if (jComboBox1.isEnabled() == false){
            reInitElement(jComboBox1);
        }
        reInitElements();
    }
    
    private void enableAll(){
        if (graphExists()){
            reInitAll();
            jComboBox1.setEnabled(true);
            jCheckBox1.setEnabled(true);
            jCheckBox2.setEnabled(true);
            jCheckBox3.setEnabled(true);
            jButton1.setEnabled(true);
        }
        else{
            disableAll();
        }
    }
    
    private void disableAll(){
        if (!graphExists()){
            jComboBox1.setEnabled(false);
            jCheckBox1.setSelected(true);
            jCheckBox1.setEnabled(false);
            jCheckBox2.setSelected(true);
            jCheckBox2.setEnabled(false);
            jCheckBox3.setSelected(false);
            jCheckBox3.setEnabled(false);
            jSpinner1.setEnabled(false);
            jSpinner2.setEnabled(false);
            jButton1.setEnabled(false);
        }
        else{
            enableAll();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jComboBox1 = new javax.swing.JComboBox<>();
        jSpinner1 = new javax.swing.JSpinner();
        jCheckBox1 = new javax.swing.JCheckBox();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jSpinner2 = new javax.swing.JSpinner();
        jCheckBox2 = new javax.swing.JCheckBox();
        jCheckBox3 = new javax.swing.JCheckBox();

        setMinimumSize(new java.awt.Dimension(200, 300));
        setName(""); // NOI18N
        addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                formFocusGained(evt);
            }
        });
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                formMouseEntered(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, "Seleccione el algoritmo:");

        org.openide.awt.Mnemonics.setLocalizedText(jButton1, "Run");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        List<String> algoritmos = new ArrayList<String>();
        if (graphExists()){
            Graph graph = Lookup.getDefault().lookup(GraphController.class).getGraphModel().getGraph();
            boolean undirected = graph.isUndirected();
            if (undirected){
                //To do
            }

            for (Algoritmo alg : Algoritmo.values()){
                if (!undirected && !alg.directedAdmisible()){
                    continue;
                }
                algoritmos.add(alg.toString());
            }
        }
        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<String>(algoritmos.toArray(new String[0])));
        jComboBox1.setMinimumSize(new java.awt.Dimension(40, 24));
        jComboBox1.setName(""); // NOI18N
        jComboBox1.setPreferredSize(new java.awt.Dimension(40, 24));
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        jSpinner1.setModel(new javax.swing.SpinnerNumberModel(1, 1, 99, 1));
        jSpinner1.setEnabled(false);
        jSpinner1.setName(""); // NOI18N

        jCheckBox1.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(jCheckBox1, "∞");
        jCheckBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox1ActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, "Valor de las variables:");

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, "r");

        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, "q");

        jSpinner2.setModel(new javax.swing.SpinnerNumberModel(1, 1, 99, 1));
        jSpinner2.setEnabled(false);
        jSpinner2.setName(""); // NOI18N
        jSpinner2.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSpinner2StateChanged(evt);
            }
        });

        jCheckBox2.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(jCheckBox2, "n-1");
        jCheckBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox2ActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jCheckBox3, "<html><p>Tomar pesos de los ejes como grados de relación</p></html>");
        jCheckBox3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jComboBox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(15, 15, 15))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jButton1)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(36, 36, 36)
                                    .addComponent(jCheckBox1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jSpinner2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(36, 36, 36)
                                    .addComponent(jCheckBox2)))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(jCheckBox3)))
                .addGap(28, 28, 28))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(jCheckBox1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jSpinner2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jCheckBox2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jCheckBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15)
                .addComponent(jButton1)
                .addContainerGap(65, Short.MAX_VALUE))
        );

        if(!graphExists()){
            jButton1.setEnabled(false);
        }
        else{
            jButton1.setEnabled(true);
        }
        if(!graphExists()){
            jComboBox1.setEnabled(false);
        }
        else{
            jComboBox1.setEnabled(true);
        }
        if(!graphExists()){
            jSpinner1.setEnabled(false);
        }
        else{
            jSpinner1.setEnabled(true);
        }
        if(!graphExists()){
            jCheckBox1.setEnabled(false);
        }
        else{
            jCheckBox1.setEnabled(true);
        }
        if(!graphExists()){
            jSpinner2.setEnabled(false);
        }
        else{
            jSpinner2.setEnabled(true);
        }
        if(!graphExists()){
            jCheckBox2.setEnabled(false);
        }
        else{
            jCheckBox2.setEnabled(true);
        }
        if(!graphExists()){
            jCheckBox3.setEnabled(false);
        }
        else{
            jCheckBox3.setEnabled(true);
        }
    }// </editor-fold>//GEN-END:initComponents

    
    // BOTÓN PARA EJECUTAR
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        if (!graphExists()){
            disableAll();
            return;
        }
        
        GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getGraphModel();
        Graph graph = graphModel.getGraph();
        int n = graph.getNodeCount();
        
        String selection = jComboBox1.getSelectedItem().toString();
        Algoritmo algorithm = Algoritmo.search(selection);
        
        int q;
        if (jCheckBox2.isSelected()){
            q = n-1;
        }
        else{
            q = ((Number)jSpinner2.getValue()).intValue();
        }
        
        int r;
        if (jCheckBox1.isSelected()){
            r = 0;
        }
        else{
            r = ((Number)jSpinner1.getValue()).intValue();
        }
        
        PathfinderAlgorithm Pathfinder = new PathfinderAlgorithm();

        boolean grados_de_relacion = jCheckBox3.isSelected();
        
        boolean column_calculated = PathfinderAlgorithm.checkColumn(graphModel.getEdgeTable(), algorithm, q, r);
        int calculate = 1;
                
        if (column_calculated){
            calculate = 0;
            OkCancelDialog popUpWindow = new OkCancelDialog((Frame) SwingUtilities.windowForComponent(this), true);
            popUpWindow.setLocationRelativeTo(null);
            popUpWindow.setVisible(true);
            calculate = popUpWindow.getReturnStatus();
        }
        
        if (calculate == 1){
            boolean estado = Pathfinder.compute(algorithm, graph, q, r, grados_de_relacion);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox1ActionPerformed
        if (!graphExists()){
            disableAll();
            return;
        }
        
        if (jCheckBox1.isSelected()){
            jSpinner1.setEnabled(false);
        }
        else{
            Algoritmo algorithm = Algoritmo.search(jComboBox1.getSelectedItem().toString());
            if (!algorithm.rAdmisible()){
                jCheckBox1.setSelected(true);
                jSpinner1.setEnabled(false);
            }
            else{
                jSpinner1.setEnabled(true);
            }
        }
    }//GEN-LAST:event_jCheckBox1ActionPerformed

    private void jCheckBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox2ActionPerformed
        if (!graphExists()){
            disableAll();
            return;
        }
        
        if (jCheckBox2.isSelected()){
            jSpinner2.setEnabled(false);
        }
        else{
            Algoritmo algorithm = Algoritmo.search(jComboBox1.getSelectedItem().toString());
            if (!algorithm.qAdmisible()){
                jCheckBox2.setSelected(true);
                jSpinner2.setEnabled(false);
            }
            else{
                jSpinner2.setEnabled(true);
            }
        }
    }//GEN-LAST:event_jCheckBox2ActionPerformed

    private void jSpinner2StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSpinner2StateChanged
        if (!graphExists()){
            disableAll();
            return;
        }
        
        GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getGraphModel();
        if (graphModel == null){
            jCheckBox2.setSelected(true);
            jSpinner2.setEnabled(false);
            return;
        }
        int n = graphModel.getGraph().getNodeCount();
        
        if (n <= ((Number) jSpinner2.getValue()).intValue()){
            jCheckBox2.setSelected(true);
            jSpinner2.setEnabled(false);
        }
        
        int max_q = n-1;
        int actual_max_q = ((Integer) ((SpinnerNumberModel) jSpinner2.getModel()).getMaximum()).intValue();
        
        if (max_q != actual_max_q){
            int actual_value = ((Number) jSpinner2.getValue()).intValue();
            jSpinner2.setModel(new javax.swing.SpinnerNumberModel(Math.min(actual_value, max_q), 1, max_q, 1));
        }
    }//GEN-LAST:event_jSpinner2StateChanged

    private void jCheckBox3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox3ActionPerformed
        if (!graphExists()){
            disableAll();
            return;
        }
    }//GEN-LAST:event_jCheckBox3ActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        if (!graphExists()){
            disableAll();
            return;
        }
        
        String selection = jComboBox1.getSelectedItem().toString();
        Algoritmo algorithm = Algoritmo.search(selection);
        
        if (!algorithm.rAdmisible()){
            jCheckBox1.setSelected(true);
            jSpinner1.setEnabled(false);
        }
        
        if (!algorithm.qAdmisible()){
            jCheckBox2.setSelected(true);
            jSpinner2.setEnabled(false);
        }
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void formFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_formFocusGained

    }//GEN-LAST:event_formFocusGained

    private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked
        if(graphExists()){
            enableAll();
        }
        if(!graphExists()){
            disableAll();
        }
    }//GEN-LAST:event_formMouseClicked

    private void formMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseEntered
        formMouseClicked(evt);
    }//GEN-LAST:event_formMouseEntered

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JCheckBox jCheckBox3;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JSpinner jSpinner1;
    private javax.swing.JSpinner jSpinner2;
    // End of variables declaration//GEN-END:variables
    @Override
    public void componentOpened() {
        // TODO add custom code on component opening
    }

    @Override
    public void componentClosed() {
        // TODO add custom code on component closing
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }
}

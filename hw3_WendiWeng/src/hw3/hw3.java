/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hw3;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import javax.swing.*;
//import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import org.jdesktop.swingx.JXDatePicker;
/**
 *
 * @author wendi
 */



public class hw3 extends javax.swing.JFrame {
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    private static HashSet<String> mainCategoriesSet = new HashSet();
    private static HashSet<String> subCategoriesSet = new HashSet();
    private static HashSet<String> attributesSet = new HashSet();
    private static StringBuilder mainCategoriesString = new StringBuilder();
    private static StringBuilder subCategoriesString = new StringBuilder();
    private static StringBuilder attributesString = new StringBuilder();
    /**
     * Creates new form main
     */
    public hw3() {
        initComponents();
        try {
            init();
        } catch (SQLException ex) {
            Logger.getLogger(hw3.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(hw3.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
    public void ERROR(String msg) {
        queryTextArea.append(msg);
    }

    private void init() throws SQLException, ClassNotFoundException {
        System.out.println("+++init+++");
        try (Connection connection = Populate.getConnect();){
            StringBuilder sql = new StringBuilder();
            PreparedStatement preparedStatement;
            ResultSet rs;
            
            //init radioButton
            ButtonGroup group = new ButtonGroup();
            group.add(businessRadioButton);
            group.add(userRadioButton);
            
            //init mainCategory
            sql.append("SELECT DISTINCT mainCategory").append("\n")
               .append("FROM MainCategory").append("\n")
               .append("ORDER BY mainCategory");
            preparedStatement = connection.prepareStatement(sql.toString());
            rs = preparedStatement.executeQuery();
            while (rs.next()) {
                String mainCategoryName = rs.getString(rs.findColumn("mainCategory"));
                JCheckBox mc = new JCheckBox(mainCategoryName);
                
                mc.addMouseListener(new MouseListener(){                                   
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        try {
                            JCheckBox mc = (JCheckBox) e.getSource();
                            String mainCategory = mc.getText();
                            if (mc.isSelected()) {
                                mainCategoriesSet.add(mainCategory);
                            }
                            else {
                                mainCategoriesSet.remove(mainCategory);
                            }
                            // get mainCategories from hashSet to arrayList
                            mainCategoriesString.setLength(0);
                            Iterator<String> it = mainCategoriesSet.iterator();
                            while (it.hasNext()) {
                              mainCategoriesString.append("'").append(it.next()).append("',");
                            }
                            if (mainCategoriesString.length() > 0) {
                                mainCategoriesString.deleteCharAt(mainCategoriesString.length() - 1);
                            }
                            System.out.println("DEBUG=========== select mainCategories: " + mainCategoriesString.toString());
                            getSubCategories();
                        } catch (SQLException ex) {
                            Logger.getLogger(hw3.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (ClassNotFoundException ex) {
                            Logger.getLogger(hw3.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                    }
                });
                mCategoryListPanel.add(mc);
            }           
            rs.close();
            preparedStatement.close();
        }
    }
    
    private void getSubCategories() throws SQLException, ClassNotFoundException {
        try (Connection connection = Populate.getConnect()) {
            sCategoryListPanel.removeAll();
            System.out.println("Get subCategories...");
            
            StringBuilder sql = new StringBuilder();
            PreparedStatement preparedStatement;
            ResultSet rs;
            
            HashMap<String, Integer> subCategories = new HashMap();
            Iterator<String> mc = mainCategoriesSet.iterator();
            while (mc.hasNext()) {
                sql.setLength(0);
                sql.append("SELECT DISTINCT sc.subCategory").append("\n")
                   .append("FROM SubCategory sc, MainCategory mc").append("\n")
                   .append("WHERE sc.business_id = mc.business_id AND mc.mainCategory = '").append(mc.next()).append("'\n")
                   .append("ORDER BY sc.subCategory");
                preparedStatement = connection.prepareStatement(sql.toString());
                rs = preparedStatement.executeQuery();
                while (rs.next()) {
                    String subCategory = rs.getString(rs.findColumn("subCategory"));
                    if (subCategories.containsKey(subCategory)) {
                        subCategories.put(subCategory, subCategories.get(subCategory) + 1);
                    }
                    else {
                        subCategories.put(subCategory, 1);
                    }
                }
                rs.close();
                preparedStatement.close();
            }
            List<String> subCategoriesList = new ArrayList();        
            for (Map.Entry<String, Integer> entry: subCategories.entrySet()) {
                if (entry.getValue() == mainCategoriesSet.size()) {
                    subCategoriesList.add(entry.getKey());
                }
            }
            Collections.sort(subCategoriesList);
            for (String scName: subCategoriesList) {
                JCheckBox sc = new JCheckBox(scName);
                
                sc.addMouseListener(new MouseListener(){                                   
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        JCheckBox sc = (JCheckBox) e.getSource();
                        String subCategory = sc.getText();
                        if (sc.isSelected()) {
                            subCategoriesSet.add(subCategory);
                        }
                        else {
                            subCategoriesSet.remove(subCategory);
                        }
                        subCategoriesString.setLength(0);
                        Iterator<String> it = subCategoriesSet.iterator();
                        while (it.hasNext()) {
                            subCategoriesString.append("'").append(it.next()).append("',");
                        }
                        if (subCategoriesString.length() > 0) {
                            subCategoriesString.deleteCharAt(subCategoriesString.length() - 1);
                        }
                        System.out.println("DEBUG=========== select subCategories: " + subCategoriesString.toString() + "\n");
//                        getAttributes();
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                    }
                });
                sCategoryListPanel.add(sc);
            }
            sCategoryListPanel.updateUI();
        }
    }
    
    
    private void getAttributes() throws SQLException, ClassNotFoundException {
        try (Connection connection = Populate.getConnect()) {
            attributeListPanel.removeAll();
            System.out.println("Get attributes...");
            
            StringBuilder sql = new StringBuilder();
            PreparedStatement preparedStatement;
            ResultSet rs;
            
            HashMap<String, Integer> attributesHash = new HashMap();
            Iterator<String> mc = mainCategoriesSet.iterator();
            Iterator<String> sc = subCategoriesSet.iterator();
            while (mc.hasNext()) {
                while (sc.hasNext()) {
                    sql.setLength(0);
                    sql.append("SELECT a.attribute\n")
                       .append("FROM Attribute a, MainCategory mc, SubCategory sc\n")
                       .append("WHERE a.business_id = mc.business_id AND a.business_id =  sc.business_id")
                       .append(" AND mc.mainCategory = '").append(mc.next()).append("' AND sc.subCategory = '").append(sc.next()).append("'\n")
                       .append("ORDER BY a.attribute\n");
                    System.out.println("DEBUG============== select attributes: " + sql.toString());
                    preparedStatement = connection.prepareStatement(sql.toString());
                    rs = preparedStatement.executeQuery();
                    while (rs.next()) {
                        String attribute = rs.getString(rs.findColumn("attribute"));
                        if (attributesHash.containsKey(attribute)) {
                            attributesHash.put(attribute, attributesHash.get(attribute) + 1);
                        }
                        else {
                            attributesHash.put(attribute, 1);
                        }
                    }
                    rs.close();
                    preparedStatement.close();
                }
            }
            List<String> attributesList = new ArrayList();        
            for (Map.Entry<String, Integer> entry: attributesHash.entrySet()) {
                if (entry.getValue() == mainCategoriesSet.size() * subCategoriesSet.size()) {
                    attributesList.add(entry.getKey());
                }
            }
            Collections.sort(attributesList);
            for (String aName: attributesList) {
                JCheckBox a = new JCheckBox(aName);
                
                a.addMouseListener(new MouseListener(){                                   
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        JCheckBox a = (JCheckBox) e.getSource();
                        String attribute = a.getText();
                        if (a.isSelected()) {
                            attributesSet.add(attribute);
                        }
                        else {
                            attributesSet.remove(attribute);
                        }
                        attributesString.setLength(0);
                        Iterator<String> it = attributesSet.iterator();
                        while (it.hasNext()) {
                            attributesString.append("'").append(it.next()).append("',");
                        }
                        if (attributesString.length() > 0) {
                            attributesString.deleteCharAt(attributesString.length() - 1);
                        } 
                        System.out.println("DEBUG=========== attributes: " + attributesString.toString());
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                    }
                });
                attributeListPanel.add(a);
            }
            attributeListPanel.updateUI();
        }
    }
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        View = new javax.swing.JPanel();
        businessPanel = new javax.swing.JPanel();
        categoriesPanel = new javax.swing.JPanel();
        mainCategoryPanel = new javax.swing.JPanel();
        categoryLabel = new javax.swing.JLabel();
        mainCategoryScrollPane = new javax.swing.JScrollPane();
        mCategoryListPanel = new javax.swing.JPanel();
        subCategoryPanel = new javax.swing.JPanel();
        subCategoryLabel = new javax.swing.JLabel();
        subCategoryScrollPane = new javax.swing.JScrollPane();
        sCategoryListPanel = new javax.swing.JPanel();
        attributePanel = new javax.swing.JPanel();
        attributeLabel = new javax.swing.JLabel();
        attributeScrollPane = new javax.swing.JScrollPane();
        attributeListPanel = new javax.swing.JPanel();
        queryPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        Selection = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        queryButton = new javax.swing.JButton();
        queryButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(null);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        View.setBackground(new java.awt.Color(255, 204, 255));
        View.setToolTipText("hw3");
        View.setMinimumSize(new java.awt.Dimension(390, 52));
        View.setPreferredSize(new java.awt.Dimension(1000, 500));
        View.setLayout(new java.awt.GridLayout(1, 2));

        businessPanel.setBackground(new java.awt.Color(0, 0, 204));
        businessPanel.setBorder(javax.swing.BorderFactory.createCompoundBorder());
        businessPanel.setToolTipText("Business");
        businessPanel.setLayout(new java.awt.BorderLayout());

        categoriesPanel.setLayout(new java.awt.GridLayout(1, 3));

        mainCategoryPanel.setLayout(new java.awt.BorderLayout());

        categoryLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        categoryLabel.setText("Category");
        mainCategoryPanel.add(categoryLabel, java.awt.BorderLayout.PAGE_START);

        mCategoryListPanel.setBackground(new java.awt.Color(153, 153, 255));
        mCategoryListPanel.setLayout(new java.awt.GridLayout(0, 1));

        //for (int i = 0; i < 20; i++) {
            //    mCategoryListPanel.add(new JCheckBox("aaa"));
            //    if (i == 18) {
                //        ((JCheckBox) mCategoryListPanel.getComponent(i)).setSelected(true);
                //    }
            //}

        mainCategoryScrollPane.setViewportView(mCategoryListPanel);

        mainCategoryPanel.add(mainCategoryScrollPane, java.awt.BorderLayout.CENTER);

        categoriesPanel.add(mainCategoryPanel);

        subCategoryPanel.setLayout(new java.awt.BorderLayout());

        subCategoryLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        subCategoryLabel.setText("Sub-category");
        subCategoryPanel.add(subCategoryLabel, java.awt.BorderLayout.PAGE_START);

        sCategoryListPanel.setBackground(new java.awt.Color(102, 153, 255));
        sCategoryListPanel.setLayout(new java.awt.GridLayout(0, 1));

        //for (int i = 0; i < 20; i++) {
            //    sCategoryListPanel.add(new JCheckBox("bbbb"));
            //    if (i == 1) {
                //        ((JCheckBox) sCategoryListPanel.getComponent(i)).setSelected(true);
                //    }
            //}

        subCategoryScrollPane.setViewportView(sCategoryListPanel);

        subCategoryPanel.add(subCategoryScrollPane, java.awt.BorderLayout.CENTER);

        categoriesPanel.add(subCategoryPanel);

        attributePanel.setLayout(new java.awt.BorderLayout());

        attributeLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        attributeLabel.setText("Store");
        attributePanel.add(attributeLabel, java.awt.BorderLayout.PAGE_START);

        attributeListPanel.setBackground(new java.awt.Color(204, 255, 255));
        attributeListPanel.setLayout(new java.awt.GridLayout(0, 1));

        //for (int i = 0; i < 20; i++) {
            //    attributeListPanel.add(new JCheckBox("ccc"));
            //    if (i == 2) {
                //        ((JCheckBox) attributeListPanel.getComponent(i)).setSelected(true);
                //    }
            //}

        attributeScrollPane.setViewportView(attributeListPanel);

        attributePanel.add(attributeScrollPane, java.awt.BorderLayout.CENTER);

        categoriesPanel.add(attributePanel);

        businessPanel.add(categoriesPanel, java.awt.BorderLayout.CENTER);

        View.add(businessPanel);

        queryPanel.setToolTipText("");
        queryPanel.setPreferredSize(new java.awt.Dimension(500, 200));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        javax.swing.GroupLayout queryPanelLayout = new javax.swing.GroupLayout(queryPanel);
        queryPanel.setLayout(queryPanelLayout);
        queryPanelLayout.setHorizontalGroup(
            queryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(queryPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 511, Short.MAX_VALUE)
                .addContainerGap())
        );
        queryPanelLayout.setVerticalGroup(
            queryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(queryPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 519, Short.MAX_VALUE))
        );

        View.add(queryPanel);

        getContentPane().add(View, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1063, 530));

        Selection.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        Selection.setPreferredSize(new java.awt.Dimension(1063, 100));

        jButton1.setText("Search");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        queryButton.setBackground(new java.awt.Color(255, 0, 255));
        queryButton.setText("Search");
        queryButton.setToolTipText("");
        queryButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                queryButtonMouseClicked(evt);
            }
        });

        queryButton1.setBackground(new java.awt.Color(255, 0, 255));
        queryButton1.setText("Close");
        queryButton1.setToolTipText("");
        queryButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                queryButton1MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout SelectionLayout = new javax.swing.GroupLayout(Selection);
        Selection.setLayout(SelectionLayout);
        SelectionLayout.setHorizontalGroup(
            SelectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SelectionLayout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 731, Short.MAX_VALUE)
                .addComponent(queryButton, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(queryButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32))
        );
        SelectionLayout.setVerticalGroup(
            SelectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, SelectionLayout.createSequentialGroup()
                .addContainerGap(27, Short.MAX_VALUE)
                .addGroup(SelectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(queryButton)
                    .addComponent(queryButton1))
                .addGap(26, 26, 26))
        );

        getContentPane().add(Selection, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 530, -1, 80));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void queryButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_queryButtonMouseClicked
        // TODO add your handling code here:
        DefaultTableModel defaultTableModel;
        String[][] data;
        
        try (Connection connection = Populate.getConnect();) {
            StringBuilder query = new StringBuilder();
            PreparedStatement preparedStatement;
            ResultSet rs;
            queryTextArea.setText("<Show Query Here:> \n\n");
            // if it's business turn
            if (businessRadioButton.isSelected()) {
                if (mainCategoriesString.length() == 0) {
                    ERROR("ERROR: ON ACTION ON SELECT \"Category\"!\n\n");
                    return; 
                }
                else {
                    query = getBusinessQueryString();
                }
            }
            // if it's user turn
            else if (userRadioButton.isSelected()) {
                query = getUserQueryString();
            }
            else {
                ERROR("ERROR: ON ACTION SELECT BUSINESS INTERFACE OR USER INTERFACE!\n\n");
                return;
            }
            
            if (query.length() == 0) {
                ERROR("ERROR: NO ACTION!\n\n");
                return;
            }
            preparedStatement = connection.prepareStatement(query.toString(), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = preparedStatement.executeQuery();
            
            rs.last();
            ResultSetMetaData rsmd = rs.getMetaData();
            int rowCount = rs.getRow();
            int columnCount = rsmd.getColumnCount();
            data = new String[rowCount][columnCount];
            String[] columnNames = new String[columnCount];
            
            // get column names
            for (int i = 1; i <= columnCount; i++) {
                columnNames[i - 1] = rsmd.getColumnName(i);
            }
            
            rs.beforeFirst();
            for (int i = 0; i < rowCount; i++) {
                if (rs.next()) {
                    for (int j = 1; j <= columnCount; j++) {
                        data[i][j - 1] = rs.getString(j);
                    }
                }
            }
            rs.close();
            preparedStatement.close();
            defaultTableModel = new DefaultTableModel(data, columnNames);
            resultTable.setModel(defaultTableModel);
            queryTextArea.append(query.toString());
            
            // lisening to resultTable and get the information of review
            resultTable.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 1) {
                        JTable target = (JTable)e.getSource();
                        int row = target.getSelectedRow();
                        String id = resultTable.getModel().getValueAt(row, 1).toString();
                        try {
                            showReview(id);
                        } catch (SQLException ex) {
                            Logger.getLogger(hw3.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (ClassNotFoundException ex) {
                            Logger.getLogger(hw3.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            });

        } catch (SQLException ex) {
            Logger.getLogger(hw3.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(hw3.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_queryButtonMouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    private void queryButton1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_queryButton1MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_queryButton1MouseClicked

    
    private StringBuilder getBusinessQueryString() {
        StringBuilder query = new StringBuilder();
        
        //get query from category
        query.append("SELECT b.name, b.business_id, b.city, b.state, b.stars, mc.MainCategory\n")
             .append("FROM Business b, MainCategory mc\n")
             .append("WHERE b.business_id = mc.business_id AND mc.mainCategory IN (").append(mainCategoriesString).append(")\n");
        
        //check subcategory and get query from subCategory
        if (subCategoriesString.length() != 0) {
            query.append("\nAND b.business_id IN (\n")
                 .append("  SELECT bc.business_id\n")
                 .append("  FROM SubCategory bc\n")
                 .append("  WHERE bc.subCategory IN (").append(subCategoriesString).append(")\n")
                 .append(")\n");
        }
        
        //check star and votes and get query from review
        boolean from = isValidDateFormat(fromTextField.getText());
        boolean to = isValidDateFormat(fromTextField.getText());
        boolean stars = isNumeric(starTextField.getText()) && starComboBox.getSelectedIndex() > 0;
        boolean votes = isNumeric(votesTextField.getText()) && votesComboBox.getSelectedIndex() > 0;
        if ((from && to) || stars || votes) {
            query.append("\nAND b.business_id IN (\n")
                 .append("  SELECT r.business_id\n")
                 .append("  FROM Review r\n")
                 .append("  WHERE r.business_id = r.business_id\n");
            if (from && to) {
                query.append("          AND r.review_date >= '").append(getDate(fromTextField.getText())).append("' AND r.review_date <= '").append(getDate(toTextField.getText())).append("'\n");
            }
            if (stars) {
                query.append("          AND r.stars ").append(starComboBox.getSelectedItem().toString()).append(" ").append(starTextField.getText()).append("\n");
            }
                 
            if (votes) {
                query.append("          AND r.votes ").append(votesComboBox.getSelectedItem().toString()).append(" ").append(votesTextField.getText()).append("\n");
            }
            query.append(")\n");
        }
        
        System.out.println("DEBUG==============business query: \n" + query.toString());
        return query;
    }
    
    private StringBuilder getUserQueryString() {
        StringBuilder query = new StringBuilder();
        if (userSearchForComboBox.getSelectedIndex() < 1) {
            return query;
        }
        boolean memberSince = isValidDateFormat(memberSinceTextField.getText());
        boolean reviewCount = isNumeric(reviewCountTextField.getText()) && reviewCountComboBox.getSelectedIndex() > 0;
        boolean numberOfFriends = isNumeric(numberOfFriendsTextField.getText()) && numberOfFriendsComboBox.getSelectedIndex() > 0;
        boolean averageStars = isNumeric(averageStarsTextField.getText()) && averageStarsComboBox.getSelectedIndex() > 0;
        boolean numberOfVotes = isNumeric(numberOfVotesTextField.getText()) && numberOfVotesComboBox.getSelectedIndex() > 0;
        String selector = userSearchForComboBox.getSelectedItem().toString();
        query.append("SELECT y.name, y.user_id, y.yelping_since, y.review_count, y.friend_count, y.average_stars, y.votes\n")
             .append("FROM YelpUser y\n");
        query.append("WHERE y.name = y.name");
        if (memberSince || reviewCount || numberOfFriends || averageStars || numberOfVotes) {         
            //check Review Count and get value
            if (memberSince) {
                query.append(" ").append(selector)
                     .append(" y.yelping_since >= '").append(getDate(memberSinceTextField.getText())).append("'");
            }
            if (reviewCount) {
                query.append(" ").append(selector)
                     .append(" y.review_count ").append(reviewCountComboBox.getSelectedItem()).append(" ").append(reviewCountTextField.getText());
            }
            if (numberOfFriends) {
                query.append(" ").append(selector)
                     .append(" y.friend_count ").append(numberOfFriendsComboBox.getSelectedItem()).append(" ").append(numberOfFriendsTextField.getText());
            }
            if (averageStars) {
                query.append(" ").append(selector)
                     .append(" y.average_stars ").append(averageStarsComboBox.getSelectedItem()).append(" ").append(averageStarsTextField.getText());
            }
            if (numberOfVotes) {
                 query.append(" ").append(selector)
                      .append(" y.votes ").append(numberOfVotesComboBox.getSelectedItem()).append(" ").append(numberOfVotesTextField.getText());
            }
        }
        
        //check star and votes and get query from review
        boolean from = isValidDateFormat(fromTextField.getText());
        boolean to = isValidDateFormat(toTextField.getText());
        boolean star = isNumeric(starTextField.getText()) && starComboBox.getSelectedIndex() > 0;
        boolean votes = isNumeric(votesTextField.getText()) && votesComboBox.getSelectedIndex() > 0;
        if ((from && to )|| star || votes) {
            query.append("\n\nAND y.user_id IN (\n")
                 .append("  SELECT r.user_id\n")
                 .append("  FROM Review r\n")
                 .append("  WHERE r.business_id = r.business_id\n");
            if (from && to) {
                query.append("          AND r.review_date >= '").append(fromTextField.getText()).append("' AND r.review_date <= '").append(toTextField.getText()).append("'");
            }
            if (star) {
                query.append("          AND r.stars " + starComboBox.getSelectedItem().toString() + " " + starTextField.getText() + "\n");
            }                
            if (votes) {
                query.append("          AND r.votes" + votesComboBox.getSelectedItem().toString() + " " + votesTextField.getText() + "\n");
            }
            query.append(")\n");
        }
        System.out.println("DEBUG============== user query: \n" + query.toString());
        return query;
    }
    
    private void showReview(String id) throws SQLException, ClassNotFoundException {
        System.out.println("Get review information...");
        JFrame reviewFrame = new JFrame("Review");
        reviewFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        reviewFrame.setSize(500, 600);
        reviewFrame.setLayout(new GridLayout(1, 1));
        reviewFrame.setVisible(true);
        TableModel dataModel = new DefaultTableModel();
        JTable reviewTable = new JTable(dataModel);
        JScrollPane scrollpane = new JScrollPane(reviewTable);
        
        DefaultTableModel defaultTableModel;
        String[][] data;
        try (Connection connection = Populate.getConnect();) {
            StringBuilder query = new StringBuilder();
            PreparedStatement preparedStatement;
            ResultSet rs;
            query.append("SELECT y.name, r.business_id, r.user_id, r.review_date, r.stars, r.votes\n")
                 .append("FROM Review r, YelpUser y\n")
                 .append("WHERE r.user_id = y.user_id");
            if (businessRadioButton.isSelected()) {
                query.append(" AND r.business_id = '").append(id).append("'\n");
            }
            else if (userRadioButton.isSelected()) {
                query.append(" AND r.user_id = '").append(id).append("'\n");
            }
            if (isValidDateFormat(fromTextField.getText()) && isValidDateFormat(toTextField.getText())) {
                query.append(" AND r.review_date >= '").append(getDate(fromTextField.getText())).append("' AND r.review_date <= '").append(getDate(toTextField.getText())).append("'");
            }
            if (isNumeric(starTextField.getText()) && starComboBox.getSelectedIndex() > 0){
                query.append(" AND r.stars ").append(starComboBox.getSelectedItem().toString()).append(" ").append(starTextField.getText()).append("\n");
            }
            if (isNumeric(votesTextField.getText()) && votesComboBox.getSelectedIndex() > 0) {
                query.append(" AND r.votes ").append(votesComboBox.getSelectedItem().toString()).append(" ").append(votesTextField.getText()).append("\n");
            }
            System.out.println("DEBUG================= review query: \n" + query.toString());
            preparedStatement = connection.prepareStatement(query.toString(), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = preparedStatement.executeQuery();

            rs.last();
            ResultSetMetaData rsmd = rs.getMetaData();
            int rowCount = rs.getRow();
            int columnCount = rsmd.getColumnCount();
            data = new String[rowCount][columnCount];
            String[] columnNames = new String[columnCount];

            // get column names
            for (int i = 1; i <= columnCount; i++) {
                columnNames[i - 1] = rsmd.getColumnName(i);
            }

            rs.beforeFirst();
            for (int i = 0; i < rowCount; i++) {
                if (rs.next()) {
                    for (int j = 1; j <= columnCount; j++) {
                        data[i][j - 1] = rs.getString(j);
                    }
                }
            }
            rs.close();
            preparedStatement.close();
            defaultTableModel = new DefaultTableModel(data, columnNames);
            reviewTable.setModel(defaultTableModel);
        

            reviewFrame.add(scrollpane);
        }
    }
    
    private boolean isNumeric(String s) {
        if (s == null || s.length() == 0) {
            return false;
        }
        try {
            Integer num = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
    
    private static String getDate(String inDate) {
        SimpleDateFormat formater = new SimpleDateFormat(DATE_FORMAT);
        DateFormat format = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
        Date date = null;
        try {
            date = format.parse(inDate);
        } catch (ParseException ex) {
            Logger.getLogger(hw3.class.getName()).log(Level.SEVERE, null, ex);
        }
        return formater.format(date);
    }
    
    private static boolean isValidDateFormat(String inDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        dateFormat.setLenient(false);
        try {
          dateFormat.parse(inDate.trim());
        } catch (ParseException pe) {
          return false;
        }
        return true;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(hw3.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(hw3.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(hw3.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(hw3.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new hw3().setVisible(true);
            }
        });
    }
    
//    private JXDatePicker datePicker = new JXDatePicker(System.currentTimeMillis());

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel Selection;
    private javax.swing.JPanel View;
    private javax.swing.JLabel attributeLabel;
    private javax.swing.JPanel attributeListPanel;
    private javax.swing.JPanel attributePanel;
    private javax.swing.JScrollPane attributeScrollPane;
    private javax.swing.JPanel businessPanel;
    private javax.swing.JPanel categoriesPanel;
    private javax.swing.JLabel categoryLabel;
    private javax.swing.JButton jButton1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JPanel mCategoryListPanel;
    private javax.swing.JPanel mainCategoryPanel;
    private javax.swing.JScrollPane mainCategoryScrollPane;
    private javax.swing.JButton queryButton;
    private javax.swing.JButton queryButton1;
    private javax.swing.JPanel queryPanel;
    private javax.swing.JPanel sCategoryListPanel;
    private javax.swing.JLabel subCategoryLabel;
    private javax.swing.JPanel subCategoryPanel;
    private javax.swing.JScrollPane subCategoryScrollPane;
    // End of variables declaration//GEN-END:variables
}

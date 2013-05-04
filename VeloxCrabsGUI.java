package scripts.VeloxRockCrabs;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created with IntelliJ IDEA.
 * User: Jon
 * Date: 4/4/13
 * Time: 1:41 AM
 * To change this template use File | Settings | File Templates.
 */
public class VeloxCrabsGUI implements Runnable {

    public static JFrame GUIFrame = new JFrame();
    public static JList foodList;
    public static JSlider healthSlider;
    public static JLabel healthLabel;
    public static JCheckBox east;
    public static JCheckBox west;
    public static Boolean isGui = true;

    @Override
    public void run() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                GUIFrame = new JFrame("Velox Crabs Premium " + VeloxRockCrabs.Version);

                // Creating the grid
                JPanel meleePanel = new JPanel(null);
                JPanel rangePanel = new JPanel(null);
                JPanel magePanel = new JPanel(null);

                GUIFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

                GUIFrame.getContentPane().add(meleePanel, BorderLayout.NORTH);
                GUIFrame.getContentPane().add(rangePanel, BorderLayout.NORTH);
                GUIFrame.getContentPane().add(magePanel, BorderLayout.NORTH);

                JTabbedPane tab = new JTabbedPane();

                // Melee Panel

                // List box

                String food[] = {"Shrimp", "Sardine", "Herring", "Anchovies", "Trout", "Cod", "Pike", "Salmon", "Tuna", "Lobster", "Swordfish", "Monkfish", "Shark"};
                foodList = new JList(food);
                foodList.setBounds(5, 5, 100, 245);
                meleePanel.add(foodList);

                // Tele options
                JLabel teleLabel = new JLabel("Teleport Options");
                teleLabel.setBounds(150, 15, 270, 20);
                teleLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.GRAY));
                meleePanel.add(teleLabel);

                JCheckBox teleTab = new JCheckBox("Use Teletabs?");
                teleTab.setBounds(150, 45, 120, 20);
                meleePanel.add(teleTab);

                JCheckBox spellBook = new JCheckBox("Use Spellbook?");
                spellBook.setBounds(150, 75, 120, 20);
                meleePanel.add(spellBook);

                JCheckBox houseTeleTab = new JCheckBox("Use House Teletab?");
                houseTeleTab.setBounds(275, 45, 150, 20);
                meleePanel.add(houseTeleTab);

                JCheckBox houseSpellBook = new JCheckBox("Use House Spellbook?");
                houseSpellBook.setBounds(275, 75, 150, 20);
                meleePanel.add(houseSpellBook);

                // End Tele Options

                // Fighting Options

                JLabel fightingLabel = new JLabel("Fighting Options");
                fightingLabel.setBounds(150, 140, 270, 20);
                fightingLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.GRAY));
                meleePanel.add(fightingLabel);

                east = new JCheckBox("Fight at East?");
                east.setBounds(150, 165, 120, 20);
                meleePanel.add(east);

                west = new JCheckBox("Fight at West?");
                west.setBounds(150, 195, 120, 20);
                west.setSelected(true);
                meleePanel.add(west);

                JCheckBox strengthPots = new JCheckBox("Use Strength Potions?");
                strengthPots.setBounds(275, 165, 150, 20);
                meleePanel.add(strengthPots);

                // Start button

                JButton startScript = new JButton("Start Script");
                startScript.setBounds(5, 255, 425, 20);
                startScript.addActionListener(new StartScript());
                meleePanel.add(startScript);

                healthLabel = new JLabel("HP to eat: 40");
                healthLabel.setBounds(150, 230, 70, 20);
                meleePanel.add(healthLabel);

                healthSlider = new JSlider(1, 98);
                healthSlider.setBounds(220, 230, 200, 20);
                healthSlider.setValue(40);
                healthSlider.setMajorTickSpacing(10);
                healthSlider.setMinorTickSpacing(1);
                healthSlider.setPaintLabels(true);
                healthSlider.setPaintTicks(true);
                healthSlider.addChangeListener(new AngleSlider());
                meleePanel.add(healthSlider);

                tab.add("Main", meleePanel);

                // End Melee Panel

                // Range Panel

                JComboBox arrows = new JComboBox();



                /*
                for(int i = 0; i < languages.length; i++)
                    comboBox.addItem(languages[count++]);
                textfield1.setEditable(false);

                comboBox.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        textfield2.setText("You Selected : " +
                                ((JComboBox)e.getSource()).getSelectedItem());
                    }
                */
                //tab.add("Range", rangePanel);

                // End Range Panel

                // Mage Panel

                //tab.add("Mage", magePanel);

                // End Mage Panel

                GUIFrame.add(tab);

                GUIFrame.setSize(450, 340);
                GUIFrame.setResizable(false);
                GUIFrame.setVisible(true);


            }
        });
    }
}

class StartScript implements ActionListener {

    public void actionPerformed(ActionEvent e){

        if (VeloxCrabsGUI.east.isSelected() && VeloxCrabsGUI.west.isSelected()) {
            JOptionPane.showMessageDialog(null, "Please only choose one side to fight on!");
        } else if (!VeloxCrabsGUI.east.isSelected() && !VeloxCrabsGUI.west.isSelected()) {
            JOptionPane.showMessageDialog(null, "Please choose a side to fight on.");
        } else {
            if (VeloxCrabsGUI.west.isSelected()) {
                VeloxRockCrabs.isWest = true;
            }
            if (VeloxCrabsGUI.east.isSelected()) {
                VeloxRockCrabs.isWest = false;
            }
        if (VeloxCrabsGUI.foodList.isSelectionEmpty()) {
            JOptionPane.showMessageDialog(null, "Please select a food item!");
        } else {

        switch(VeloxCrabsGUI.foodList.getSelectedIndex()) {
            case 0:
                VeloxRockCrabs.foodId = 315;
                break;
            case 1:
                VeloxRockCrabs.foodId = 325;
                break;
            case 2:
                VeloxRockCrabs.foodId = 347;
                break;
            case 3:
                VeloxRockCrabs.foodId = 319;
                break;
            case 4:
                VeloxRockCrabs.foodId = 333;
                break;
            case 5:
                VeloxRockCrabs.foodId = 339;
                break;
            case 6:
                VeloxRockCrabs.foodId = 251;
                break;
            case 7:
                VeloxRockCrabs.foodId = 329;
                break;
            case 8:
                VeloxRockCrabs.foodId = 361;
                break;
            case 9:
                VeloxRockCrabs.foodId = 379;
                break;
            case 10:
                VeloxRockCrabs.foodId = 373;
                break;
            case 11:
                VeloxRockCrabs.foodId = 7946;
                break;
            case 12:
                VeloxRockCrabs.foodId = 385;
                break;
        }
        VeloxRockCrabs.eatLevel = VeloxCrabsGUI.healthSlider.getValue();
        VeloxCrabsGUI.GUIFrame.dispose();
        VeloxCrabsGUI.isGui = false;
        }
        }
    }
}

class AngleSlider implements ChangeListener {

    @Override
    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider) e.getSource();
        VeloxCrabsGUI.healthLabel.setText("HP to eat: " + source.getValue());
    }
}

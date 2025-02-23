package swing.propretiesView;

import classDiagram.IDiagramComponent.UpdateMessage;
import classDiagram.components.InterfaceEntity;
import classDiagram.components.SimpleEntity;
import classDiagram.relationships.Inheritance;
import swing.MultiViewManager;
import swing.Slyum;
import swing.slyumCustomizedComponents.FlatButton;
import swing.slyumCustomizedComponents.FlatPanel;
import utility.PersonalizedIcon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class InheritanceProperties extends GlobalPropreties implements ActionListener {
  public static final String ACTION_OI = "O&I";

  private static InheritanceProperties instance;

  private JLabel lblName, lblType;
  private JButton btnOI, btnAdjustInheritance;
  private ButtonChangeOrientation btnChangeOrientation;

  public static InheritanceProperties getInstance() {
    if (instance == null) instance = new InheritanceProperties();

    return instance;
  }

  public InheritanceProperties() {
    JPanel panel = new FlatPanel();

    setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

    lblType = new JLabel("inheritanceType");
    panel.add(lblType);

    lblName = new JLabel("inheritanceName");
    panel.add(lblName);

    btnOI = new FlatButton("Overrides & Implementations...",
                           PersonalizedIcon.createImageIcon("method.png"));
    btnOI.setToolTipText("Open Overrides & Implementations");
    btnOI.setActionCommand(ACTION_OI);
    btnOI.addActionListener(this);
    btnOI.setMaximumSize(new Dimension(250, 100));
    btnOI.setHorizontalAlignment(SwingUtilities.LEFT);

    btnAdjustInheritance = new FlatButton("Autopath",
                                          PersonalizedIcon.createImageIcon("adjust-inheritance.png"));
    btnAdjustInheritance.setActionCommand(Slyum.ACTION_ADJUST_INHERITANCE);
    btnAdjustInheritance.addActionListener(this);
    btnAdjustInheritance.setMaximumSize(new Dimension(250, 100));
    btnAdjustInheritance.setHorizontalAlignment(SwingUtilities.LEFT);

    panel.add(Box.createVerticalGlue());
    panel.add(btnChangeOrientation = new ButtonChangeOrientation());
    panel.add(Box.createVerticalStrut(5));
    panel.add(btnOI);
    panel.add(Box.createVerticalStrut(5));
    panel.add(btnAdjustInheritance);

    panel.setMaximumSize(new Dimension(250, Short.MAX_VALUE));
    panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
    add(panel);
  }

  @Override
  public void updateComponentInformations(UpdateMessage msg) {
    if (currentObject != null) {
      Inheritance i = (Inheritance) currentObject;
      SimpleEntity parent = i.getParent();
      String lblTypeText = "generalize";

      if (parent.getClass() == InterfaceEntity.class) lblTypeText = "realize";

      lblType.setText(lblTypeText);

      lblName.setText(i.getChild().getName() + " -> " + parent.getName());
      btnOI.setEnabled(!parent.isEveryMethodsStatic());
      btnChangeOrientation.changeActionListener(
          MultiViewManager.getSelectedGraphicView()
                          .searchAssociedComponent(currentObject));
    }
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    Inheritance i = (Inheritance) currentObject;
    switch (e.getActionCommand()) {
      case ACTION_OI:
        i.showOverridesAndImplementations();
        break;
      case Slyum.ACTION_ADJUST_INHERITANCE:
        MultiViewManager.getSelectedGraphicView().adjustInheritances();
        break;
    }
  }

}

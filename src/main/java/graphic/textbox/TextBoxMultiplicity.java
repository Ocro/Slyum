package graphic.textbox;

import classDiagram.relationships.Association;
import classDiagram.relationships.Multiplicity;
import classDiagram.relationships.Role;
import graphic.GraphicComponent;
import graphic.GraphicView;
import graphic.relations.AssociationView;
import graphic.relations.LineView;
import graphic.relations.MagneticGrip;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Observable;

/**
 * A TextBox is a graphic component from Slyum containing a String. The particularity of a TextBox is it text can be
 * changed by double-clinking on it.
 * <p>
 * A TextBoxLabel add the possibility to be moved by user in dragging mouse on it. The TextBoxLabel's position is
 * relative to it's anchor. When the anchor moves, this label moves too to keep the dimension of the anchor always the
 * same.
 * <p>
 * A TextBox label have an associated graphic component and draw a line between the label and the component when mouse
 * hover the label. Points of the line is compute in this way:
 * <p>
 * first point : it's the middle bounds of the label second point : it's compute by calling the computeAnchor() abstract
 * method.
 * <p>
 * A TextBoxMultiplicity is associated with a MagneticGrip. The computeAnchor() method return the position of the grip
 * like a second point.
 * <p>
 * When text is edited, the multiplicity associated changed and notifiy it's listener. This TextBox parse the text into
 * a multiplicity.
 *
 * @author David Miserez
 * @version 1.0 - 25.07.2011
 */
public class TextBoxMultiplicity extends TextBoxLabel {
  /**
   * Return multiplicity corresponding to the string given in parameter or null if the string is in an incorrect format.
   * Format must be the following : [inferiorBound]..[superiorBound] (bounds is from integer type) or can be just a
   * number. In this case, inferiorBound and superiodBound will be the same.
   *
   * @param text string to convert.
   *
   * @return multiplicity from string.
   */
  public static Multiplicity convertStringToMultiplicity(String text) {
    Multiplicity multiplicity = null;
    final String[] split = text.split("\\.\\.");

    for (int i = 0; i < split.length; i++)

      split[i] = split[i].trim();

    if (split.length == 1)
      try {
        int bound;

        if (isInfinity(split[0]))
          bound = Integer.MAX_VALUE;
        else
          bound = Integer.parseInt(split[0]);

        multiplicity = new Multiplicity(bound);
      } catch (final NumberFormatException e) {
        System.err.println("Invalide multiplicity parsing.");
      }
    else if (split.length == 2) try {
      int min, max;

      if (isInfinity(split[0]))
        min = Integer.MAX_VALUE;
      else
        min = Integer.parseInt(split[0]);

      if (isInfinity(split[1]))
        max = Integer.MAX_VALUE;
      else
        max = Integer.parseInt(split[1]);

      if (min <= max) multiplicity = new Multiplicity(min, max);
    } catch (final NumberFormatException e) {
      System.err.println("Invalide multiplicity parsing.");
    }

    return multiplicity;
  }

  private static boolean isInfinity(String car) {
    return car.equals("*") || car.equals("m") || car.equals("n")
           || car.equals("N") || car.equals("M");
  }

  private final MagneticGrip grip;

  private final Multiplicity multiplicity;

  /**
   * Create a new TextBoxMultiplicity associated with a MagneticGrip and a multiplicity.
   *
   * @param parent the graphic view
   * @param multiplicity the multiplicity
   * @param grip the grip associated with
   */
  public TextBoxMultiplicity(GraphicView parent, Multiplicity multiplicity,
                             MagneticGrip grip) {
    super(parent, multiplicity.toString());

    if (grip == null) throw new IllegalArgumentException("grip is null");

    this.grip = grip;
    grip.addObserver(this);

    this.multiplicity = multiplicity;
    multiplicity.addObserver(this);
  }

  @Override
  public void reinitializeLocation() {

    final Rectangle classBounds = grip.getAssociedComponentView().getBounds();
    final Point gripAnchor = grip.getAnchor();

    deplacement = new Point(); // (0, 0)

    // Permet d'attendre que la taille de la textbox soit définie.
    SwingUtilities.invokeLater(new Runnable() {

      @Override
      public void run() {

        if (gripAnchor.x <= classBounds.x)
          deplacement.x -= getBounds().width + TextBox.MARGE;
        else
          deplacement.x += TextBox.MARGE;

        if (gripAnchor.y <= classBounds.y)
          deplacement.y -= getBounds().height + TextBox.MARGE;
        else
          deplacement.y += TextBox.MARGE;

        // La classe est alignée horizontalement.
        if (gripAnchor.x <= classBounds.x
            || gripAnchor.x >= classBounds.x + classBounds.width)
          deplacement.y -= getBounds().height + TextBox.MARGE * 2;

        // La classe est alignée verticalement.
        if (gripAnchor.y <= classBounds.y
            || gripAnchor.y >= classBounds.y + classBounds.height)
          deplacement.x -= getBounds().width + TextBox.MARGE * 2;

        computeLabelPosition();
      }
    });
  }

  @Override
  protected Point computeAnchor() {
    return grip.getAnchor();
  }

  @Override
  public String getText() {
    return multiplicity.toString();
  }

  @Override
  public void gMouseClicked(MouseEvent e) {
    super.gMouseClicked(e);
    GraphicComponent view = null;

    // search a multiplicity corresponding to the textBox multiplicity...
    firstloop:
    for (LineView av : parent.getLinesView())
      // take all line view
      if (av instanceof AssociationView) // test if it's an associationView
        // (have some role)
        for (Role r : ((Association) ((AssociationView) av)
            .getAssociatedComponent()).getRoles())
          // iterate through roles
          if (r.getMultiplicity().equals(multiplicity)) { // multiplicity's
            // role is the same than textBoxMultiplicity multiplicity?
            view = av;
            break firstloop;
          }

    if (view != null) {
      if (!GraphicView.isAddToSelection(e)) {
        parent.unselectAll();
        view.setSelected(true);
      } else {
        view.setSelected(!view.isSelected());
      }
    }
  }

  @Override
  public void gMousePressed(MouseEvent e) {
    super.gMousePressed(e);
    maybeShowPopup(e, grip.getRelation());
  }

  @Override
  public void gMouseReleased(MouseEvent e) {
    super.gMouseReleased(e);
    maybeShowPopup(e, grip.getRelation());
  }

  @Override
  public void setText(String text) {
    final Multiplicity newMultiplicity = convertStringToMultiplicity(text);

    if (newMultiplicity != null) {
      multiplicity.setLowerBound(newMultiplicity.getLowerBound());
      multiplicity.setUpperBound(newMultiplicity.getUpperBound());

      super.setText(multiplicity.toString());

      multiplicity.notifyObservers();
    }
  }

  @Override
  public void update(Observable arg0, Object arg1) {
    super.update(arg0, arg1);

    super.setText(multiplicity.toString());
  }

}

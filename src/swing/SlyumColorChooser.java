package swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import utility.PersonalizedIcon;
import utility.Utility;

/**
 * Show a dialog for choosing a color.
 * 
 * @author David Miserez
 * @version 1.0 - 25.07.2011
 */
public class SlyumColorChooser extends JDialog
{
	private static final long serialVersionUID = -1975479020681307211L;
	private boolean accepted = false;
	private JColorChooser colorChooser;
	private final JPanel contentPanel = new JPanel();

	/**
	 * Create the dialog.
	 */
	public SlyumColorChooser(Color defaultColor)
	{
		// Generated by WindowBuilder from Google.
		
        Utility.setRootPaneExitOnEsc(getRootPane(), new AbstractAction() {
		
            public void actionPerformed(ActionEvent e)
            {
            	setVisible(false);
            }
		});
		
		setModalityType(ModalityType.APPLICATION_MODAL);
		setResizable(false);
		setTitle("Slyum - Choose a color...");
		setIconImage(PersonalizedIcon.getLogo().getImage());
		setBounds(100, 100, 452, 421);
		setLocationRelativeTo(null);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setLayout(new FlowLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		{
			colorChooser = new JColorChooser(defaultColor);
			contentPanel.add(colorChooser);
		}
		{
			final JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				final JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e)
					{
						accepted = true;
						setVisible(false);
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				final JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e)
					{
						setVisible(false);
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

	/**
	 * Get the color choosed by user.
	 * 
	 * @return the color choosed
	 */
	public Color getColor()
	{
		return colorChooser.getColor();
	}

	/**
	 * Get if user clicked on Ok or Cancel button.
	 * 
	 * @return true if user clicked on Ok; false otherwise
	 */
	public boolean isAccepted()
	{
		return accepted;
	}

}

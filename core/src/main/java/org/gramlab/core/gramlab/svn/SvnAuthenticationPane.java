package org.gramlab.core.gramlab.svn;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;



@SuppressWarnings("serial")
public class SvnAuthenticationPane extends JPanel {

	private JTextField login=new JTextField();
	private JPasswordField password=new JPasswordField();

	
	public SvnAuthenticationPane() {
		super(new GridBagLayout());
		setBorder(BorderFactory.createTitledBorder("Authentication required:"));
		GridBagConstraints gbc=new GridBagConstraints();
		gbc.anchor=GridBagConstraints.WEST;
		add(new JLabel("Login: "),gbc);
		gbc.weightx=1;
		gbc.fill=GridBagConstraints.HORIZONTAL;
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		add(login,gbc);
		gbc.weightx=0;
		gbc.gridwidth=1;
		add(new JLabel("Password: "),gbc);
		gbc.weightx=1;
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		add(password,gbc);
	}
	
	
	public String getLogin() {
		return login.getText();
	}
	
	public char[] getPassword() {
		return password.getPassword();
	}
		
}

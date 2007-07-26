/**
 * This file is distributed under the GPL 
 * $Id: Version.java 433 2007-07-26 15:13:53Z scotta $
 */

package bnubot.bot.gui;

import java.awt.FlowLayout;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;

import bnubot.Version;

@SuppressWarnings("serial")
public class AboutWindow extends JDialog {

	public AboutWindow() {
		initializeGUI();
		setTitle("About BNU-Bot");
		
		pack();
		setAlwaysOnTop(true);
		setVisible(true);
	}

	public void initializeGUI() {
		setLayout(new FlowLayout(FlowLayout.CENTER));

		Box b = new Box(BoxLayout.Y_AXIS);
		{
			b.add(new JLabel("BNU-Bot v" + Version.version()));
			b.add(Box.createVerticalStrut(20));
			b.add(new JLabel("Created by:"));
			b.add(new JLabel("BNU-Camel"));
			b.add(Box.createVerticalStrut(20));
			b.add(new JLabel("Distributed under the GPL"));
			b.add(Box.createVerticalStrut(20));
			b.add(new JLabel("Special thanks to:"));
			b.add(new JLabel("Google: project hosting"));
			b.add(new JLabel("BNU-Fantasma: beta testing"));
			b.add(Box.createVerticalStrut(20));
			b.add(new JLabel("Want to contribute?"));
			b.add(new JLabel("Visit the project website:"));
			b.add(new JLabel("http://code.google.com/p/bnubot/"));
		}
		add(b);
	}
}
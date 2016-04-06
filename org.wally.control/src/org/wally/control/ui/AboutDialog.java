package org.wally.control.ui;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Panel;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;

public class AboutDialog extends JDialog {
	public AboutDialog(Container parent) {
		super((Frame) parent, true);
		setBackground(Color.gray);
		setLayout(new BorderLayout());
		Panel panel = new Panel();
		panel.add(new Button("Close"));
		add("South", panel);
		int width = 200;
		int height = 200;
		setSize(width, height);
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension d = tk.getScreenSize();
		setLocation(d.width/2 - width/2, d.height/2 - height/2);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent windowEvent) {
				dispose();
			}
		});
	}

	public boolean action(Event evt, Object arg) {
		if (arg.equals("Close")) {
			dispose();
			return true;
		}
		return false;
	}

	public void paint(Graphics g) {
		g.setColor(Color.white);
		g.drawString("Wall-E Controller", 50, 70);
		g.drawString("Version 1.0", 60, 90);
	}
}
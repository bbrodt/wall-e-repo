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
import java.io.File;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.util.Date;

import javax.swing.JDialog;

import org.wally.control.WallyController;

public class AboutDialog extends JDialog {
	public static String APP_NAME = "Wall-E Controller";
	public static String VERSION_INFO = "Version 1.0";
	private static AboutDialog instance;
	
	private AboutDialog(Container parent) {
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

	public static AboutDialog getInstance() {
		if (instance==null) {
			instance = new AboutDialog(WallyController.getMainWindow());
		}
		return instance;
	}
	
	public String getBuildDate() {
		Date buildDate;
		try {
			buildDate = new Date(new File(getClass().getClassLoader().getResource(getClass().getCanonicalName().replace('.', '/') + ".class").toURI()).lastModified());
			return DateFormat.getInstance().format(buildDate);
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return "Can't determine Build Date: "+e.getMessage();
		}
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
		g.drawString(APP_NAME, 50, 70);
		g.drawString(VERSION_INFO, 60, 90);
		g.drawString(getBuildDate(), 60, 110);
	}
}
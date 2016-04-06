package org.wally.control.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.wally.clientserver.ClientServerConstants;
import org.wally.control.ControlConstants;
import org.wally.control.WallyController;
import org.wally.control.actuators.ActuatorEvent;
import org.wally.control.actuators.ActuatorEvent.ActuatorEventType;
import org.wally.control.actuators.ActuatorEventListener;

public class MainWindow extends JFrame implements ControlConstants, ClientServerConstants
{

	private ChoreographyPanel choreographyPanel;
	private JPanel actuatorsPanel;
	private JPanel sensorsPanel;
	private JLabel statusLabel;
	private String status = "";
	private Map<String, ServoActuator> servoActuators = new HashMap<String, ServoActuator>();
	
	public MainWindow() {
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension d = tk.getScreenSize();
		this.setLocation(0,0);
		this.setSize(d.width, d.height);
		this.setLayout(new BorderLayout());
		this.setUndecorated(true);
		createMenus();

		choreographyPanel = createChoreographyPanel();
		actuatorsPanel = createActuators();
		sensorsPanel = createSensors();
		statusLabel = createStatusArea();

		this.add(actuatorsPanel, BorderLayout.LINE_START);
		this.add(choreographyPanel, BorderLayout.CENTER);
		this.add(sensorsPanel, BorderLayout.LINE_END);
		this.add(statusLabel, BorderLayout.PAGE_END);
	}

	public Insets getInsets() {
		return new Insets(2, 2, 2, 2);
	}

	private void createMenus() {
		JMenuBar menuBar = new JMenuBar();
		JMenu wallEMenu = new JMenu("Wall-E");
		final JCheckBoxMenuItem autoItem = new JCheckBoxMenuItem("Autonomous Mode");
		autoItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				System.out.println("Auto="+autoItem.getState());
				setStatus(autoItem.getState() ? "Autonomous Mode" : "Manual Mode");
			}
		});
		setStatus("Manual Mode");
		wallEMenu.add(autoItem);
		
		JMenuItem exitItem = new JMenuItem("Exit");
		exitItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		wallEMenu.add(exitItem);
		menuBar.add(wallEMenu);

//		JMenu choreographyMenu = new JMenu("Choreography");
//		JMenuItem choreoLoadItem = new JMenuItem("Load");
//		choreoLoadItem.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent arg0) {
//				choreographyPanel.load();
//			}
//		});
//		choreographyMenu.add(choreoLoadItem);
//		JMenuItem choreoSaveItem = new JMenuItem("Save");
//		choreoSaveItem.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent arg0) {
//				choreographyPanel.save();
//			}
//		});
//		choreographyMenu.add(choreoSaveItem);
//		JMenuItem choreoRunItem = new JMenuItem("Run");
//		choreoRunItem.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent arg0) {
//				choreographyPanel.run();
//			}
//		});
//		choreographyMenu.add(choreoRunItem);
//		JMenuItem choreoStopItem = new JMenuItem("Stop");
//		choreoStopItem.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent arg0) {
//				choreographyPanel.stop();
//			}
//		});
//		choreographyMenu.add(choreoStopItem);
//		menuBar.add(choreographyMenu);
		
		JMenu helpMenu = new JMenu("Help");
		JMenuItem aboutItem = new JMenuItem("About");
		aboutItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				AboutDialog aboutDialog = new AboutDialog(MainWindow.this);
				aboutDialog.setVisible(true);
			}
		});
		helpMenu.add(aboutItem);
		menuBar.add(helpMenu);
		
		setJMenuBar(menuBar);
	}

	private ChoreographyPanel createChoreographyPanel() {
		ChoreographyPanel panel = new ChoreographyPanel();
		return panel;
	}

	private JPanel createActuators() {
		JPanel panel = new JPanel();
		panel.setBackground(Color.GREEN);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(new Label("Actuators", Label.CENTER));

		servoActuators.put(HEAD_NOD_SERVO, createServoActuator(panel, HEAD_NOD_SERVO, 0));
		servoActuators.put(HEAD_TILT_SERVO, createServoActuator(panel, HEAD_TILT_SERVO, 1));
		servoActuators.put(HEAD_TURN_SERVO, createServoActuator(panel, HEAD_TURN_SERVO, 2));
		ServoActuator sa[];
		sa = createCoupledServoActuators(panel, new String[] {LEFT_EYEBROW_SERVO, RIGHT_EYEBROW_SERVO}, new int[] {3,4});
		sa[1].setReversed(true);
		servoActuators.put(LEFT_EYEBROW_SERVO, sa[0]);
		servoActuators.put(RIGHT_EYEBROW_SERVO, sa[1]);
		sa = createCoupledServoActuators(panel, new String[] {LEFT_HEAD_DROOP_SERVO, RIGHT_HEAD_DROOP_SERVO}, new int[] {5,6});
		sa[1].setReversed(true);
		servoActuators.put(LEFT_HEAD_DROOP_SERVO, sa[0]);
		servoActuators.put(RIGHT_HEAD_DROOP_SERVO, sa[1]);

		servoActuators.put(NECK_TOP_SERVO, createServoActuator(panel, REMOTE_1_ADDRESS+NECK_TOP_SERVO, 0) );
		servoActuators.put(NECK_BOTTOM_SERVO, createServoActuator(panel, REMOTE_1_ADDRESS+NECK_BOTTOM_SERVO, 1) );

		panel.add(Box.createVerticalStrut(1000));
		return panel;
	}

	private ServoActuator createServoActuator(final Container container, final String label, final int servo) {
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		p.setBackground(Color.LIGHT_GRAY);
		p.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
		JPanel p2 = new JPanel();
		final JLabel labelField = new JLabel("",Label.LEFT);
		p2.add(labelField);
		final JLabel positionField = new JLabel();
		positionField.setText("");
		p2.add(positionField, Label.CENTER);
		p.add(p2);
		final ServoActuator sa = new ServoActuator(label,servo);
		positionField.setText(""+sa.getServoPosition());
		sa.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				positionField.setText(""+sa.getServoPosition());
			}
		});
		sa.addListener(new ActuatorEventListener() {
			public void handleEvent(ActuatorEvent event) {
				if (event.type==ActuatorEventType.CONNECTED) {
					sa.setBackground(Color.GREEN);
					WallyController.setStatus(event.source.getName()+" connected");
				}
				else if (event.type==ActuatorEventType.DISCONNECTED) {
					sa.setBackground(Color.GRAY);
					WallyController.setStatus(event.source.getName()+" disconnected");
				}
				else {
					sa.setBackground(Color.RED);
					WallyController.setStatus(event.source.getName()+" can't connect");
				}
			}
		});
		labelField.setText(sa.getName());
		p.add(sa);
		container.add(p);
		
		sa.connect();
		
		return sa;
	}
	
	private ServoActuator[] createCoupledServoActuators(final Container container, final String[] labels, final int[] servos) {
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		final JCheckBox cb = new JCheckBox("Coupled");
		panel.add(cb);
		
		final ServoActuator sas[] = new ServoActuator[labels.length];
		for (int i=0; i<labels.length; ++i) {
			sas[i] = createServoActuator(panel, labels[i], servos[i]);
		}
		for (int i=0; i<sas.length; ++i) {
			for (ChangeListener cl : sas[i].getChangeListeners()) {
				if (cl instanceof ServoActuatorListener) {
					for (ServoActuator s : sas) {
						((ServoActuatorListener)cl).addCoupledSlider(s);
					}
				}
			}
		}
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent event) {
				boolean coupled = (event.getStateChange()==1);
				for (int i=0; i<sas.length; ++i) {
					for (ChangeListener cl : sas[i].getChangeListeners()) {
						if (cl instanceof ServoActuatorListener) {
							((ServoActuatorListener)cl).setCoupled(coupled);
						}
					}
				}
			}
		});
		container.add(panel);
		return sas;
	}

	private JPanel createSensors() {
		JPanel panel = new JPanel();
		panel.setBackground(Color.YELLOW);
		panel.setLayout(new FlowLayout());
		panel.add(new Label("Sensors", Label.CENTER));
		return panel;
	}

	private JLabel createStatusArea() {
		JLabel label = new JLabel();
		label.setBackground(Color.LIGHT_GRAY);
		label.setText("Status: "+status);
		return label;
	}
	
	public void setStatus(String status) {
		this.status = status;
		if (statusLabel!=null) {
			statusLabel.setText("Status: "+status);
		}
	}
	
	public Map<String, ServoActuator> getServoActuators() {
		return servoActuators;
	}
}
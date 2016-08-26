package org.wally.control.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileFilter;

import jsyntaxpane.syntaxkits.JavaSyntaxKit;

import org.wally.control.WallyController;
import org.wally.control.choreography.Choreography;
import org.wally.control.choreography.ScriptEvent;
import org.wally.control.choreography.ScriptEventType;
import org.wally.control.choreography.ScriptStateListener;
import org.wally.control.util.FileUtils;

public class ChoreographyPanel extends JPanel implements ScriptStateListener {
	private final static String UNNAMED = "unnamed";
	private JLabel title;
	private File currentFile = null;
	private JEditorPane editor;
	private Choreography choreography;
	
	public ChoreographyPanel() {
		super();
		this.setBackground(Color.WHITE);
		this.setBorder(new LineBorder(Color.BLUE, 2));
		this.setLayout(new BorderLayout());

		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
		title = new JLabel("Choreography", JLabel.LEFT);
		setCurrentFile(null);
		topPanel.add(title);

		topPanel.add(Box.createHorizontalGlue());

		JButton loadButton = new JButton("Load");
		loadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				load();
			}
		});
		topPanel.add(loadButton);
		
		JButton saveButton = new JButton("Save");
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				save();
			}
		});
		topPanel.add(saveButton);
		
		JButton runButton = new JButton("Run");
		runButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				run();
			}
		});
		topPanel.add(runButton);
		
		JButton stopButton = new JButton("Stop");
		stopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				stop();
			}
		});
		topPanel.add(stopButton);

		this.add(topPanel, BorderLayout.PAGE_START);
		
		editor = new JEditorPane();
		JScrollPane scrollPane = new JScrollPane(editor);
		editor.addKeyListener(new KeyListener() {
			
			public void keyTyped(KeyEvent event) {
			}
			
			public void keyReleased(KeyEvent event) {
				if (event.getKeyChar()==19) {
					doSave(currentFile);
				}
			}
			
			public void keyPressed(KeyEvent event) {
			}
		});
		editor.setBorder(new LineBorder(Color.BLACK, 2));
		
		this.add(scrollPane, BorderLayout.CENTER);
		editor.setEditorKit(new JavaSyntaxKit());
	}
	
	protected void setCurrentFile(File file) {
		currentFile = file;
		if (file==null)
			title.setText("Unnamed Choreography");
		else
			title.setText("Choreography: "+file.getName());
	}
	
	protected File getCurrentFile() {
		return currentFile;
	}
	
	public void load() {
		JFileChooser fileChooser = getFileChooser();
		int result = fileChooser.showOpenDialog(this);
		if (result == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			String text = FileUtils.load(file);
			if (text!=null) {
				editor.setText(text);
				setCurrentFile(file);
			}
		}
	}
	
	public void save() {
		File file = getCurrentFile();
		if (file==null) {
			JFileChooser fileChooser = getFileChooser();
			int result = fileChooser.showSaveDialog(this);
			if (result == JFileChooser.APPROVE_OPTION)
				file = fileChooser.getSelectedFile();
		}
		doSave(file);
	}
	
	private void doSave(File file) {
		if (file!=null) {
			if (FileUtils.saveScript(file, editor.getText()))
				setCurrentFile(file);
			else
				setCurrentFile(null);
		}
	}
	
	public void run() {
		if (!editor.getText().isEmpty()) {
			choreography = new Choreography(editor.getText());
			choreography.setScriptName(getCurrentFileName());
			choreography.addStateListener(this);
			choreography.addBinding("servo", WallyController.getMainWindow().getServoObjects());
			choreography.schedule();
		}
	}
	
	public void stop() {
		if (choreography!=null) {
			choreography.stop();
//			choreography = null;
		}
	}
	
	private JFileChooser getFileChooser() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new FileFilter() {
			@Override
			public String getDescription() {
				return "Wall-E Choreography Scripts";
			}
			
			@Override
			public boolean accept(File file) {
				if (file.getName().endsWith(".js"))
					return true;
				return false;
			}
		});
		fileChooser.setCurrentDirectory(new File(FileUtils.getScriptDirectory()));
		
		return fileChooser;
	}

	private String getCurrentFileName() {
		return currentFile==null? UNNAMED : currentFile.getName();
	}
	
	public void scriptStateChanged(ScriptEvent event) {
		try {
			if (event.type==ScriptEventType.RUN)
				WallyController.println("Running Choreography "+getCurrentFileName());
			else if (event.type==ScriptEventType.STOP)
				WallyController.println("Choreography "+getCurrentFileName()+" stopped with status "+event.source.getResult());
			else if (event.type==ScriptEventType.MSG)
				WallyController.println("Choreography message: "+event.msg);
			else if (event.type==ScriptEventType.ERROR)
				WallyController.println("Choreography error: "+event.error.getMessage());
			else
				WallyController.println("Unhandled Choreography event: "+event.type);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
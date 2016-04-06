package org.wally.control.util;

import javax.swing.JOptionPane;

import org.wally.control.WallyController;

public class UIUtils {

	public static void showErrorDialog(String message) {
		JOptionPane.showMessageDialog(WallyController.getMainWindow(),
				message,
				"Dialog", JOptionPane.ERROR_MESSAGE);
	}
}

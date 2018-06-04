package com.tqbdev.gui.components;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Dialog {
	public static void showErrorMessage(JFrame jFrame,String errorMessage, String errorTitle) {
		JOptionPane.showMessageDialog(jFrame, errorMessage, errorTitle, JOptionPane.ERROR_MESSAGE);
	}

	public static void showWarning(JFrame jFrame, String warnMessage, String warnTitle) {
		JOptionPane.showMessageDialog(jFrame, warnMessage, warnTitle, JOptionPane.WARNING_MESSAGE);
	}
	
	public static void showInform(JFrame jFrame, String informMessage, String informTitle) {
		JOptionPane.showMessageDialog(jFrame, informMessage, informTitle, JOptionPane.INFORMATION_MESSAGE);
	}
	
	public static int showConfirm(JFrame jFrame, String confirmMessage, String confirmTitle) {
		return JOptionPane.showConfirmDialog(jFrame, confirmMessage, confirmTitle, JOptionPane.YES_NO_OPTION);
	}
}
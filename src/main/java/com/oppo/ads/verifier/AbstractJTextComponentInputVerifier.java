package com.oppo.ads.verifier;

import com.intellij.ui.JBColor;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;

/**
 * JtextField校验器
 *
 * @author 80275131
 * @version 1.0
 * @date 2021/2/19 14:31
 * @since 1.0
 **/
public abstract class AbstractJTextComponentInputVerifier extends InputVerifier {

	@Override
	public boolean verify(JComponent input) {
		JTextComponent jTextComponent = (JTextComponent) input;
		boolean verify = verify(jTextComponent);
		if (!verify) {
			input.setBackground(JBColor.RED);
			Point location = input.getLocation();
			double x = location.getX();
			double y = location.getY();
			Point p = new Point();
			p.setLocation(x, y + input.getHeight());
			JOptionPane.showMessageDialog(input, "输入有误");
		}else {
			input.setBackground(JBColor.background());
		}
		return verify;
	}

	public abstract boolean verify(JTextComponent jTextComponent);
}

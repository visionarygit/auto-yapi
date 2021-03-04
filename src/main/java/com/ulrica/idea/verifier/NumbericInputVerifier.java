package com.ulrica.idea.verifier;

import org.apache.commons.lang.math.NumberUtils;

import javax.swing.text.JTextComponent;

/**
 * 数字校验器
 *
 * @author 80275131
 * @version 1.0
 * @date 2021/2/19 18:48
 * @since 1.0
 **/
public class NumbericInputVerifier extends AbstractJTextComponentInputVerifier {
	@Override
	public boolean verify(JTextComponent jTextComponent) {
		return NumberUtils.isDigits(jTextComponent.getText());
	}
}

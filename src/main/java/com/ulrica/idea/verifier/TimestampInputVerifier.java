package com.ulrica.idea.verifier;

import com.ulrica.idea.utils.TimestampUtil;

import javax.swing.text.JTextComponent;

/**
 * 时间戳输入校验
 *
 * @author 80275131
 * @version 1.0
 * @date 2021/2/19 14:25
 * @since 1.0
 **/
public class TimestampInputVerifier extends AbstractJTextComponentInputVerifier {

	@Override
	public boolean verify(JTextComponent jTextField) {
		String text = jTextField.getText();
		return TimestampUtil.isTimestamp(text);
	}
}

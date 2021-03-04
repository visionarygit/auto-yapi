package com.ulrica.idea.verifier;

import com.ulrica.idea.utils.FileUtil;

import javax.swing.text.JTextComponent;
import java.util.stream.Stream;

/**
 * 输入目录校验
 *
 * @author 80275131
 * @version 1.0
 * @date 2021/2/19 14:30
 * @since 1.0
 **/
public class DirInputVerifier extends AbstractJTextComponentInputVerifier {

	@Override
	public boolean verify(JTextComponent jTextComponent) {
		String text = jTextComponent.getText();
		if (text.contains(",")) {
			String[] split = text.split(",");
			return Stream.of(split).allMatch(FileUtil::dirExists);
		}
		return FileUtil.dirExists(text);
	}
}

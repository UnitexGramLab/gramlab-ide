package fr.umlv.unitex.frames;

import java.io.File;

public class TransducerListConfigurationFrameFactory{
	private TransducerListConfigurationFrame frame;
	
	TransducerListConfigurationFrame newTransducerListConfigurationFrame(
			File file) {
		
		frame = new TransducerListConfigurationFrame();
		if (file != null) {
			frame.fill_table(file);
		}
		return frame;
	}
	
	public boolean existsFrame() {
		return frame != null;
	}
	
	public TransducerListConfigurationFrame getFrame() {
		return frame;
	}
	
	void closeFrame() {
		if (frame == null)
			return;
		frame.setVisible(false);
	}
}

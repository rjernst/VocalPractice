import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JComponent;


public class ProgressSlider extends JComponent {

	private static final long serialVersionUID = 1L;

	
	public Dimension getSize() { return new Dimension(300, 15); }
	public Dimension getPreferredSize() { return getSize(); }
	
	Color shadow = new Color(106, 96, 94);
	Color border = new Color(147, 145, 145);
	Color empty = new Color(177, 177, 177);
	
	private static int ARC = 15;
	
	protected void paintComponent(Graphics g) {
		int w = getWidth();
		int h = getHeight();
		
		g.setColor(Color.YELLOW);
	    g.fillRect(0, 0, w, h);
		g.setColor(empty);		
		g.fillRoundRect(0, 0, w - 1, h - 1, ARC, ARC);
		g.setColor(border);
		g.drawRoundRect(0, 0, w - 1, h - 1, ARC, ARC);
	}
}

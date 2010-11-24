import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

public class SwitchImageButton extends JComponent implements MouseListener {

	private static final long serialVersionUID = 1L;

	BufferedImage[] images;
	int ndx;
	Vector<ActionListener> listeners;
	
	SwitchImageButton(String on_img, String on_p_img, String off_img, String off_p_img) throws IOException {	
		addMouseListener(this);
		listeners = new Vector<ActionListener>();
		images = new BufferedImage[4];
		images[0] = ImageIO.read(getClass().getResource(on_img));
		images[1] = ImageIO.read(getClass().getResource(on_p_img));
		images[2] = ImageIO.read(getClass().getResource(off_img));
		images[3] = ImageIO.read(getClass().getResource(off_p_img));
		ndx = 2;
	}
	
	boolean getState() { return ndx < 2; }
	
	void setState(boolean s) {
		ndx = s ? 0 : 2;
		fireStateChange();
		repaint();
	}
	
	public Dimension getSize() { return new Dimension(images[0].getWidth(), images[0].getHeight()); }
	public Dimension getPreferredSize() { return getSize(); }
	
	protected void paintComponent(Graphics g) { g.drawImage(images[ndx], 0, 0, this); }

	public void mouseClicked(MouseEvent e) { setState(!getState()); }
	public void mousePressed(MouseEvent e) { ++ndx; repaint(); }
	public void mouseReleased(MouseEvent e) { mouseExited(e); }
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) { 
		if (ndx % 2 == 1) {
			--ndx; 
		}
		repaint();
	}
	
	public void addActionListener(ActionListener l) { listeners.add(l); }
	public void removeActionListener(ActionListener l) { listeners.remove(l); }
	
	public void fireStateChange() {
		ActionEvent e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, getState() ? "on" : "off");
		for (ActionListener l : listeners) {
			l.actionPerformed(e);
		}
	}

}

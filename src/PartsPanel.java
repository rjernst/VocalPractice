import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;


public class PartsPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	
	class PartButtons {
		PartButtons(int t) {
			track = t;
			group = new ButtonGroup();
			one = new JRadioButton();
			two = new JRadioButton();
			mute = new JRadioButton();
			group.add(one);
			group.add(two);
			group.add(mute);
			group.setSelected(one.getModel(), true);
			
			ActionListener handler = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (e.getSource() == one) {
						filter.setTrackChannel(track, PartFilter.Channel.One);
					} else if (e.getSource() == two) {
						filter.setTrackChannel(track, PartFilter.Channel.Two);
					} else if (e.getSource() == mute) {
						filter.setTrackChannel(track, PartFilter.Channel.Mute);
					}
				}		
			};
			one.addActionListener(handler);
			two.addActionListener(handler);
			mute.addActionListener(handler);
		}
		public ButtonGroup group;
		public JRadioButton one;
		public JRadioButton two;	
		public JRadioButton mute;	
		public int track;
	};
	
	PartButtons[] buttons;
	PartFilter filter;
	GridLayout grid;
	
	PartsPanel(PartFilter f) {
		filter = f;
	}
	
	public void reset() {
		grid = new GridLayout(filter.getNumTracks() + 1, 4);
		buttons = new PartButtons[filter.getNumTracks()];
		removeAll();
		setLayout(grid);
		add(new JLabel()); // placeholder
		add(new JLabel("1"));
		add(new JLabel("2"));
		add(new JLabel("Mute"));
		for (int i = 0; i < filter.getNumTracks(); ++i) {
			if (filter.getTrackChannel(i) == PartFilter.Channel.Mask) continue;
			buttons[i] = new PartButtons(i);
			add(new JLabel(filter.getTrackName(i)));
			add(buttons[i].one);
			add(buttons[i].two);
			add(buttons[i].mute);
		}
	}
}

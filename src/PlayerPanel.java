
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.URL;

import javax.swing.*;
import javax.swing.event.*;

public class PlayerPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	
	SwitchImageButton play_pause;
	JButton play = new JButton("Play");
	JButton pause = new JButton("Pause");
	JButton stop = new JButton("Stop");
	
	JLabel time = new JLabel();
	JLabel end_time = new JLabel();
	JSlider progress = new JSlider();
	int position; // current position, for synchronization between updating slider and user movement
	Timer timer;
	
	JPanel tempo_box = new JPanel();
	JLabel tempo_txt = new JLabel("Tempo: 100%");
	JSlider tempo_sld = new JSlider(50, 100);
	GridLayout tempo_grid = new GridLayout(2, 1);
	
	MidiPlayer player;

	public ImageIcon getImage(String name) {
		
		System.out.println("Class at " + getClass().getProtectionDomain().getCodeSource().getLocation());
		URL url = getClass().getResource(name);
		if (url == null) {
			System.out.println("Failed to get image: " + name);
		}
		return new ImageIcon(url);
	}
	
	public PlayerPanel(MidiPlayer p) throws IOException {
		player = p;
		setBackground(Color.WHITE);
		
		play_pause = new SwitchImageButton("pause.png", "pause-pressed.png", "play.png", "play-pressed.png");
		play_pause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (player.isRunning()) {
					timer.stop();
					player.pause();
				} else {
					player.play();
					timer.start();
				}
			}
		});
		
		progress.setMinimum(0);
		progress.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int value = progress.getValue();
				time.setText(format_seconds(value));
				if (progress.getValueIsAdjusting() || value == position) return;
				position = value;
				player.setPosition(value);
			}
		});
		
		// update slider as midi is played
		timer = new Timer(100, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (player.isRunning()) {
					if (!progress.getValueIsAdjusting()) {
						position = player.getPosition();
						progress.setValue(position);
					}
				} else {
					progress.setValue(0);
				}
		    }
		});	
		
		tempo_box.setLayout(tempo_grid);
		tempo_box.add(tempo_txt);
		tempo_box.add(tempo_sld);
		tempo_sld.setValue(100);
		tempo_sld.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int value = tempo_sld.getValue();
				tempo_txt.setText("Tempo: " + Integer.toString(value) + "%");
				player.setTempoFactor(value/100.0f);
			}
		});
			
		add(play_pause);
		add(time);
		add(progress);
		add(end_time);
		add(new ProgressSlider());
		add(tempo_box);
		
	}
	
	public void reset() {
		time.setText("0:00");
		end_time.setText(format_seconds(player.getLength()));
		progress.setMaximum(player.getLength());			
		progress.setMinimum(0);
		progress.setValue(0);
	}
	
	String format_seconds(int sec) {
		return String.format("%d:%02d", sec / 60, sec % 60);
	}

}

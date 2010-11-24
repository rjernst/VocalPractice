import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.sound.midi.Instrument;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.Synthesizer;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class ChannelPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	static int VOLUME = 7;
	static int PAN = 10;
	
	MidiChannel channel;
	GridLayout grid = new GridLayout(3, 2);
		
	JSlider volume = new JSlider(0, 127);
	JSlider pan = new JSlider(0, 127);
	JComboBox instrument = new JComboBox();
	Instrument[] instruments = new Instrument[3];
	Synthesizer synthesizer;
	
	ChannelPanel(MidiChannel c, Synthesizer synth) {
		channel = c;
		synthesizer = synth;
		Instrument[] instrs = synth.getAvailableInstruments();
		instruments[0] = instrs[0];
		instruments[1] = instrs[61];
		instruments[2] = instrs[69];
		setLayout(grid);
		
		volume.setValue(100);
		volume.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				channel.controlChange(VOLUME, volume.getValue());
			}		
		});
		pan.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				channel.controlChange(PAN, pan.getValue());
			}		
		});
		for (int i = 0; i < instruments.length; ++i) {
			instrument.addItem(instruments[i].getName());
		}
		instrument.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Instrument i = instruments[instrument.getSelectedIndex()];
				//synthesizer.loadInstrument(i);
				channel.programChange(i.getPatch().getBank(), i.getPatch().getProgram());
			}
		});
		
		add(new JLabel("Volume"));
		add(volume);
		add(new JLabel("Pan"));
		add(pan);
		add(new JLabel("Instrument"));
		add(instrument);
	}
}

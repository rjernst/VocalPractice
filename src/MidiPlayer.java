
import java.io.*;
import javax.sound.midi.*;

public class MidiPlayer {
		
	Sequencer sequencer;
	Synthesizer synthesizer;
	Sequence sequence;
	MidiChannel[] channels;
	PartFilter filter;
	
	private static int TRACKNAME = 3;
	private static int CHANNEL_PREFIX = 32;
	
	public MidiPlayer() {
		try {
			sequencer = MidiSystem.getSequencer(false);
			sequencer.open();
			synthesizer = MidiSystem.getSynthesizer();		
			synthesizer.open();
			channels = synthesizer.getChannels();
			
			 // setup our track->channel mapper between sequencer and synthesizer
			filter = new PartFilter();
            sequencer.getTransmitter().setReceiver(filter);
            filter.setReceiver(synthesizer.getReceiver());
			System.out.println("Channels: " + channels.length);
            
		} catch (Exception e) {
			System.err.println("Error opening midi system: " + e.getMessage());
		}	
	}
	
	public void close() {
		pause();
		sequencer.close();
		synthesizer.close();
	}
	
	public boolean isOpen() {
		return sequencer.isOpen();
	}
	
	public PartFilter getFilter() { return filter; }
	
	// precondition: file exists and is accessible
	public void loadFile(String filename) {
				
		try {
			FileInputStream fs = new FileInputStream(filename);
            BufferedInputStream buffer = new BufferedInputStream(fs, 1024);
            sequencer.setSequence(buffer);
            Track[] tracks = sequencer.getSequence().getTracks();
            assert tracks.length <= 16;            
            filter.setNumTracks(tracks.length);
            
            // scan track data for initial values
            for (int i = 0; i < tracks.length; ++i) {      	
            	Track t = tracks[i];
            	boolean found_data = false;
            	for (int j = 0; j < t.size(); ++j) {
        			MidiEvent e = t.get(j);
        			MidiMessage m = e.getMessage();
        			
        			if (m instanceof ShortMessage) {
        				found_data = true;
        				ShortMessage sm = (ShortMessage)m;		
        				// change channel of each track to the track number
        				if (sm.getCommand() == ShortMessage.CONTROL_CHANGE && sm.getData1() == 10) {
        					System.out.println("pan (" + i + ") = " + sm.getData2());
        				}
        				if (sm.getCommand() == ShortMessage.PROGRAM_CHANGE) {
        					System.out.println("instrument (" + i + ") = " + sm.getData1());
        				}
        				sm.setMessage(sm.getCommand(), i, sm.getData1(), sm.getData2());
        				
        				
        			} else if (m instanceof MetaMessage) {
        				MetaMessage mm = (MetaMessage)m;
        				if (mm.getType() == TRACKNAME) {
        					filter.setTrackName(i, new String(mm.getData()));
        				} else if (mm.getType() == CHANNEL_PREFIX){
        					byte[] d = {(byte)i};
        					mm.setMessage(mm.getType(), d, 1);      					
        				} 
        			}
        		}
            	
            	if (!found_data) {
            		filter.setTrackChannel(i, PartFilter.Channel.Mask);
            		System.out.println("Masking track " + i);
            	} else {
            		filter.setTrackChannel(i, PartFilter.Channel.One);
            	}
            }       
            
        } catch (Exception e) {  
        	System.err.println("Error loading file: " + e.getMessage());      	
        }   
	}
	
	public void play() {
		sequencer.start(); 
	}
	
	public void pause() {
		sequencer.stop(); 
	}
	
	public void stop() { 
		sequencer.stop();
		sequencer.setMicrosecondPosition(0L);
	}
	
	public boolean isRunning() { return sequencer.isRunning(); }
	
	public float getTempoFactor() { return sequencer.getTempoFactor(); }
	
	public void setTempoFactor(float f) { sequencer.setTempoFactor(f); }
		
	// length in seconds
	public int getLength() { return (int)sequencer.getMicrosecondLength()/1000000; }
	
	// current position in seconds
	public int getPosition() { return (int)sequencer.getMicrosecondPosition()/1000000; }
	
	public void setPosition(int sec) { sequencer.setMicrosecondPosition(sec*1000000); }		
	
	
}

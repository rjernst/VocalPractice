import javax.sound.midi.*;


public class PartFilter implements Receiver, Transmitter {

	enum Channel {
		One,
		Two,
		Mute,
		Mask,
	}
	
	Receiver rec;
	ShortMessage message;
	Channel[] channels;
	String[] names;
	
	static int BANK = 0;
	static int VOLUME = 7;
	static int PAN = 10;
	static int DAMPER_PEDAL = 64;	
	static int RESET_ALL_CONTROLLERS = 121;
	static int ALL_NOTES_OFF = 123;
	
	public PartFilter() {
		message = new ShortMessage();
	}
	
	public void setNumTracks(int num_tracks) {
		channels = new Channel[num_tracks];
		names = new String[num_tracks];
	}
	
	public void setTrackName(int track, String name) {
		names[track] = name;
	}
	
	public String getTrackName(int track) {
		return names[track];
	}
	
	public void setTrackChannel(int track, Channel channel) {
		channels[track] = channel;
	}
	
	public Channel getTrackChannel(int track) {
		return channels[track];
	}
	
	public int getNumTracks() {
		return channels.length;
	}
	
	public void setReceiver(Receiver receiver) { rec = receiver; }

	public Receiver getReceiver() {	return rec; }

	public void send(MidiMessage m, long t) {
		if (m instanceof ShortMessage) {
			ShortMessage sm = (ShortMessage)m;
			
			// skip control messages so we can control channels directly
			if (sm.getCommand() == ShortMessage.PROGRAM_CHANGE) {
				return;				
			} else if (sm.getCommand() == ShortMessage.CONTROL_CHANGE) {
				int d1 = sm.getData1();
				if (d1 == BANK || d1 == VOLUME || d1 == PAN) {
					return;
				} else if (d1 == DAMPER_PEDAL || d1 == ALL_NOTES_OFF || d1 == RESET_ALL_CONTROLLERS) {
					rec.send(m, t);
					return;
				}
			}
			
			try {
				if (sm.getChannel() < channels.length) {
					Channel c = channels[sm.getChannel()];
					if (c == Channel.Mute) return;
					message.setMessage(sm.getCommand(), c.ordinal(), sm.getData1(), sm.getData2());
					m = message;
				} else {
					System.err.println("Found bad message, channel (track?) " + sm.getChannel() + " command " + sm.getCommand() + " data1 = " + sm.getData1());
				}
			} catch (InvalidMidiDataException e) {
				System.out.println("Error copying midi data");
			}
		} 
		rec.send(m, t);
	}

	public void close() {
		if (rec != null) {
			rec.close();
		}	
	}

	
	
}


import java.awt.*;

import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;

public class VocalPractice extends JFrame {

	private static final long serialVersionUID = 1L;
	
	int width = 760, height = 500;
	MidiPlayer player;
	
	PlayerPanel player_ui;
	PartsPanel parts_ui;
	JTabbedPane channels_ui;
	JPanel controls_ui;
	Preferences prefs;

	public VocalPractice() throws IOException {
		super("Vocal Practice");
		prefs = Preferences.userNodeForPackage(this.getClass());
		Runtime.getRuntime().addShutdownHook(new Thread() {
		    public void run() {
		    	player.close();
		    }
		});
		addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {System.exit(0);}
            //public void windowDeiconified(WindowEvent e) { demo.open(); }
            //public void windowIconified(WindowEvent e) { demo.close(); }
        });
		
		JMenuBar menus = new JMenuBar();
		JMenu file_menu = new JMenu("File");
		JMenuItem file_open = new JMenuItem("Open");
		file_open.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { choose_file(); }		
		});
		file_menu.add(file_open);
		menus.add(file_menu);
		setJMenuBar(menus);
		
		player = new MidiPlayer();
		
		player_ui = new PlayerPanel(player);
		parts_ui = new PartsPanel(player.getFilter());
		channels_ui = new JTabbedPane();
		channels_ui.add("1", new ChannelPanel(player.channels[0], player.synthesizer));
		channels_ui.add("2", new ChannelPanel(player.channels[1], player.synthesizer));
		
		JPanel main = new JPanel();
		setLayout(new BorderLayout());
		add(player_ui, BorderLayout.PAGE_START);
		main.add(parts_ui);
		main.add(channels_ui);
		getContentPane().add("Center", main);
        pack();
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(d.width/2 - width/2, d.height/2 - height/2);
        setSize(new Dimension(width, height));
	}
	
	private void choose_file() {
		player.pause();
		String dir = prefs.get("last.opendir", "");
		JFileChooser chooser = new JFileChooser();
		if (!dir.isEmpty()) {
			chooser.setCurrentDirectory(new File(dir));
		}
	    chooser.setFileFilter(new FileFilter() {
			public boolean accept(File p) { return p.isFile() && p.getPath().endsWith(".mid"); }
			public String getDescription() { return "MIDI Files"; }
	    });
	    int returnVal = chooser.showOpenDialog(this);
	    if (returnVal == JFileChooser.APPROVE_OPTION) {
	    	File f = chooser.getSelectedFile();
	    	open_file(f.getAbsolutePath());
	    	prefs.put("last.opendir", f.getParent());
	    }
	}
	
	private void open_file(String path) {
		player.loadFile(path);
		player_ui.reset();
		parts_ui.reset();
	}
	
	public Dimension getPreferredSize() {
        return new Dimension(width, height);
    }	
	
	public static void main(String[] args) {
		try {
			final VocalPractice app = new VocalPractice();
			app.setVisible(true);
		} catch (IOException e) {
			System.err.println("Error: " + e.getMessage());
			System.err.println(e.getStackTrace());
		}
	}
}

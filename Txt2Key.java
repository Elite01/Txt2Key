import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import net.miginfocom.swing.MigLayout;

public class Txt2Key {

	private static final String VERSION = "1.0.2";

	private JFrame frmTxt2Key;
	private String txt;
	private Robot robot;

	private JSpinner spinnerSecondsBeforeStart;
	private JSpinner spinnerTimes;
	private JSpinner spinnerBeteenLetters;
	private JSpinner spinnerBeteenTimes;

	private JScrollPane scrollPane;
	private JTextArea txtarea;
	private JProgressBar progressBar;

	private JButton btnPause;
	private JButton btnStop;
	private JButton btnStart;

	private boolean boolPause=false;
	private boolean boolStop=false;
	private JMenuBar menuBar;
	private JMenu mnThemes;
	private JRadioButtonMenuItem radioMenuDefault;
	private ButtonGroup buttonGroupThemes = new ButtonGroup();
	private ButtonGroup buttonGroupType = new ButtonGroup();
	private JRadioButtonMenuItem[]radioMenuThemes;
	private JCheckBox chckbxAlwaysOnTop;

	private JRadioButton radioButtonTimes;
	private JRadioButton radioButtonTimer;
	private JCheckBox cbBetweenLetters;
	private JCheckBox cbBetweenTimes;
	private JSpinner spinnerSeconds;
	private JCheckBox checkBox;
	private JComboBox<String>comboBox;
	private JPanel panelButtons;
	private JLabel lblTimes;

	private int[]keycodes={	// keycodes[(TheHebChar)-'א']=keyEvent
			'T', 'C', 'D', 'S', 'V', 'U',
			'Z', 'J', 'Y', 'H', 'L', 'F',
			'K', 'O', 'N', 'I', 'B', 'X',
			'G', KeyEvent.VK_COLON, 'P',
			KeyEvent.VK_PERIOD, 'M', 'E',
			'R', 'A', KeyEvent.VK_COMMA	};

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					Txt2Key window = new Txt2Key();
					window.frmTxt2Key.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Txt2Key() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmTxt2Key = new JFrame("Txt2Key " + VERSION);
		frmTxt2Key.setSize(300, 500);
		frmTxt2Key.setMinimumSize(new Dimension(300, 500));
		frmTxt2Key.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container pane = frmTxt2Key.getContentPane();
		pane.setLayout(new MigLayout("", "[20px][55px][65px][100px,grow]", "[20px][20px][20px][20px][20px][20px][grow][20px]"));

		try {UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());} 
		catch (Exception e) {e.printStackTrace();}

		panelButtons = new JPanel();
		pane.add(panelButtons, "cell 0 0 4 1,grow");
		panelButtons.setLayout(new GridLayout(1, 3));

		btnStart = new JButton("Start");
		btnStart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				btnStart.setEnabled(false);
				btnStop.setEnabled(true);
				btnPause.setEnabled(true);
				new background().execute();
			}
		});
		panelButtons.add(btnStart);

		btnPause = new JButton("Pause");
		btnPause.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (!boolPause)
				{
					btnPause.setText("Unpause");
					progressBar.setForeground(Color.RED);
				}
				else
				{
					btnPause.setText("Pause");
					progressBar.setForeground(new Color(51, 153, 255));
				}
				boolPause=!boolPause;
			}
		});
		btnPause.setEnabled(false);
		panelButtons.add(btnPause);

		btnStop = new JButton("Stop");
		btnStop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boolStop=true;
			}
		});
		btnStop.setEnabled(false);
		panelButtons.add(btnStop);

		checkBox = new JCheckBox("", true);
		checkBox.setEnabled(false);
		frmTxt2Key.getContentPane().add(checkBox, "cell 0 1,grow");

		spinnerSecondsBeforeStart = new JSpinner();
		frmTxt2Key.getContentPane().add(spinnerSecondsBeforeStart, "flowx,cell 1 1,grow");
		spinnerSecondsBeforeStart.setModel(new SpinnerNumberModel(1, 1, null, 1));

		frmTxt2Key.getContentPane().add(new JLabel("Seconds before start/unpause."), "cell 2 1 2 1,grow");

		radioButtonTimes = new JRadioButton();
		frmTxt2Key.getContentPane().add(radioButtonTimes, "cell 0 2,grow");
		radioButtonTimes.setSelected(true);
		radioButtonTimes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				spinnerTimes.setEnabled(true);
				spinnerSeconds.setEnabled(false);
				comboBox.setEnabled(false);
				lblTimes.setText("0");
			}
		});
		buttonGroupType.add(radioButtonTimes);

		spinnerTimes = new JSpinner(new SpinnerNumberModel(0, 0, null, 1));
		frmTxt2Key.getContentPane().add(spinnerTimes, "cell 1 2,grow");

		frmTxt2Key.getContentPane().add(new JLabel("Times to type text (0 = no limit)."), "cell 2 2 2 1,grow");

		radioButtonTimer = new JRadioButton();
		frmTxt2Key.getContentPane().add(radioButtonTimer, "cell 0 3,grow");
		radioButtonTimer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				spinnerTimes.setEnabled(false);
				spinnerSeconds.setEnabled(true);
				comboBox.setEnabled(true);
				lblTimes.setText("00:00:00");
			}
		});
		buttonGroupType.add(radioButtonTimer);

		spinnerSeconds = new JSpinner(new SpinnerNumberModel(0, 0, null, 1));
		frmTxt2Key.getContentPane().add(spinnerSeconds, "cell 1 3,grow");
		spinnerSeconds.setEnabled(false);

		comboBox = new JComboBox<String>();
		comboBox.setEnabled(false);
		String[]items={	"Seconds", "Minutes", "Hours",
						"Days", "Weeks", "Months",
						"Years", "Decades", "Centuries"};
		for (String item:items)
			comboBox.addItem(item);
		frmTxt2Key.getContentPane().add(comboBox, "cell 2 3,grow");
		frmTxt2Key.getContentPane().add(new JLabel(" to type text."), "cell 3 3,grow");

		cbBetweenLetters = new JCheckBox();
		frmTxt2Key.getContentPane().add(cbBetweenLetters, "cell 0 4,grow");
		cbBetweenLetters.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				spinnerBeteenLetters.setEnabled(cbBetweenLetters.isSelected());
			}
		});

		spinnerBeteenLetters = new JSpinner();
		frmTxt2Key.getContentPane().add(spinnerBeteenLetters, "cell 1 4,grow");
		spinnerBeteenLetters.setEnabled(false);
		spinnerBeteenLetters.setModel(new SpinnerNumberModel(0, 0, null, 1));

		frmTxt2Key.getContentPane().add(new JLabel("Miliseconds between letters."), "cell 2 4 2 1,grow");

		cbBetweenTimes = new JCheckBox();
		frmTxt2Key.getContentPane().add(cbBetweenTimes, "cell 0 5,grow");
		cbBetweenTimes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				spinnerBeteenTimes.setEnabled(cbBetweenTimes.isSelected());
			}
		});

		spinnerBeteenTimes = new JSpinner(new SpinnerNumberModel(0, 0, null, 1));
		frmTxt2Key.getContentPane().add(spinnerBeteenTimes, "cell 1 5,grow");
		spinnerBeteenTimes.setEnabled(false);

		frmTxt2Key.getContentPane().add(new JLabel("Miliseconds between times."), "cell 2 5 2 1,grow");

		txtarea = new JTextArea();
		scrollPane = new JScrollPane(txtarea);
		pane.add(scrollPane, "cell 0 6 4 1,grow");
		txtarea.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent arg0) {
				String tmp=txtarea.getText().toString();
				for (int i=0; i<tmp.length(); i++)
				{
					if (tmp.charAt(i)>='א' && tmp.charAt(i)<='ת')
					{
						txtarea.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
						scrollPane.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
						break;
					}
					if (tmp.charAt(i)>='a' && tmp.charAt(i)<='z')
					{
						txtarea.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
						scrollPane.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
						break;
					}
					if (tmp.charAt(i)>='A' && tmp.charAt(i)<='Z')
					{
						txtarea.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
						scrollPane.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
						break;
					}
				}
			}
		});
		txtarea.setDragEnabled(true);
		txtarea.setFont(new Font("Arial", Font.PLAIN, 12));

		progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
		progressBar.setForeground(new Color(51, 153, 255));
		pane.add(progressBar, "cell 0 7 4 1,grow");

		menuBar = new JMenuBar();
		frmTxt2Key.setJMenuBar(menuBar);

		mnThemes = new JMenu("Themes");
		menuBar.add(mnThemes);

		radioMenuDefault = new JRadioButtonMenuItem("Default");
		radioMenuDefault.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				Dimension d=frmTxt2Key.getSize();
				SwingUtilities.updateComponentTreeUI(frmTxt2Key);
				frmTxt2Key.pack();
				frmTxt2Key.setSize(d);
			}
		});
		radioMenuDefault.setSelected(true);
		buttonGroupThemes.add(radioMenuDefault);
		mnThemes.add(radioMenuDefault);

		LookAndFeelInfo[]LAFI=UIManager.getInstalledLookAndFeels();
		radioMenuThemes=new JRadioButtonMenuItem[LAFI.length];
		for (int i = 0; i < LAFI.length; i++)
		{
			radioMenuThemes[i] = new JRadioButtonMenuItem(LAFI[i].getName());
			String LAFname=LAFI[i].getClassName();
			radioMenuThemes[i].addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					try {
						UIManager.setLookAndFeel(LAFname);
					} catch (Exception e) {
						e.printStackTrace();
					}
					Dimension d=frmTxt2Key.getSize();
					SwingUtilities.updateComponentTreeUI(frmTxt2Key);
					frmTxt2Key.pack();
					frmTxt2Key.setSize(d);
				}
			});
			buttonGroupThemes.add(radioMenuThemes[i]);
			mnThemes.add(radioMenuThemes[i]);
		}

		chckbxAlwaysOnTop = new JCheckBox("Always On Top");
		chckbxAlwaysOnTop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				frmTxt2Key.setAlwaysOnTop(chckbxAlwaysOnTop.isSelected());
			}
		});
		menuBar.add(chckbxAlwaysOnTop);

		menuBar.add(new JLabel("  |  "));
		lblTimes = new JLabel();
		lblTimes.setText("0");
		lblTimes.setHorizontalAlignment(SwingConstants.CENTER);
		menuBar.add(lblTimes);

	}

	public int keyevent(char c)
	{
		if ((c>='A' && c<='Z') || (c>='0' && c<='9'))
			return c;
		if ((c>='א' && c<='ת'))
			return keycodes[c-'א']; // convert heb char to keycode
		if (c=='\n') // newline
			return(KeyEvent.VK_ENTER);

		if (c=='!')	return (KeyEvent.VK_EXCLAMATION_MARK);
		if (c=='@')	return (KeyEvent.VK_AT);
		if (c=='#')	return (KeyEvent.VK_NUMBER_SIGN);
		if (c=='$')	return (KeyEvent.VK_DOLLAR);
		if (c=='%') return ('5'); // no keycode for '%' so shift+'5'
		if (c=='^')	return (KeyEvent.VK_CIRCUMFLEX);
		if (c=='&')	return (KeyEvent.VK_AMPERSAND);
		if (c=='*')	return (KeyEvent.VK_ASTERISK);
		if (c=='(')	return (KeyEvent.VK_LEFT_PARENTHESIS);
		if (c==')')	return (KeyEvent.VK_RIGHT_PARENTHESIS);
		if (c=='-')	return (KeyEvent.VK_MINUS);
		if (c=='_')	return (KeyEvent.VK_UNDERSCORE);
		if (c=='+')	return (KeyEvent.VK_PLUS);
		if (c=='=')	return (KeyEvent.VK_EQUALS);

		if (c=='[')	return (KeyEvent.VK_OPEN_BRACKET);
		if (c==']')	return (KeyEvent.VK_CLOSE_BRACKET);
		if (c==':')	return (KeyEvent.VK_SEMICOLON);
		if (c==';')	return (KeyEvent.VK_SEMICOLON);
		if (c=='.')	return (KeyEvent.VK_PERIOD);
		if (c==',')	return (KeyEvent.VK_COMMA);
		if (c=='/')	return (KeyEvent.VK_SLASH);
		if (c=='\\')return (KeyEvent.VK_BACK_SLASH);

		if (c!=' ')
			System.err.println("Dont know what is '"+c+"' / "+(int)c);
		return (KeyEvent.VK_SPACE);//If all else fails
	}

	private class background extends SwingWorker<Void, Void> {
		@Override
		protected Void doInBackground() throws Exception {
			txt=txtarea.getText();

			robot = new Robot();
			robot.setAutoWaitForIdle(true);
			if (txt.length()!=0) // cant type empty box
			{
				if (Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_CAPS_LOCK))
				{ // if CAPSLOCK is on than turn it off
					robot.keyPress  (KeyEvent.VK_CAPS_LOCK);
					robot.keyRelease(KeyEvent.VK_CAPS_LOCK);
				}

				Thread.sleep((int)spinnerSecondsBeforeStart.getValue()*1000); //timer before start
				if (radioButtonTimes.isSelected()) // number of times
				{
					int times=(int)spinnerTimes.getValue();
					if (times==0) // unlimited
					{
						progressBar.setIndeterminate(true);
						progressBar.setStringPainted(false);
					}
					for (int a=0; (a<times || times==0) && !boolStop; a++) // Times to write
					{
						TypeText(false, a, times);
						if (cbBetweenTimes.isSelected())
							Thread.sleep((int)spinnerBeteenTimes.getValue());
					}
				}
				else // timer 
				{
					long seconds=(int)spinnerSeconds.getValue();
					switch (comboBox.getSelectedItem().toString())
					{
						case "Seconds":		seconds*=1;		break;
						case "Minutes":		seconds*=1*60;	break;
						case "Hours":		seconds*=1*60*60;	  break;
						case "Days":		seconds*=1*60*60*24;  break;
						case "Weeks":		seconds*=1*60*60*24*7;		break;
						case "Months":		seconds*=1*60*60*24*7*4;	break;
						case "Years":		seconds*=1*60*60*24*7*4*12;		  break;
						case "Decades":		seconds*=1*60*60*24*7*4*12*10;	  break;
						case "Centuries":	seconds*=1*60*60*24*7*4*12*10*10;		break;
						case "Millenia":	seconds*=1*60*60*24*7*4*12*10*10*10;	break;
					}

					for (long stop=System.currentTimeMillis()+seconds*1000; stop > System.currentTimeMillis() && !boolStop;)
					{
						TypeText(true, stop, seconds);
						if (cbBetweenTimes.isSelected())
							Thread.sleep((int)spinnerBeteenTimes.getValue());
					}
				}
			}

			btnStop.setEnabled(false);
			btnPause.setEnabled(false);
			btnPause.setText("Pause");
			boolPause=false;
			boolStop=false;
			btnStart.setEnabled(true);
			progressBar.setValue(0);
			progressBar.setForeground(new Color(51, 153, 255));
			progressBar.setIndeterminate(false);
			progressBar.setStringPainted(true);
			return null;
		}

		public void TypeText(boolean isTimerBool, double progressbarParamA, long progressbarParamB) throws Exception
		{
			for (int b=0; b<txt.length() && !boolStop; b++) // looping the txt
			{
				while(boolPause && !boolStop) // loop until unpause or stop
					Thread.sleep(1000); // check every second

				char c=txt.charAt(b);
				if (c>='A' && c<='Z' || c=='%' || c==':')//uppercase=needs shift (or %)
					robot.keyPress(KeyEvent.VK_SHIFT);
				if (c>='a' && c<='z')//lowercase=not good - change to uppercase
					c-=32;

				int key=0; // temp to hold keyevent
				try
				{
					key=keyevent(c);
					robot.keyPress  (key);
					robot.keyRelease(key);
				}
				catch(IllegalArgumentException e)
				{
					System.err.println(key + " doesnt exist");
					robot.keyPress  (KeyEvent.VK_SPACE);
					robot.keyRelease(KeyEvent.VK_SPACE); // space instead
					e.printStackTrace();
				}

				robot.keyRelease(KeyEvent.VK_SHIFT); // if shifted

				if (cbBetweenLetters.isSelected())
					Thread.sleep((int)spinnerBeteenLetters.getValue()); //spinnerBeteenLetters

				if (!isTimerBool) // to handle progressbar mid-txt
				{
					lblTimes.setText(""+(int)progressbarParamA+1);
					if 	(progressbarParamB!=0)	// times!=0
						progressBar.setValue((int) ((b*100/txt.length()/progressbarParamB)+(progressbarParamA*100/progressbarParamB)));
				}
				else
				{
					double secondspassed=progressbarParamB-((double)progressbarParamA-System.currentTimeMillis())/1000;
					lblTimes.setText((secondspassed/3600<10 ? "0":"") + (int)secondspassed/3600 + ":" + (secondspassed/60<10 ? "0":"") + (int)secondspassed/60 + ":" + (secondspassed<10 ? "0":"") + (int)secondspassed);
					progressBar.setValue((int) (secondspassed*100/progressbarParamB));
					
					if (progressBar.getValue()==100) // in case txt is longer than time (100%)
						return;
				}
			}
		}
	}
}

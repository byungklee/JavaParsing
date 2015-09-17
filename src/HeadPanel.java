import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

//3 button
public class HeadPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6089292256508620236L;
	private JButton parseButton;
	private JButton displayButton;
	private JButton extraButton;
	private MainController m;
	public HeadPanel(MainController m) {
		setSize(MainView.WIDTH, MainView.HEIGHT/10);
		setLayout(new FlowLayout());
		this.m = m;
		initialize();
		add(parseButton);
		add(displayButton);
		add(extraButton);
	}
	
	private void initialize() {
		initializeParseButton();
		initializeDisplayButton();
		initializeExtraButton();
	}
	
	private void initializeParseButton() {
		parseButton = new JButton();
		parseButton.setText("Parse File");
		parseButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				m.executeParse();
			}
		});
	}
	
	private void initializeDisplayButton() {
		displayButton = new JButton();
		displayButton.setText("Display Database");
		displayButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				System.out.println(e.getID());
				m.openDisplayDatabase();
				
			}
		});
	}
	
	private void initializeExtraButton() {
		extraButton = new JButton();
		extraButton.setText("Extra Char Statistic");
		extraButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				System.out.println(e.getID());
				m.analyzeFile();
			}
		});
	}
	
	
}

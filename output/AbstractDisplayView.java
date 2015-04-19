import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


public abstract class AbstractDisplayView extends JPanel {
	protected MainController main;
	protected JList packageList;
	protected DefaultListModel packageListModel;
	protected JList classList;
	protected DefaultListModel classListModel;
	protected JLabel packageLabel;
	protected JLabel classLabel;
	protected JLabel informationLabel;
	protected JButton saveButton;
	protected JPanel leftPanel;
	protected JPanel middlePanel;
	protected JPanel rightPanel;
	protected JEditorPane editorPane;

	public AbstractDisplayView(MainController main, DefaultListModel packageModel) {
		this.main = main;
		this.packageListModel = packageModel;
		//setBackground(Color.BLUE);

		setLayout(new FlowLayout());
		leftPanel = new JPanel();
		leftPanel.setLayout(new BorderLayout());
		leftPanel.setPreferredSize(new Dimension((int) (MainView.WIDTH*0.16),  (int) (MainView.HEIGHT*0.8)));
		//add(leftPanel, BorderLayout.WEST);
		add(leftPanel);

		middlePanel = new JPanel();
		middlePanel.setLayout(new BorderLayout());
		//middlePanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, thickness));
		middlePanel.setPreferredSize(new Dimension((int) (MainView.WIDTH*0.21), (int) ( MainView.HEIGHT*0.8)));
		//add(middlePanel, BorderLayout.CENTER);
		add(middlePanel);
		rightPanel = new JPanel();
		rightPanel.setLayout(new BorderLayout());
		rightPanel.setPreferredSize(new Dimension((int) (MainView.WIDTH*0.6), (int) (MainView.HEIGHT*0.8)));
		add(rightPanel);
		//add(rightPanel, BorderLayout.EAST);

		initialize();

	}

	public void initialize() {

		packageLabel = new JLabel();
		packageLabel.setText("List of Package");
		leftPanel.add(packageLabel,BorderLayout.PAGE_START);

		classLabel = new JLabel();
		classLabel.setText("List of Class");
		middlePanel.add(classLabel, BorderLayout.PAGE_START);

		informationLabel = new JLabel();
		informationLabel.setText("Infomation");
		rightPanel.add(informationLabel, BorderLayout.PAGE_START);


		initPackageList();
		initClassList();
		initInformation();
		initSaveButton();
		validate();
	}
	public void initPackageList() {
		System.out.println("initializing pakckage view ");


		packageList = new JList<String>();
		//packageListModel = new DefaultListModel();
		packageList.setModel(packageListModel);


		packageList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		//  packageList.setSelectedIndex(0);
		//packageList.setS

		//packageList.addListSelectionListener(this);
		packageList.setVisibleRowCount(20);
		
		JScrollPane listScrollPane = new JScrollPane(packageList);


		listScrollPane.setSize(leftPanel.getWidth(), MainView.HEIGHT/8);
		Dimension d = new Dimension();
		d.setSize(MainView.WIDTH/3, MainView.HEIGHT/8);
		listScrollPane.setMinimumSize(d);

		leftPanel.add(listScrollPane,BorderLayout.CENTER);

	}

	public void initClassList() {
		classListModel = new DefaultListModel();
		classList = new JList(classListModel);

		classList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		//  packageList.setSelectedIndex(0);

		//packageList.addListSelectionListener(this);
		classList.setVisibleRowCount(20);
//		classList.addListSelectionListener(new ListSelectionListener() {
//
//			@Override
//			public void valueChanged(ListSelectionEvent e) {
//				// TODO Auto-generated method stub
//			//	System.out.println(classList.getSelectedValue());
//				if(classList.getSelectedValue() != null) {
//					main.openClassDetail((ClassInfo) classList.getSelectedValue());
//					packageList.clearSelection();
//
//				}
//
//
//			}
//		});



		JScrollPane listScrollPane = new JScrollPane(classList);
		Dimension d = new Dimension();
		d.setSize(MainView.WIDTH/3, MainView.HEIGHT/8);
		listScrollPane.setPreferredSize(d);
		middlePanel.add(classList,BorderLayout.CENTER);
	}

	public void updateClassList(DefaultListModel model) {
		classListModel = model;
		classList.setModel(classListModel);
		classList.updateUI();
	}


	public void initInformation() {
		editorPane = new JEditorPane();
		editorPane.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(editorPane);
		rightPanel.add(scrollPane,BorderLayout.CENTER);

	}
	public void updateInformation(ClassInfo ci) {
		StringBuilder sb = new StringBuilder();
		sb.append(ci.getStringSimple());


		editorPane.setText(sb.toString());
		editorPane.updateUI();
	}

	public void updateInformation(PackageInfo pi) {

		StringBuilder sb = new StringBuilder();
		sb.append(pi.getStringSimple());
		//pi.getString();
		sb.append("\n\n");
		for(int i=0;i<classListModel.getSize();i++) {
			ClassInfo ci = (ClassInfo) classListModel.getElementAt(i);
			sb.append(ci.getString());
			sb.append("\n");
		}

		editorPane.setText(sb.toString());
		editorPane.updateUI();
	}

	abstract void initSaveButton();
	//abstract public void updateClassList(DefaultListModel model);
	abstract public void updatePackageList(DefaultListModel model);
	
	
}

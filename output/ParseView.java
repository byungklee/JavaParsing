import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


public class ParseView extends AbstractDisplayView {
//	MainController main;
//	JList packageList;
//	private DefaultListModel packageListModel;
//	JList classList;
//	private DefaultListModel classListModel;
//	JLabel packageLabel;
//	JLabel classLabel;
//	JLabel informationLabel;
//	JButton saveButton;
//	JPanel leftPanel;
//	JPanel middlePanel;
//	JPanel rightPanel;
//	JEditorPane editorPane;

	public ParseView(MainController main, DefaultListModel packageListModel) {
		super(main, packageListModel);
		//this.packageListModel = packageListModel;
		packageList.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				// TODO Auto-generated method stub
				//System.out.println(packageList.getSelectedValue());
				if(packageList.getSelectedValue() != null) {

					main.openClassListAndDetailOfPackage((PackageInfo) packageList.getSelectedValue());
					classList.clearSelection();
				}
			}
		});
		classList.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				// TODO Auto-generated method stub
			//	System.out.println(classList.getSelectedValue());
				if(classList.getSelectedValue() != null) {
					main.openClassDetail((ClassInfo) classList.getSelectedValue());
					packageList.clearSelection();

				}


			}
		});
	}

//	public void initialize() {
//
//		packageLabel = new JLabel();
//		packageLabel.setText("List of Package");
//		leftPanel.add(packageLabel,BorderLayout.PAGE_START);
//
//		classLabel = new JLabel();
//		classLabel.setText("List of Class");
//		middlePanel.add(classLabel, BorderLayout.PAGE_START);
//
//		informationLabel = new JLabel();
//		informationLabel.setText("Infomation");
//		rightPanel.add(informationLabel, BorderLayout.PAGE_START);
//
//
//		initPackageList();
//		initClassList();
//		initInformation();
//		initSaveButton();
//		
//		validate();
//	}

//	public void initPackageList() {
//		System.out.println("initializing pakckage view ");
//
//
//		packageList = new JList<String>();
//		packageListModel = new DefaultListModel();
//
//		Iterator it = main.packageInfoManager.iterator();
//		while(it.hasNext()) {
//			Entry entry = (Entry) it.next();
//			PackageInfo pi = (PackageInfo) entry.getValue();
//			packageListModel.addElement(pi);
//		}
//		packageList.setModel(packageListModel);
//
//
//		packageList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//		//  packageList.setSelectedIndex(0);
//		//packageList.setS
//
//		//packageList.addListSelectionListener(this);
//		packageList.setVisibleRowCount(20);
//
//		JScrollPane listScrollPane = new JScrollPane(packageList);
//
//
//		listScrollPane.setSize(leftPanel.getWidth(), MainView.HEIGHT/8);
//		Dimension d = new Dimension();
//		d.setSize(MainView.WIDTH/3, MainView.HEIGHT/8);
//		listScrollPane.setMinimumSize(d);
//
//		leftPanel.add(listScrollPane,BorderLayout.CENTER);
//
//	}
//
//	public void initClassList() {
//		classListModel = new DefaultListModel();
//		classList = new JList(classListModel);
//
//		classList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//		//  packageList.setSelectedIndex(0);
//
//		//packageList.addListSelectionListener(this);
//		classList.setVisibleRowCount(20);
//		
//
//
//
//		JScrollPane listScrollPane = new JScrollPane(classList);
//		Dimension d = new Dimension();
//		d.setSize(MainView.WIDTH/3, MainView.HEIGHT/8);
//		listScrollPane.setPreferredSize(d);
//		middlePanel.add(classList,BorderLayout.CENTER);
//	}

	public void updateClassList(DefaultListModel model) {
		classListModel = model;
		classList.setModel(classListModel);
		classList.updateUI();
	}


//	public void initInformation() {
//		editorPane = new JEditorPane();
//		editorPane.setEditable(false);
//		JScrollPane scrollPane = new JScrollPane(editorPane);
//		rightPanel.add(scrollPane,BorderLayout.CENTER);
//
//	}
	public void updateInformation(ClassInfo ci) {
		StringBuilder sb = new StringBuilder();
		//sb.append(ci.getStringSimple());
		sb.append(ci.getStringHalstead());


		editorPane.setText(sb.toString());
		editorPane.updateUI();
	}

	public void updateInformation(PackageInfo pi) {

		StringBuilder sb = new StringBuilder();
		//sb.append(pi.getStringSimple());
		sb.append(pi.getStringHalstead());
		//pi.getString();
		sb.append("\n\n");
		for(int i=0;i<classListModel.getSize();i++) {
			ClassInfo ci = (ClassInfo) classListModel.getElementAt(i);
			//sb.append(ci.getStringSimple());
			sb.append(ci.getStringHalstead());
			sb.append("\n");
		}

		editorPane.setText(sb.toString());
		editorPane.updateUI();
	}

	public void initSaveButton() {
		saveButton = new JButton();
		saveButton.setText("Save");
		saveButton.setVisible(true);
		saveButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				System.out.println("save Button");
//				if(main)
				main.loadPackages();
				main.checkDuplicate();
				if(main.classesByPackage.isEmpty()) {
				//	main.updatePackageByNonDuplicateClass();
					main.updatePackageByNonDuplicateClass();
					main.saveCurrentInfoToSQL();
					JOptionPane.showMessageDialog(ParseView.this, "Data Saved.");
					main.openDisplayDatabase();
				} else {
					int button = JOptionPane.showConfirmDialog(null, "Would you replace duplicate(s)?");
					if(button == JOptionPane.OK_OPTION) {
						System.out.println("OKAY BUTTON");
						//main.updateDuplicates();
						//main.removeDuplicates();
						main.updateDuplicates();
						main.updatePackageByNonDuplicateClass();
						main.removeDuplicates();
						main.saveCurrentInfoToSQL();
						JOptionPane.showMessageDialog(ParseView.this, "Data Saved.");
						main.openDisplayDatabase();
						
					} else if(button == JOptionPane.NO_OPTION) {
						System.out.println("NO BUTTON");
						main.updatePackageByNonDuplicateClass();
						main.removeDuplicates();
						main.saveCurrentInfoToSQL();
						JOptionPane.showMessageDialog(ParseView.this, "Data Saved.");
						main.openDisplayDatabase();
						
					}
					
					
					
				}

			}
		});

		saveButton.setPreferredSize(new Dimension(MainView.WIDTH/11, 40));
		add(saveButton,BorderLayout.PAGE_END);

	}

	@Override
	public void updatePackageList(DefaultListModel model) {
		// TODO Auto-generated method stub
		
	}

}

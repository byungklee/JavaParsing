import javax.swing.DefaultListModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


public class DatabaseDisplayView extends AbstractDisplayView {
	public DatabaseDisplayView(MainController main, DefaultListModel packageModel) {
		// TODO Auto-generated constructor stub
		super(main,packageModel);
		
		//saveButton.setVisible(false);
		packageList.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				// TODO Auto-generated method stub
				//System.out.println(packageList.getSelectedValue());
				if(packageList.getSelectedValue() != null) {

					//main.openClassListAndDetailOfPackage((PackageInfo) packageList.getSelectedValue());
					classListModel =main.getClassByPackage((PackageInfo) packageList.getSelectedValue());
					PackageInfo pi = (PackageInfo) packageModel.get(packageList.getSelectedIndex());
					
					//pi.cleanAll();
//					for(int i =0; i<classListModel.size();i++) {
//						ClassInfo ci = (ClassInfo) classListModel.get(i);
//						pi.constants.addAll(ci.constants);
//						pi.constantsSet.addAll(ci.constantsSet);
//						pi.specialChars.addAll(ci.specialChars);
//						pi.specialCharsSet.addAll(ci.specialCharsSet);
//						pi.udis.addAll(ci.udis);
//						pi.udisSet.addAll(ci.udisSet);
//						pi.keywords.addAll(ci.keywords);
//						pi.keywordsSet.addAll(ci.keywordsSet);
//					}
					updateClassList(classListModel);					
					updateInformation(pi);
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
					//main.openClassDetail((ClassInfo) classList.getSelectedValue());
					
					updateInformation((ClassInfo) classList.getSelectedValue());
					packageList.clearSelection();

				}


			}
		});
		
	}

	@Override
	void initSaveButton() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateClassList(DefaultListModel model) {
		// TODO Auto-generated method stub
		classList.setModel(classListModel);
		classList.updateUI();
	}

	@Override
	public void updatePackageList(DefaultListModel model) {
		// TODO Auto-generated method stub
		
	}
	public void updateInformation(ClassInfo ci) {
		StringBuilder sb = new StringBuilder();
		sb.append(ci.getString());


		editorPane.setText(sb.toString());
		editorPane.updateUI();
		
	}

	
}

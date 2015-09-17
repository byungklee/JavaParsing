import java.awt.BorderLayout;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class MainView extends JFrame {
	public static final int WIDTH = 800;
	public static final int HEIGHT = 600;
	MainController mainController;
	JPanel head;
	JPanel body;

	// JPanel footer;

	public MainView(MainController mainController) {
		this.mainController = mainController;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		initializeWindow();
		head = new HeadPanel(mainController);
		add(head, BorderLayout.PAGE_START);
		setResizable(false);

	}

	// public initializeButtonsInHead() {
	//
	// }

	public void initializeWindow() {
		setTitle("Assignment3");
		setSize(WIDTH, HEIGHT);
		setLocation(100, 100);
	}

	public void openParseView(DefaultListModel packageListModel) {
		System.out.println("Opening parseView");
		if (body == null) {
			System.out.println("Opening parseView");

			ParseView pView = new ParseView(mainController, packageListModel);
			body = pView;

			body.setSize(WIDTH, (int) (HEIGHT * 0.9));
			add(body, BorderLayout.CENTER);

			// } else if (!(body instanceof ParseView)) {
		} else {
			body.removeAll();
			remove(body);
			ParseView pView = new ParseView(mainController, packageListModel);
			body = pView;

			body.setSize(WIDTH, (int) (HEIGHT * 0.9));
			add(body, BorderLayout.CENTER);

		}

		body.updateUI();
		// body.repaint();

	}

	public void openClassAndDetailOfPackage(PackageInfo pInfo,
			DefaultListModel classModel) {
		((ParseView) body).updateClassList(classModel);
		((ParseView) body).updateInformation(pInfo);
		// openClassAndDetailOfPackage
		validate();
	}

	public void openClassDetail(ClassInfo cInfo) {
		((ParseView) body).updateInformation(cInfo);
		validate();
	}

	public void openDisplayDatabaseView(DefaultListModel packageModel) {
		System.out.println("Opening Database display view");
		if (body == null) {
			System.out.println("Opening Database display view");

			DatabaseDisplayView dbdView = new DatabaseDisplayView(
					mainController, packageModel);
			body = dbdView;

			body.setSize(WIDTH, (int) (HEIGHT * 0.9));
			add(body, BorderLayout.CENTER);

		} else if (!(body instanceof DatabaseDisplayView)) {
			body.removeAll();
			remove(body);
			DatabaseDisplayView dbdView = new DatabaseDisplayView(
					mainController, packageModel);
			body = dbdView;

			body.setSize(WIDTH, (int) (HEIGHT * 0.9));
			add(body, BorderLayout.CENTER);
		}
		// body.repaint();
		body.updateUI();
	}

}

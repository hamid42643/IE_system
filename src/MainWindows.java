
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JEditorPane;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.border.LineBorder;
import java.awt.Color;


public class MainWindows extends JFrame {

	private JPanel contentPane;
	private JTextField textField;
	private JEditorPane dtrpnTexthtml;
	private JScrollPane scrollPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					
					
					MainWindows frame = new MainWindows();
					frame.setVisible(true);
					
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	
	public MainWindows() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent arg0) {
				try {
					//onload perform these actions......
					
					main_UserInterface.loadDatabasejdbm();
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			@Override
			public void windowClosed(WindowEvent e) {
				try {
					main_UserInterface.closeDatabasejdbm();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1024, 600);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton btnSearch = new JButton("search");

		btnSearch.setBounds(408, 11, 89, 23);
		contentPane.add(btnSearch);
		
		textField = new JTextField();
		textField.setBounds(169, 12, 196, 20);
		contentPane.add(textField);
		textField.setColumns(10);
		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 60, 988, 462);
		contentPane.add(scrollPane);
		
		dtrpnTexthtml = new JEditorPane();
		scrollPane.setViewportView(dtrpnTexthtml);
		
		
		
		
		btnSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String q = textField.getText();
				try {
					
					String result = main_UserInterface.performSearch(q);
					dtrpnTexthtml.setText(result);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
	
	
	private static ArrayList<String> addColor(String str, String q){
		ArrayList<String> arr = new ArrayList<String>();
		
		String[] queryWords = q.split("\\W+");
		
		String[] strWords = str.split("\\W+");
		
		for(int i=0 ; i<strWords.length ; i++){
			for(int j=0 ; j<queryWords.length ; j++){
			
				if(strWords[i].equalsIgnoreCase(queryWords[j])){
					arr.add("<font color='red'>"+strWords[i]+"</font>");
				}else{
					arr.add(strWords[i]);
				}
			}
			
			
		}
		return arr;
	}
	
	
}



package transcribe;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Button;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Text;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.*;
import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.*;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.jface.dialogs.MessageDialog;

public class TranscribeGUI {

	protected Shell shlTranscriber;
	private Text txtFirstRow;
	/**
	 * @wbp.nonvisual location=98,81
	 */
	private final JFileChooser fileChooser = new JFileChooser();
	private Text txtLastRow;
	private Text txtFile;
	private Text txtColumn;
	private Text txtSheet;
	private VerifyListener verifyListener;
	private Text txtColumnWrite;

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			TranscribeGUI window = new TranscribeGUI();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shlTranscriber.open();
		shlTranscriber.layout();
		while (!shlTranscriber.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		
		// Checks input and allows only integers and empty string  
		verifyListener = new VerifyListener() {
			public void verifyText(VerifyEvent e) {

				String input = e.text;

				//System.out.println(input);
				boolean isAllowed = true;
				try {
					Integer.parseInt(input);
				} catch (NumberFormatException ex) {
					if (input != "")
						isAllowed = false;
				}

				if (!isAllowed)
					e.doit = false;
			}
		};

		fileChooser.addMouseListener(new java.awt.event.MouseAdapter() {
		});
		// TODO: add xlsx format, also in RequestHandler
		FileFilter filter = new FileNameExtensionFilter("Excel Workbooks", "xls");
		fileChooser.setFileFilter(filter);

		shlTranscriber = new Shell();
		shlTranscriber.setSize(290, 266);
		shlTranscriber.setText("Transcriber");
		shlTranscriber.setLayout(null);

		Button btnTranscribe = new Button(shlTranscriber, SWT.NONE);
		btnTranscribe.addMouseListener(new MouseAdapter() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.eclipse.swt.events.MouseAdapter#mouseDown(org.eclipse.swt.
			 * events.MouseEvent)
			 */

			@Override
			public void mouseUp(MouseEvent e) {
				if (e.button == 1) {

					List<String> list = new ArrayList<String>();
					int col, fRow, lRow, sheet;
					
					String file = txtFile.getText();
					//TODO: check and format fields, similar to JFormattedTextString
					col = Integer.parseInt(txtColumn.getText());
					fRow = Integer.parseInt(txtFirstRow.getText());
					lRow = Integer.parseInt(txtLastRow.getText());
					sheet = Integer.parseInt(txtSheet.getText());
				
					// TODO: possibly unnecessary builder
					RequestHandler rq = new HandlerBuilder()
							.excelFile(file)
							.col(col)
							.firstRow(fRow)
							.lastRow(lRow)
							.sheet(sheet)
							.buildRequestHandler();
						
					try {
						rq.findTranscription();
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						//MessageDialog.openError(shlTranscriber, "Error:\n", e1.getMessage());
						e1.printStackTrace();
					}
					// TODO: remove
//					List<String> list1 = new ArrayList<String>();
//					list.add("alternative");
//					list.add("fast");
//					list.add("yuri gagarin");
//					list.add("peer-to-peer");
//					try {
//						list1 = ServiceCaller.getTranscription(list);
//					} catch (Exception e1) { 
//						// TODO Auto-generated catch block
//						e1.printStackTrace();
//					}
//					
//					for(String el : list) {
//					    System.out.println(el);
//					}
//					
//				    System.out.println();
//				    
//					for(String el : list1) {
//					    System.out.println(el);
//					}
//					
//					try {
//						rq.writeListToFile(list1);
//					} catch (IOException e1) {
//						// TODO Auto-generated catch block
//						e1.printStackTrace();
//					}

				}
			}
		});

		Button btnFileOpen = new Button(shlTranscriber, SWT.NONE);
		btnFileOpen.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				if (e.button == 1) {
					int ret = fileChooser.showOpenDialog(null);

					//TODO: change to SWT
					if (ret == JFileChooser.APPROVE_OPTION) {
						txtFile.setText(fileChooser.getSelectedFile().getPath());
						txtFile.setSelection(txtFile.getText().length());
					}
				}
			}
		});

		btnFileOpen.setText("Open file...");
		btnFileOpen.setBounds(205, 65, 67, 23);

		btnTranscribe.setBounds(110, 206, 62, 23);
		btnTranscribe.setText("Transcribe");

		txtFirstRow = new Text(shlTranscriber, SWT.BORDER);
		txtFirstRow.setText("1");
		txtFirstRow.setBounds(113, 128, 49, 19);

		txtLastRow = new Text(shlTranscriber, SWT.BORDER);
		txtLastRow.setText("100");
		txtLastRow.setBounds(168, 128, 49, 19);

		Label lblFirstRow = new Label(shlTranscriber, SWT.NONE);
		lblFirstRow.setBounds(113, 109, 49, 13);
		lblFirstRow.setText("First row");

		Label lblLastRow = new Label(shlTranscriber, SWT.NONE);
		lblLastRow.setText("Last row");
		lblLastRow.setBounds(168, 109, 49, 13);

		txtFile = new Text(shlTranscriber, SWT.BORDER);
		txtFile.setBounds(37, 67, 162, 19);

		Label lblFile = new Label(shlTranscriber, SWT.NONE);
		lblFile.setText("File");
		lblFile.setBounds(37, 48, 49, 13);

		txtColumn = new Text(shlTranscriber, SWT.BORDER);
		txtColumn.setText("1");
		txtColumn.setBounds(10, 128, 49, 19);

		Label lblColumn = new Label(shlTranscriber, SWT.NONE);
		lblColumn.setText("Read from column");
		lblColumn.setBounds(10, 109, 86, 13);

		txtSheet = new Text(shlTranscriber, SWT.BORDER);
		txtSheet.setText("1");
		txtSheet.addVerifyListener(verifyListener);
		txtSheet.setBounds(223, 128, 49, 19);

		Label lblSheetNumber = new Label(shlTranscriber, SWT.NONE);
		lblSheetNumber.setText("Sheet");
		lblSheetNumber.setBounds(223, 109, 49, 13);

		txtColumnWrite = new Text(shlTranscriber, SWT.BORDER);
		txtColumnWrite.setText("2");
		txtColumnWrite.setBounds(10, 172, 49, 19);

		Label lblWriteToColumn = new Label(shlTranscriber, SWT.NONE);
		lblWriteToColumn.setText("Write to column");
		lblWriteToColumn.setBounds(10, 153, 75, 13);

	}
}

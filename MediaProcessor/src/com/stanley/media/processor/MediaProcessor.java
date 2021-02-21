package com.stanley.media.processor;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

public class MediaProcessor {

	JTextArea output;
	JScrollPane scrollPane;

	static JFrame frame = null;
	static Gson gson = new Gson();
	static Configuration config = null;

	private String currentInputpath = ".";
	private FileChoosePanel videoChooser = null;
	private FileChoosePanel audioChooser = null;
	private FileChoosePanel pictureChooser = null;
	private FolderChoosePanel inputFolderChooser = null;
	private FolderChoosePanel outputFolderChooser = null;

	private File curVideo = null;

	public JMenuBar createMenuBar() {
		JMenuBar menuBar;
		JMenu menu, submenu;
		JMenuItem menuItem;
		JRadioButtonMenuItem rbMenuItem;
		JCheckBoxMenuItem cbMenuItem;

		// Create the menu bar.
		menuBar = new JMenuBar();

		{
			// Build the first menu.
			menu = new JMenu("To Video");
			menu.setMnemonic(KeyEvent.VK_V);
			menu.getAccessibleContext().setAccessibleDescription("The only menu in this program that has menu items");
			menuBar.add(menu);
			// a group of JMenuItems
			{
				menuItem = new JMenuItem("Picture + Audio = Video", KeyEvent.VK_T);
				// menuItem.setMnemonic(KeyEvent.VK_T); //used constructor instead
				menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
				menuItem.getAccessibleContext().setAccessibleDescription("This doesn't really do anything");
				{
					menuItem.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {
							runCommand("./ffmpeg/bin/ffmpeg.exe -i " + config.getInput() + "/" + config.getVideo()
									+ "  -i " + config.getInput() + "/" + config.getPicture()
									+ " -filter_complex \"overlay=100:200\"  -vcodec libx264 -crf 22   "
									+ config.getOutput() + "/" + config.getVideo());
						}

					});
				}
				menu.add(menuItem);
			}
			{
				// ImageIcon icon = createImageIcon("images/middle.gif");
				menuItem = new JMenuItem("Convert to MP4");
				menuItem.setMnemonic(KeyEvent.VK_B);
				menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2, ActionEvent.ALT_MASK));
				menu.add(menuItem);
				{
					menuItem.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {
							runCommand("./ffmpeg/bin/ffmpeg.exe   -i " + config.getInput() + "/" + config.getVideo()
									+ " " + config.getOutput() + "/" + config.getVideo() + ".mp4");
							MediaProcessor.this.curVideo = new File(
									config.getOutput() + "/" + config.getVideo() + ".mp4");
						}

					});
				}
			}
			{
				menuItem = new JMenuItem("Upload Video", KeyEvent.VK_U);
				// menuItem.setMnemonic(KeyEvent.VK_T); //used constructor instead
				menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_3, ActionEvent.ALT_MASK));
				menuItem.getAccessibleContext().setAccessibleDescription("Uploads the Video");
				{
					menuItem.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {

							// winscp "User:pw@Host" /command "put C:\File.txt /Home/" "exit"
							runCommand("winscp \"User:ubuntu@3.141.216.183\" /command \"put "
									+ MediaProcessor.this.curVideo.getPath() + " /home/ubuntu/\" \"exit\"");

							insertPage("newMP4", "/home/ubuntu/" + MediaProcessor.this.curVideo.getName());
						}

					});
				}
				menu.add(menuItem);
			}

		}

		{
			// Build second menu in the menu bar.
			menu = new JMenu("About");
			menu.setMnemonic(KeyEvent.VK_N);
			menu.getAccessibleContext().setAccessibleDescription("Media Processor");
			menuBar.add(menu);
		}

		return menuBar;
	}

	protected void runCommand(String command) {
		Thread one = new Thread() {
			public void run() {
				try {
					System.out.println("runCommand ~ " + command);
					Runtime rt = Runtime.getRuntime();
					Process pr = rt.exec(command);
					int exitVal = pr.waitFor();
					System.out.println("finish");
				} catch (Exception v) {
					System.out.println(v);
				}
			}
		};
		one.start();
	}

	public Container createContentPane() {
		// Create the content-pane-to-be.
		JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.setOpaque(true);
		{
			JPanel buttons = new JPanel(new BorderLayout());

			BoxLayout boxlayout = new BoxLayout(buttons, BoxLayout.Y_AXIS);

			buttons.setLayout(boxlayout);

			videoChooser = new FileChoosePanel("Video File", currentInputpath) {
				void updateConfig(String value) {
					config.setVideo(value);
					output.setText(gson.toJson(config));

				}
			};
			audioChooser = new FileChoosePanel("Audio File", currentInputpath) {
				void updateConfig(String value) {
					config.setAudio(value);
					output.setText(gson.toJson(config));
				}
			};
			pictureChooser = new FileChoosePanel("Picture File", currentInputpath) {
				void updateConfig(String value) {
					config.setPicture(value);
					output.setText(gson.toJson(config));
				}
			};
			inputFolderChooser = new FolderChoosePanel("Input File", currentInputpath) {
				void updateConfig(String value) {
					config.setInput(value);
					output.setText(gson.toJson(config));
				}
			};
			outputFolderChooser = new FolderChoosePanel("Output File", currentInputpath) {
				void updateConfig(String value) {
					config.setOutput(value);
					output.setText(gson.toJson(config));
				}
			};

			buttons.add(videoChooser);
			buttons.add(audioChooser);
			buttons.add(pictureChooser);
			buttons.add(inputFolderChooser);
			buttons.add(outputFolderChooser);

			contentPane.add(buttons, BorderLayout.CENTER);
		}

		// Create a scrolled text area.
		output = new JTextArea(1, 30);
		output.setEditable(false);

		// Add the text area to the content pane.
		contentPane.add(output, BorderLayout.SOUTH);

		return contentPane;
	}

	/** Returns an ImageIcon, or null if the path was invalid. */
	protected static ImageIcon createImageIcon(String path) {
		java.net.URL imgURL = MediaProcessor.class.getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

	/**
	 * Create the GUI and show it. For thread safety, this method should be invoked
	 * from the event-dispatching thread.
	 */
	private static void createAndShowGUI() {
		// Create and set up the window.
		frame = new JFrame("Media Processor");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Create and set up the content pane.
		MediaProcessor demo = new MediaProcessor();
		frame.setJMenuBar(demo.createMenuBar());
		frame.setContentPane(demo.createContentPane());

		// Display the window.
		frame.setSize(450, 260);
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		JsonReader reader;
		try {
			reader = new JsonReader(new FileReader("config.json"));
			config = gson.fromJson(reader, Configuration.class);
			System.out.println(gson.toJson(config));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

	//
	private abstract class FileChoosePanel extends JPanel {
		abstract void updateConfig(String value);

		JButton button = null;
		JLabel label = null;

		ActionListener actionListener = null;
		JFileChooser fileChooser = null;
		File file = null;

		FileChoosePanel(String text, String curFolder) {
			this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

			button = new JButton(text);
			button.setSize(200, 50);
			button.setPreferredSize(new Dimension(200, 50));
			this.add(button);

			label = new JLabel("");
			label.setSize(300, 50);
			label.setPreferredSize(new Dimension(300, 50));
			this.add(label);

			fileChooser = new JFileChooser(curFolder);
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

			actionListener = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					int option = fileChooser.showOpenDialog(frame);
					if (option == JFileChooser.APPROVE_OPTION) {
						file = fileChooser.getSelectedFile();
						label.setText(file.getName());
						updateConfig(file.getName());
					}
				}
			};

			button.addActionListener(actionListener);
		}
	}

	//
	private abstract class FolderChoosePanel extends JPanel {
		abstract void updateConfig(String value);

		JButton button = null;
		JLabel label = null;

		ActionListener actionListener = null;
		JFileChooser fileChooser = null;
		File file = null;

		FolderChoosePanel(String text, String curFolder) {
			this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			button = new JButton();
			button.setSize(200, 50);
			this.add(button);
			label = new JLabel();
			label.setSize(300, 50);
			this.add(label);
			button.setText(text);
			fileChooser = new JFileChooser(curFolder);
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

			actionListener = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					int option = fileChooser.showOpenDialog(frame);
					if (option == JFileChooser.APPROVE_OPTION) {
						file = fileChooser.getSelectedFile();
						label.setText(file.getPath());
						updateConfig(file.getPath());
					}
				}
			};

			button.addActionListener(actionListener);
		}
	}

	
	
	private void insertPage(String title, String videopath) {
		MySQLUtil.init("jdbc:mysql://3.141.216.183:3306/wordpress?useUnicode=true&characterEncoding=UTF-8", "wordpress", "wordpress");
		MySQLUpdater.catchMax();
		//insert(Str, String title, String content,)
		MySQLUpdater.insert("temp", "<iframe width=\"420\" height=\"315\"\r\n"
				+ "src=\"https://www.youtube.com/embed/tgbNymZ7vqY\">\r\n"
				+ "</iframe>");
	}
	
	

}





















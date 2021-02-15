package com.stanley.media.processor;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
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
							+  " " + config.getOutput() + "/" + config.getVideo()+".webm");
						}

					});
				}
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
		System.out.println("runCommand ~ " + command);
		Runtime rt = Runtime.getRuntime();
		try {
			Process pr = rt.exec(command);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("finish");
	}

	private String currentInputpath = ".";
	private FileChooseButton videoChooser = null;
	private FileChooseButton audioChooser = null;
	private FileChooseButton pictureChooser = null;
	private FolderChooseButton inputFolderChooser = null;
	private FolderChooseButton outputFolderChooser = null;

	public Container createContentPane() {
		// Create the content-pane-to-be.
		JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.setOpaque(true);
		{
			JPanel buttons = new JPanel(new BorderLayout());

			BoxLayout boxlayout = new BoxLayout(buttons, BoxLayout.Y_AXIS);

			buttons.setLayout(boxlayout);

			videoChooser = new FileChooseButton("Video File", currentInputpath) {
				void updateConfig(String value) {
					config.setVideo(value);
					output.setText(gson.toJson(config));

				}
			};
			audioChooser = new FileChooseButton("Audio File", currentInputpath) {
				void updateConfig(String value) {
					config.setAudio(value);
					output.setText(gson.toJson(config));
				}
			};
			pictureChooser = new FileChooseButton("Picture File", currentInputpath) {
				void updateConfig(String value) {
					config.setPicture(value);
					output.setText(gson.toJson(config));
				}
			};
			inputFolderChooser = new FolderChooseButton("Input File", currentInputpath) {
				void updateConfig(String value) {
					config.setInput(value);
					output.setText(gson.toJson(config));
				}
			};
			outputFolderChooser = new FolderChooseButton("Output File", currentInputpath) {
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
	private abstract class FileChooseButton extends JButton {
		abstract void updateConfig(String value);

		ActionListener actionListener = null;
		JFileChooser fileChooser = null;
		File file = null;

		FileChooseButton(String text, String curFolder) {
			this.setText(text);
			this.fileChooser = new JFileChooser(curFolder);
			this.fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

			actionListener = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					int option = fileChooser.showOpenDialog(frame);
					if (option == JFileChooser.APPROVE_OPTION) {
						file = fileChooser.getSelectedFile();
						setText(getText() + "...." + file.getName());
						updateConfig(file.getName());
					}
				}
			};

			this.addActionListener(actionListener);
		}
	}

	//
	private abstract class FolderChooseButton extends JButton {
		abstract void updateConfig(String value);

		ActionListener actionListener = null;
		JFileChooser fileChooser = null;
		File file = null;

		FolderChooseButton(String text, String curFolder) {
			this.setText(text);
			this.fileChooser = new JFileChooser(curFolder);
			this.fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

			actionListener = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					int option = fileChooser.showOpenDialog(frame);
					if (option == JFileChooser.APPROVE_OPTION) {
						file = fileChooser.getSelectedFile();
						setText(getText() + "...." + file.getPath());
						updateConfig(file.getPath());
					}
				}
			};

			this.addActionListener(actionListener);
		}
	}

}

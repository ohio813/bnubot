/**
 * This file is distributed under the GPL 
 * $Id$
 */

/**
 * This file is distributed under the GPL 
 * $Id$
 */

package net.bnubot.bot.gui;

import java.awt.*;
import java.awt.event.*;
import java.util.Date;

import javax.swing.*;

import net.bnubot.bot.EventHandler;
import net.bnubot.bot.database.Database;
import net.bnubot.bot.gui.ColorScheme.ColorScheme;
import net.bnubot.bot.gui.components.*;
import net.bnubot.bot.gui.database.DatabaseRankEditor;
import net.bnubot.bot.gui.icons.IconsDotBniReader;
import net.bnubot.core.BNetUser;
import net.bnubot.core.Connection;
import net.bnubot.core.StatString;
import net.bnubot.core.clan.ClanMember;
import net.bnubot.core.friend.FriendEntry;

public class GuiEventHandler implements EventHandler {
	private JFrame frame = null;
	private Connection c = null;
	private TextWindow2 mainTextArea = null;
	private JTextArea chatTextArea = null;
	private JTextArea channelTextArea = null;
	private UserList userList = null;
	private FriendList friendList = null;
	private ClanList clanList = null;
	private RealmWindow w = null;
	
	public void initialize(Connection c) {
		ColorScheme cs = ColorScheme.createColorScheme(c.getConnectionSettings().colorScheme);
		
		if(c != null) {
			this.c = c;
			initializeGui(c.toString(), cs);
		} else {
			initializeGui("BNU`Bot", cs);
		}
	}

	private void initializeGui(String title, ColorScheme cs) {
		//Create and set up the window
		frame = new JFrame(title);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//Create the menu bar.
		JMenuBar menuBar = new JMenuBar();
		menuBar.setOpaque(true);
		menuBar.setPreferredSize(new Dimension(200, 20));
		{
			JMenu menu;
			JMenuItem menuItem;

			menu = new JMenu("File");
			{	
				menuItem = new JMenuItem("Connect");
				menuItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if(!c.isConnected())
							c.setConnected(true);
					} });
				menu.add(menuItem);
				
				menuItem = new JMenuItem("Disconnect");
				menuItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if(c.isConnected())
							c.setConnected(false);
					} });
				menu.add(menuItem);
				
				menu.addSeparator();
				
				menuItem = new JMenuItem("Settings");
				menuItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						new ConfigurationFrame(c.getConnectionSettings()).setVisible(true);
					} });
				menu.add(menuItem);
				
				menu.addSeparator();
				
				menuItem = new JMenuItem("Exit");
				menuItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						System.exit(0);
					} });
				menu.add(menuItem);
			}
			menuBar.add(menu);	
			
			menu = new JMenu("Realm");
			{
				menuItem = new JMenuItem("Show Realms Window");
				menuItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						try {
							c.sendQueryRealms();
						} catch (Exception e) {
							e.printStackTrace();
							System.exit(1);
						}
					} });
				menu.add(menuItem);
			}
			menuBar.add(menu);
			
			menu = new JMenu("Clan");
			{
				menuItem = new JMenuItem("Edit MOTD");
				menuItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						try {
							c.sendClanMOTD(new ClanMOTDEditor(c));
						} catch(Exception e) {
							e.printStackTrace();
							System.exit(1);
						}
					} });
				menu.add(menuItem);
			}
			menuBar.add(menu);
			
			menu = new JMenu("Database");
			{
				menuItem = new JMenuItem("Rank editor");
				menuItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						Database d = Database.getInstance();
						if(d != null)
							new DatabaseRankEditor(d);
					} });
				menu.add(menuItem);
			}
			menuBar.add(menu);
			
			menu = new JMenu("Debug");
			{
				menuItem = new JMenuItem("Show Icons");
				menuItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						IconsDotBniReader.showWindow();
					} });
				menu.add(menuItem);
			}
			menuBar.add(menu);
			
			menu = new JMenu("Help");
			{
				menuItem = new JMenuItem("Complain about scrollbars");
				menu.add(menuItem);
				
				menuItem = new JMenuItem("About");
				menuItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						new AboutWindow();
					} });
				menu.add(menuItem);
			}
			menuBar.add(menu);
		}
		frame.setJMenuBar(menuBar);
		
		//Create a LayoutManager to organize the frame
		frame.setLayout(new BotLayoutManager());
		
		//Main text area
		mainTextArea = new TextWindow2(cs);
		//Send chat textbox
		chatTextArea = new JTextArea();
		chatTextArea.setBackground(cs.getBackgroundColor());
		chatTextArea.setForeground(Color.LIGHT_GRAY);
		chatTextArea.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {}
			public void keyReleased(KeyEvent e) {}
			public void keyTyped(KeyEvent e) {
				if(e.getKeyChar() == '\n') {
					String text[] = chatTextArea.getText().split("\n");
					for(int i = 0; i < text.length; i++) {
						if(text[i].trim().length() > 0)
							c.sendChat(text[i]);
					}
					chatTextArea.setText(null);
				}
			}
		});
		//Channel text box (above userlist)
		channelTextArea = new JTextArea();
		channelTextArea.setAlignmentX(SwingConstants.CENTER);
		channelTextArea.setAlignmentY(SwingConstants.CENTER);
		channelTextArea.setBackground(cs.getBackgroundColor());
		channelTextArea.setForeground(Color.LIGHT_GRAY);
		
		IconsDotBniReader.initialize(c.getConnectionSettings());
		
		//The userlist
		userList = new UserList(cs, c, this);
		//Friends list
		friendList = new FriendList(cs);
		//Clan list
		clanList = new ClanList(cs);
		
		JTabbedPane allLists = new JTabbedPane();
		allLists.addTab("Channel", userList);
		allLists.addTab("Friends", friendList);
		allLists.addTab("Clan", clanList);
		
		//Add them to the frame
		frame.add(mainTextArea);
		frame.add(chatTextArea);
		frame.add(channelTextArea);
		frame.add(allLists);
		/*JSplitPane leftPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, mainTextScroll, chatTextArea);
		leftPane.setResizeWeight(1);
		JSplitPane rightPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, channelTextArea, userList);
		JSplitPane mainPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPane, rightPane);
		mainPane.setResizeWeight(1);
		frame.add(mainPane);*/
		
		//Display the window
		frame.pack();
		frame.setVisible(true);
	}

	public void channelJoin(BNetUser user, StatString statstr) {
		userList.showUser(user, statstr);
		mainTextArea.channelInfo(user + " has joined" + statstr.toString() + ".");
	}

	public void channelLeave(BNetUser user) {
		userList.removeUser(user);
		mainTextArea.channelInfo(user + " has left.");
	}

	public void channelUser(BNetUser user, StatString statstr) {
		mainTextArea.channelInfo(user + statstr.toString() + ".");
		userList.showUser(user, statstr);
	}

	public void joinedChannel(String channel) {
		userList.clear();
		mainTextArea.channelInfo("Joining channel " + channel + ".");
		channelTextArea.setText(channel);
		frame.setTitle(c.toString());
	}

	public void recieveChat(BNetUser user, String text) {
		mainTextArea.userChat(user, text);
	}

	public void recieveEmote(BNetUser user, String text) {
		mainTextArea.userEmote(user, text);
	}

	private static long lastInfoRecieved = 0;
	private static String lastInfo = null;
	public void recieveInfo(String text) {
		long now = new Date().getTime();
		// Do not allow duplicate info strings unless there's a 50ms delay
		if((now - lastInfoRecieved < 50)
		&& text.equals(lastInfo)) {
			lastInfoRecieved = now;
			return;
		}
		
		lastInfo = text;
		lastInfoRecieved = now;
		mainTextArea.recieveInfo(text);
	}

	public void recieveError(String text) {
		mainTextArea.recieveError(text);
	}

	public void whisperRecieved(BNetUser user, String text) {
		mainTextArea.whisperRecieved(user, text);
	}

	public void whisperSent(BNetUser user, String text) {
		mainTextArea.whisperSent(user, text);
	}

	public void bnetConnected() {
		userList.clear();
		channelTextArea.setText(null);
	}

	public void bnetDisconnected() {
		userList.clear();
		channelTextArea.setText(null);
	}

	public void titleChanged() {
		frame.setTitle(c.toString());
	}

	public void friendsList(FriendEntry[] entries) {
		friendList.showFriends(entries);
	}
	
	public void friendsUpdate(FriendEntry friend) {
		friendList.update(friend);
	}
	
	public void friendsAdd(FriendEntry friend) {
		friendList.add(friend);
	}
	
	public void friendsPosition(byte oldPosition, byte newPosition) {
		friendList.position(oldPosition, newPosition);
	}
	
	public void friendsRemove(byte entry) {
		friendList.remove(entry);
	}

	public void clanMOTD(Object cookie, String text) {
		if(cookie instanceof ClanMOTDEditor) {
			ClanMOTDEditor motd = (ClanMOTDEditor)cookie;
			motd.setMOTD(text);
			motd.setVisible(true);
		}
	}

	public void clanMemberList(ClanMember[] members) {
		clanList.showMembers(members);
	}
	
	public void clanMemberRemoved(String username) {
		clanList.remove(username);
	}
	
	public void clanMemberStatusChange(ClanMember member) {
		clanList.statusChange(member);
	}
	
	public void clanMemberRankChange(byte oldRank, byte newRank, String user) {
		clanList.rankChange(oldRank, newRank, user);
	}
	
	public void queryRealms2(String[] realms) {
		if(w == null)
			w = new RealmWindow(realms);
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				c.addEventHandler(w);
				w.setVisible(true);
			} });
	}

	public void logonRealmEx(int[] MCPChunk1, int ip, int port, int[] MCPChunk2, String uniqueName) {}

	public void setChatText(String chatText) {
		chatTextArea.setText(chatText);
		chatTextArea.setSelectionStart(chatText.length());
		chatTextArea.requestFocus();
	}

	public void parseCommand(BNetUser user, String command, String param, boolean wasWhispered) {
		mainTextArea.recieveInfo(String.format("parseCommand(\"%1$s\", \"%2$s\", \"%3$s\")", user.getShortLogonName(), command, param));
	}
}
/**
 * This file is distributed under the GPL 
 * $Id$
 */

package net.bnubot.bot.gui.components;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.*;

import net.bnubot.bot.gui.GuiEventHandler;
import net.bnubot.bot.gui.ColorScheme.ColorScheme;
import net.bnubot.bot.gui.icons.BNetIcon;
import net.bnubot.bot.gui.icons.IconsDotBniReader;
import net.bnubot.core.BNetUser;
import net.bnubot.core.Connection;
import net.bnubot.core.StatString;
import net.bnubot.core.bncs.ProductIDs;

@SuppressWarnings("serial")
public class UserList extends JPanel {
	private class UserInfo {
		BNetUser user;
		int lastFlags;
		int priority;
		StatString statstr;
		JLabel label;
		JPopupMenu menu;
	}
	
	private Hashtable<BNetUser, UserInfo> users = null;
	private Box b = null;
	private ColorScheme cs = null;
	private Connection c = null;
	private GuiEventHandler geh = null;
	
	/**
	 * Get UserInfo from JLabel
	 * @param lbl The JLabel to look fo
	 * @return The UserInfo, or null if not found
	 */
	private UserInfo getUI(JLabel lbl) {
		Enumeration<UserInfo> en = users.elements();
		while(en.hasMoreElements()) {
			UserInfo ui = en.nextElement();
			if(ui.label == lbl)
				return ui;
		}
		return null;
	}
	
	private UserInfo getUI(BNetUser user) {
		UserInfo ui = users.get(user);
		if(ui != null)
			return ui;
		
		Enumeration<UserInfo> en = users.elements();
		while(en.hasMoreElements()) {
			ui = en.nextElement();
			if(ui.user.equals(user))
				return ui;
		}
		
		return null;
	}
	
	public UserList(ColorScheme cs, Connection c, GuiEventHandler geh) {
		super(new FlowLayout(FlowLayout.LEFT));
		this.users = new Hashtable<BNetUser, UserInfo>();
		this.cs = cs;
		this.c = c;
		this.geh = geh;
		setBackground(cs.getBackgroundColor());
		b = new Box(BoxLayout.Y_AXIS);
		add(b);
	}
	
	public void clear() {
		b.removeAll();
		users.clear();
		validate();
	}
	
	public int count() {
		return users.size();
	}

	private static final int PRIORITY_BLIZZARD_REP = 5;
	private static final int PRIORITY_BNET_REP = 4;
	private static final int PRIORITY_OPERATOR= 3;
	private static final int PRIORITY_SPEAKER = 2;
	private static final int PRIORITY_BIZZARD_GUEST = 1;
	private static final int PRIORITY_NORMAL = 0;
	private int getPrioByFlags(int flags) {
		if((flags & 0x01) != 0)	return PRIORITY_BLIZZARD_REP;
		if((flags & 0x08) != 0)	return PRIORITY_BNET_REP;
		if((flags & 0x02) != 0)	return PRIORITY_OPERATOR;
		if((flags & 0x04) != 0)	return PRIORITY_SPEAKER;
		if((flags & 0x40) != 0)	return PRIORITY_BIZZARD_GUEST;
		return PRIORITY_NORMAL;
	}
	
	private int getInsertPosition(int priority) {
		for(int i = 0; i < b.getComponentCount(); i++) {
			JLabel lbl = (JLabel)b.getComponent(i);
			UserInfo ui = getUI(lbl);
			int pCurrent = getPrioByFlags(ui.user.getFlags());
			
			if(priority > pCurrent)
				return i;
		}
		return b.getComponentCount();
	}
	
	private UserInfo getUserInfo(ActionEvent arg0) {
		JMenuItem jmi = (JMenuItem) arg0.getSource();
		JPopupMenu jp = (JPopupMenu) jmi.getParent();
		
		Enumeration<UserInfo> en = users.elements();
		while(en.hasMoreElements()) {
			UserInfo ui = en.nextElement();
			if(ui.menu == jp) {
				//Found them
				return ui;
			}
		}
		return null;
	}
	
	public void showUser(BNetUser user, StatString statstr) {
		UserInfo ui = getUI(user);
		if(ui == null) {
			ui = new UserInfo();
			ui.user = user;
			ui.statstr = statstr;
			ui.priority = getPrioByFlags(user.getFlags());
			
			ui.menu = new JPopupMenu();
			ui.menu.add(new JLabel(user.getShortPrettyName() + ": " + statstr.toString()));
			JMenuItem menuItem = new JMenuItem("Whisper");
			menuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					UserInfo ui = getUserInfo(arg0);
					if(ui != null)
						geh.setChatText("/w " + ui.user.getShortLogonName() + " ");
				}});
			ui.menu.add(menuItem);
			menuItem = new JMenuItem("Whois");
			menuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					UserInfo ui = getUserInfo(arg0);
					if(ui != null)
						c.sendChat("/whois " + ui.user.getShortLogonName());
				}});
			ui.menu.add(menuItem);
			menuItem = new JMenuItem("Profile");
			menuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					UserInfo ui = getUserInfo(arg0);
					if(ui != null)
						try { c.sendProfile(ui.user); } catch(Exception e) { e.printStackTrace(); }
				}});
			ui.menu.add(menuItem);
			ui.menu.add(Box.createHorizontalGlue());
		}
		if(ui.label == null) {
			ui.label = new JLabel(user.getFullLogonName());
			ui.label.setForeground(cs.getUserNameListColor(user.getFlags()));
			b.add(ui.label, getInsertPosition(ui.priority));
			
			ui.label.addMouseListener(new MouseListener() {
				public void mouseClicked(MouseEvent arg0) {
					JLabel jl = (JLabel) arg0.getSource();
					Enumeration<UserInfo> en = users.elements();
					while(en.hasMoreElements()) {
						UserInfo ui = en.nextElement();
						if(ui.label == jl) {
							switch(arg0.getButton()) {
							case MouseEvent.BUTTON1:
								System.out.println("Left clicked on " + ui.label.getText());
								break;
							case MouseEvent.BUTTON2:
								try { c.sendProfile(ui.user); } catch(Exception e) { e.printStackTrace(); }
								break;
							case MouseEvent.BUTTON3:
								ui.menu.show(arg0.getComponent(), arg0.getX(), arg0.getY());
								break;
							}
							break;
						}
					}
				}
				public void mouseEntered(MouseEvent arg0) {}
				public void mouseExited(MouseEvent arg0) {}
				public void mousePressed(MouseEvent arg0) {}
				public void mouseReleased(MouseEvent arg0) {}
			});
		}
		
		//Check if the user's flags updated
		if(ui.lastFlags != ui.user.getFlags()) {
			//They did; order the list appropriately
			ui.lastFlags = ui.user.getFlags();

			int newPriority = getPrioByFlags(ui.lastFlags);
			if(ui.priority != newPriority) {
				ui.priority = newPriority;
				b.remove(ui.label);
				b.add(ui.label, getInsertPosition(newPriority));
			}
			
			ui.label.setForeground(cs.getUserNameListColor(ui.lastFlags));
		}
		ui.statstr = statstr;
				
		Icon icon = null;
		int product = ui.statstr.getProduct();
		int specialIcon = ui.statstr.getIcon();
		if(specialIcon == product)
			specialIcon = 0;
		
		BNetIcon[] icons = IconsDotBniReader.getIcons();
		boolean keepThisIcon = false;
		for(int i = 0; i < icons.length; i++) {
			//Look for 
			if(icons[i].useFor(ui.user.getFlags(), specialIcon)) {
				keepThisIcon = true;
				icon = icons[i].getIcon();
				break;
			}
			if(icons[i].useFor(ui.user.getFlags(), product)) {
				icon = icons[i].getIcon();
			}
		}
		
		if(!keepThisIcon) {
			if(true) { //(product == c.getProductID()) {
				switch(product) {
				case ProductIDs.PRODUCT_STAR:
				case ProductIDs.PRODUCT_SEXP:
				case ProductIDs.PRODUCT_W2BN:
					icons = IconsDotBniReader.getLegacyIcons();
					break;
				case ProductIDs.PRODUCT_WAR3:
					icons = IconsDotBniReader.getIconsWAR3();
					break;
				case ProductIDs.PRODUCT_W3XP:
					icons = IconsDotBniReader.getIconsW3XP();
					break;
				default:
					icons = null;
					break;
				}
				
				if(icons != null) {
					switch(product) {
					case ProductIDs.PRODUCT_STAR:
					case ProductIDs.PRODUCT_SEXP:
						int w = Math.max(Math.min(statstr.getWins(), 10), 0);
						icon = icons[w].getIcon();
						int r = statstr.getLadderRank();
						if(r > 0) {
							if(r == 1)
								icon = icons[IconsDotBniReader.LEGACY_LADDERNUM1].getIcon();
							else
								icon = icons[IconsDotBniReader.LEGACY_LADDER].getIcon();
						}
						break;

					case ProductIDs.PRODUCT_W2BN:
						w = Math.max(Math.min(statstr.getWins(), 10), 0);
						icon = icons[IconsDotBniReader.LEGACY_W2BNWIN + w].getIcon();
						
						r = statstr.getLadderRank();
						if(r > 0) {
							if(r == 1)
								icon = icons[IconsDotBniReader.LEGACY_LADDERNUM1].getIcon();
							else
								icon = icons[IconsDotBniReader.LEGACY_LADDER].getIcon();
						}
						break;
						
					default:
						if(icons != null)
							for(int i = 0; i < icons.length; i++) {
								if(icons[i].useFor(ui.user.getFlags(), specialIcon)) {
									keepThisIcon = true;
									icon = icons[i].getIcon();
									break;
								}
							}
						break;
					}
				}
			}
		}
		
		if(icon != null)
			ui.label.setIcon(icon);
		
		users.put(user, ui);
		validate();
	}
	
	public void removeUser(BNetUser user) {
		UserInfo ui = getUI(user);
		
		if(ui != null) {
			b.remove(ui.label);
			ui.label = null;
			users.remove(user);
			validate();
		} else {
			System.err.println("Attempted to remove a user that was not in the UserList: " + user);
		}
	}

}

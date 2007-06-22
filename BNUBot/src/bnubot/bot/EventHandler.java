package bnubot.bot;

import bnubot.core.BNetUser;
import bnubot.core.Connection;
import bnubot.core.StatString;
import bnubot.core.clan.ClanMember;
import bnubot.core.friend.FriendEntry;

public interface EventHandler {
	//Initialization
	public void initialize(Connection c);
	
	//Connection
	public void bnetConnected();
	public void bnetDisconnected();
	public void titleChanged();
	
	//Workarounds
	public void parseCommand(BNetUser user, String command, String param, boolean wasWhispered);

	//Channel events
	public void joinedChannel(String channel);
	public void channelUser(BNetUser user, StatString statstr);
	public void channelJoin(BNetUser user, StatString statstr);
	public void channelLeave(BNetUser user);
	public void recieveChat(BNetUser user, String text);
	public void recieveEmote(BNetUser user, String text);
	public void recieveInfo(String text);
	public void recieveError(String text);
	public void whisperSent(BNetUser user, String text);
	public void whisperRecieved(BNetUser user, String text);
	
	//Realms
	public void queryRealms2(String[] realms);
	public void logonRealmEx(int[] MCPChunk1, int ip, int port, int[] MCPChunk2, String uniqueName);
	
	//Friends
	public void friendsList(FriendEntry[] entries);
	public void friendsUpdate(byte entry, byte location, byte status, int product, String locationName);
	
	//Clan
	public void clanMemberList(ClanMember[] members);
	public void clanMemberRankChange(byte oldRank, byte newRank, String user);
	public void clanMOTD(Object cookie, String text);
}

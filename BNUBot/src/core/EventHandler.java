package core;

public interface EventHandler {
	public void initialize(Connection c);
	
	public void joinedChannel(String channel);
	public void channelUser(String user, int flags, int ping, String statstr);
	public void channelJoin(String user, int flags, int ping, String statstr);
	public void channelLeave(String user, int flags, int ping, String statstr);
	public void recieveChat(String user, String text);
	public void recieveEmote(String user, String text);
}

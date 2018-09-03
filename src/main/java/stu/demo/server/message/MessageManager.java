package stu.demo.server.message;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import edu.demo.common.entity.User;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class MessageManager {
	private static EventLoopGroup bossGroup = new NioEventLoopGroup();
	private static EventLoopGroup workerGroup = new NioEventLoopGroup();

	private static final ConcurrentHashMap<User, ChannelHandlerContext> USER_MAP = new ConcurrentHashMap<>();

	static ChannelHandlerContext getChannelByUser(User user) {
		return USER_MAP.get(user);
	}

	static void userIntoGroup(User user, ChannelHandlerContext channel) {

		System.err.println("用户(" + user + ") 加入在线用户组");
		USER_MAP.put(user, channel);
	}

	static void userOffGroup(ChannelHandlerContext channel) {

		Iterator<Entry<User, ChannelHandlerContext>> it = USER_MAP.entrySet().iterator();
		while (it.hasNext()) {
			Entry<User, ChannelHandlerContext> entry = it.next();
			if (entry.getValue() == channel) {
				System.err.println("用户(" + entry.getKey() + ") 移除在线用户组");
				it.remove();
				break;
			}
		}

	}

	static Set<User> allUserFromGroup() {

		Set<User> result = USER_MAP.keySet();

		return result;
	}

	static void sendMessageToAll(String message) {
		for (Entry<User, ChannelHandlerContext> entry : USER_MAP.entrySet()) {
			ChannelHandlerContext context = entry.getValue();
			context.writeAndFlush(message + "\r\n");
		}
	}

	public static void startup() {
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup);
			b.channel(NioServerSocketChannel.class);
			b.childHandler(new StringServerInitializer());

			// 服务器绑定端口监听
			ChannelFuture f = b.bind(8888).sync();
			// 监听服务器关闭监听
			f.channel().closeFuture().sync();

			// 可以简写为
			/* b.bind(portNumber).sync().channel().closeFuture().sync(); */
		} catch (InterruptedException e) {
			e.printStackTrace();
			shutdown();
		}
	}

	public static void shutdown() {
		bossGroup.shutdownGracefully();
		workerGroup.shutdownGracefully();
	}
}

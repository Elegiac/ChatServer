package stu.demo.server.message;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import edu.demo.common.entity.User;

public class MessageManager {
	private static EventLoopGroup bossGroup = new NioEventLoopGroup();
	private static EventLoopGroup workerGroup = new NioEventLoopGroup();

	private static final Map<User, ChannelHandlerContext> USER_MAP = new HashMap<>();

	static ReadWriteLock lock = new ReentrantReadWriteLock(); 
	
	
	static void userIntoGroup(User user, ChannelHandlerContext channel) {
		lock.writeLock().lock();
		
		System.out.println("add user(" + user + ") into group");
		USER_MAP.put(user, channel);
		
		lock.writeLock().unlock();
	}

	static void userOffGroup(ChannelHandlerContext channel) {
		lock.writeLock().lock();
		
		Iterator<Entry<User, ChannelHandlerContext>> it = USER_MAP.entrySet()
				.iterator();
		while (it.hasNext()) {
			Entry<User, ChannelHandlerContext> entry = it.next();
			if (entry.getValue() == channel) {
				System.out.println("remove user(" + entry.getKey()
						+ ") from group");
				it.remove();
				break;
			}
		}
		
		lock.writeLock().unlock();
	}
	
	static Set<User> allUserFromGroup(User user, ChannelHandlerContext channel) {
		lock.readLock().lock();
		
		Set<User> users = USER_MAP.keySet();
		
		lock.readLock().unlock();
		
		return users;
	}
	

	public static void startup() {
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup);
			b.channel(NioServerSocketChannel.class);
			b.childHandler(new StringServerInitializer());

			// 服务器绑定端口监听
			ChannelFuture f = b.bind(8878).sync();
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

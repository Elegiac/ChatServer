package stu.demo.server.message;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class Connection {
	static EventLoopGroup bossGroup = new NioEventLoopGroup();
	static EventLoopGroup workerGroup = new NioEventLoopGroup();

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

package stu.demo.server.message;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Map;

import edu.demo.common.entity.User;
import edu.demo.common.entity.action.Login;
import edu.demo.common.utils.JsonUtil;

public class StringServerHandler extends SimpleChannelInboundHandler<String> {
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String msg)
			throws Exception {
		// 收到消息直接打印输出
		System.out.println(ctx.channel().remoteAddress() + " send : " + msg);

		Map<String, Object> map = JsonUtil.jsonToMap(msg);

		String actionName = map.get("actionName").toString();

		Login result = null;
		switch (actionName) {
		case "LOGIN":

			System.out.println("user login");
			Login login = JsonUtil.jsonToObject(msg, Login.class);
			// 数据库校验
			User user = login.getUser();
			if ("123".equals(user.getLoginName())
					&& "123".equals(user.getPassword())) {
				user.setId("123");
				MessageManager.userIntoGroup(user, ctx);
				result = (Login) login.generateSuccess("login success!");
			} else if ("321".equals(user.getLoginName())
					&& "321".equals(user.getPassword())) {
				user.setId("321");
				MessageManager.userIntoGroup(user, ctx);
				result = (Login) login.generateSuccess("login success!");
			} else {
				result = (Login) login.generateFail("loginName/password error!");
			}
		}
		System.out.println(result);

		ctx.writeAndFlush(JsonUtil.objectToJson(result) + "\r\n");
	}

	/*
	 * 
	 * 覆盖 channelActive 方法 在channel被启用的时候触发 (在建立连接的时候)
	 * 
	 * channelActive 和 channelInActive 在后面的内容中讲述，这里先不做详细的描述
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {

		System.out.println("RamoteAddress : " + ctx.channel().remoteAddress()
				+ " active !");

		super.channelActive(ctx);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("RamoteAddress : " + ctx.channel().remoteAddress()
				+ " inactive !");
		MessageManager.userOffGroup(ctx);
		super.channelInactive(ctx);
	}

}

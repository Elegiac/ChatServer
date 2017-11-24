package stu.demo.server.message;

import java.util.Map;

import edu.demo.common.entity.User;
import edu.demo.common.entity.action.BaseAction;
import edu.demo.common.entity.action.Login;
import edu.demo.common.utils.JsonUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class StringServerHandler extends SimpleChannelInboundHandler<String> {
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
		// 收到消息直接打印输出
		System.out.println(ctx.channel().remoteAddress() + " 消息 : " + msg);

		Map<String, Object> map = JsonUtil.jsonToMap(msg);

		String actionName = map.get("actionName").toString();

		BaseAction result = null;
		switch (actionName) {
		case "LOGIN":

			Login login = JsonUtil.jsonToObject(msg, Login.class);
			// 数据库校验
			User user = login.getUser();
			if ("123".equals(user.getLoginName()) && "123".equals(user.getPassword())) {
				user.setId("123");
				MessageManager.userIntoGroup(user, ctx);
				result = (Login) login.generateSuccess("登陆成功!");
			} else if ("321".equals(user.getLoginName()) && "321".equals(user.getPassword())) {
				user.setId("321");
				MessageManager.userIntoGroup(user, ctx);
				result = (Login) login.generateSuccess("登陆成功!");
			} else {
				result = (Login) login.generateFail("用户名/密码错误!");
			}
		}

		ctx.writeAndFlush(JsonUtil.objectToJson(result) + "\r\n");
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {

		System.out.println("RamoteAddress : " + ctx.channel().remoteAddress() + " active !");

		super.channelActive(ctx);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("RamoteAddress : " + ctx.channel().remoteAddress() + " inactive !");
		MessageManager.userOffGroup(ctx);
		super.channelInactive(ctx);
	}

}

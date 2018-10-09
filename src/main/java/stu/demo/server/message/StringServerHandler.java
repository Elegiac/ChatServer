package stu.demo.server.message;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.demo.common.entity.User;
import edu.demo.common.entity.action.BaseAction;
import edu.demo.common.entity.action.Chat;
import edu.demo.common.entity.action.Login;
import edu.demo.common.entity.action.UserList;
import edu.demo.common.utils.JsonUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class StringServerHandler extends SimpleChannelInboundHandler<String> {

	private static final Logger log = LoggerFactory.getLogger(StringServerHandler.class);

	private static final Map<String, User> USER_MAP;

	static {
		List<User> userList = Arrays.asList(new User("1", "123", "123"), new User("2", "321", "321"),
				new User("3", "124", "124"));

		Map<String, User> userMap = new HashMap<String, User>();
		for (User user : userList) {
			userMap.put(user.getLoginName() + user.getPassword(), user);
		}

		USER_MAP = Collections.unmodifiableMap(userMap);
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
		// 收到消息直接打印输出
		log.info("{} 消息: {} ", ctx.channel().remoteAddress(), msg);

		Map<String, Object> map = JsonUtil.jsonToMap(msg);

		String actionName = map.get("actionName").toString();

		BaseAction result = null;
		switch (actionName) {
		case "LOGIN":
			Login login = JsonUtil.jsonToObject(msg, Login.class);
			// 数据库校验
			User loginUser = login.getUser();

			User user = USER_MAP.get(loginUser.getLoginName() + loginUser.getPassword());

			if (user != null) {
				login.setUser(user);
				MessageManager.userIntoGroup(user, ctx);
				result = login.generateSuccess("登陆成功!");
			} else {
				result = login.generateFail("用户名/密码错误!");
			}
			ctx.writeAndFlush(JsonUtil.objectToJson(result) + "\r\n");

			UserList userList = new UserList();
			userList.setUsers(MessageManager.allUserFromGroup());
			userList.generateSuccess("success");
			MessageManager.sendMessageToAll(JsonUtil.objectToJson(userList));
			break;
		case "CHAT":
			Chat chat = JsonUtil.jsonToObject(msg, Chat.class);
			result = chat.generateSuccess("received");
			System.err.println(chat);

			ChannelHandlerContext to = MessageManager.getChannelByUser(chat.getTo());
			if (to != null) {
				to.writeAndFlush(JsonUtil.objectToJson(chat) + "\r\n");
			}
			ctx.writeAndFlush(JsonUtil.objectToJson(result) + "\r\n");
			break;
		}
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {

		log.info("RamoteAddress : {} active !", ctx.channel().remoteAddress());

		super.channelActive(ctx);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		log.info("RamoteAddress : {} inactive !", ctx.channel().remoteAddress());
		MessageManager.userOffGroup(ctx);
		super.channelInactive(ctx);
	}

}

package Commands;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Envelope;

import Model.Notification;

public class CreateNotification extends Command {

	@SuppressWarnings("unused")
	@Override
	protected void execute() {
		// TODO Auto-generated method stub
		HashMap<String, Object> props = parameters;
		Channel channel = (Channel) props.get("channel");
		JSONParser parser = new JSONParser();
		try {
			JSONObject messageBody = (JSONObject) parser.parse((String) props.get("body"));
			String url = ((JSONObject) parser.parse((String) props.get("body"))).get("uri").toString();
			url = url.substring(1);
			System.out.println(Arrays.toString(url.split("/")));
			
			String[] parametersArray = url.split("/");
			String target_id = parametersArray[1];
			
			HashMap<String, Object> requestBodyHash = jsonToMap((JSONObject) messageBody.get("body"));
			AMQP.BasicProperties properties = (AMQP.BasicProperties) props.get("properties");
			AMQP.BasicProperties replyProps = (AMQP.BasicProperties) props.get("replyProps");
			Envelope envelope = (Envelope) props.get("envelope");
			HashMap<String, Object> createdMessage = Notification.create(requestBodyHash, target_id);
			JSONObject response = jsonFromMap(createdMessage);
			System.out.println("RESPONSE");
			channel.basicPublish("", properties.getReplyTo(), replyProps, response.toString().getBytes("UTF-8"));
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}

package Commands;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Envelope;

import Model.Notification;

public class RetrieveNotifications extends Command {

	@SuppressWarnings("unused")
	@Override
    public void execute() {
        HashMap < String, Object > props = parameters;
        Channel channel = (Channel) props.get("channel");
        JSONParser parser = new JSONParser();

        try {
			JSONObject messageBody = (JSONObject) parser.parse((String) props.get("body"));
			String url = ((JSONObject) parser.parse((String) props.get("body"))).get("uri").toString();
			url = url.substring(1);
			System.out.println(Arrays.toString(url.split("/")));
			
			String[] parametersArray = url.split("/");
			String target_username = parametersArray[1];

            AMQP.BasicProperties properties = (AMQP.BasicProperties) props.get("properties");
            AMQP.BasicProperties replyProps = (AMQP.BasicProperties) props.get("replyProps");
            Envelope envelope = (Envelope) props.get("envelope");
			
			ArrayList<HashMap<String, Object>> notification = Notification.get(target_username);
            JSONObject response = Command.jsonFromArray(notification, "notification");
            channel.basicPublish("", properties.getReplyTo(), replyProps, response.toString().getBytes("UTF-8"));
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
    }

}

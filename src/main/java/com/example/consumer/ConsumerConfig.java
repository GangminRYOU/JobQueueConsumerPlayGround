package com.example.consumer;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConsumerConfig {
	@Bean
	public org.springframework.amqp.core.Queue queue(){
		return new Queue(
			//이름: producer와 consumer가 같은 Queue를 사용하기 위해서
			//작성하는 식별자
			//RabbitMQ가 메세지 브로커고 그안에 우체통이 있다고 하면
			//얘가 그 우체통같은 개념
			"boot.amqp.worker-queue",
			/*서버가 종료된 후에도 Queue가 유지될지에 대한 옵션*/
			/*Queue를 CloudAMQP에 생성하는데, 그게 서버 종료 후에도 남아있을지*/
			true,
			/*지금 이 서버만 Queue를 사용할 수 있는지
			 * 사실상 메세지를 주고 받는 P-C모델에서는 true면 안된다.*/
			false,
			/*Auto-delete : 데이터가 사용되고있지 않은 큐를 자동으로 삭제할 것인지*/
			true
		);
		//밑에 3개는 생략하면, 그냥 기본값으로 들어가게 된다. true, false, false
	}
}

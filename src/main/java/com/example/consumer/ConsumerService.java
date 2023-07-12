package com.example.consumer;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.example.consumer.dto.JobPayload;
import com.example.consumer.entity.JobEntity;
import com.example.consumer.entity.JobRepository;
import com.google.gson.Gson;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
//이 클래스는 RabbitMQ의 QUeue에 적재되는 메세지를 받아서 처리하기 위한
//Queue이다.
/*이 클래스가 저 큐에 대해 반응하는 lister다.*/
@RabbitListener(queues = "boot.amqp.worker-queue")
@RequiredArgsConstructor
public class ConsumerService {

	private final JobRepository jobRepository;
	private final Gson gson;

	@RabbitHandler
	/*반응 했을떄 실행해줄 메소드
	* 메세지에 json형태가 들어있다.
	* 메세지 큐에 담겼던 문자열이 전달된다
	* */
	public void receive(String message) throws InterruptedException {
		/*역직렬화*/
		JobPayload newJob = gson.fromJson(message, JobPayload.class);
		String jobId = newJob.getJobId();
		log.info("Received Job : {}", jobId);
		Optional<JobEntity> optionalJob = jobRepository.findByJobId(jobId);
		/*TODO 존재하지 않을 경우 예외 처리를 해줘야 마땅하나 생략*/
		JobEntity jobEntity = optionalJob.get();
		jobEntity.setStatus("PROCESSING");
		jobEntity = jobRepository.save(jobEntity);
		log.info("Start Processing Job : {}", jobId);
		//처리하는데 시간이 걸린다고 치자.
		TimeUnit.SECONDS.sleep(5);

		//처리가 끝난뒤
		jobEntity.setStatus("DONE");
		jobEntity.setResultPath(
			String.format("/media/user-uploaded/processed/%s", newJob.getFilename())
		);
		jobRepository.save(jobEntity);
		log.info("Finished Job : {}", jobId);
	}
}

package felipedomingos.com.playground.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.Executors;

@Configuration
@EnableScheduling
public class SchedulingConfig implements SchedulingConfigurer {

	@Bean
	TaskScheduler taskScheduler() {
		var factory = Thread.ofVirtual().name("vt-sched-", 0).factory();
		var executor = Executors.newScheduledThreadPool(1, factory);
		return new ConcurrentTaskScheduler(executor);
	}

	@Override
	public void configureTasks(ScheduledTaskRegistrar registrar) {
		registrar.setScheduler(taskScheduler());
	}
}

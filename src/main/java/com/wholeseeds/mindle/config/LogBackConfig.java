package com.wholeseeds.mindle.config;

import static org.slf4j.Logger.*;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Configuration
@Profile("local")
public class LogBackConfig {

	public static final String STDOUT = "STDOUT";
	private static final String LOG_PATTERN =
		"%green(%d{yyyy-MM-dd HH:mm:ss, Asia/Seoul}) "
			+ "%highlight(%5level) %magenta(%method) "
			+ "%replace(%logger){'com.wholeseeds.mindle.', ''} - %blue(%msg%n)";

	private final LoggerContext logCtx = (LoggerContext) LoggerFactory.getILoggerFactory();

	@Value("${logging.level.app:TRACE}")
	private String appLogLevel;

	@Value("${logging.level.root:ERROR}")
	private String rootLogLevel;

	@Bean
	public ConsoleAppender<ILoggingEvent> logConfig() {
		ConsoleAppender<ILoggingEvent> consoleAppender = getLogConsoleAppender();
		createLoggers(consoleAppender);
		return consoleAppender;
	}

	private void createLoggers(ConsoleAppender<ILoggingEvent> appender) {
		createLogger("com.book.backend", Level.toLevel(appLogLevel), false, appender);
		createLogger(ROOT_LOGGER_NAME, Level.toLevel(rootLogLevel), true, appender);
	}

	private void createLogger(
		String loggerName,
		Level logLevel,
		boolean additive,
		ConsoleAppender<ILoggingEvent> appender
	) {
		Logger logger = logCtx.getLogger(loggerName);
		logger.setAdditive(additive);
		logger.setLevel(logLevel);

		// Appender 중복 방지
		if (logger.getAppender(appender.getName()) == null) {
			logger.addAppender(appender);
		}
	}

	private ConsoleAppender<ILoggingEvent> getLogConsoleAppender() {
		PatternLayoutEncoder consoleLogEncoder = createLogEncoder();
		return createLogConsoleAppender(consoleLogEncoder);
	}

	private PatternLayoutEncoder createLogEncoder() {
		PatternLayoutEncoder encoder = new PatternLayoutEncoder();
		encoder.setContext(logCtx);
		encoder.setPattern(LogBackConfig.LOG_PATTERN);
		encoder.start();
		return encoder;
	}

	/**
	 * 콘솔에 로그를 출력하는 Appender를 생성합니다.
	 *
	 * @param encoder 로그 패턴을 설정한 인코더
	 * @return 콘솔 Appender
	 */
	private ConsoleAppender<ILoggingEvent> createLogConsoleAppender(
		PatternLayoutEncoder encoder
	) {
		ConsoleAppender<ILoggingEvent> appender = new ConsoleAppender<>();
		appender.setName(STDOUT);
		appender.setContext(logCtx);
		appender.setEncoder(encoder);
		appender.start();
		return appender;
	}

	@PostConstruct
	public void startLoggerContext() {
		logCtx.start();
	}

	@PreDestroy
	public void stopLoggerContext() {
		logCtx.stop();
	}
}

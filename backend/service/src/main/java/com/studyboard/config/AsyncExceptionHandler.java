package com.studyboard.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;

@Component
public class AsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

  private static final Logger logger = LoggerFactory.getLogger(AsyncExceptionHandler.class);

  @Override
  public void handleUncaughtException(Throwable throwable, Method method, Object... objects) {
    logger.info(
        "Method name: "
            + method.getName()
            + " --- "
            + Arrays.toString(objects)
            + "---"
            + "error message: "
            + throwable.getMessage());
  }
}

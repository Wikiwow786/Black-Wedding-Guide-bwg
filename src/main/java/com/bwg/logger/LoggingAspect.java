package com.bwg.logger;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.text.MessageFormat;
import java.util.Arrays;

import static com.bwg.logger.LoggingEvent.*;

@Aspect
@Configuration
public class LoggingAspect {
    private final Environment env;

    public LoggingAspect(Environment env) {
        this.env = env;
    }

    /**
     * Pointcut that matches all repositories, services and Web REST endpoints.
     */
    @Pointcut("within(@org.springframework.stereotype.Repository *)" +
            " || within(@org.springframework.stereotype.Service *)")
    public void springBeanPointcut() {
        // Method is empty as this is just a Pointcut, the implementations are in the advices.
    }

    /**
     * Pointcut that matches all Spring beans in the application's main packages.
     */
    @Pointcut("within(com.bwg.service..*)" +
            " || within(com.bwg.repository..*)")
    public void applicationPackagePointcut() {
        // Method is empty as this is just a Pointcut, the implementations are in the advices.
    }

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void springBeanControllerPointcut() {
        // Method is empty as this is just a Pointcut, the implementations are in the advices.
    }

    /**
     * Pointcut that matches all Spring beans in the application's main packages.
     */
    @Pointcut("within(com.bwg.restapi..*)")
    public void applicationControllerPackagePointcut() {
        // Method is empty as this is just a Pointcut, the implementations are in the advices.
    }

    /**
     * Advice that logs methods throwing exceptions.
     *
     * @param joinPoint join point for advice
     * @param e         exception
     */
    @AfterThrowing(pointcut = "applicationPackagePointcut() && springBeanPointcut()", throwing = "e")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {

        String message = MessageFormat.format("Exception in {0}.{1}() with cause = {2}", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName(), e.getCause() != null ? e.getCause() : "NULL", e.getMessage());
        Logger.error(LOG_SERVICE_OR_REPOSITORY_EXCEPTION, message, joinPoint.getSignature().getClass(), e);

    }

    /**
     * Advice that logs when a method is entered and exited.
     *
     * @param joinPoint join point for advice
     * @return result
     * @throws Throwable throws IllegalArgumentException
     */
    @Around("applicationPackagePointcut() && springBeanPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {

        Logger.debug(LOG_SERVICE_OR_REPOSITORY, format( "Enter= {0}.{1}() with argument[s] = {2}", joinPoint), this);
        try
        {
            Object result = joinPoint.proceed();
            Logger.debug(LOG_SERVICE_OR_REPOSITORY, format( "Exit= {0}.{1}() with result = {2}", joinPoint), this);
            return result;
        }
        catch (IllegalArgumentException e)
        {
            Logger.error(LOG_SERVICE_OR_REPOSITORY_EXCEPTION, format( "Illegal argument: {0} in {1}.{2}()", joinPoint), this,  e);
            throw e;
        }
        catch (Exception e) {
            Logger.error(LOG_SERVICE_OR_REPOSITORY_EXCEPTION, MessageFormat.format("Error occurred please contact support {0}", e.getMessage()), this, e);
            throw e;
        }
    }

    /**
     * Advice that logs when a method is entered and exited.
     *
     * @param joinPoint join point for advice
     * @return result
     * @throws Throwable throws IllegalArgumentException
     */
    @Around("applicationControllerPackagePointcut() && springBeanControllerPointcut()")
    public Object logAroundController(ProceedingJoinPoint joinPoint) throws Throwable {

        Logger.debug(LOG_CONTROLLER, format( "Enter: Controller Name: {0}, Method Name: {1}, Args:{2}", joinPoint), this);

        try
        {
            Object result = joinPoint.proceed();
            Logger.debug(LOG_CONTROLLER, format( "Exit: Controller Name: {0}, Method Name: {1}, Args:{2}", joinPoint), this);
            return result;
        }
        catch (IllegalArgumentException e)
        {
            Logger.error(LOG_CONTROLLER_EXCEPTION, format( "Illegal argument: {0} in {1}.{2}()", joinPoint), this, e);
            throw e;
        }
        catch (Exception e)
        {
            Logger.error(LOG_CONTROLLER_EXCEPTION, MessageFormat.format( "Error occurred please contact support {0}",e.getMessage()), this, e);
            throw e;
        }
    }

    private String format(String message, JoinPoint joinPoint)
    {
        return MessageFormat.format(message, joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName(), Arrays.toString(joinPoint.getArgs()));
    }

}

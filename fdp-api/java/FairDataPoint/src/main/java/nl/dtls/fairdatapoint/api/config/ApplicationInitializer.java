package nl.dtls.fairdatapoint.api.config;

import javax.servlet.Filter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

/**
 * Application config (Similar to web.xml). * 
 * @author Rajaram Kaliyaperumal
 * @since 2015-11-19
 * @version 0.1
 */
public class ApplicationInitializer extends
        AbstractAnnotationConfigDispatcherServletInitializer {
    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[]{};
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class[]{RestApiContext.class};
    }

    @Override
    protected String[] getServletMappings() {
        return new String[]{"/*"};
    }

    @Override
    protected Filter[] getServletFilters() {
        return new Filter[]{new ApplicationFilter()};
    }
}

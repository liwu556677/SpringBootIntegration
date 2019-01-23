package com.futao.springmvcdemo.annotation.impl.interceptor;

import com.futao.springmvcdemo.model.entity.User;
import com.futao.springmvcdemo.model.system.SystemConfiguration;
import com.futao.springmvcdemo.utils.TimeUtilsKt;
import org.joda.time.DateTime;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.format.Formatter;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;

/**
 * 扩展spring mvc的功能
 * 如果在该类上标注@EnableWebMvc,将会全面接管springboot对springmvc的配置。（springboot的自动配置全部失效）
 * 所有的配置都需要由自己实现
 *
 * @author futao
 * Created on 2018/9/18-15:15.
 */
@SpringBootConfiguration
public class WebMvcConfiguration implements WebMvcConfigurer {

    @Resource
    private SignInterceptor signInterceptor;
    @Resource
    private LoginUserInterceptor loginUserInterceptor;
    @Resource
    private RequestLogInterceptor requestLogInterceptor;
    @Resource
    private LocaleChangeInterceptor localeChangeInterceptor;

    /**
     * 添加拦截器
     * addInterceptor()的顺序需要严格按照程序的执行的顺序
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestLogInterceptor).addPathPatterns("/**");
        registry.addInterceptor(loginUserInterceptor).addPathPatterns("/**");
        //  "/**"和"/*"是有区别的
        registry.addInterceptor(signInterceptor).addPathPatterns("/**");
        registry.addInterceptor(localeChangeInterceptor).addPathPatterns("/**");
    }

    /**
     * 不起作用，无效
     * <p>
     * Add Converter and {@link Formatter}s in addition to the ones
     * registered by default.
     *
     * @param registry
     */
    @Override
    public void addFormatters(FormatterRegistry registry) {
        Formatter<Timestamp> timestampFormatter = new Formatter<Timestamp>() {
            @Override
            public Timestamp parse(String text, Locale locale) throws ParseException {
                System.out.println("parse 时间转换器");
                DateTime dateTime = TimeUtilsKt.toDateTime(text);
                return TimeUtilsKt.toTimestamp(dateTime);
            }

            @Override
            public String print(Timestamp object, Locale locale) {
                System.out.println("print 时间转换器");
                return null;
            }
        };
        registry.addFormatter(timestampFormatter);
        registry.addFormatterForFieldType(Timestamp.class, timestampFormatter);

        Formatter<User> userFormatter = new Formatter<User>() {
            @Override
            public User parse(String text, Locale locale) throws ParseException {
                System.out.println("=================");
                return null;
            }

            @Override
            public String print(User object, Locale locale) {
                System.out.println("-------------");
                return null;
            }
        };
        registry.addFormatter(userFormatter);
    }

    /**
     * 添加静态资源映射
     * <p>
     * Add handlers to serve static resources such as images, js, and, css
     * files from specific locations under web application root, the classpath,
     * and others.
     * <p>
     * 配置了该资源解析器之后会导致swagger 404 ，需要加上对/swagger-ui.html的映射
     *
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
        registry.addResourceHandler("/swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");

    }

    /**
     * Configure cross origin requests processing.
     *
     * @param registry
     * @since 4.2
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowCredentials(true)
                .allowedMethods("*")
                .allowedHeaders("Content-Type")
                .allowedOrigins(SystemConfiguration.ALLOW_ORIGINS)
                .maxAge(SystemConfiguration.ORIGIN_MAX_AGE);
    }

    /**
     * Configure the {@link HttpMessageConverter}s to use for reading or writing
     * to the body of the request or response. If no converters are added, a
     * default list of converters is registered.
     * <p><strong>Note</strong> that adding converters to the list, turns off
     * default converter registration. To simply add a converter without impacting
     * default registration, consider using the method
     * {@link #extendMessageConverters(List)} instead.
     *
     * @param converters initially an empty list of converters
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
//        converters.add(messageConvertConfiguration);
//        converters.add(fastJsonHttpMessageConverter);
//        暂时无效
    }

}

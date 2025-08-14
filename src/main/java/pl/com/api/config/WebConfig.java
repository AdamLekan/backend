package pl.com.api.config;

import at.qubic.api.network.Nodes;
import at.qubic.api.service.ComputorService;
import at.qubic.api.service.TransactionService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;

@Configuration
public class WebConfig implements WebMvcConfigurer {

//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
    @Bean
    public TransactionService transactionService() {
        return new TransactionService(); // Adjust the constructor as needed
    }
    @Bean
    public Nodes nodes() {Nodes nodes = new Nodes();
//        nodes.addNode("65.21.10.217:21841");
////        nodes.addNode("104.26.0.125"); // node
//        nodes.addNode("104.26.1.125"); // node
        nodes.addNode("64.226.122.206");
//        nodes.addNode("38.242.220.233");
        nodes.addNode("104.248.252.163");
        return nodes; // Adjust the constructor as needed
    }
    @Bean
    public ComputorService computorService() {
        return new ComputorService(nodes()); // Adjust the constructor as needed
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .resourceChain(true)
                .addResolver(new PathResourceResolver() {
                    @Override
                    protected Resource getResource(String resourcePath, Resource location) throws IOException {
                        Resource requestedResource = location.createRelative(resourcePath);
                        return requestedResource.exists() && requestedResource.isReadable() 
                                ? requestedResource 
                                : new ClassPathResource("/static/index.html");
                    }
                });
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("forward:/index.html");
        registry.addViewController("/login").setViewName("forward:/index.html");
        registry.addViewController("/access-check").setViewName("forward:/index.html");
        registry.addViewController("/doctor-visit").setViewName("forward:/index.html");
        registry.addViewController("/create-user").setViewName("forward:/index.html");
        registry.addViewController("/secure").setViewName("forward:/index.html");
    }
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")
//                .allowedOrigins("http://localhost:4200")
//                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
//                .allowedHeaders("*")
//                .allowCredentials(true);
//    }
}
package com.ask.modulecore.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

@Slf4j
public class YamlEnvironmentPostProcessor implements EnvironmentPostProcessor {

  private final String[] properties = {"classpath*:config/application-core.yml"};

  private final YamlPropertySourceLoader yamlPropertySourceLoader = new YamlPropertySourceLoader();
  private final ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

  @Override
  public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {

    try {
      List<Resource> resources = new ArrayList<>();
      for (String property : properties) {
        resources.addAll(Arrays.asList(resourcePatternResolver.getResources(property)));
      }

      resources.stream()
          .filter(Resource::exists)
          .map(this::loadYaml)
          .forEach(propertySources -> addProperties(environment, propertySources));
    } catch (Exception e) {
      throw new BeanCreationException(e.getMessage(), e);
    }
  }

  private List<PropertySource<?>> loadYaml(Resource resource) {
    try {
      return yamlPropertySourceLoader.load(resource.getURL().toString(), resource);
    } catch (IOException e) {
      throw new IllegalStateException();
    }
  }

  private void addProperties(ConfigurableEnvironment environment, List<PropertySource<?>> propertySources) {
    propertySources.forEach(propertySource -> environment.getPropertySources().addLast(propertySource));
  }
}

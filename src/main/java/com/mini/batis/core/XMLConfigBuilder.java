package com.mini.batis.core;

import com.mini.batis.model.Configuration;
import org.apache.commons.dbcp.BasicDataSource;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

public class XMLConfigBuilder {

    public Configuration parse(String configPath) {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(configPath)) {
            if (inputStream == null) {
                throw new RuntimeException("Can not find config file: " + configPath);
            }

            SAXReader saxReader = new SAXReader();
            Document configDocument = saxReader.read(inputStream);
            Element rootElement = configDocument.getRootElement();

            Configuration configuration = new Configuration();
            parseEnvironments(rootElement, configuration);
            parseMappers(rootElement, configuration);
            return configuration;
        } catch (Exception e) {
            throw new RuntimeException("Can not parse config file: " + configPath, e);
        }
    }

    private void parseMappers(Element rootElement, Configuration configuration) throws Exception {
        Element mappersElement = rootElement.element("mappers");
        if (mappersElement == null) {
            throw new RuntimeException("Can not find mappers config");
        }

        List<Element> mappers = mappersElement.elements("mapper");
        for (Element mapper : mappers) {
            String resource = mapper.attributeValue("resource");
            try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resource)) {
                if (inputStream == null) {
                    throw new RuntimeException("Can not find mapper file: " + resource);
                }
                XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(configuration);
                xmlMapperBuilder.parse(inputStream);
            }
        }
    }

    private void parseEnvironments(Element rootElement, Configuration configuration) {
        Element environmentsElement = rootElement.element("environments");
        if (environmentsElement == null) {
            throw new RuntimeException("Can not find environments config");
        }

        String defaultEnvironment = environmentsElement.attributeValue("default");
        List<Element> environmentElements = environmentsElement.elements("environment");
        Element matchedEnvironmentElement = null;
        for (Element environmentElement : environmentElements) {
            String id = environmentElement.attributeValue("id");
            if (Objects.equals(id, defaultEnvironment)) {
                matchedEnvironmentElement = environmentElement;
                break;
            }
        }

        if (matchedEnvironmentElement == null) {
            throw new RuntimeException("Can not find default environment: " + defaultEnvironment);
        }

        Element dataSourceElement = matchedEnvironmentElement.element("dataSource");
        if (dataSourceElement == null) {
            throw new RuntimeException("Can not find dataSource config");
        }

        String dataSourceType = dataSourceElement.attributeValue("type");
        if (!Objects.equals(dataSourceType, "DBCP")) {
            throw new RuntimeException("Unsupported dataSource type: " + dataSourceType);
        }

        Properties properties = parseDBCPDataSource(dataSourceElement);
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(properties.getProperty("driver"));
        dataSource.setUrl(properties.getProperty("url"));
        dataSource.setUsername(properties.getProperty("username"));
        dataSource.setPassword(properties.getProperty("password"));
        configuration.setDataSource(dataSource);
    }

    private Properties parseDBCPDataSource(Element dataSourceElement) {
        Properties properties = new Properties();
        List<Element> propertyElements = dataSourceElement.elements("property");
        for (Element propertyElement : propertyElements) {
            String name = propertyElement.attributeValue("name");
            String value = propertyElement.attributeValue("value");
            if (name == null || name.isEmpty() || value == null || value.isEmpty()) {
                continue;
            }
            properties.setProperty(name, value);
        }
        return properties;
    }
}

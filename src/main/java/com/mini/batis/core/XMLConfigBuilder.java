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
        InputStream is = null;
        try {
            Configuration configuration = new Configuration();
            is = getClass().getClassLoader().getResourceAsStream(configPath);
            if (null == is) {
                throw new RuntimeException("Can not find config file: " + configPath);
            }
            SAXReader saxReader = new SAXReader();
            Document configDocument = saxReader.read(is);
            Element rootElement = configDocument.getRootElement();
            parseEnvironments(rootElement, configuration);
            parseMappers(rootElement, configuration);
            return configuration;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (Exception e) {
                    System.out.println("Close input stream error: " + e.getMessage());
                }
            }
        }
    }

    private void parseMappers(Element rootElement, Configuration configuration) throws Exception {
        Element mappersElement = rootElement.element("mappers");
        if (null == mappersElement) {
            return;
        }
        List<Element> mappers = mappersElement.elements("mapper");
        for (Element mapper : mappers) {
            InputStream inputStream = null;
            try {
                String resource = mapper.attributeValue("resource");
                inputStream = getClass().getClassLoader().getResourceAsStream(resource);
                if (null == inputStream) {
                    throw new RuntimeException("Can not find mapper file:" + resource);
                }
                XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder();
                xmlMapperBuilder.parse(inputStream, configuration);
            } finally {
                if (null != inputStream) {
                    inputStream.close();
                }
            }

        }
    }

    private void parseEnvironments(Element rootElement, Configuration configuration) {
        Element environmentsElement = rootElement.element("environments");
        String environDefault = environmentsElement.attributeValue("default");
        List<Element> elements = environmentsElement.elements("environment");
        Element matchedEnvironmentElement = null;
        for (Element element : elements) {
            String id = element.attributeValue("id");
            if (Objects.equals(id, environDefault)) {
                matchedEnvironmentElement = element;
                break;
            }
        }
        if (null == matchedEnvironmentElement) {
            return;
        }
        Element dataSourceElement = matchedEnvironmentElement.element("dataSource");
        if (null == dataSourceElement) {
            return;
        }
        String dataSourceType = dataSourceElement.attributeValue("type");
        if (null == dataSourceType || dataSourceType.isEmpty()) {
            return;
        }
        if (Objects.equals(dataSourceType, "DBCP")) {
            Properties dbcpDataSource = parseDBCPDataSource(dataSourceElement, configuration);
            BasicDataSource basicDataSource = new BasicDataSource();
            basicDataSource.setDriverClassName(dbcpDataSource.getProperty("driver"));
            basicDataSource.setUrl(dbcpDataSource.getProperty("url"));
            basicDataSource.setUsername(dbcpDataSource.getProperty("username"));
            basicDataSource.setPassword(dbcpDataSource.getProperty("password"));
            configuration.setDataSource(basicDataSource);
        }
    }

    private Properties parseDBCPDataSource(Element dataSourceElement, Configuration configuration) {
        List<Element> property = dataSourceElement.elements("property");
        Properties properties = new Properties();
        for (Element element : property) {
            if (null == element) {
                continue;
            }
            String name = element.attributeValue("name");
            String value = element.attributeValue("value");
            if (null == name || name.isEmpty() || null == value || value.isEmpty()) {
                continue;
            }
            properties.setProperty(name, value);
        }
        return properties;
    }
}

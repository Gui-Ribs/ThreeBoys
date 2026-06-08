package com.threeboys.config;

import java.util.Objects;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class DatabaseConfig {

	private static final String PROPERTIES_FILE = "db.properties";
	private static final Pattern ENV_VARIABLE_PATTERN = Pattern.compile("\\$\\{([A-Za-z_][A-Za-z0-9_]*)}");

	private final String url;
	private final String user;
	private final String password;

	private DatabaseConfig() {
		Properties properties = loadProperties();
		this.url = getEnv(trimProperty(properties, "db.url"));
		this.user = getEnv(trimProperty(properties, "db.user"));
		this.password = getEnv(trimProperty(properties, "db.password"));
	}

	public static DatabaseConfig getInstance() {
		return Holder.INSTANCE;
	}

	// carrega as propriedade do arquivo db.properties

	private Properties loadProperties() {
		Properties properties = new Properties();
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

		try (var inputStream = classLoader.getResourceAsStream(PROPERTIES_FILE)) {
			if (inputStream == null) {
				throw new IllegalStateException("Arquivo de propriedades " + PROPERTIES_FILE + " não foi encontrado");
			}
			properties.load(inputStream);
			return properties;
		} catch (Exception e) {
			throw new RuntimeException("Falha em carregar a configuração do banco: " + e.getMessage(), e);
		}
	}

	// remove espaços em branco e valida se a propriedade não é vazia

	private String trimProperty(Properties properties, String key) {
		String value = properties.getProperty(key);

		if (value.isBlank()) {
			throw new IllegalStateException("A propriedade '" + key + "' não pode ser vazia");
		}

		return value.trim();
	}

	// substitui as variáveis de ambiente pelos valores

	private String getEnv(String value) {
		Objects.requireNonNull(value, "O valor não pode ser nulo");
		Matcher matcher = ENV_VARIABLE_PATTERN.matcher(value);
		StringBuffer result = new StringBuffer();
		while (matcher.find()) {
			String envVarName = matcher.group(1);
			String envVarValue = System.getenv(envVarName);
			if (envVarValue == null || envVarValue.isBlank()) {
				throw new IllegalStateException("A variável de ambiente '" + envVarName + "' não está definida");
			}
			matcher.appendReplacement(result, Matcher.quoteReplacement(envVarValue));
		}
		matcher.appendTail(result);
		return result.toString();
	}

	// getters

	public String getUrl() {
		return url;
	}

	public String getUser() {
		return user;
	}

	public String getPassword() {
		return password;
	}

	// classe interna para implementar o singleton

	private static class Holder {
		private static final DatabaseConfig INSTANCE = new DatabaseConfig();
	}
}

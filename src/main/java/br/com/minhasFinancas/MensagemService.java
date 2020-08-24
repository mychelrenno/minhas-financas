package br.com.minhasFinancas;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("production") // especifica que nesta classe sera usado o arquivo "aplication-production.properties"
public class MensagemService {

	@Value("${aplication.name}")
	private String appName;
}

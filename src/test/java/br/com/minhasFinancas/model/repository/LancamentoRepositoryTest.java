package br.com.minhasFinancas.model.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;//static import
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.minhasFinancas.model.entity.Lancamento;
import br.com.minhasFinancas.model.entity.Usuario;
import br.com.minhasFinancas.model.enums.StatusLancamento;
import br.com.minhasFinancas.model.enums.TipoLancamento;

@ExtendWith( SpringExtension.class )
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles("test") // ativa o arquivo .properties de test
public class LancamentoRepositoryTest {

	@Autowired
	LancamentoRepository repository;
	
	@Autowired
	TestEntityManager entityManager;
	
	public static Lancamento criarLancamento() {
		return Lancamento.builder()
				.ano(2020)
				.mes(8)
				.descricao("Lançamento qualquer")
				.valor(BigDecimal.valueOf(10))
				.tipo(TipoLancamento.RECEITA)
				.status(StatusLancamento.PENDENTE)
				.dataCadastro(LocalDate.now())
				.usuario( Usuario.builder().id(1l).build() )
				.build();
	}
	
	private Lancamento criaEPersistUmLancamento() {
		Lancamento lancamento = criarLancamento();
		entityManager.persist(lancamento);
		return lancamento;
	}
	
	@Test
	public void deveSalvarUmLancamento() {
		Lancamento lancamento = criarLancamento();
		
		lancamento = repository.save(lancamento);
		
		assertThat(lancamento.getId()).isNotNull();
	}
	
	@Test
	private void deveDeletarUmLancamento() {
		Lancamento lancamento = criaEPersistUmLancamento();
		
		lancamento = entityManager.find(Lancamento.class, lancamento.getId());
		
		repository.delete(lancamento);
		
		Lancamento lancamentoDeletado = entityManager.find(Lancamento.class, lancamento.getId());
		assertThat(lancamentoDeletado).isNull();
	}

	@Test
	public void deveAtualizarUmLancamento() {
		Lancamento lancamento = criaEPersistUmLancamento();
		
		lancamento.setAno(2018);
		lancamento.setDescricao("Test atualiza lançamento");
		lancamento.setStatus( StatusLancamento.CANCELADO );
		
		repository.save(lancamento);
		
		Lancamento lancamentoAtualizado = entityManager.find(Lancamento.class, lancamento.getId());
		
		assertThat(lancamentoAtualizado.getAno()).isEqualTo(2018);
		assertThat(lancamentoAtualizado.getDescricao()).isEqualTo("Test atualiza lançamento");
		assertThat(lancamentoAtualizado.getStatus()).isEqualTo( StatusLancamento.CANCELADO );
	}
	
	public void deveBuscarUmLancamentoPorId() {
		Lancamento lancamento = criaEPersistUmLancamento();
		
		Optional<Lancamento> lancamentoEncontrado = repository.findById(lancamento.getId());
		
		assertThat(lancamentoEncontrado.isPresent()).isTrue();
	}
	
}

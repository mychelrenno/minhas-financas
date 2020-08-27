package br.com.minhasFinancas.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.minhasFinancas.exception.RegraNegocioException;
import br.com.minhasFinancas.model.entity.Lancamento;
import br.com.minhasFinancas.model.entity.Usuario;
import br.com.minhasFinancas.model.enums.StatusLancamento;
import br.com.minhasFinancas.model.enums.TipoLancamento;
import br.com.minhasFinancas.model.repository.LancamentoRepository;
import br.com.minhasFinancas.model.repository.LancamentoRepositoryTest;
import br.com.minhasFinancas.service.impl.LancamentoServiceImpl;

@ExtendWith( SpringExtension.class )
@ActiveProfiles("test")
public class LancamentoServiceTest {

	@SpyBean
	LancamentoServiceImpl service;
	
	@MockBean
	LancamentoRepository repository;
	
	@Test
	public void deveSalvarUmlancamento() {
		//cenário
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
		Mockito.doNothing().when(service).validar(lancamentoASalvar);
		
		Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
		lancamentoSalvo.setId(1l);
		lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
		Mockito.when(repository.save(lancamentoASalvar)).thenReturn(lancamentoSalvo);
		
		//execução
		Lancamento lancamento = service.salvar(lancamentoASalvar);
		
		//verificação
		Assertions.assertThat( lancamento.getId() ).isEqualTo( lancamentoSalvo.getId() );
		Assertions.assertThat( lancamento.getStatus() ).isEqualTo( StatusLancamento.PENDENTE );
	}
	
	@Test
	public void naoDeveSalvarUmLancamentoQuandoHouverErroDeValidacao() {
		//cenário
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
		Mockito.doThrow(RegraNegocioException.class).when(service).validar(lancamentoASalvar);
		
		//execução e verificação
		Assertions.catchThrowableOfType( () -> service.salvar(lancamentoASalvar), RegraNegocioException.class);
		
		Mockito.verify(repository, Mockito.never()).save(lancamentoASalvar);
	}
	
	@Test
	public void deveAtualizarUmlancamento() {
		//cenário
		Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
		lancamentoSalvo.setId(1l);
		lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
		
		Mockito.doNothing().when(service).validar(lancamentoSalvo);
		
		Mockito.when(repository.save(lancamentoSalvo)).thenReturn(lancamentoSalvo);
		
		//execução
		service.atualizar(lancamentoSalvo);
		
		//verificação
		Mockito.verify( repository, Mockito.times(1) ).save(lancamentoSalvo);
	}
	
	@Test
	public void deveLancarErroAoTentarAtualizarUmLancamentoQueAindaNaoFoiSalvo() {
		//cenário
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
		
		//execução e verificação
		Assertions.catchThrowableOfType( () -> service.atualizar(lancamentoASalvar), NullPointerException.class);
		
		Mockito.verify(repository, Mockito.never()).save(lancamentoASalvar);
	}
	
	@Test
	public void deveDeletarUmLancamento() {
		//cenário
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1l);
		
		//execução
		service.deletar(lancamento);
		
		//verificação
		Mockito.verify( repository ).delete( lancamento );
	}
	
	@Test
	public void deveLancarErroAoTentarDeletarUmLancamentoQueAindaNaoFoiSalvo() {
		//cenário
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		
		//execução
		Assertions.catchThrowableOfType( () -> service.deletar(lancamento), NullPointerException.class);
		
		//verificação
		Mockito.verify( repository, Mockito.never() ).delete( lancamento );
	}
	
	@Test
	private void deveFiltrarLancamento() {
		//cenário
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1l);
		
		List<Lancamento> lista = Arrays.asList(lancamento);
		Mockito.when( repository.findAll( Mockito.any( Example.class) ) ).thenReturn(lista);
		
		//execução
		List<Lancamento> resultado = service.buscar(lancamento);
		
		//verificação
		Assertions.assertThat(resultado).isNotEmpty().hasSize(1).contains(lancamento);
	}
	
	@Test
	public void deveAtualizarOStatusDeUmLancamento() {
		//cenário
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1l);
		lancamento.setStatus( StatusLancamento.PENDENTE );
		
		StatusLancamento novoStatus = StatusLancamento.EFETIVADO;
		Mockito.doReturn( lancamento ).when( service ).atualizar(lancamento);
		
		//execução
		service.atualizarStatus(lancamento, novoStatus);
		
		//validação
		Assertions.assertThat( lancamento.getStatus() ).isEqualTo( StatusLancamento.EFETIVADO );
		Mockito.verify( service ).atualizar(lancamento);
	}
	
	@Test
	public void deveObterUmLancamentoPorId() {
		//cenário
		Long id = 1l;
		
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(id);
		
		Mockito.when( repository.findById(id) ).thenReturn( Optional.of(lancamento) );
		//execução
		Optional<Lancamento> resultado = service.buscarPorId(id);
		
		//validação
		Assertions.assertThat( resultado.isPresent() ).isTrue();
		
	}
	
	@Test
	public void deveRetornalVazioQuandoOLancamentoNaoExiste() {
		//cenário
		Long id = 1l;
		
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(id);
		
		Mockito.when( repository.findById(id) ).thenReturn( Optional.empty() );
		//execução
		Optional<Lancamento> resultado = service.buscarPorId(id);
		
		//validação
		Assertions.assertThat( resultado.isPresent() ).isFalse();
		
	}
	
	@Test
	public void deveRetornalOErroCorretoParaTodosOsCasos() {
		//cenário
		Lancamento lancamento = new Lancamento();
		
		//execução e validação
		//valida descrição null
		Throwable exception = Assertions.catchThrowable( () -> service.validar( lancamento ) );
		Assertions.assertThat(exception).isInstanceOf(RegraNegocioException.class).hasMessage("Informe uma descrição válida.");
		
		lancamento.setDescricao("");
		exception = Assertions.catchThrowable( () -> service.validar( lancamento ) );
		Assertions.assertThat(exception).isInstanceOf(RegraNegocioException.class).hasMessage("Informe uma descrição válida.");
		
		lancamento.setDescricao("descrição do lançamento");
		//valida mês null
		exception = Assertions.catchThrowable( () -> service.validar( lancamento ) );
		Assertions.assertThat(exception).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um mês válido.");
		
		lancamento.setMes(15);
		exception = Assertions.catchThrowable( () -> service.validar( lancamento ) );
		Assertions.assertThat(exception).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um mês válido.");
		
		lancamento.setMes(-5);
		exception = Assertions.catchThrowable( () -> service.validar( lancamento ) );
		Assertions.assertThat(exception).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um mês válido.");
		
		lancamento.setMes(1); 
		//valida ano null
		exception = Assertions.catchThrowable( () -> service.validar( lancamento ) );
		Assertions.assertThat(exception).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um ano válido.");
		
		lancamento.setAno(12345);
		exception = Assertions.catchThrowable( () -> service.validar( lancamento ) );
		Assertions.assertThat(exception).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um ano válido.");
		
		lancamento.setAno(2020);
		//valida usuario null
		exception = Assertions.catchThrowable( () -> service.validar( lancamento ) );
		Assertions.assertThat(exception).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um usuário.");
		
		lancamento.setUsuario(new Usuario());
		exception = Assertions.catchThrowable( () -> service.validar( lancamento ) );
		Assertions.assertThat(exception).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um usuário.");
		
		lancamento.getUsuario().setId(1l);
		// valida valor null
		exception = Assertions.catchThrowable( () -> service.validar( lancamento ) );
		Assertions.assertThat(exception).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um valor válido.");
		
		lancamento.setValor(new BigDecimal("-555"));
		exception = Assertions.catchThrowable( () -> service.validar( lancamento ) );
		Assertions.assertThat(exception).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um valor válido.");
		
		lancamento.setValor(new BigDecimal("333"));
		// valida tipo lançamento null
		exception = Assertions.catchThrowable( () -> service.validar( lancamento ) );
		Assertions.assertThat(exception).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um tipo de lançamento.");
	}
}









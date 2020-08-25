package br.com.minhasFinancas.service;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.minhasFinancas.exception.ErroAutenticacao;
import br.com.minhasFinancas.exception.RegraNegocioException;
import br.com.minhasFinancas.model.entity.Usuario;
import br.com.minhasFinancas.model.repository.UsuarioRepository;
import br.com.minhasFinancas.service.impl.UsuarioServiceImpl;

@ExtendWith( SpringExtension.class )
@ActiveProfiles("test")
public class UsuarioServiceTest {

	@SpyBean
	UsuarioServiceImpl service;
	@MockBean
	UsuarioRepository repository;
	
//	@BeforeEach
//	public void setUp() {
//		service = new UsuarioServiceImpl(repository);
//	}
	
	@Test
	public void naoDeveSalvarUmUsuarioComEmailJaCadastrado() {
		//cenario
		String email = "email@email.com";
		Usuario usuario = Usuario.builder().email(email).build();
		Mockito.doThrow( RegraNegocioException.class ).when( service ).validarEmail(email);
		//ação
		Throwable exception = Assertions.catchThrowable( () ->  service.salvarUsuario(usuario) );
		
		//verificação
		Mockito.verify( repository, Mockito.never() ).save(usuario);
		Assertions.assertThat(exception).isInstanceOf(RegraNegocioException.class);
	}
	
	@Test
	public void deveSalvarUmUsuario() {
		//cenário
		Mockito.doNothing().when(service).validarEmail(Mockito.anyString());
		Usuario usuario = Usuario.builder()
				.id(1l)
				.nome("nome")
				.email("email@email.com")
				.senha("senha").build();
		Mockito.when( repository.save(Mockito.any(Usuario.class)) ).thenReturn(usuario);
		//ação
		Usuario usuarioSalvo = service.salvarUsuario( new Usuario() );
		
		//validação
		Assertions.assertThat(usuarioSalvo).isNotNull();
		Assertions.assertThat(usuarioSalvo.getId()).isEqualTo(1l);
		Assertions.assertThat(usuarioSalvo.getNome()).isEqualTo("nome");
		Assertions.assertThat(usuarioSalvo.getEmail()).isEqualTo("email@email.com");
		Assertions.assertThat(usuarioSalvo.getSenha()).isEqualTo("senha");
	}
	
	@Test
	public void deveAutenticarUmUsuarioComSucesso() {
		//cenário
		String email = "email@email.com";
		String senha = "senha";
		
		Usuario usuario = Usuario.builder().email(email).senha(senha).id(1l).build();
		Mockito.when( repository.findByEmail(email) ).thenReturn( Optional.of(usuario) );
		
		//ação
		Usuario result = service.autenticar(email, senha);
		
		//verificação
		Assertions.assertThat(result).isNotNull();
	}
	
	@Test//( expected = ErroAutenticacao.class)
	public void deveLancarErroQuandoNaoEncontrarUsuarioCadastradoComOEmailInformado() {
		//cenário
		Mockito.when( repository.findByEmail(Mockito.anyString()) ).thenReturn(Optional.empty());
		
		//ação
		Throwable exception = Assertions.catchThrowable( () -> service.autenticar("email@email.com", "senha") );
		
		//verificação
		Assertions.assertThat(exception).isInstanceOf(ErroAutenticacao.class).hasMessage("Usuário não encontrado para o email informado.");
	}
	
	@Test
	public void deveLancarErroQuandoSenhaNaoBater() {
		//cenário
		String senha = "senha";
		Usuario usuario = Usuario.builder().email("email@email.com").senha(senha).build();
		Mockito.when( repository.findByEmail(Mockito.anyString()) ).thenReturn( Optional.of(usuario) );
		
		//ação
		Throwable exception = Assertions.catchThrowable( () -> service.autenticar("email@email.com", "123") );
		
		//verificação
		Assertions.assertThat(exception).isInstanceOf(ErroAutenticacao.class).hasMessage("senha inválida");
	}
	
	@Test
	public void deveValidarEmail() {
		//cenário
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);
		
		//ação
//		Assertions.assertDoesNotThrow( () -> service.validarEmail("email@email.com") );
		service.validarEmail("email@email.com");
		
		//verificação
		
	}
	
	@Test
	public void deveLancarErroAoValidarEmailQuandoExistirEmailCadastrado() {
		//cenário
		Mockito.when( repository.existsByEmail(Mockito.anyString()) ).thenReturn(true);
		
		//ação
		Throwable exception = Assertions.catchThrowable( () ->  service.validarEmail("email@email.com") );
		
		//verificação
		//forma 1
		assertThrows(RegraNegocioException.class, () -> service.validarEmail("email@email.com"));
		//forma 2
		Assertions.assertThat(exception).isInstanceOf(RegraNegocioException.class);
	}
}

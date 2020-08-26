package br.com.minhasFinancas.api.resource;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.minhasFinancas.api.dto.UsuarioDTO;
import br.com.minhasFinancas.exception.ErroAutenticacao;
import br.com.minhasFinancas.exception.RegraNegocioException;
import br.com.minhasFinancas.model.entity.Usuario;
import br.com.minhasFinancas.service.LancamentoService;
import br.com.minhasFinancas.service.UsuarioService;

@RestController
@RequestMapping("/api/usuarios") // espesifica que todas as requisições que começarem com "/api/usuarios" viram aqui
public class UsuarioResource {

	@GetMapping("/")
	public String helloWorld() {
		return "Hello world!";
	}
	
	private UsuarioService service;
	private LancamentoService lancamentoService;
	
	public UsuarioResource(UsuarioService service, LancamentoService lancamentoService) {
		this.service = service;
		this.lancamentoService = lancamentoService;
	}
	
	@GetMapping("{id}/saldo")
	public ResponseEntity obterSaldo( @PathVariable("id") Long id ) {
		Optional<Usuario> usuario = service.finById(id);
		if (!usuario.isPresent()) {
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}
		BigDecimal saldo = lancamentoService.obterSaldoPorUsuario(id);
		return ResponseEntity.ok(saldo);
	}
	
	@PostMapping
	public ResponseEntity salvar( @RequestBody UsuarioDTO dto) {
		Usuario usuario = Usuario.builder()
				.nome(dto.getNome())
				.email(dto.getEmail())
				.senha(dto.getSenha())
				.build();
		try {
			Usuario usuarioSalvo = service.salvarUsuario(usuario);
			return new ResponseEntity(usuarioSalvo, HttpStatus.CREATED);
		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@PostMapping("/autenticar")
	public ResponseEntity autenticar ( @RequestBody UsuarioDTO dto ) {
		try {
			Usuario usuarioAutenticado = service.autenticar(dto.getEmail(), dto.getSenha());
			return ResponseEntity.ok(usuarioAutenticado);
		} catch (ErroAutenticacao e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
}

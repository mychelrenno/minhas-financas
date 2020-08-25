package br.com.minhasFinancas.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.minhasFinancas.exception.ErroAutenticacao;
import br.com.minhasFinancas.exception.RegraNegocioException;
import br.com.minhasFinancas.model.entity.Usuario;
import br.com.minhasFinancas.model.repository.UsuarioRepository;
import br.com.minhasFinancas.service.UsuarioService;

@Service
public class UsuarioServiceImpl implements UsuarioService {
	
	private UsuarioRepository repository;
	
	@Autowired
	public UsuarioServiceImpl(UsuarioRepository repository) {
		super();
		this.repository = repository;
	}

	@Override
	public Usuario autenticar(String email, String senha) {
		Optional<Usuario> usuario = repository.findByEmail(email);
		if (!usuario.isPresent()) { 
			throw new ErroAutenticacao("Usuário não encontrado para o email informado.");
		}
		if (!usuario.get().getSenha().equals(senha)) {
			throw new ErroAutenticacao("senha inválida");
		}
		return usuario.get();
	}

	@Override
	@Transactional // cria uma transação e commita
	public Usuario salvarUsuario(Usuario usuario) {
		validarEmail(usuario.getEmail());
		return repository.save(usuario);
	}

	@Override
	public void validarEmail(String email) {
		boolean existis = repository.existsByEmail(email);
		if (existis) {
			throw new RegraNegocioException("Já existe um usuário cadastrado com este email.");
		}
	}

	@Override
	public Optional<Usuario> finById(Long id) {
		return repository.findById(id);
	}

}

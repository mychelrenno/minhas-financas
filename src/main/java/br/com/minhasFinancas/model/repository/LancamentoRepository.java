package br.com.minhasFinancas.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.minhasFinancas.model.entity.Lancamento;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long>{

}

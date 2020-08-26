package br.com.minhasFinancas.model.repository;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.minhasFinancas.model.entity.Lancamento;
import br.com.minhasFinancas.model.enums.TipoLancamento;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long>{

	// sql do tipo JPQL (a consulta deve seguir o padrão de nomes e atributos iguais das classes)
	@Query(value = " select sum(l.valor) from Lancamento l "
			+ "join l.usuario u "
			+ "where u.id = :idUsuario and l.tipo = :tipo "
			+ "group by u")
	BigDecimal obterSaldoPorTipoLancamentoEUsuario( @Param(value = "idUsuario") Long idUsuario, @Param(value = "tipo") TipoLancamento tipo);

}

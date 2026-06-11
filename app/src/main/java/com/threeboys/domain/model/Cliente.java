package com.threeboys.domain.model;

public class Cliente {

	private Long id;
	private String nome;
	private String telefone;
	private String endereco;
	private Contato prefContato;
	private Pagamento prefPagamento;
	private String observacao;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public String getEndereco() {
		return endereco;
	}

	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}

	public Contato getPrefContato() {
		return prefContato;
	}

	public void setPrefContato(Contato prefContato) {
		this.prefContato = prefContato;
	}

	public Pagamento getPrefPagamento() {
		return prefPagamento;
	}

	public void setPrefPagamento(Pagamento prefPagamento) {
		this.prefPagamento = prefPagamento;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}
}

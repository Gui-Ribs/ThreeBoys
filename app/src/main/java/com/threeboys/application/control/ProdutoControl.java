package com.threeboys.application.control;

import com.threeboys.application.repository.ProdutoRepository;
import com.threeboys.domain.model.Produto;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ProdutoControl {

	private final ProdutoRepository pr;

	private ObservableList<Produto> lista = FXCollections.observableArrayList();

	private LongProperty id = new SimpleLongProperty(0);
	private StringProperty nome = new SimpleStringProperty("");
	private StringProperty tamanho = new SimpleStringProperty("");
	private StringProperty chocolate = new SimpleStringProperty("");
	private StringProperty tipo = new SimpleStringProperty("");
	private DoubleProperty preco = new SimpleDoubleProperty(0);
	private IntegerProperty qtde = new SimpleIntegerProperty(0);
	private StringProperty observacao = new SimpleStringProperty("");

	public ProdutoControl(ProdutoRepository produtoRepository) {
		this.pr = produtoRepository;
		carregar();
	}

	public Produto toModel() {
		Produto p = new Produto();

		if (id.get() > 0) {
			p.setId(id.get());
		}

		p.setNome(nome.get());
		p.setTamanho(tamanho.get());
		p.setChocolate(chocolate.get());
		p.setTipo(tipo.get());
		p.setPreco(preco.get());
		p.setQtde(qtde.get());
		p.setObservacao(observacao.get());

		return p;
	}

	public void fromModel(Produto p) {
		if (p == null) {
			limparCampos();
			return;
		}

		id.set(p.getId());
		nome.set(p.getNome());
		tamanho.set(p.getTamanho());
		chocolate.set(p.getChocolate());
		tipo.set(p.getTipo());
		preco.set(p.getPreco());
		qtde.set(p.getQtde());
		observacao.set(p.getObservacao());
	}

	public void salvar() {
		Produto p = toModel();
		valid(p);

		if (id.get() > 0) {
			atualizar(p);
		} else {
			cadastrar(p);
		}

		carregar();
		limparCampos();
	}

	public void pesquisar() {
		if (empty(nome.get())) {
			carregar();
			return;
		}

		lista.setAll(pr.findByName(nome.get()));
	}

	public void deletar() {
		if (id.get() <= 0) {
			throw new IllegalArgumentException("Selecione um produto");
		}

		pr.delete(id.get());

		carregar();
		limparCampos();
	}

	private void atualizar(Produto produto) {
		pr.update(produto);
	}

	private void cadastrar(Produto produto) {
		pr.save(produto);
	}

	public void limparCampos() {
		id.set(0);
		nome.set("");
		tamanho.set("");
		chocolate.set("");
		tipo.set("");
		preco.set(0);
		qtde.set(0);
		observacao.set("");
	}

	public void carregar() {
		lista.setAll(pr.findAll());
	}

	public ObservableList<Produto> getLista() {
		return lista;
	}

	public StringProperty nomeProperty() {
		return nome;
	}
	public StringProperty tamanhoProperty() {
		return tamanho;
	}
	public StringProperty chocolateProperty() {
		return chocolate;
	}
	public StringProperty tipoProperty() {
		return tipo;
	}
	public DoubleProperty precoProperty() {
		return preco;
	}
	public IntegerProperty qtdeProperty() {
		return qtde;
	}
	public StringProperty observacaoProperty() {
		return observacao;
	}

	private void valid(Produto produto) {
		if (empty(produto.getNome())) {
			throw new IllegalArgumentException("O nome do cliente não pode ser nulo");
		}
		if (empty(produto.getTamanho())) {
			throw new IllegalArgumentException("O tamanho não pode ser nulo");
		}
		if (empty(produto.getChocolate())) {
			throw new IllegalArgumentException("O chocolate não pode ser nulo");
		}
		if (empty(produto.getTipo())) {
			throw new IllegalArgumentException("O tipo não pode ser nulo");
		}
		if (empty(String.valueOf(produto.getPreco()))) {
			throw new IllegalArgumentException("O preço não pode ser nulo");
		}
		if (empty(String.valueOf(produto.getQtde()))) {
			throw new IllegalArgumentException("A quantidade não pode ser nulo");
		}
	}

	private static boolean empty(String x) {
		return x == null || x.isBlank();
	}
}

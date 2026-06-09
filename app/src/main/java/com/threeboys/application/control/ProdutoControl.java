package com.threeboys.application.control;

import com.threeboys.application.repository.ProdutoRepository;
import com.threeboys.domain.model.Produto;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ProdutoControl {

	private final ProdutoRepository produtoRepository;

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
		this.produtoRepository = produtoRepository;
		carregar();
	}

	public Produto toModel(){
		Produto p = new Produto();
		p.setNome(nome.get());
		p.setTamanho(tamanho.get());
		p.setChocolate(chocolate.get());
		p.setTipo(tipo.get());
		p.setPreco(preco.get());
		p.setQtde(qtde.get());
		p.setObservacao(observacao.get());
		return p;
	}

	public void fromModel(Produto p){
		id.set(p.getId());
		nome.set(p.getNome());
		tamanho.set(p.getTamanho());
		chocolate.set(p.getChocolate());
		tipo.set(p.getTipo());
		preco.set(p.getPreco());
		qtde.set(p.getQtde());
		observacao.set(p.getObservacao());
	}

	public void limparCampos(){
		id.set(0);
		nome.set("");
		tamanho.set("");
		chocolate.set("");
		tipo.set("");
		preco.set(0);
		qtde.set(0);
		observacao.set("");
	}

	public void salvar(){
		Produto p = toModel();

		carregar();
		limparCampos();
	}

	public void pesquisar(){

	}

	public void deletar(){

	}

	public void carregar(){

	}

	public ObservableList<Produto> getLista(){
		return lista;
	}

	public StringProperty nomeProperty(){
		return nome;
	}

	public StringProperty tamanhoProperty(){
		return tamanho;
	}

	public StringProperty chocolateProperty(){
		return chocolate;
	}

	public StringProperty tipoProperty(){
		return tipo;
	}
	public DoubleProperty precoProperty(){
		return preco;
	}
	public IntegerProperty qtdeProperty(){
		return qtde;
	}
	public StringProperty observacaoProperty(){
		return observacao;
	}
}

package com.threeboys.application.control;

import com.threeboys.application.repository.MaterialRepository;
import com.threeboys.domain.model.Material;
import com.threeboys.domain.model.Produto;
import com.threeboys.infrastructure.database.MaterialDAO;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class MaterialControl {

	private final MaterialRepository mr;

	private ObservableList<Material> lista = FXCollections.observableArrayList();

	private LongProperty id = new SimpleLongProperty(0);
	private StringProperty nome = new SimpleStringProperty("");
	private IntegerProperty qtde = new SimpleIntegerProperty(0);
	private StringProperty unidadeMedida = new SimpleStringProperty("");
	private StringProperty marca = new SimpleStringProperty("");
	private DoubleProperty preco = new SimpleDoubleProperty(0);
	private IntegerProperty estoque = new SimpleIntegerProperty(0);
	private StringProperty descricao = new SimpleStringProperty("");

	public MaterialControl(MaterialRepository materialRepository) {
		this.mr = materialRepository;
		carregar();
	}

	public Material toModel() {
		Material m = new Material();
		m.setId(id.get());
		m.setNome(nome.get());
		m.setQtde(qtde.get());
		m.setUnidadeMedida(unidadeMedida.get());
		m.setMarca(marca.get());
		m.setPreco(preco.get());
		m.setEstoque(estoque.get());
		m.setDescricao((descricao.get()));
		return m;
	}

	public void fromModel(Material m) {
		if (m != null) {
			id.set(m.getId());
			nome.set(m.getNome());
			qtde.set(m.getQtde());
			unidadeMedida.set(m.getUnidadeMedida());
			marca.set(m.getMarca());
			preco.set(m.getPreco());
			estoque.set(m.getEstoque());
			descricao.set(m.getDescricao());
		}
	}

	public void limparCampos() {
		id.set(0);
		nome.set("");
		qtde.set(0);
		unidadeMedida.set("");
		marca.set("");
		preco.set(0);
		estoque.set(0);
		descricao.set("");
	}

	public void salvar() {
		Material m = toModel();
		valid(m);
		if(m.getId() > 0){
			atualizar(m);
		}else{
			cadastra(m);
		}
		carregar();
		limparCampos();
	}

	public void pesquisar() {
		lista.clear();
		lista.addAll(mr.findByName(nome.get()));
	}

	public void deletar() {
		Material m = toModel();
		if (empty(String.valueOf(m.getId()))) {
			throw new IllegalArgumentException("Selecione um material");
		}
		mr.delete(m.getId());
		carregar();
		limparCampos();
	}

	public void carregar() {
		lista.clear();
		lista.addAll(mr.findAll());
	}

	private void cadastra(Material material){
		mr.save(material);
	}

	private void atualizar(Material material){
		mr.update(material);
	}

	public ObservableList<Material> getLista() {
		return lista;
	}

	public StringProperty nomeProperty() {
		return nome;
	}

	public IntegerProperty qtdeProperty() {
		return qtde;
	}

	public StringProperty unidadeMedidaProperty() {
		return unidadeMedida;
	}

	public StringProperty marcaProperty() {
		return marca;
	}

	public DoubleProperty precoProperty() {
		return preco;
	}

	public IntegerProperty estoqueProperty() {
		return estoque;
	}

	public StringProperty descricaoProperty() {
		return descricao;
	}

	private void valid(Material material) {
		if (empty(material.getNome())) {
			throw new IllegalArgumentException("O nome do cliente não pode ser nulo");
		}
		if (empty(material.getUnidadeMedida())) {
			throw new IllegalArgumentException("A unidade de medida não pode ser nulo");
		}
		if (empty(String.valueOf(material.getEstoque()))) {
			throw new IllegalArgumentException("O estoque não pode ser nulo");
		}
		if (empty(String.valueOf(material.getPreco()))) {
			throw new IllegalArgumentException("O preço não pode ser nulo");
		}
		if (empty(String.valueOf(material.getQtde()))) {
			throw new IllegalArgumentException("A quantidade não pode ser nulo");
		}
	}

	private static boolean empty(String x) {
		return x == null || x.isBlank();
	}
}

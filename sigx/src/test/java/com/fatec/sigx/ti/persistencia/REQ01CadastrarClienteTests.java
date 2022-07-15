package com.fatec.sigx.ti.persistencia;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import com.fatec.sigx.model.Cliente;
import com.fatec.sigx.model.ClienteRepository;

@SpringBootTest
class REQ01CadastrarClienteTests {
	@Autowired
	ClienteRepository repository;
	private Validator validator;
	private ValidatorFactory validatorFactory;

	@Test
	void ct01_cadastrar_cliente_com_sucesso() {
		// dado que o repositorio esta vazio
		repository.deleteAll();
		// quando o usuario cadastra um cliente valido
		Cliente cliente = new Cliente("Jose Pedro", "02/04/1960", "M", "99504993052", "04280130", "1234");
		repository.save(cliente);
		// entao a quantidade de registros muda de 0 para 1
		assertEquals(1, repository.count());
	}

	@Test
	void ct02_cadastrar_cliente_com_data_nascimento_invalida() {
		// dado que o repositorio esta vazio
		repository.deleteAll();
		try {
			// quando o usuario cadastra um cliente com data de nascimento invalida
			Cliente cliente = new Cliente("Jose Pedro", "31/02/1960", "M", "99504993052", "04280130", "1234");
			fail("deve disparar uma exception");
		} catch (Exception e) {
			// entao o sistema envia uma mensagem de data invalida
			assertEquals("Data invalida", e.getMessage());
		}

	}
	@Test
	void ct03_cadastrar_cliente_com_data_nascimento_com_formato_invalido() {
		// dado que o repositorio esta vazio
		repository.deleteAll();
		try {
			// quando o usuario cadastra um cliente com data de nascimento invalida
			Cliente cliente = new Cliente("Jose Pedro", "1960/02/", "M", "99504993052", "04280130", "1234");
			fail("deve disparar uma exception");
		} catch (Exception e) {
			// entao o sistema envia uma mensagem de data invalida
			assertEquals("Data invalida", e.getMessage());
		}

	}

	@Test
	void ct04_cadastrar_cliente_ja_cadastrado() {
		// dado que o cliente ja esta cadastrado
		repository.deleteAll();
		Cliente cliente = new Cliente("Jose Pedro", "02/04/1960", "M", "99504993052", "04280130", "1234");
		repository.save(cliente);
		try {
			// quando o usuario cadastra um cliente com cpf ja cadastrado
			repository.save(new Cliente("Jose Pedro", "02/04/1960", "M", "99504993052", "04280130", "1234"));
			fail("deve disparar uma exception");
		} catch (DataIntegrityViolationException e) {
			// entao o sistema envia uma mensagem de violacao de integridade
			assertEquals("could not execute statement", e.getMessage().substring(0, 27));
		}

	}

	@Test
	void ct05_cadastrar_cliente_com_nome_vazio() {
		// dado que o repositorio esta vazio
		repository.deleteAll();

		// quando o usuario cadastra um cliente com nome vazio
		Cliente cliente = new Cliente("", "02/04/1960", "M", "99504993052", "04280130", "1234");
		validatorFactory = Validation.buildDefaultValidatorFactory();
		validator = validatorFactory.getValidator();
		Set<ConstraintViolation<Cliente>> violations = validator.validate(cliente);
		// Então – o sistema rejeita o cadastro.
		//assertTrue(violations.isEmpty());
		assertFalse(violations.isEmpty());

	}

	@Test
	void ct06_cadastrar_cliente_com_cep_invalido() {
		// dado que o repositorio esta vazio
		repository.deleteAll();
		// quando o usuario cadastra um cliente valido
		Cliente cliente = new Cliente("Jose Pedro", "02/04/1960", "M", "99504993052", "", "1234");
		validatorFactory = Validation.buildDefaultValidatorFactory();
		validator = validatorFactory.getValidator();
		Set<ConstraintViolation<Cliente>> violations = validator.validate(cliente);
		// Então – o sistema rejeita o cadastro.
		//assertTrue(violations.isEmpty());
		assertFalse(violations.isEmpty());
	}
}

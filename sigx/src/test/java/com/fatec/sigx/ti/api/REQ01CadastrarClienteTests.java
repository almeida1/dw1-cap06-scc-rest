package com.fatec.sigx.ti.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.fatec.sigx.model.Cliente;
import com.fatec.sigx.model.ClienteDTO;
import com.fatec.sigx.model.ClienteRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class REQ01CadastrarClienteTests {
	@Autowired
	private TestRestTemplate testRestTemplate;
	@Autowired
	private ClienteRepository repository;
	String urlBase = "/api/v1/clientes";

	/**********************************************************************************
	 * Teste de integracao controler -> servico -> repository -> DB
	 *********************************************************************************/
	@Test
	void ct01_cadastrar_cliente_com_sucesso() {
		// dado que nao existem clientes cadastrados
		repository.deleteAll();
		// quando cadastra cliente valido
		ClienteDTO cliente = new ClienteDTO("Jose Pedro", "02/04/1960", "M", "43011831084", "04280130", "1234");
		HttpEntity<ClienteDTO> httpEntity = new HttpEntity<ClienteDTO>(cliente);
		ResponseEntity<String> resposta = testRestTemplate.exchange(urlBase, HttpMethod.POST, httpEntity, String.class);
		// Entao retorna os detalhes do cliente
		assertEquals(HttpStatus.CREATED, resposta.getStatusCode());
		Optional<Cliente> re = repository.findByCpf("43011831084");
		System.out.println(">>>>>>" + re.get().getDataCadastro());
		assertTrue(re.isPresent());
	}

	@Test
	void ct02_cadastrar_cliente_com_data_nascimento_invalida() {
		// dado que nao existem clientes cadastrados
		repository.deleteAll();
		// quando cadastra cliente valido
		ClienteDTO cliente = new ClienteDTO("Jose Pedro", "31/02/1960", "M", "43011831084", "04280130", "1234");
		HttpEntity<ClienteDTO> httpEntity = new HttpEntity<ClienteDTO>(cliente);
		ResponseEntity<String> resposta = testRestTemplate.exchange(urlBase, HttpMethod.POST, httpEntity, String.class);
		// Entao retorna os detalhes do cliente
		assertEquals(HttpStatus.BAD_REQUEST, resposta.getStatusCode());
		assertEquals("Data invalida", resposta.getBody());
		Optional<Cliente> re = repository.findByCpf("43011831084");
		// assertTrue(re.isPresent());
		assertFalse(re.isPresent());
	}

	@Test
	void ct03_cadastrar_cliente_com_data_nascimento_formato_invalido() {
		// dado que nao existem clientes cadastrados
		repository.deleteAll();
		// quando cadastra com data de nascimento invalida
		ClienteDTO cliente = new ClienteDTO("Jose Pedro", "/02/1960", "M", "43011831084", "04280130", "1234");
		HttpEntity<ClienteDTO> httpEntity = new HttpEntity<ClienteDTO>(cliente);
		ResponseEntity<String> resposta = testRestTemplate.exchange(urlBase, HttpMethod.POST, httpEntity, String.class);
		// Entao retorna requisicao invalida
		assertEquals(HttpStatus.BAD_REQUEST, resposta.getStatusCode());
		Optional<Cliente> re = repository.findByCpf("43011831084");
		// assertTrue(re.isPresent());
		assertFalse(re.isPresent());
	}

	@Test
	void ct04_cadastrar_cliente_ja_cadastrado() {
		// dado que o cliente ja esta cadastrado
		repository.deleteAll();
		Cliente c1 = new Cliente("Jose Pedro", "02/04/1960", "M", "99504993052", "04280130", "1234");
		repository.save(c1);
		// quando cadastra cliente
		ClienteDTO c2 = new ClienteDTO("Jose Pedro", "02/04/1960", "M", "99504993052", "04280130", "1234");
		HttpEntity<ClienteDTO> httpEntity = new HttpEntity<ClienteDTO>(c2);
		ResponseEntity<String> resposta = testRestTemplate.exchange(urlBase, HttpMethod.POST, httpEntity, String.class);
		// Entao retorna requisicao invalida
		assertEquals(HttpStatus.CONFLICT, resposta.getStatusCode());
		assertEquals(1, repository.count());
	}

	@Test
	void ct05_cadastrar_cliente_com_nome_vazio() {
		// dado que nao existem clientes cadastrados
		repository.deleteAll();
		// quando cadastra cliente com nome invalido
		ClienteDTO cliente = new ClienteDTO("", "31/02/1960", "M", "43011831084", "04280130", "1234");
		HttpEntity<ClienteDTO> httpEntity = new HttpEntity<ClienteDTO>(cliente);
		ResponseEntity<String> resposta = testRestTemplate.exchange(urlBase, HttpMethod.POST, httpEntity, String.class);
		// Entao retorna requisicao invalida
		assertEquals(HttpStatus.BAD_REQUEST, resposta.getStatusCode());
		Optional<Cliente> re = repository.findByCpf("43011831084");
		assertFalse(re.isPresent());
	}

	@Test
	void ct06_cadastrar_cliente_com_cep_invalido() {
		// dado que nao existem clientes cadastrados
		repository.deleteAll();
		// quando cadastra cliente com cdp invalido
		ClienteDTO cliente = new ClienteDTO("Jose Pedro", "04/02/1960", "M", "43011831084", "0428", "1234");
		HttpEntity<ClienteDTO> httpEntity = new HttpEntity<ClienteDTO>(cliente);
		ResponseEntity<String> resposta = testRestTemplate.exchange(urlBase, HttpMethod.POST, httpEntity, String.class);
		// Entao retorna requisicao invalida
		assertEquals(HttpStatus.BAD_REQUEST, resposta.getStatusCode());
		assertEquals("CEP invalido", resposta.getBody());
		Optional<Cliente> re = repository.findByCpf("43011831084");
		assertFalse(re.isPresent());
	}

}
